package com.example.food_drugs.service.impl;

import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.net.ssl.SSLContext;

import com.example.food_drugs.dto.ApiResponse;
import com.example.food_drugs.dto.ApiResponseBuilder;
import com.example.food_drugs.dto.AuditGObject;
import com.example.food_drugs.dto.responses.InventoryListDashBoardResponse;
import com.example.food_drugs.dto.responses.allInventoriesLastInfoResponse;
import com.example.food_drugs.exception.ApiGetException;
import com.example.food_drugs.helpers.Impl.AssistantServiceImpl;
import com.example.food_drugs.helpers.Impl.Dictionary;
import com.example.food_drugs.helpers.Impl.Utilities;
import com.example.food_drugs.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.ParseException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import com.example.food_drugs.entity.BindData;
import com.example.food_drugs.entity.Inventory;
import com.example.food_drugs.entity.InventoryLastData;
import com.example.food_drugs.entity.InventoryNotification;
import com.example.food_drugs.entity.MonogInventoryNotification;
import com.example.food_drugs.entity.MonogoInventoryLastData;
import com.example.food_drugs.entity.MonogoInventoryLastDataElmSend;
import com.example.food_drugs.entity.NotificationAttributes;
import com.example.food_drugs.entity.SensorsInventories;
import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.DriverSelect;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.food_drugs.entity.Warehouse;
import com.example.food_drugs.entity.userClientInventory;
import com.example.food_drugs.repository.InventoryRepository;
import com.example.food_drugs.repository.MongoInventoryLastDataRepo;
import com.example.food_drugs.repository.MongoInventoryLastDataRepository;
import com.example.food_drugs.repository.MongoInventoryLastDataRepositoryElmData;
import com.example.food_drugs.repository.MongoInventoryNotificationRepo;
import com.example.food_drugs.repository.MongoInventoryNotificationRepository;
import com.example.food_drugs.repository.SensorsInventoriesRepository;
import com.example.food_drugs.repository.UserClientInventoryRepository;
import com.example.food_drugs.repository.UserClientWarehouseRepository;
import com.example.food_drugs.repository.UserRepositorySFDA;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
import com.example.food_drugs.repository.WarehousesRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;
import com.example.examplequerydslspringdatajpamaven.service.UserRoleService;
import com.example.examplequerydslspringdatajpamaven.service.UserServiceImpl;

@Component
public class InventoryServiceImpl extends RestServiceController implements InventoryService {

	private static final Log logger = LogFactory.getLog(InventoryServiceImpl.class);

	private GetObjectResponse getObjectResponse;
	private final Dictionary dictionary;
	private final AssistantServiceImpl assistantServiceImpl;

	@Autowired
	private MongoInventoryLastDataRepository mongoInventoryLastDataRepository;

	@Autowired
	private MongoInventoryLastDataRepositoryElmData mongoInventoryLastDataRepositoryElmData;

	@Autowired
	private MongoInventoryNotificationRepository mongoInventoryNotificationRepository;

	@Autowired
	private SensorsInventoriesRepository sensorsInventoriesRepository;

	@Autowired
	private UserServiceImpl userServiceImpl;


	@Autowired
	private UserRoleService userRoleService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private InventoryRepository inventoryRepository;

	@Autowired
	UserClientWarehouseRepository userClientWarehouseRepository;

	@Autowired
	private WarehousesRepository warehousesRepository;

	@Autowired
	private UserRepositorySFDA userRepositorySFDA;

	@Autowired
	private UserClientInventoryRepository userClientInventoryRepository;

	@Autowired
	private MongoInventoryNotificationRepo mongoInventoryNotificationRepo;

	@Autowired
	private MongoInventoryLastDataRepo mongoInventoryLastDataRepo;

	private final Utilities utilities;

	public InventoryServiceImpl( Dictionary dictionary, AssistantServiceImpl assistantServiceImpl, Utilities utilities) {
		this.dictionary = dictionary;
		this.assistantServiceImpl = assistantServiceImpl;
		this.utilities = utilities;
	}

	@Override
	public ResponseEntity<?> getInventoriesList(String TOKEN, Long id, int offset, String search, int active,String exportData) {
        logger.info("************************ getInventoriesList STARTED ***************************");
	    Integer size = 0;
	    List<Map> data = new ArrayList<>();
		List<Inventory> inventories = new ArrayList<Inventory>();

		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",inventories);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}

		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(id != 0) {

			User user = userServiceImpl.findById(id);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",inventories);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(user.getAccountType()!= 1) {
					if(!userRoleService.checkUserHasPermission(id, "INVENTORY", "list")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get INVENTORY list",null);
						 logger.info("************************ getInventoryList ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				if(user.getDeleteDate() == null) {
					 List<Long>usersIds= new ArrayList<>();

				    if(user.getAccountType().equals(4)) {
							 List<Long> inventoryIds = userClientInventoryRepository.getInventoryIds(id);
							 if(inventoryIds.size()>0) {
								 if(active == 0) {
									if(exportData.equals("exportData")) {
										inventories = inventoryRepository.getInventoriesByIdsDeactiveExport(inventoryIds,search);

									}
									else {
										inventories = inventoryRepository.getInventoriesByIdsDeactive(inventoryIds,offset,search);
										size = inventoryRepository.getInventoriesSizeByIdsDeactive(inventoryIds);
									}


								 }

								 if(active == 2) {
									if(exportData.equals("exportData")) {
										inventories = inventoryRepository.getInventoriesByIdsAllExport(inventoryIds,search);


									}
									else {
										inventories = inventoryRepository.getInventoriesByIdsAll(inventoryIds,offset,search);
										size = inventoryRepository.getInventoriesSizeByIdsAll(inventoryIds);
									}


								 }

								 if(active == 1) {
									if(exportData.equals("exportData")) {
										inventories = inventoryRepository.getInventoriesByIdsExport(inventoryIds,search);

									}
									else {
										inventories = inventoryRepository.getInventoriesByIds(inventoryIds,offset,search);
										size = inventoryRepository.getInventoriesSizeByIds(inventoryIds);
									}


								 }

							 }
					}
					else {
						List<User>childernUsers = userServiceImpl.getActiveAndInactiveChildern(id);
						 if(childernUsers.isEmpty()) {
							 usersIds.add(id);
						 }
						 else {
							 usersIds.add(id);
							 for(User object : childernUsers) {
								 usersIds.add(object.getId());
							 }
						 }


						 if(active == 0) {
							if(exportData.equals("exportData")) {
								inventories = inventoryRepository.getInventoriesDeactiveExport(usersIds,search);

							}
							else {
								inventories = inventoryRepository.getInventoriesDeactive(usersIds,offset,search);
								size = inventoryRepository.getInventoriesSizeDeactive(usersIds);
							}


						 }

						 if(active == 2) {
							if(exportData.equals("exportData")) {
								inventories = inventoryRepository.getInventoriesAllExport(usersIds,search);

							}
							else {
								inventories = inventoryRepository.getInventoriesAll(usersIds,offset,search);
								size = inventoryRepository.getInventoriesSizeAll(usersIds);
							}


						 }

						 if(active == 1) {
							if(exportData.equals("exportData")) {
								inventories = inventoryRepository.getInventoriesExport(usersIds,search);

							}
							else {
								inventories = inventoryRepository.getInventories(usersIds,offset,search);
								size = inventoryRepository.getInventoriesSize(usersIds);
							}


						 }
					}






					 for(Inventory inventory:inventories) {
						 Date now=new Date();
						 Long leftDays=null;
						 if(inventory.getEndDate()==null||inventory.getEndDate().before(now)){
							 leftDays=Long.valueOf(0);
						 }
						 else if(inventory.getEndDate()!=null){
							 leftDays= ChronoUnit.DAYS.between(now.toInstant(),inventory.getEndDate().toInstant());
						 }
					     Map InventoriesList= new HashMap();


					     InventoriesList.put("id", inventory.getId());
					     InventoriesList.put("trackerIMEI", inventory.getTrackerIMEI());
					     InventoriesList.put("inventoryNumber", inventory.getInventoryNumber());
					     InventoriesList.put("name", inventory.getName());
					     InventoriesList.put("storingCategory", inventory.getStoringCategory());
					     InventoriesList.put("userId", inventory.getUserId());
					     InventoriesList.put("delete_date", inventory.getDeleteDate());
						 InventoriesList.put("warehouseId", inventory.getWarehouseId());
						 InventoriesList.put("referenceKey", inventory.getReferenceKey());
						 InventoriesList.put("protocolType", inventory.getProtocolType());
						 InventoriesList.put("userName", null);
						 InventoriesList.put("warehouserName", null);
						 InventoriesList.put("leftDays",leftDays);
						 InventoriesList.put("endDate",inventory.getEndDate());
						 InventoriesList.put("create_date", inventory.getCreate_date());
						 InventoriesList.put("regestration_to_elm_date", inventory.getRegestration_to_elm_date());
						 InventoriesList.put("delete_from_elm_date", inventory.getDelete_from_elm_date());
						 InventoriesList.put("update_date_in_elm", inventory.getUpdate_date_in_elm());

						 Warehouse war = new Warehouse();
						 User us = new User();

						 if(inventory.getWarehouseId() != null) {
							 war = warehousesRepository.findOne(inventory.getWarehouseId());

						 }

                         if(inventory.getUserId() != null) {
    						 us = userRepository.findOne(inventory.getUserId());

						 }
						 if(us != null) {
							 InventoriesList.put("userName", us.getName());

						 }
						 if(war != null) {
							 InventoriesList.put("warehouserName", war.getName());

						 }
						 data.add(InventoriesList);




					}
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",data,size);
					logger.info("************************ getInventoriesList ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);

				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",inventories);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}

			}

		}
		else{

			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",inventories);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
	}

	@Override
	public ResponseEntity<?> createInventories(String TOKEN, Inventory inventory, Long userId)  {
		logger.info("************************ createInventories STARTED ***************************");
		Date now = new Date();
		SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String nowTime = isoFormat.format(now);

		inventory.setCreate_date(nowTime);
		inventory.setDeleteDate(null);

		List<Inventory> inventories= new ArrayList<Inventory>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",inventories);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}

		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId != 0) {
			User user = userServiceImpl.findById(userId);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",inventories);
				return ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(user.getAccountType()!= 1) {
					if(!userRoleService.checkUserHasPermission(userId, "INVENTORY", "create")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to create",null);
						 logger.info("************************ createInventories ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				if(user.getAccountType()==3||user.getAccountType()==4){
					inventory.setEndDate(now);
				}

				if(user.getDeleteDate()==null) {
					if(inventory.getName()== null || inventory.getName().equals("")
					   || inventory.getActivity()== null || inventory.getActivity().equals("")
					   || inventory.getInventoryNumber()== null || inventory.getInventoryNumber().equals("")
					   || inventory.getStoringCategory()== null || inventory.getStoringCategory().equals("")
					   ) {
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "[name ,activity ,InventoryNumber ,StoringCategory] are Required",null);
						return ResponseEntity.badRequest().body(getObjectResponse);

					}

					 User parent = null;

					if(inventory.getId()==null || inventory.getId()==0) {
						if(user.getAccountType().equals(4)) {
							 Set<User> parentClients = user.getUsersOfUser();
							 if(parentClients.isEmpty()) {
								 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are account type 4 and not has parent",null);
								 return  ResponseEntity.badRequest().body(getObjectResponse);
							 }else {
								 for(User object : parentClients) {
									 parent = object;
								 }
								 inventory.setUserId(parent.getId());


							 }
						 }
						 else {
							 inventory.setUserId(userId);

						 }

						List<Inventory> res = inventoryRepository.checkDublicateAdd(inventory.getUserId(), inventory.getName(), inventory.getInventoryNumber());
					    List<Integer> duplictionList =new ArrayList<Integer>();
						List<Inventory> res2 = inventoryRepository.checkDublicateAddByInv(inventory.getInventoryNumber());

						if(!res.isEmpty() || !res2.isEmpty()) {
							for(int i=0;i<res.size();i++) {

								if(res.get(i).getName() != null) {
									if(res.get(i).getName().equals(inventory.getName())) {
										duplictionList.add(1);

									}
								}



							}
							for(int i=0;i<res2.size();i++) {

								if(res2.get(i).getInventoryNumber() != null) {

									if(res2.get(i).getInventoryNumber().equals(inventory.getInventoryNumber())) {
										duplictionList.add(2);

									}

								}

							}
							getObjectResponse = new GetObjectResponse( 201, "This inventorie was found before",duplictionList);
							return ResponseEntity.ok().body(getObjectResponse);

						}
						String createDate=inventory.getCreate_date();
						try {
							Date date=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(createDate);
							inventory.setStartDate(date);
						} catch (java.text.ParseException e) {
							throw new RuntimeException(e);
						}
						String ra ; Date date= new Date();
						SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

						//							date = sdf.format(inventory.getEndDate());
						date = inventory.getEndDate();
						ra = sdf.format(date);
						try {
							date =sdf.parse(ra);

						}catch (Exception e){

						}
						inventory.setEndDate(date);


						inventory.getEndDate();

						inventory.setConvertToUtc(Boolean.TRUE);
						inventoryRepository.save(inventory);
						AuditGObject<Inventory> newEntity = new AuditGObject<>();
						newEntity.setEntity(inventory);
						assistantServiceImpl.saveOnAuditChanges(userId,null,newEntity,"create",inventory.getId());
						inventories.add(inventory);

						if(user.getAccountType().equals(4)) {
				    		userClientInventory saveData = new userClientInventory();

					        Long invId = inventoryRepository.getInventoryIdByName(parent.getId(),inventory.getName(),inventory.getInventoryNumber());
				    		if(invId != null) {
					    		saveData.setUserid(userId);
					    		saveData.setInventoryid(invId);
					    		userClientInventoryRepository.save(saveData);
				    		}

				    	}

						getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"sucsess",null);
						logger.info("************************ createInventories ENDED ***************************");

						return ResponseEntity.ok().body(getObjectResponse);

					}
					else {
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not allow to update inventories Id",inventories);
						return ResponseEntity.badRequest().body(getObjectResponse);

					}


				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",inventories);
					return ResponseEntity.status(404).body(getObjectResponse);

				}

			}


		}
		else {

			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",inventories);
			return ResponseEntity.badRequest().body(getObjectResponse);


		}
	}

	@Override
	public ResponseEntity<?> editInventories(String TOKEN, Inventory inventory, Long userId) {
		List<Inventory> inventories = new ArrayList<Inventory>();

		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",inventories);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}

		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		User loggedUser = userServiceImpl.findById(userId);
		 if(loggedUser == null) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged User is not found ",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		 }
		 if(loggedUser.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "INVENTORY", "edit")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to edit Inventories",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}

		 if(inventory.getId() == null) {
			 getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Inventory ID is Required",inventories);
			return ResponseEntity.status(404).body(getObjectResponse);
		 }
		 Inventory inventoryCheck = inventoryRepository.findOne(inventory.getId());
			if(inventoryCheck == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Inventories is not found",inventories);
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			if(inventoryCheck.getDeleteDate() != null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Inventories is not found or deleted",inventories);
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			Date end=inventoryCheck.getEndDate();
			if(loggedUser.getAccountType()==3||loggedUser.getAccountType()==4){
				inventory.setEndDate(end);
			}
		inventory.setUserId(inventoryCheck.getUserId());
		Long createdBy=inventory.getUserId();
		Boolean isParent=false;

		if(createdBy.toString().equals(userId.toString())) {
			isParent=true;
		}

		List<User>childs = new ArrayList<User>();
		if(loggedUser.getAccountType().equals(4)) {
			 List<User> parents=userServiceImpl.getAllParentsOfuser(loggedUser,loggedUser.getAccountType());
			 if(parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this inventories.",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 else {
				 User parentClient = new User() ;

				 for(User object : parents) {
					 parentClient = object;
					 break;
				 }

				userServiceImpl.resetChildernArray();
				childs = userServiceImpl.getAllChildernOfUser(parentClient.getId());
			 }

		}
		else {
			userServiceImpl.resetChildernArray();
			childs = userServiceImpl.getAllChildernOfUser(userId);
		}



		User parentChilds = new User();
		if(!childs.isEmpty()) {
			for(User object : childs) {
				parentChilds = object;
				if(parentChilds.getId().toString().equals(createdBy.toString())) {
					isParent=true;
					break;
				}
			}
		}

		if(loggedUser.getAccountType().equals(4)) {
			List<Long> inventoryData = userClientInventoryRepository.getInventory(userId,inventory.getId());
			if(inventoryData.isEmpty()) {
				isParent = false;
			}
			else {
				isParent = true;
			}
		}

		if(isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get inventories",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}

		if(inventory.getName()== null || inventory.getName().equals("")
				   || inventory.getActivity()== null || inventory.getActivity().equals("")
				   || inventory.getInventoryNumber()== null || inventory.getInventoryNumber().equals("")
				   || inventory.getStoringCategory()== null || inventory.getStoringCategory().equals("")
				   ) {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "[name ,activity ,InventoryNumber ,StoringCategory] are Required",null);
			return ResponseEntity.badRequest().body(getObjectResponse);

		}
		else {

			List<Inventory> res = inventoryRepository.checkDublicateEdit(inventory.getId(),inventory.getUserId(), inventory.getName(), inventory.getInventoryNumber());
		    List<Integer> duplictionList =new ArrayList<Integer>();
			List<Inventory> res2 = inventoryRepository.checkDublicateEditByInv(inventory.getId(), inventory.getInventoryNumber());

			if(!res.isEmpty() || !res2.isEmpty()) {
				for(int i=0;i<res.size();i++) {

					if(res.get(i).getName() != null) {

						if(res.get(i).getName().equals(inventory.getName())) {
							duplictionList.add(1);

						}
					}


				}
				for(int i=0;i<res2.size();i++) {
					if(res2.get(i).getInventoryNumber() != null) {

						if(res2.get(i).getInventoryNumber().equals(inventory.getInventoryNumber())) {
							duplictionList.add(2);

						}
					}



				}
				getObjectResponse = new GetObjectResponse( 201, "This inventory was found before",duplictionList);
				return ResponseEntity.ok().body(getObjectResponse);

			}



			AuditGObject<Inventory> oldEntity = new AuditGObject<>();
			AuditGObject<Inventory> newEntity = new AuditGObject<>();
			oldEntity.setEntity(inventoryCheck);
			newEntity.setEntity(inventory);
			assistantServiceImpl.saveOnAuditChanges(userId,oldEntity,newEntity,"update",inventory.getId());
			inventoryRepository.save(inventory);


			inventories.add(inventory);
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Updated Successfully",inventories);
			logger.info("************************ editInventories ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);





		}



	}

	@Override
	public ResponseEntity<?> getInventoryById(String TOKEN, Long InventoryId, Long userId) {
		// TODO Auto-generated method stub

		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}

		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(InventoryId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No InventoryId  to return",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		if(userId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No loggedId",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		Inventory inventory= inventoryRepository.findOne(InventoryId);
		if(inventory == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this Inventory not found",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}
		if(inventory.getDeleteDate() != null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this Inventory not found or deleted",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}

		User loggedUser = userServiceImpl.findById(userId);
		if(loggedUser == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this user not found",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}


		Long createdBy=inventory.getUserId();
		Boolean isParent=false;

		if(createdBy.toString().equals(userId.toString())) {
			isParent=true;
		}
		List<User>childs = new ArrayList<User>();
		if(loggedUser.getAccountType().equals(4)) {
			 List<User> parents=userServiceImpl.getAllParentsOfuser(loggedUser,loggedUser.getAccountType());
			 if(parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this inventory.",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 else {
				 User parentClient = new User() ;

				 for(User object : parents) {
					 parentClient = object;
					 break;
				 }

				userServiceImpl.resetChildernArray();
				childs = userServiceImpl.getAllChildernOfUser(parentClient.getId());
			 }

		}
		else {
			userServiceImpl.resetChildernArray();
			childs = userServiceImpl.getAllChildernOfUser(userId);
		}



		User parentChilds = new User();
		if(!childs.isEmpty()) {
			for(User object : childs) {
				parentChilds = object;
				if(parentChilds.getId().toString().equals(createdBy.toString())) {
					isParent=true;
					break;
				}
			}
		}

		if(loggedUser.getAccountType().equals(4)) {
			List<Long> inventoryData = userClientInventoryRepository.getInventory(userId,inventory.getId());
			if(inventoryData.isEmpty()) {
				isParent = false;
			}
			else {
				isParent = true;
			}
		}


		if(isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get inventory",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}



		List<Inventory> inventories = new ArrayList<Inventory>();
		inventories.add(inventory);
		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",inventories);
		return  ResponseEntity.ok().body(getObjectResponse);
	}

	@Override
	public ResponseEntity<?> activeInventory(String TOKEN, Long InventoryId, Long userId) {
		// TODO Auto-generated method stub
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}

		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		User loggedUser = userServiceImpl.findById(userId);
		 if(loggedUser == null) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged User is not found",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		 }
		 if(loggedUser.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "INVENTORY", "delete")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to delete inventory",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		if(InventoryId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No InventoryId  to delete",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		Inventory inventory= inventoryRepository.findOne(InventoryId);
		if(inventory == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this InventoryId not found",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}


		Long createdBy=inventory.getUserId();
		Boolean isParent=false;

		if(createdBy.toString().equals(userId.toString())) {
			isParent=true;
		}
		List<User>childs = new ArrayList<User>();
		if(loggedUser.getAccountType().equals(4)) {
			 List<User> parents=userServiceImpl.getAllParentsOfuser(loggedUser,loggedUser.getAccountType());
			 if(parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this inventory.",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 else {
				 User parentClient = new User() ;

				 for(User object : parents) {
					 parentClient = object;
					 break;
				 }

				userServiceImpl.resetChildernArray();
				childs = userServiceImpl.getAllChildernOfUser(parentClient.getId());
			 }

		}
		else {
			userServiceImpl.resetChildernArray();
			childs = userServiceImpl.getAllChildernOfUser(userId);
		}



		User parentChilds = new User();
		if(!childs.isEmpty()) {
			for(User object : childs) {
				parentChilds = object;
				if(parentChilds.getId().toString().equals(createdBy.toString())) {
					isParent=true;
					break;
				}
			}
		}

		if(loggedUser.getAccountType().equals(4)) {
			List<Long> inventoryData = userClientInventoryRepository.getInventory(userId,inventory.getId());
			if(inventoryData.isEmpty()) {
				isParent = false;
			}
			else {
				isParent = true;
			}
		}

		if(isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get inventory",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}


	    inventory.setDeleteDate(null);
	    inventoryRepository.save(inventory);


		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",null);
		return  ResponseEntity.ok().body(getObjectResponse);
	}

	@Override
	public ResponseEntity<?> deleteInventory(String TOKEN, Long InventoryId, Long userId) {
		// TODO Auto-generated method stub
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}

		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		User loggedUser = userServiceImpl.findById(userId);
		 if(loggedUser == null) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged User is not found",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		 }
		 if(loggedUser.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "INVENTORY", "delete")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to delete inventory",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		if(InventoryId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No InventoryId  to delete",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		Inventory inventory= inventoryRepository.findOne(InventoryId);
		if(inventory == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this InventoryId not found",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}

		if(inventory.getDeleteDate() != null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this Inventory not found or deleted",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}



		Long createdBy=inventory.getUserId();
		Boolean isParent=false;

		if(createdBy.toString().equals(userId.toString())) {
			isParent=true;
		}
		List<User>childs = new ArrayList<User>();
		if(loggedUser.getAccountType().equals(4)) {
			 List<User> parents=userServiceImpl.getAllParentsOfuser(loggedUser,loggedUser.getAccountType());
			 if(parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this inventory.",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 else {
				 User parentClient = new User() ;

				 for(User object : parents) {
					 parentClient = object;
					 break;
				 }

				userServiceImpl.resetChildernArray();
				childs = userServiceImpl.getAllChildernOfUser(parentClient.getId());
			 }

		}
		else {
			userServiceImpl.resetChildernArray();
			childs = userServiceImpl.getAllChildernOfUser(userId);
		}



		User parentChilds = new User();
		if(!childs.isEmpty()) {
			for(User object : childs) {
				parentChilds = object;
				if(parentChilds.getId().toString().equals(createdBy.toString())) {
					isParent=true;
					break;
				}
			}
		}

		if(loggedUser.getAccountType().equals(4)) {
			List<Long> inventoryData = userClientInventoryRepository.getInventory(userId,inventory.getId());
			if(inventoryData.isEmpty()) {
				isParent = false;
			}
			else {
				isParent = true;
			}
		}

		if(isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get inventory",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}


		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DATE);
	    int month = cal.get(Calendar.MONTH) + 1;
	    int year = cal.get(Calendar.YEAR);
	    String date = year +"-"+ month +"-"+ day;
	    inventory.setDeleteDate(date);
	    inventory.setWarehouseId(null);
		AuditGObject<Inventory> oldEntity = new AuditGObject<>();
		oldEntity.setEntity(inventory);
		assistantServiceImpl.saveOnAuditChanges(userId,oldEntity,null,"delete",InventoryId);
	    inventoryRepository.save(inventory);


		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",null);
		return  ResponseEntity.ok().body(getObjectResponse);
	}

	@Override
	public ResponseEntity<?> removeWarehouseFromInventory(String TOKEN, Long userId, Long InventoryId,
			Long warehouseId) {
		// TODO Auto-generated method stub
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}

		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		User loggedUser = userServiceImpl.findById(userId);
		 if(loggedUser == null) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged User is not found",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		 }
		 if(loggedUser.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "INVENTORY", "assignWarehouse")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to remove Warehouse",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		if(InventoryId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No InventoryId  to remove",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		Inventory inventory= inventoryRepository.findOne(InventoryId);
		if(inventory == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this InventoryId not found",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}

		if(inventory.getDeleteDate() != null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this Inventory not found or deleted",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}


		Boolean isParentInventory=true;

		if(loggedUser.getAccountType().equals(4)) {
			List<Long> inventoryData = userClientInventoryRepository.getInventory(userId,inventory.getId());
			if(inventoryData.isEmpty()) {
				isParentInventory = false;
			}
			else {
				isParentInventory = true;
			}


		}

		if(isParentInventory == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get inventory",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}


		inventory.setWarehouseId(null);
		inventoryRepository.save(inventory);



		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "removed successfully",null);
		return  ResponseEntity.ok().body(getObjectResponse);
	}

	@Override
	public ResponseEntity<?> assignWarehouseToInventory(String TOKEN, Long userId, Long InventoryId, Long warehouseId) {
		// TODO Auto-generated method stub
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}

		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No userId  to get his inventory or warehouse",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userServiceImpl.findById(userId);
		 if(loggedUser == null) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "loggedUser is not found",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		 }
		 if(loggedUser.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "INVENTORY", "assignWarehouse")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to assign Warehouse",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		if(InventoryId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No InventoryId to assign",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		Inventory inventory= inventoryRepository.findOne(InventoryId);
		if(inventory == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this InventoryId not found",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}

		if(inventory.getDeleteDate() != null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this Inventory not found or deleted",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}

		if(warehouseId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No warehouseId  to assign",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		Warehouse warehouse= warehousesRepository.findOne(warehouseId);
		if(warehouse == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this warehouseId not found",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}

		if(warehouse.getDeleteDate() != null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this warehouse not found or deleted",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}

		Boolean isParentWarehouse=true;
		Boolean isParentInventory=true;

		if(loggedUser.getAccountType().equals(4)) {
			List<Long> inventoryData = userClientInventoryRepository.getInventory(userId,inventory.getId());
			if(inventoryData.isEmpty()) {
				isParentInventory = false;
			}
			else {
				isParentInventory = true;
			}

			List<Long> warehousesData = userClientWarehouseRepository.getWarehouse(userId,warehouse.getId());
			if(warehousesData.isEmpty()) {
				isParentWarehouse = false;
			}
			else {
				isParentWarehouse = true;
			}
		}

		if(isParentInventory == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get inventory",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}

		if(isParentWarehouse == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get warehouse",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}

		inventory.setWarehouseId(warehouseId);
		inventoryRepository.save(inventory);



		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "assigned successfully",null);
		return  ResponseEntity.ok().body(getObjectResponse);
	}

	@Override
	public ResponseEntity<?> getInventoriesNotifications(String TOKEN, Long userId, int offset, String search) {
		logger.info("************************ getNotifications STARTED ***************************");

		List<InventoryNotification> notifications = new ArrayList<InventoryNotification>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",notifications);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}

		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		List<Long> allInventories = new ArrayList<Long>();
		List<Long>usersIds= new ArrayList<>();
		if(userId != 0) {

			User user = userServiceImpl.findById(userId);
			if(user != null) {
				userServiceImpl.resetChildernArray();

				 if(user.getAccountType() == 4) {
					 allInventories = userClientInventoryRepository.getInventoryIds(userId);

				 }
				 else {
					 usersIds.add(userId);
					 allInventories = inventoryRepository.getAllInventoriesIds(usersIds);

				 }




					notifications = mongoInventoryNotificationRepo.getNotificationsToday(allInventories, offset);
					Integer size=0;
					if(notifications.size()>0) {
						//size= mongoInventoryNotificationRepo.getNotificationsTodaySize(allInventories);
						for(int i=0;i<notifications.size();i++) {

							Inventory inventory = inventoryRepository.findOne(notifications.get(i).getInventory_id());
							if(inventory != null) {
								notifications.get(i).setInventoryName(inventory.getName());

							}
						}

					}



					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",notifications,size);
					logger.info("************************ getNotifications ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);


			}
			else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "User ID is not found",notifications);
				logger.info("************************ getNotifications ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);

			}



		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",notifications);
			logger.info("************************ getNotifications ENDED ***************************");
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}

	}

	@Override
	public ResponseEntity<?> getAllInventoriesLastInfo(String TOKEN, Long userId, int offset, String search) {
		logger.info("************************ getAllInventoriesLastInfo STARTED ***************************");

		List<InventoryLastData> inventories = new ArrayList<InventoryLastData>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",inventories);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}

		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		List<Long> allInventories = new ArrayList<Long>();
		List<Long>usersIds= new ArrayList<>();
		if(userId != 0) {

			User user = userServiceImpl.findById(userId);
			if(user != null) {
				userServiceImpl.resetChildernArray();

				 if(user.getAccountType() == 4) {

					 allInventories = userClientInventoryRepository.getInventoryIds(userId);

				 }
				 else {
					 usersIds.add(userId);

					 List<User>childernUsers = userServiceImpl.getActiveAndInactiveChildern(userId);
					 if(childernUsers.isEmpty()) {
						 usersIds.add(userId);
					 }
					 else {
						 usersIds.add(userId);
						 for(User object : childernUsers) {
							 usersIds.add(object.getId());
						 }
					 }

					 allInventories = inventoryRepository.getAllInventoriesIds(usersIds);


				 }



					inventories = mongoInventoryLastDataRepo.getLastData(allInventories, offset);
					Integer size=0;
					if(inventories.size()>0) {
						size= mongoInventoryLastDataRepo.getLastDataSize(allInventories);
						for(int i=0;i<inventories.size();i++) {

							Inventory inventory = inventoryRepository.findOne(inventories.get(i).getInventory_id());
							if(inventory != null) {
								inventories.get(i).setInventoryName(inventory.getName());

							}
						}
						if(Pattern.matches(".*\\S.*" , search)){
							inventories = inventories.stream().filter(inventoryLastData ->
									inventoryLastData.getInventoryName().contains(search)).collect(Collectors.toList());
						}
					}



					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",inventories,size);
					logger.info("************************ getNotifications ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);


			}
			else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "User ID is not found",inventories);
				logger.info("************************ getNotifications ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);

			}



		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",inventories);
			logger.info("************************ getNotifications ENDED ***************************");
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
	}

	@Override
	public ResponseEntity<?> getAllInventoriesLastInfoNew(String TOKEN, Long userId, int offset, String search ,String timeOffset) {
		logger.info("************************ getAllInventoriesLastInfo STARTED ***************************");

		List<InventoryLastData> inventories = new ArrayList<InventoryLastData>();
		if(TOKEN.equals("")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",inventories);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}

		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		List<allInventoriesLastInfoResponse> inventoryLastData =  new ArrayList<>();
//		List<InventorySummaryDataWrapperResponse> allInventoriesSumDataFromMySQL = new ArrayList<>();
		List<Inventory> allInventoriesSumDataFromMySQL = new ArrayList<>();
		List<Long>usersIds= new ArrayList<>();
		if(userId != 0) {

			User user = userServiceImpl.findById(userId);
			if(user != null) {
				userServiceImpl.resetChildernArray();

				if(user.getAccountType() == 4)
//					allInventoriesSumDataFromMySQL = inventoryRepository.findAllByUserIdInAndDeleteDate(usersIds,null, new PageRequest(offset,10));
					allInventoriesSumDataFromMySQL = inventoryRepository.findAllByUserIdInAndDeleteDate(usersIds,null);
				else {
					usersIds.add(userId);
					List<User>childernUsers = userServiceImpl.getActiveAndInactiveChildern(userId);
					if(childernUsers.isEmpty()) {
						usersIds.add(userId);
					}
					else {
						usersIds.add(userId);
						for(User object : childernUsers) {
							usersIds.add(object.getId());
						}
					}

//					allInventoriesSumDataFromMySQL = inventoryRepository.findAllByUserIdInAndDeleteDate(usersIds,null, new PageRequest(offset,10));
					allInventoriesSumDataFromMySQL = inventoryRepository.findAllByUserIdInAndDeleteDate(usersIds,null);

				}
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				DecimalFormat df = new DecimalFormat("#.##");
				List<Long> wareHouseIds = new ArrayList<>();
				List<String> lastDataIds = new ArrayList<>();
//				for (Inventory inventory : allInventoriesSumDataFromMySQL) {
//					if (inventory.getWarehouseId() != null){
//						wareHouseIds.add(inventory.getWarehouseId());
//					}
//				}
				wareHouseIds = allInventoriesSumDataFromMySQL.stream()
						.map(Inventory::getWarehouseId)
						.filter(Objects::nonNull).collect(Collectors.toList());
				lastDataIds = allInventoriesSumDataFromMySQL.stream()
						.map(Inventory::getLastDataId)
						.filter(Objects::nonNull).collect(Collectors.toList());
				Optional<List<MonogoInventoryLastData>> mongoInv = mongoInventoryLastDataRepository.findAllBy_idIn(lastDataIds);
				Optional<List<Warehouse>> optionalWarehouseList = warehousesRepository.findAllByIdIn(wareHouseIds);
				List<Warehouse> warehouseList = optionalWarehouseList.get();
				List<MonogoInventoryLastData> monogoInventoryLastDataList = mongoInv.get();
				String wareHouseName=null;
				ObjectId _id = null;
				Double temp = null;
				Double hum = null;
				Date createDate = null;

				for (Inventory inventorySummaryWrapper : allInventoriesSumDataFromMySQL){
					Date now=new Date();
					Long leftDays=null;
					if(inventorySummaryWrapper.getEndDate()==null||inventorySummaryWrapper.getEndDate().before(now)){
						leftDays= Long.valueOf(0);
					}
					else if(inventorySummaryWrapper.getEndDate()!=null){
						leftDays= ChronoUnit.DAYS.between(now.toInstant(),inventorySummaryWrapper.getEndDate().toInstant());
					}
					if(inventorySummaryWrapper.getLastDataId()!=null){
						if(mongoInv!=null){
							try {
								List<Warehouse> warehouseList1= warehouseList.stream().filter(warehouse -> warehouse.getId().equals(inventorySummaryWrapper.getWarehouseId())).collect(Collectors.toList());
								List<MonogoInventoryLastData> monogoInventoryLastDataList1 = monogoInventoryLastDataList.stream().filter(monogoInventoryLastData -> monogoInventoryLastData.getInventoryId().equals(inventorySummaryWrapper.getId())).collect(Collectors.toList());
								if(!warehouseList1.isEmpty())
								{
									wareHouseName= warehouseList1.get(0).getName();
								}else {
									wareHouseName = null;
								}
								if (!monogoInventoryLastDataList1.isEmpty()){
									_id = monogoInventoryLastDataList1.get(0).get_id();
									temp = monogoInventoryLastDataList1.get(0).getTemperature();
									hum = monogoInventoryLastDataList1.get(0).getHumidity();
									createDate = monogoInventoryLastDataList1.get(0).getCreateDate();

								}else {
									_id = null;
									temp =null;
									hum = null;
									createDate = null;
								}
								inventoryLastData.add(
										allInventoriesLastInfoResponse
												.builder()
												._id(_id)
												.temperature(Double.valueOf(df.format(temp)))
												.inventoryId(inventorySummaryWrapper.getId())
												.inventoryName(inventorySummaryWrapper.getName())
												.wareHouseName(wareHouseName)
												.storingCategory(dictionary.Type(inventorySummaryWrapper.getStoringCategory()))
												.createDate(utilities.monitoringTimeZoneConverter(createDate,timeOffset))
												.humidity(Double.valueOf(df.format(hum)))
												.leftDays(leftDays)
												.build());
							}catch (Exception e){
								throw new ApiGetException(e.getLocalizedMessage());
							}

						}
					}
				}

				Integer size=inventoryRepository.getInventoriesSize(usersIds);
				if(inventoryLastData.size()>0 && Pattern.matches(".*\\S.*" , search)){
					inventoryLastData = inventoryLastData.stream().filter(inventoryLastDatas ->
							inventoryLastDatas.getInventoryName().contains(search)).collect(Collectors.toList());
				}

				getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",inventoryLastData,size);
				logger.info("************************ getNotifications ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);

			}
			else {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "User ID is not found",inventoryLastData);
				logger.info("************************ getNotifications ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);

			}

		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",inventoryLastData);
			logger.info("************************ getNotifications ENDED ***************************");
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
	}


	@Override
	public ResponseEntity<?> getSelectListInventories(String TOKEN, Long id) {
		logger.info("************************ getInventoriesList STARTED ***************************");
			
		List<Inventory> inventories = new ArrayList<Inventory>();
		
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",inventories);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(id != 0) {
			
			User user = userServiceImpl.findById(id);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",inventories);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(user.getAccountType()!= 1) {
					if(!userRoleService.checkUserHasPermission(id, "INVENTORY", "list")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get INVENTORY list",null);
						 logger.info("************************ getInventoryList ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				if(user.getDeleteDate() == null) {
					 List<Long>usersIds= new ArrayList<>();

					userServiceImpl.resetChildernArray();
				    if(user.getAccountType().equals(4)) {
				    	List<Long> inventoryIds = userClientInventoryRepository.getInventoryIds(id);
						if(inventoryIds.size()>0) {
							 inventories = inventoryRepository.getAllInventoriesSelectByIds(inventoryIds);
						}
					}
					else {
						List<User>childernUsers = userServiceImpl.getActiveAndInactiveChildern(id);
						 if(childernUsers.isEmpty()) {
							 usersIds.add(id);
						 }
						 else {
							 usersIds.add(id);
							 for(User object : childernUsers) {
								 usersIds.add(object.getId());
							 }
						 }
						 inventories = inventoryRepository.getAllInventoriesSelect(usersIds);
					}
				    

					
					
					 List<Map> data = new ArrayList<>();
					 if(inventories.size() >0) {

						 for(Inventory inventory:inventories) {
							 Date now=new Date();
							 Long leftDays=null;
							 if(inventory.getEndDate()==null||inventory.getEndDate().before(now)){
								 leftDays=Long.valueOf(0);
							 }
							 else if(inventory.getEndDate()!=null){
								 leftDays= ChronoUnit.DAYS.between(now.toInstant(),inventory.getEndDate().toInstant());
							 }
						     Map InventoriesList= new HashMap();

						     
						     InventoriesList.put("id", inventory.getId());
						     InventoriesList.put("name", inventory.getName());
							 InventoriesList.put("leftDays",leftDays);

							 data.add(InventoriesList);

						 }
						

					}
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",data);
					logger.info("************************ getInventoriesList ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);

				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",inventories);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
				
			}

		}
		else{
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",inventories);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
	}

	@Override
	public ResponseEntity<?> getInventoryUnSelectOfClient(String TOKEN, Long loggedUserId, Long userId) {
		// TODO Auto-generated method stub

		logger.info("************************ getWarehouseUnSelectOfClient STARTED ***************************");
		List<DriverSelect> drivers = new ArrayList<DriverSelect>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",drivers);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		
        if(loggedUserId == 0) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "logged User ID is Required",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User client = userServiceImpl.findById(loggedUserId);
		if(client == null) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This logged user is not found",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
       
		if(client.getAccountType() != 3) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "logged User should be type client to assign his users",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(userId == 0) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User user = userServiceImpl.findById(userId);
		if(user == null) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "User is not found",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
       
		if(user.getAccountType() != 4) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User should be type user to assign him users",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		Set<User> UserParents = user.getUsersOfUser();
		if(UserParents.isEmpty()) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to get this user",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		else {
			User Parent= null;
			for(User object : UserParents) {
				Parent = object ;
				break;
			}
			if(!Parent.getId().toString().equals(loggedUserId.toString())) {
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to get this user",null);
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			
		}
		

		drivers = inventoryRepository.getInventoryUnSelectOfClient(loggedUserId,userId);
		List<DriverSelect> selectedInventories = userClientInventoryRepository.getInventoriesOfUserList(userId);

		
		List<Map> data = new ArrayList<>();
	    Map obj = new HashMap();
	    
	    obj.put("selectedInventories", selectedInventories);
	    obj.put("inventories", drivers);

	    data.add(obj);
	    
		getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data);
		logger.info("************************ getNotificationSelect ENDED ***************************");
		return ResponseEntity.ok().body(getObjectResponse);
		
				
	}

	@Override
	public ResponseEntity<?> assignClientInventories(String TOKEN, Long loggedUserId, Long userId, Long[] inventoryIds) {
		if(TOKEN.equals("")) {
			
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(loggedUserId == 0) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "logged User ID is Required",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User client = userServiceImpl.findById(loggedUserId);
		if(client == null) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This logged user is not found",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
      
		if(client.getAccountType() != 3) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "logged User should be type client to assign his users",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(userId == 0) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User user = userServiceImpl.findById(userId);
		if(user == null) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "User is not found",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
      
		if(user.getAccountType() != 4) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User should be type user to assign him users",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		Set<User> UserParents = user.getUsersOfUser();
		if(UserParents.isEmpty()) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to get this user",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		else {
			User Parent= null;
			for(User object : UserParents) {
				Parent = object ;
				break;
			}
			if(!Parent.getId().toString().equals(loggedUserId.toString())) {
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to get this user",null);
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			
		}
		
       if(inventoryIds.length > 0 && inventoryIds[0] != 0) {
			
       	List<userClientInventory> checkData = userClientInventoryRepository.getInventoryByInvIds(inventoryIds,userId);
       	if(checkData.size()>0) {
       		getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "There is inventory assigned to another user before it should only share with one user",null);
				return ResponseEntity.badRequest().body(getObjectResponse);
       	}
       	
			for(Long id:inventoryIds) {
				if(id == 0) {
					
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "assigned ID is Required",null);
					return ResponseEntity.badRequest().body(getObjectResponse);
				}
				Inventory assignedInventory = inventoryRepository.findOne(id);
				if(assignedInventory == null) {
					
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "assigned inventory is not found",null);
					return ResponseEntity.status(404).body(getObjectResponse);
				}
		        
				
			}
			
			userClientInventoryRepository.deleteInventoriesByUserId(userId);
			for(Long assignedId:inventoryIds) {
				userClientInventory userInventory = new userClientInventory();
				userInventory.setUserid(userId);
				userInventory.setInventoryid(assignedId);
				userClientInventoryRepository.save(userInventory);
			}

			


			getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Assigend Successfully",null);
			return ResponseEntity.ok().body(getObjectResponse);

		}
		else {
			List<userClientInventory> inventories = userClientInventoryRepository.getInventoriesOfUser(userId);
			
			if(inventories.size() > 0) {

				userClientInventoryRepository.deleteInventoriesByUserId(userId);
				getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Removed Successfully",null);
				return ResponseEntity.ok().body(getObjectResponse);
			}
			else {

				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no computeds for this user to remove",null);
				return ResponseEntity.badRequest().body(getObjectResponse);
			}

		}
	}

	@Override
	public ResponseEntity<?> getClientInventories(String TOKEN, Long loggedUserId, Long userId) {
		
		if(TOKEN.equals("")) {
			
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(loggedUserId == 0) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "logged User ID is Required",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User client = userServiceImpl.findById(loggedUserId);
		if(client == null) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This logged user is not found",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
      
		if(client.getAccountType() != 3) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "logged User should be type client to assign his users",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(userId == 0) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User user = userServiceImpl.findById(userId);
		if(user == null) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "User is not found",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
      
		if(user.getAccountType() != 4) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User should be type user to assign him users",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		Set<User> UserParents = user.getUsersOfUser();
		if(UserParents.isEmpty()) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to get this user",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		else {
			User Parent= null;
			for(User object : UserParents) {
				Parent = object ;
				break;
			}
			if(!Parent.getId().toString().equals(loggedUserId.toString())) {
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to get this user",null);
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			
		}

		List<DriverSelect> inventories = userClientInventoryRepository.getInventoriesOfUserList(userId);

		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Success",inventories);
		logger.info("************************ assignClientUsers ENDED ***************************");
		return ResponseEntity.ok().body(getObjectResponse);
	}

	@Override
	public ResponseEntity<?> getSelectedAndListWarehouse(String TOKEN, Long loggedUserId, Long userId,
			Long inventoryId) {
		logger.info("************************ getUnassignedDrivers STARETED ***************************");
		List<Warehouse> war = new ArrayList<>();

		if(TOKEN.equals("")) {

			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",war);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		List<Map> data = new ArrayList<>();
	    Map obj = new HashMap();
	    
	    
		if(inventoryId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No InventoryId  to delete",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		Inventory inventory= inventoryRepository.findOne(inventoryId);
		if(inventory == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this InventoryId not found",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}

		if(inventory.getDeleteDate() != null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this Inventory not found or deleted",null);
			return  ResponseEntity.status(404).body(getObjectResponse);
		}
		List<Warehouse> invWarehouse = new ArrayList<Warehouse>();

		if(inventory.getWarehouseId() != null) {
			Warehouse warehouse = warehousesRepository.findOne(inventory.getWarehouseId());
			invWarehouse.add(warehouse);
		}
		obj.put("selectedWarehouses", invWarehouse);

        List<DriverSelect> warehouses = new ArrayList<DriverSelect>();
		if(loggedUserId != 0) {
	    	User loggedUser = userServiceImpl.findById(loggedUserId);
	    	
	    	if(loggedUser != null) {
	    		if(loggedUser.getDeleteDate() == null) {
	    			if(loggedUser.getAccountType().equals(4)) {	    				
	    				List<Long>usersIds= new ArrayList<>();
					    usersIds.add(loggedUserId);
					    warehouses = warehousesRepository.getWarehousesSelectClient(usersIds);
	            		obj.put("warehouses", warehouses);
	            		data.add(obj);

						getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",data);
						logger.info("************************ getUnassignedDrivers ENDED ***************************");
						return ResponseEntity.ok().body(getObjectResponse);
	    				
	    				
	    			}
	    		}
	    	}
	    	
		}
		
		if(userId.equals(0)) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",warehouses);
			
			logger.info("************************ getUnassignedDrivers ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		else {
			User user = userServiceImpl.findById(userId);

			if(user == null) {
				
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",warehouses);
				return ResponseEntity.status(404).body(getObjectResponse);
			}
			else {
				if(user.getAccountType().equals(4)) {
					 

					List<Long>usersIds= new ArrayList<>();
				    usersIds.add(userId);
                    warehouses = warehousesRepository.getWarehousesSelectClient(usersIds);
            		obj.put("warehouses", warehouses);
            		data.add(obj);				
					getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",data);
					logger.info("************************ getUnassignedDrivers ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);
				 }
				
				 List<User>childernUsers = userServiceImpl.getActiveAndInactiveChildern(userId);
				 List<Long>usersIds= new ArrayList<>();
				 if(childernUsers.isEmpty()) {
					 usersIds.add(userId);
				 }
				 else {
					 usersIds.add(userId);
					 for(User object : childernUsers) {
						 usersIds.add(object.getId());
					 }
				 }
				 
				warehouses = warehousesRepository.getWarehousesSelect(usersIds);
        		obj.put("warehouses", warehouses);
        		data.add(obj);
				getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",data);
				logger.info("************************ getUnassignedDrivers ENDED ***************************");
				return ResponseEntity.ok().body(getObjectResponse);
			}
			
			
		}
	}

	@Override
	public ResponseEntity<?> getInventoryStatus(String TOKEN, Long userId) {
		logger.info("************************ getDevicesStatusAndDrives STARTED ***************************");
		if(TOKEN.equals("")) {
			 List<Device> devices = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",devices);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		
		
		if(userId.equals(0)) {
			List<Device> devices = null;
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is required",devices);
			logger.info("************************ getDevicesStatusAndDrives ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		
        if(userId == 0) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User user = userServiceImpl.findById(userId);
		if(user == null) {
			
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "User is not found",null);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		List<Long>usersIds= new ArrayList<>();
		
		Integer inventoryON = 0;
		Integer inventoryOFF = 0;
		Integer inventoryOutOfNetwork = 0;
		Integer totalInventories = 0;
		Integer inventoryNoData = 0;
		
		Integer totalWarehouses = 0;

		if(user.getAccountType().equals(4)) {
			 List<Long> inventoryIds = userClientInventoryRepository.getInventoryIds(userId);
			 List<Long> warehouseIds = userClientWarehouseRepository.getWarehouseIds(userId);

			 if(warehouseIds.size()>0) {
				 totalWarehouses = warehousesRepository.getTotalNumberOfUserWarehouseByIds(warehouseIds);

			 }
			 
			 if(inventoryIds.size()>0) {
				 List<String> onlineInventoryIds = inventoryRepository.getNumberOfOnlineInventoryListByIds(inventoryIds);
				 inventoryON = onlineInventoryIds.size();
				 
				 List<String> offlineInventoryIds = inventoryRepository.getNumberOfOfflineInventoryListByIds(inventoryIds);
				 inventoryOFF = offlineInventoryIds.size();
				 
				 List<String> outInventoryIds = inventoryRepository.getNumberOfOutOfNetworkInventoryListByIds(inventoryIds);
				 inventoryOutOfNetwork = outInventoryIds.size();
				 
				 totalInventories = inventoryRepository.getTotalNumberOfUserInventoryByIds(inventoryIds);
				 inventoryNoData = inventoryRepository.getTotalNumberOfUserInventoryNoDataByIds(inventoryIds);
			 }
		}
		else {
			List<User>childernUsers = userServiceImpl.getActiveAndInactiveChildern(userId);
			 if(childernUsers.isEmpty()) {
				 usersIds.add(userId);
			 }
			 else {
				 usersIds.add(userId);
				 for(User object : childernUsers) {
					 usersIds.add(object.getId());
				 }
			 }
			 List<String> onlineInventoryIds = inventoryRepository.getNumberOfOnlineInventoryList(usersIds);
			 inventoryON = onlineInventoryIds.size();
			 
			 List<String> offlineInventoryIds = inventoryRepository.getNumberOfOfflineInventoryList(usersIds);
			 inventoryOFF = offlineInventoryIds.size();
			 
			 List<String> outInventoryIds = inventoryRepository.getNumberOfOutOfNetworkInventoryList(usersIds);
			 inventoryOutOfNetwork = outInventoryIds.size();
			 
			 totalInventories = inventoryRepository.getTotalNumberOfUserInventory(usersIds);
			 inventoryNoData = inventoryRepository.getTotalNumberOfUserInventoryNoData(usersIds);
			 
			 totalWarehouses = warehousesRepository.getTotalNumberOfUserWarehouse(usersIds);

		 
		}

		
		Map invStatus = new HashMap();
		
		invStatus.put("inventoryON", inventoryON);
		invStatus.put("inventoryOFF" ,inventoryOFF);
		invStatus.put("inventoryOutOfNetwork", inventoryOutOfNetwork);
		invStatus.put("totalInventories", totalInventories);
		invStatus.put("inventoryNoData", inventoryNoData);
		invStatus.put("warehouses", totalWarehouses);

		List<Map> data = new ArrayList<>();
		data.add(invStatus);
		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",data);
		logger.info("************************ invStatus ENDED ***************************");
		return ResponseEntity.ok().body(getObjectResponse);		
	}
	
	@Override
	public ResponseEntity<?> callApiEasyClould(Inventory inv) {
		
    	String GUID = inv.getGUID();
    	String emailEasyCloud = inv.getEmailEasyCloud();
    	String passwordEasyCloud = inv.getPasswordEasyCloud();
    	String APIToken = inv.getAPIToken();
    	
		TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

		SSLContext sslContext = null;
		try {
			sslContext = org.apache.http.ssl.SSLContexts.custom()
			        .loadTrustMaterial(null, acceptingTrustStrategy)
			        .build();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

		CloseableHttpClient httpClient = HttpClients.custom()
		        .setSSLSocketFactory(csf)
		        .build();

		HttpComponentsClientHttpRequestFactory requestFactory =
		        new HttpComponentsClientHttpRequestFactory();

		requestFactory.setHttpClient(httpClient);

		RestTemplate restTemplate = new RestTemplate(requestFactory);
		List<Map<?, ?>>list= new ArrayList<>();
		  restTemplate.getMessageConverters()
	        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
		  
		  HttpEntity<Object> entity = new HttpEntity<Object>(list);
		  ResponseEntity<String> rateResponse = null;

		  
		  
		  String URL = "https://apiwww.easylogcloud.com/Locations.svc/NewDevices?APIToken="+APIToken+"&userGUID="+GUID;
		  try {
			  rateResponse = restTemplate.exchange(URL, HttpMethod.GET, entity, String.class);
			  
			  JSONArray data = new JSONArray(rateResponse.getBody());
			  ArrayList<Map<Object,Object>> easyCloudData = new ArrayList<Map<Object,Object>>();

			  for(Object d: data) {
				  HashMap<Object,Object> result =
					        new ObjectMapper().readValue(d.toString(), HashMap.class);
				  easyCloudData.add(result);
			  }
			  

			  return getEasyCloudProtocols(inv,easyCloudData);
			  
          } catch (Exception e) {
//        	  System.out.println("unauthorized");
        	  String Auth = "https://apiwww.easylogcloud.com/Users.svc/Login?email="+emailEasyCloud+"&password="+passwordEasyCloud+"&APIToken="+APIToken;
        	  try {
    			  rateResponse = restTemplate.exchange(Auth, HttpMethod.GET, entity, String.class);
    			  
    	          
    	          JSONObject obj = new JSONObject(rateResponse.getBody().toString());
    	          String user_guid = null;
    	          if(obj.has("GUID")) {
    	        	  user_guid = obj.getString("GUID");
    	          }
    	          
    	          inv.setGUID(user_guid);
    	          inventoryRepository.save(inv);

    	          String access = "https://apiwww.easylogcloud.com/Locations.svc/NewDevices?APIToken="+APIToken+"&userGUID="+user_guid;
    	          try {
    	        	  
    				  rateResponse = restTemplate.exchange(access, HttpMethod.GET, entity, String.class);
    				  
    				  JSONArray data = new JSONArray(rateResponse.getBody());
    				  ArrayList<Map<Object,Object>> easyCloudData = new ArrayList<Map<Object,Object>>();

    				  for(Object d: data) {
    					  HashMap<Object,Object> result =
    						        new ObjectMapper().readValue(d.toString(), HashMap.class);
    					  easyCloudData.add(result);
    				  }
    				  

    				  return getEasyCloudProtocols(inv,easyCloudData);
    				  
    				  
        	         
    				  
    	          } catch (Exception eee) {
                	  System.out.println("can't get data");

    	        	  
    	          }
    			  
              } catch (Exception ee) {
            	  
//            	  System.out.println("can't login");

    	     }


	     }
		  
		 
		  
		return null;
	}
	@Override
	public ResponseEntity<?> getEasyCloudData() {
        List<Inventory> inventories = inventoryRepository.getInventoriesTypeProtocol("easyCloud");
		
        for(Inventory inv :inventories) {

        	callApiEasyClould(inv);
            		
        }
        
		
		return null;
	}
	
	
	@Override
	public ResponseEntity<?> getDataProtocols(ArrayList<Map<Object,Object>> data,String type,String email) {
		// TODO Auto-generated method stub

		
		if(!email.equals("")) {
			Long userId = userRepositorySFDA.getUserByEmail(email);

			if(userId != null) {
				if(!type.equals("")) {
					if(type.equals("csv")) {
						return getCsvProtocols(userId,data);
					}
				}
				
			}
		}
		

	    return null;
	}
	
	@Override
	public ResponseEntity<?> getCsvProtocols(Long userId,ArrayList<Map<Object,Object>> data) {
		// TODO Auto-generated method stub
		System.out.println("*******************getCsvProtocols Started*******************");
		
		List<Inventory> inventories = inventoryRepository.getAllInventoriesTypeProtocolCSV(userId,"csv");
		
		for(Inventory inventory:inventories) {
			Double temperature = 0.0;
			Double humidity = 0.0;
			Integer countTemp = 0;
			Integer countHum = 0;
			String DateOfSensor = null;
			List<SensorsInventories> sensorsInventories = sensorsInventoriesRepository.getAllSensorsOfInventory(inventory.getId());
			for(SensorsInventories sensorsInventory:sensorsInventories) {
				for(Map<Object, Object> obj:data) {
					
					if(obj.containsKey("Date")&&obj.containsKey("Time")) {
						DateOfSensor = obj.get("Date").toString()+" "+obj.get("Time").toString();

					}
					
					
					for (Map.Entry<Object, Object> entry : obj.entrySet()) {

						String name = sensorsInventory.getName();
						if(entry.getKey().toString().contains(name)) {

							if(sensorsInventory.getType().equals("temp")) {

								temperature += Double.valueOf(entry.getValue().toString());
								countTemp +=1;
								

							}
                            if(sensorsInventory.getType().equals("hum")) {
								humidity += Double.valueOf(entry.getValue().toString());
								countHum +=1;

							}

						}
					}
	
				}
				
			}

		
			
			Double AvgTemp = (double) 0;
        	Double AvgHum = (double) 0;
        	
        	if(countTemp != 0) {
        		AvgTemp = temperature/countTemp;

        	}
        	if(countHum != 0) {
        		AvgHum = humidity/countHum;

        	}

			Date dateTime = null;
			String create_date = null;
			if(DateOfSensor == null) {
			
//				Date now = new Date();
//				SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				 input.setTimeZone(TimeZone.getTimeZone("Aisa/Riyadh"));
				Instant nowUtc = Instant.now();
				ZoneId asiaSingapore = ZoneId.of("Asia/Riyadh");
				ZonedDateTime nowAsiaSingapore = ZonedDateTime.ofInstant(nowUtc, asiaSingapore);
				System.out.println(nowAsiaSingapore);
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.Z");
				String formattedString = nowAsiaSingapore.format(formatter);
				System.out.println(formattedString);
				
				SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				 try {
					dateTime = input.parse(formattedString) ;
				} catch (java.text.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
						//input.parse(formattedString);
				System.out.println("finalDate"+dateTime);
				
//				
//				try {
//					create_date = input.format(now);
//					try {
//						dateTime = input.parse(create_date);
//						
//					} catch (java.text.ParseException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//
//				} catch (ParseException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
			else {
				SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
				SimpleDateFormat input = new SimpleDateFormat("ddMMMyy hh:mm"); 

				try {
					try {
					
						
						dateTime = input.parse(DateOfSensor.toString());
					} catch (java.text.ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					create_date = output.format(dateTime);
					try {
						dateTime = output.parse(create_date);
					} catch (java.text.ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			
			Double oldTemp = null;
			Double oldTHum = null;
			if(inventory.getLastDataId() != null) {
				MonogoInventoryLastData check = mongoInventoryLastDataRepository.findById(inventory.getLastDataId());
				if(check != null) {
					oldTemp = check.getTemperature();
					oldTHum = check.getHumidity();
				}
			}
			
			
					
			ObjectId lastDataId = saveLastDataHandler(inventory,dateTime,AvgTemp,AvgHum);

			saveHumidityHandler(inventory, dateTime, AvgHum, oldTHum);
			saveTemperatureHandler(inventory, dateTime, AvgTemp, oldTemp);
		
			//save setLastUpdate setLastDataId after push data and notification data
			inventory.setLastUpdate(create_date);
			inventory.setLastDataId(lastDataId.toString());
			inventoryRepository.save(inventory);

			
		}
		
		System.out.println("*******************getCSVProtocols Ended*******************");

		
		return null;
	}

	@Override
	public ResponseEntity<?> getEasyCloudProtocols(Inventory inventory,ArrayList<Map<Object,Object>> data) {
		// TODO Auto-generated method stub
		System.out.println("*******************getEasyClouldProtocols Started*******************");
		
		
		Double temperature = 0.0;
		Double humidity = 0.0;
		Integer countTemp = 0;
		Integer countHum = 0;
		
		for(Map<Object, Object> obj:data) {

			if(obj.get("channels") != null ) {
				ArrayList<Map<Object,Object>> channels = (ArrayList<Map<Object, Object>>) obj.get("channels");
				for(Map<Object, Object> channel:channels) {
					if(channel.get("label").equals("Temperature") ) {

						temperature += Double.valueOf(channel.get("currentReading").toString());
						countTemp +=1;

					}
                    if(channel.get("label").equals("Humidity") ) {
                    	humidity += Double.valueOf(channel.get("currentReading").toString());
                    	countHum +=1;

					}

					
				}


			}
		}


		Double AvgTemp = (double) 0;
    	Double AvgHum = (double) 0;
    	
    	if(countTemp != 0) {
    		AvgTemp = temperature/countTemp;

    	}
    	if(countHum != 0) {
    		AvgHum = humidity/countHum;

    	}
		
		Date now = new Date();
		SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Date dateTime = null;
		String create_date = null;
		
		
		
		try {
			create_date = input.format(now);
			try {
				dateTime = input.parse(create_date);
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Calendar calendarTo = Calendar.getInstance();
		calendarTo.setTime(dateTime);
		calendarTo.add(Calendar.HOUR_OF_DAY, 3);
		dateTime = calendarTo.getTime();

		Double oldTemp = null;
		Double oldHum = null;
		if(inventory.getLastDataId() != null) {
			MonogoInventoryLastData check = mongoInventoryLastDataRepository.findById(inventory.getLastDataId());
			if(check != null) {
				oldTemp = check.getTemperature();
				oldHum = check.getHumidity();
			}
		}
		


		ObjectId lastDataId = saveLastDataHandler(inventory,dateTime,AvgTemp,AvgHum);

		saveHumidityHandler(inventory, dateTime, AvgHum, oldHum);
		saveTemperatureHandler(inventory, dateTime, AvgTemp, oldTemp);
	
		//save setLastUpdate setLastDataId after push data and notification data

		inventory.setLastUpdate(create_date);
		inventory.setLastDataId(lastDataId.toString());
		inventoryRepository.save(inventory);
	
	
		
		
		System.out.println("*******************getEasyClouldProtocols Ended*******************");

		return null;
	}

	@Override
	public ResponseEntity<?> getSensorsInventoriesList(String TOKEN, Long InventoryId, Long userId) {
		// TODO Auto-generated method stub
		logger.info("************************ getSensorsInventoriesList STARTED ***************************");

		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		User loggedUser = userServiceImpl.findById(userId);
		if(loggedUser == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged User is not found",null);
			return  ResponseEntity.status(404).body(getObjectResponse); 
		}

		if(InventoryId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No InventoryId to get data",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		Inventory inventory= inventoryRepository.findOne(InventoryId);
		if(inventory == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this InventoryId not found",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}

		if(inventory.getDeleteDate() != null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this Inventory not found or deleted",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}
				


		Long createdBy=inventory.getUserId();
		Boolean isParent=false;

		if(createdBy.toString().equals(userId.toString())) {
			isParent=true;
		}
		List<User>childs = new ArrayList<User>();
		if(loggedUser.getAccountType().equals(4)) {
			 List<User> parents=userServiceImpl.getAllParentsOfuser(loggedUser,loggedUser.getAccountType());
			 if(parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this inventory.",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 else {
				 User parentClient = new User() ;

				 for(User object : parents) {
					 parentClient = object;
					 break;
				 }
				 
				userServiceImpl.resetChildernArray();
				childs = userServiceImpl.getAllChildernOfUser(parentClient.getId()); 
			 }
			 
		}
		else {
			userServiceImpl.resetChildernArray();
			childs = userServiceImpl.getAllChildernOfUser(userId);
		}
		
		
		
		User parentChilds = new User();
		if(!childs.isEmpty()) {
			for(User object : childs) {
				parentChilds = object;
				if(parentChilds.getId().toString().equals(createdBy.toString())) {
					isParent=true;
					break;
				}
			}
		}
		if(isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get inventory",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		List<SensorsInventories> sensors = new ArrayList<SensorsInventories>();

		sensors = sensorsInventoriesRepository.getAllSensorsOfInventory(InventoryId);

		getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",sensors);
		logger.info("************************ getSensorsInventoriesList ENDED ***************************");
		return  ResponseEntity.ok().body(getObjectResponse);
	}

	@Override
	public ResponseEntity<?> addSensor(String TOKEN, SensorsInventories sensorsInventories, Long InventoryId,
			Long userId) {
		logger.info("************************ addSensor STARTED ***************************");

		// TODO Auto-generated method stub
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		User loggedUser = userServiceImpl.findById(userId);
		if(loggedUser == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged User is not found",null);
			return  ResponseEntity.status(404).body(getObjectResponse); 
		}

		if(InventoryId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No InventoryId to get data",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		Inventory inventory= inventoryRepository.findOne(InventoryId);
		if(inventory == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this InventoryId not found",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}

		if(inventory.getDeleteDate() != null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this Inventory not found or deleted",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}
				


		Long createdBy=inventory.getUserId();
		Boolean isParent=false;

		if(createdBy.toString().equals(userId.toString())) {
			isParent=true;
		}
		List<User>childs = new ArrayList<User>();
		if(loggedUser.getAccountType().equals(4)) {
			 List<User> parents=userServiceImpl.getAllParentsOfuser(loggedUser,loggedUser.getAccountType());
			 if(parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this inventory.",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 else {
				 User parentClient = new User() ;

				 for(User object : parents) {
					 parentClient = object;
					 break;
				 }
				 
				userServiceImpl.resetChildernArray();
				childs = userServiceImpl.getAllChildernOfUser(parentClient.getId()); 
			 }
			 
		}
		else {
			userServiceImpl.resetChildernArray();
			childs = userServiceImpl.getAllChildernOfUser(userId);
		}
		
		
		
		User parentChilds = new User();
		if(!childs.isEmpty()) {
			for(User object : childs) {
				parentChilds = object;
				if(parentChilds.getId().toString().equals(createdBy.toString())) {
					isParent=true;
					break;
				}
			}
		}
		if(isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get inventory",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(sensorsInventories.getName()== null || sensorsInventories.getName().equals("")
				   || sensorsInventories.getType()== null || sensorsInventories.getType().equals("")) {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "[name ,type ] are Required",null);
			return ResponseEntity.badRequest().body(getObjectResponse);

		}
		if(sensorsInventories.getId()==null || sensorsInventories.getId()==0) {
			
			
            List<Long> usersIds= new  ArrayList<>();
            usersIds.add(userId);
            
            List<Long> inveLongs = inventoryRepository.getAllInventoriesIds(usersIds);
			List<SensorsInventories> sensors = new ArrayList<SensorsInventories>();
			sensors=sensorsInventoriesRepository.getAllSensorsOfInventoryDublicate(inveLongs, sensorsInventories.getName());

			if(sensors.isEmpty()) {
				
				sensorsInventories.setInventoryId(InventoryId);
				sensorsInventoriesRepository.save(sensorsInventories);
				
				List<SensorsInventories> sensorsInventory = new ArrayList<SensorsInventories>();
				sensorsInventory.add(sensorsInventories);
				
				getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"sucsess",sensorsInventory);
				logger.info("************************ addSensor ENDED ***************************");
				return ResponseEntity.ok().body(getObjectResponse);
			}
			else {
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "name was add before for the same user inventories",null);
				return ResponseEntity.badRequest().body(getObjectResponse);
			}
			
			

			
		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not allow to update sensors inventories Id",null);
			return ResponseEntity.badRequest().body(getObjectResponse);

		}
	}

	@Override
	public ResponseEntity<?> removeSensor(String TOKEN, Long sensorInventoryId, Long InventoryId, Long userId) {
		// TODO Auto-generated method stub
		logger.info("************************ removeSensor STARTED ***************************");

		// TODO Auto-generated method stub
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		User loggedUser = userServiceImpl.findById(userId);
		if(loggedUser == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged User is not found",null);
			return  ResponseEntity.status(404).body(getObjectResponse); 
		}

		if(InventoryId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No InventoryId to get data",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		Inventory inventory= inventoryRepository.findOne(InventoryId);
		if(inventory == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this InventoryId not found",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}

		if(inventory.getDeleteDate() != null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this Inventory not found or deleted",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}
				


		Long createdBy=inventory.getUserId();
		Boolean isParent=false;

		if(createdBy.toString().equals(userId.toString())) {
			isParent=true;
		}
		List<User>childs = new ArrayList<User>();
		if(loggedUser.getAccountType().equals(4)) {
			 List<User> parents=userServiceImpl.getAllParentsOfuser(loggedUser,loggedUser.getAccountType());
			 if(parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this inventory.",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 else {
				 User parentClient = new User() ;

				 for(User object : parents) {
					 parentClient = object;
					 break;
				 }
				 
				userServiceImpl.resetChildernArray();
				childs = userServiceImpl.getAllChildernOfUser(parentClient.getId()); 
			 }
			 
		}
		else {
			userServiceImpl.resetChildernArray();
			childs = userServiceImpl.getAllChildernOfUser(userId);
		}
		
		
		
		User parentChilds = new User();
		if(!childs.isEmpty()) {
			for(User object : childs) {
				parentChilds = object;
				if(parentChilds.getId().toString().equals(createdBy.toString())) {
					isParent=true;
					break;
				}
			}
		}
		if(isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get inventory",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(sensorInventoryId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No sensorInventoryId to get data",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		SensorsInventories sensors= sensorsInventoriesRepository.findOne(sensorInventoryId);
		if(sensors == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this sensorInventoryId not found",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}
		if(sensors.getInventoryId() == InventoryId) {
			sensorsInventoriesRepository.delete(sensorInventoryId);
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"sucsess",null);
			logger.info("************************ removeSensor ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);
		}
		else {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "inventoryId not the owner of sensorInventoryId",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}


	}

	@Override
	public ObjectId saveLastDataHandler(Inventory inventory,Date dateTime,Double AvgTemp,Double AvgHum) {
		
		// TODO Auto-generated method stub
		System.out.println("*******************saveLastDataHandler Started*******************");

		MonogoInventoryLastData monogoInventoryLastData = new MonogoInventoryLastData();
		
		monogoInventoryLastData.setTemperature(AvgTemp);
		monogoInventoryLastData.setHumidity(AvgHum);
		monogoInventoryLastData.setInventoryId(inventory.getId());
		monogoInventoryLastData.setCreateDate(dateTime);
		
		mongoInventoryLastDataRepository.save(monogoInventoryLastData);

		
		if(inventory.getReferenceKey() != null) {
			MonogoInventoryLastDataElmSend monogoInventoryLastDataElmSend = new MonogoInventoryLastDataElmSend();

			monogoInventoryLastDataElmSend.setTemperature(AvgTemp);
			monogoInventoryLastDataElmSend.setHumidity(AvgHum);
			monogoInventoryLastDataElmSend.setName(inventory.getName());
			monogoInventoryLastDataElmSend.setActivity(inventory.getActivity());
			monogoInventoryLastDataElmSend.setInventory_id(inventory.getId());
			monogoInventoryLastDataElmSend.setReferenceKey(inventory.getReferenceKey());
			
			mongoInventoryLastDataRepositoryElmData.save(monogoInventoryLastDataElmSend);

		}
		
		
		
		return monogoInventoryLastData.get_id();
	}

	@Override
	public ResponseEntity<?> saveTemperatureHandler(Inventory inventory, Date dateTime, Double AvgTemp, Double oldTemp) {
		// TODO Auto-generated method stub
        
		System.out.println("*******************saveTemperatureHandler Started*******************");

		MonogInventoryNotification monogInventoryNotification =new MonogInventoryNotification();
		
		monogInventoryNotification.setType("temperature alarm");
		monogInventoryNotification.setInventoryId(inventory.getId());
		monogInventoryNotification.setCreatedDate(dateTime);
		
		NotificationAttributes notificationAttributes = new NotificationAttributes();
		notificationAttributes.setKey("temperature");
		notificationAttributes.setValue(AvgTemp);

		monogInventoryNotification.setAttributes(notificationAttributes);
	
		
		//SCD1 -20°C to -10°C
		if(inventory.getStoringCategory().equals("SCD1") && (AvgTemp < -20 || AvgTemp > -10) ) {
			
			/*if(oldTemp != null) {
				if(oldTemp < -20 || oldTemp > -10){
					return null;
				}
			}*/
			
		    mongoInventoryNotificationRepository.save(monogInventoryNotification);
			
		}
		
        //SCD2 2°C to 8°C
		else if(inventory.getStoringCategory().equals("SCD2") && (AvgTemp < 2 || AvgTemp > 8) ) {
			
			/*if(oldTemp != null) {
				if(oldTemp < 2 || oldTemp > 8){
					return null;
				}
			}*/
			
        	mongoInventoryNotificationRepository.save(monogInventoryNotification);
			
		}
		
        //SCD3 Less than 25°C
		else if(inventory.getStoringCategory().equals("SCD3") && (AvgTemp >= 25)) {


			/*if(oldTemp != null) {
				if(oldTemp >= 25){
					return null;
				}
			}*/
			
        	mongoInventoryNotificationRepository.save(monogInventoryNotification);			
			
		}
		
        //SCC1 Less than 25°C
		else if(inventory.getStoringCategory().equals("SCC1") && (AvgTemp >= 25)) {
			
			/*if(oldTemp != null) {
				if(oldTemp >= 25){
					return null;
				}
			}*/
			
        	mongoInventoryNotificationRepository.save(monogInventoryNotification);
			
		}
		
        //SCM1 -20°C to -10°C
		else if(inventory.getStoringCategory().equals("SCM1") && (AvgTemp < -20 || AvgTemp > -10)) {
			
			/*if(oldTemp != null) {
				if(oldTemp < -20 || oldTemp > -10){
					return null;
				}
			}*/
			
		    mongoInventoryNotificationRepository.save(monogInventoryNotification);
			
		}
	
        //SCM2 2°C to 8°C
		else if(inventory.getStoringCategory().equals("SCM2") && (AvgTemp < 2 || AvgTemp > 8)) {
			
			/*if(oldTemp != null) {
				if(oldTemp < 2 || oldTemp > 8){
					return null;
				}
			}*/
		    
			mongoInventoryNotificationRepository.save(monogInventoryNotification);
	
		}
		
		//SCM3 8°C to 15°C
		else if(inventory.getStoringCategory().equals("SCM3") && (AvgTemp < 8 || AvgTemp > 15)) {
            
			/*if(oldTemp != null) {
				if(oldTemp < 8 || oldTemp > 15){
					return null;
				}
			}*/
			
		    mongoInventoryNotificationRepository.save(monogInventoryNotification);
		
		}
		
		//SCM4 15°C to 30°C
		else if(inventory.getStoringCategory().equals("SCM4") && (AvgTemp < 15 || AvgTemp > 30)) {
            
			/*if(oldTemp != null) {
				if(oldTemp < 15 || oldTemp > 30){
					return null;
				}
			}*/
			
			mongoInventoryNotificationRepository.save(monogInventoryNotification);
			
		}
		
        //SCM5 Should not exceed 40°C
		else if(inventory.getStoringCategory().equals("SCM5") && (AvgTemp > 40)) {
			
			/*if(oldTemp != null) {
				if(oldTemp > 40){
					return null;
				}
			}*/
			
			mongoInventoryNotificationRepository.save(monogInventoryNotification);

		}
		
		//SCF1 Should not exceed 25°C
		else if(inventory.getStoringCategory().equals("SCF1") && (AvgTemp > 25)) {
            
			/*if(oldTemp != null) {
				if(oldTemp > 25){
					return null;
				}
			}*/
			
			mongoInventoryNotificationRepository.save(monogInventoryNotification);
			
			
		}
		
		//SCF2 -1.5°C to 10°C
		else if(inventory.getStoringCategory().equals("SCF2") && (AvgTemp < -1.5 || AvgTemp > 10)) {

			/*if(oldTemp != null) {
				if(oldTemp < -1.5 || oldTemp > 10){
					return null;
				}
			}*/
			
            mongoInventoryNotificationRepository.save(monogInventoryNotification);
			
		}
		
        //SCF3 -1.5°C to 21°C 
		else if(inventory.getStoringCategory().equals("SCF3") && (AvgTemp < -1.5 || AvgTemp > 21)) {
			
			/*if(oldTemp != null) {
				if(oldTemp < -1.5 || oldTemp > 21){
					return null;
				}
			}*/
			
			mongoInventoryNotificationRepository.save(monogInventoryNotification);

		}
		
		//SCF4 Should not exceed (-18)°C
		else if(inventory.getStoringCategory().equals("SCF4") && (AvgTemp > -18)) {
            
			/*if(oldTemp != null) {
				if(oldTemp > -18){
					return null;
				}
			}*/
			
			mongoInventoryNotificationRepository.save(monogInventoryNotification);
			
		}
		
		//SCA1 Should not exceed 30°C
		else if(inventory.getStoringCategory().equals("SCA1") && (AvgTemp > 30)) {
           
			/*if(oldTemp != null) {
				if(oldTemp > 30){
					return null;
				}
			}*/
			
			mongoInventoryNotificationRepository.save(monogInventoryNotification);
			
		}
		
        //SCP1 Should not exceed 35°C
		else if(inventory.getStoringCategory().equals("SCP1") && (AvgTemp > 35)) {
			
			/*if(oldTemp != null) {
				if(oldTemp > 35){
					return null;
				}
			}*/
			
			mongoInventoryNotificationRepository.save(monogInventoryNotification);
			
		}
		
		
		return null;
	}

	@Override
	public ResponseEntity<?> saveHumidityHandler(Inventory inventory, Date dateTime,Double AvgHum, Double oldHum) {
		// TODO Auto-generated method stub
		
		System.out.println("*******************saveHumidityHandler Started*******************");

		MonogInventoryNotification monogInventoryNotification =new MonogInventoryNotification();
		
		monogInventoryNotification.setType("humidity alarm");
		monogInventoryNotification.setInventoryId(inventory.getId());
		monogInventoryNotification.setCreatedDate(dateTime);
		
		NotificationAttributes notificationAttributes = new NotificationAttributes();
		notificationAttributes.setKey("humidity");
		notificationAttributes.setValue(AvgHum);

		monogInventoryNotification.setAttributes(notificationAttributes);
		
		//SCD3 Less than 60%
		if(inventory.getStoringCategory().equals("SCD3") && (AvgHum >= 60)) {
			
			/*if(oldHum != null) {
				if(oldHum >= 60){
					return null;
				}
			}*/
			
		    mongoInventoryNotificationRepository.save(monogInventoryNotification);

		}
		
		//SCC1 Less than 60%
		else if(inventory.getStoringCategory().equals("SCC1") && (AvgHum >= 60)) {
            	
			/*if(oldHum != null) {
				if(oldHum >= 60){
					return null;
				}
			}*/
			
		    mongoInventoryNotificationRepository.save(monogInventoryNotification);
			
		}
		
		//SCM3 Less than 60%
		else if(inventory.getStoringCategory().equals("SCM3") && (AvgHum >= 60)) {
           
			/*if(oldHum != null) {
				if(oldHum >= 60){
					return null;
				}
			}*/
			
		    mongoInventoryNotificationRepository.save(monogInventoryNotification);
			
		}
		
		//SCM4 Less than 60%
		else if(inventory.getStoringCategory().equals("SCM4") && (AvgHum >= 60)) {
           
			/*if(oldHum != null) {
				if(oldHum >= 60){
					return null;
				}
			}*/
			
			mongoInventoryNotificationRepository.save(monogInventoryNotification);
			
		}
		
		//SCF1 Less than 60%
		else if(inventory.getStoringCategory().equals("SCF1") && (AvgHum >= 60)) {
            
			/*if(oldHum != null) {
				if(oldHum >= 60){
					return null;
				}
			}*/
			
			mongoInventoryNotificationRepository.save(monogInventoryNotification);
			
		}
		
		//SCF2 75% to 90%
		else if(inventory.getStoringCategory().equals("SCF2") && (AvgHum < 75 || AvgHum > 90)) {
            
			/*if(oldHum != null) {
				if(oldHum < 75 || oldHum > 90){
					return null;
				}
			}*/
			
			mongoInventoryNotificationRepository.save(monogInventoryNotification);
			
		}
		
		//SCF3 85% to 95%
		else if(inventory.getStoringCategory().equals("SCF3") && (AvgHum < 85 || AvgHum > 95)) {
            
			/*if(oldHum != null) {
				if(oldHum < 85 || oldHum > 95){
					return null;
				}
			}*/
			
			mongoInventoryNotificationRepository.save(monogInventoryNotification);
			
		}
		
		//SCF4 75% to 99%
		else if(inventory.getStoringCategory().equals("SCF4") && (AvgHum < 75 || AvgHum > 99)) {
            	
			/*if(oldHum != null) {
				if(oldHum < 75 || oldHum > 99){
					return null;
				}
			}*/
			
			mongoInventoryNotificationRepository.save(monogInventoryNotification);
			
		}
		
		//SCA1 Should not exceed 60%
		else if(inventory.getStoringCategory().equals("SCA1") && (AvgHum > 60)) {
           
			/*if(oldHum != null) {
				if(oldHum > 60){
					return null;
				}
			}*/
			
			mongoInventoryNotificationRepository.save(monogInventoryNotification);
			
		}
		
		return null;
	}

	@Override
	public ResponseEntity<?> getDataProtocolsSkarpt(Map<Object, Object> data) {
		// TODO Auto-generated method stub
		
		if(data.get("type").equals("skarpt")) {
			getSkarptProtocols((List<BindData>) data.get("data"));
		}
		
		getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",null);
		return  ResponseEntity.ok().body(getObjectResponse);
	}
	
	public void getSkarptProtocols(List<BindData> data) {
		ObjectMapper mapper = new ObjectMapper();

		List<BindData> list = mapper.convertValue(
				data, 
			    new TypeReference<List<BindData>>(){}
			);

		for(BindData obj:list) {

			Long inventoryId = inventoryRepository.getInventoryByNumber(obj.getInventoryNumber(), "skarpt");
			Date dateTime = null;
			String create_date = null;

//			if(inventoryId  != null) {
//				Inventory inventory = inventoryRepository.findOne(inventoryId);
//
////				Date dateTime = null;
////				String create_date = null;
//
//				SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//
//				try {
//					dateTime = input.parse(obj.getCreate_date().toString());
//				} catch (java.text.ParseException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			if(inventoryId  != null) {
				Inventory inventory = inventoryRepository.findOne(inventoryId);

				SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

				if (inventory.getConvertToUtc() != true){
					try{
						input.setTimeZone(TimeZone.getTimeZone("UTC"));
						dateTime = input.parse(obj.getCreate_date().toString());
					} catch (Exception e){
						e.printStackTrace();
					}

				}
				else {
					try {
						input.setTimeZone(TimeZone.getTimeZone("Asia/Riyadh"));
						dateTime = input.parse(obj.getCreate_date().toString());
					} catch (java.text.ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				create_date = output.format(dateTime);

				Double oldTemp = null;
				Double oldTHum = null;
				if(inventory.getLastDataId() != null) {
					MonogoInventoryLastData check = mongoInventoryLastDataRepository.findById(inventory.getLastDataId());
					if(check != null) {
						oldTemp = check.getTemperature();
						oldTHum = check.getHumidity();
					}
				}

				ObjectId lastDataId = saveLastDataHandler(inventory,dateTime,obj.getTemperature(),obj.getHumidity());

				saveHumidityHandler(inventory, dateTime, obj.getHumidity(), oldTHum);
				saveTemperatureHandler(inventory, dateTime, obj.getTemperature(), oldTemp);
			
				//save setLastUpdate setLastDataId after push data and notification data
				inventory.setLastUpdate(create_date);
				inventory.setLastDataId(lastDataId.toString());
				inventoryRepository.save(inventory);
			
			}
			
		}
	}

	@Override
	public ResponseEntity<?> assignInventoryToUser(String TOKEN, Long userId, Long inventoryId, Long toUserId) {
		// TODO Auto-generated method stub
		

		if(TOKEN.equals("")) {
			 List<Device> devices = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",devices);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId == 0 || inventoryId == 0 || toUserId == 0 ) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "userId , inventoryId and toUserId  are required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		else {
			User loggedUser = userServiceImpl.findById(userId);
			
			if(loggedUser == null) {
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "loggedUser is not found",null);
				
				return ResponseEntity.status(404).body(getObjectResponse);
			}else {
				if(!loggedUser.getAccountType().equals(1)) {
					if(!userRoleService.checkUserHasPermission(userId, "INVENTORY", "assignToUser")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to assignToInventory",null);
						 logger.info("************************ assignToUser ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				if(loggedUser.getAccountType().equals(3) || loggedUser.getAccountType().equals(4)) {
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to assign inventory to any user",null);
					
					return ResponseEntity.status(404).body(getObjectResponse);
				}
				
				User toUser = userServiceImpl.findById(toUserId);
			    if(toUser == null) {
			    	getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "user you want to assign to  is not found",null);
					return ResponseEntity.status(404).body(getObjectResponse);
			    }
			    else {
			    	
			    	 if(toUser.getAccountType().equals(4)) {
			    		  getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to assign inventory to this user type 4 assign to his parents",null);
						  return ResponseEntity.status(404).body(getObjectResponse);
			    	 }
			    	  
			    	  
			    	 List<User>toUserParents = userServiceImpl.getAllParentsOfuser(toUser, toUser.getAccountType());
			    	 if(toUserParents.isEmpty()) {
			    		 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to assign inventory to this user",null);
						 return ResponseEntity.status(404).body(getObjectResponse);
			    	 }else {
			    		
			    		 boolean isParent = false;
			    		 for(User object : toUserParents) {
			    			 if(loggedUser.getId().equals(object.getId())) {
			    				 isParent = true;
			    				 break;
			    			 }
			    		 }
			    		 if(isParent) {
			    			 if(inventoryId == 0) {
			 					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No inventoryId  to delete",null);
			 					 return  ResponseEntity.badRequest().body(getObjectResponse);
			 				}
			 				Inventory inventory= inventoryRepository.findOne(inventoryId);
			 				if(inventory == null) {
			 					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this inventoryId not found",null);
			 					 return  ResponseEntity.status(404).body(getObjectResponse);
			 				}

			 				if(inventory.getDeleteDate() != null) {
			 					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this inventory not found or deleted",null);
			 					 return  ResponseEntity.status(404).body(getObjectResponse);
			 				}
			 				inventory.setUserId(toUserId);
			 			     
			 				inventoryRepository.save(inventory);
			 			    
			 			    
			 				getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",null);
			 				return  ResponseEntity.ok().body(getObjectResponse);
			    		 }
			    		 else {
			    			 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to assign inventory to this user",null);
							 return ResponseEntity.status(404).body(getObjectResponse);
			    		 }
			    	 }
				
			    }
			    
			}
		}

		
	}

	@Override
	public ResponseEntity<?> inventoryMonthelyView(String endDate) {
		return ResponseEntity.status(200).body(inventoryRepository.inventoryMonthelyView(endDate));
	}

	@Override
	public ApiResponse<List<InventoryListDashBoardResponse>> inventoryListDashBoard(String TOKEN, Long userId) {

		ApiResponseBuilder<List<InventoryListDashBoardResponse>> builder = new ApiResponseBuilder<>();
		List<InventoryListDashBoardResponse> result = new ArrayList<>();
		List<Long> userIds;
		List<String> lastDataIds;
		List<Long> wareHouseIds;
		userIds = assistantServiceImpl.getChildrenOfUser(userId);

		if (TOKEN.equals("")) {
			builder.setMessage("TOKEN id is required");
			builder.setStatusCode(HttpStatus.BAD_REQUEST.value());
			builder.setEntity(null);
			builder.setSize(0);
			builder.setSuccess(false);
			return builder.build();
		}

		if (super.checkActiveByApi(TOKEN) != null) {
			return super.checkActiveByApi(TOKEN);
		}

		List<Inventory> inventoryList = inventoryRepository.findAllByUserIdInAndDeleteDate(userIds,null);
		if (!inventoryList.isEmpty()){
			List<Inventory> inventories = inventoryList;

			lastDataIds = inventories.stream()
					.map(Inventory::getLastDataId)
					.filter(Objects::nonNull).collect(Collectors.toList());
			wareHouseIds = inventories.stream()
					.map(Inventory::getWarehouseId)
					.filter(Objects::nonNull).collect(Collectors.toList());

			Optional<List<MonogoInventoryLastData>> optionalMonogoInventoryLastDataList =
					mongoInventoryLastDataRepository.findAllBy_idIn(lastDataIds);
			List<MonogoInventoryLastData> monogoInventoryLastDataList = optionalMonogoInventoryLastDataList.get();
			Optional<List<Warehouse>> optionalWarehouseList =
					warehousesRepository.findAllByIdIn(wareHouseIds);
			List<Warehouse> warehouseList = optionalWarehouseList.get();

			String wareHouseName; Double temp; Double hum;

			for (Inventory inventory:inventories) {
				Date now=new Date();
				Long leftDays=null;
				if (inventory.getEndDate()==null||inventory.getEndDate().before(now)){
					leftDays= Long.valueOf(0);
				}
				else if (inventory.getEndDate() !=null){
					leftDays = ChronoUnit.DAYS.between(now.toInstant(),inventory.getEndDate().toInstant());
				}
				try {
					List<Warehouse> warehouseNames = warehouseList.stream()
							.filter(warehouse -> warehouse.getId()
									.equals(inventory.getWarehouseId())).collect(Collectors.toList());
					List<MonogoInventoryLastData> mongoInv = monogoInventoryLastDataList.stream()
							.filter(monogoInventoryLastData -> monogoInventoryLastData.getInventoryId()
									.equals(inventory.getId())).collect(Collectors.toList());
					if (!warehouseNames.isEmpty()) {
						wareHouseName= warehouseNames.get(0).getName();
					}else {
						wareHouseName = null;
					}
					if (!mongoInv.isEmpty()){
						temp = mongoInv.get(0).getTemperature();
						hum = mongoInv.get(0).getHumidity();
					}else {
						temp= null;
						hum = null;
					}
					result.add(
							InventoryListDashBoardResponse
									.builder()
									.inventoryName(inventory.getName())
									.wareHouseName(wareHouseName)
									.lastUpdate(inventory.getLastUpdate())
									.temp(temp)
									.humidity(hum)
									.storingCategory(dictionary.RangeInTempAndHumValid(inventory.getStoringCategory(),temp,hum))
									.leftDays(leftDays)
									.build()
					);
				}catch (Exception e){
					throw new ApiGetException(e.getLocalizedMessage());
				}

			}
			builder.setEntity(result);
			builder.setStatusCode(200);
			builder.setMessage("Data Found");
			builder.setSize(result.size());
			builder.setSuccess(true);
			return builder.build();

		}else {
			throw new ApiGetException("No Inventories Found");
		}
	}
}
