package com.example.food_drugs.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.food_drugs.dto.AuditGObject;
import com.example.food_drugs.dto.responses.*;
import com.example.food_drugs.entity.MonogoInventoryLastData;
import com.example.food_drugs.helpers.Impl.AssistantServiceImpl;
import com.example.food_drugs.repository.MongoInventoryLastDataRepository;
import com.example.food_drugs.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.DriverSelect;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.food_drugs.entity.Inventory;
import com.example.food_drugs.entity.Warehouse;
import com.example.food_drugs.entity.userClientWarehouse;
import com.example.food_drugs.photo.DecodePhotoSFDA;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
import com.example.food_drugs.repository.InventoryRepository;
import com.example.food_drugs.repository.UserClientWarehouseRepository;
import com.example.food_drugs.repository.WarehousesRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;
import com.example.examplequerydslspringdatajpamaven.service.UserRoleService;
import com.example.examplequerydslspringdatajpamaven.service.UserServiceImpl;


/**
 * services functionality related to  Warehouses SFDA
 * @author fuinco
 *
 */
@Component
@RequiredArgsConstructor
public class WarehouseServiceImpl extends RestServiceController implements WarehouseService {

	private static final Log logger = LogFactory.getLog(WarehouseServiceImpl.class);
	private GetObjectResponse getObjectResponse;
	private final AssistantServiceImpl assistantServiceImpl;
	private final InventoryRepository inventoryRepository;

	private final UserServiceImpl userServiceImpl;
	private final UserRoleService userRoleService;
	private final UserRepository userRepository;

	private final WarehousesRepository warehousesRepository;
	private final UserClientWarehouseRepository userClientWarehouseRepository;
	private final UserServiceImplSFDA userServiceImplSFDA;

	private final MongoInventoryLastDataRepository mongoInventoryLastDataRepository;


	@Override
	public ResponseEntity<?> getWarehousesList(String TOKEN, Long id, int offset, String search,int active,String exportData) {
		logger.info("************************ getWarehousesList STARTED ***************************");
		
		List<Warehouse> warehouses = new ArrayList<Warehouse>();
		 Integer size = 0;
		 List<Map> data = new ArrayList<>();
		 
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",warehouses);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(id != 0) {
			
			User user = userServiceImpl.findById(id);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",warehouses);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(user.getAccountType()!= 1) {
					if(!userRoleService.checkUserHasPermission(id, "WAREHOUSE", "list")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get WAREHOUSE list",null);
						 logger.info("************************ getWarehouseList ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				if(user.getDeleteDate() == null) {
					List<Long>usersIds= new ArrayList<>();
				    if(user.getAccountType().equals(4)) {
						 List<Long> warehouseIds = userClientWarehouseRepository.getWarhouseIds(id);
						 if(warehouseIds.size()>0) {
							 
							 
							 if(active == 0) {
								if(exportData.equals("exportData")) {
									warehouses = warehousesRepository.getWarehousesByIdsDeactiveExport(warehouseIds,search);

								}
								else {
									warehouses = warehousesRepository.getWarehousesByIdsDeactive(warehouseIds,offset,search);
									size = warehousesRepository.getWarehouseSizeByIdsDeactive(warehouseIds);	
								}

							 }
								
							 if(active == 2) {
								if(exportData.equals("exportData")) {
									 warehouses = warehousesRepository.getWarehousesByIdsAllExport(warehouseIds,search);

								}
								else {
									 warehouses = warehousesRepository.getWarehousesByIdsAll(warehouseIds,offset,search);
									 size = warehousesRepository.getWarehouseSizeByIdsAll(warehouseIds);
								}

								
							 }
								
							 if(active == 1) {
								if(exportData.equals("exportData")) {
									 warehouses = warehousesRepository.getWarehousesByIdsExport(warehouseIds,search);

								}
								else{
									 warehouses = warehousesRepository.getWarehousesByIds(warehouseIds,offset,search);
									 size = warehousesRepository.getWarehouseSizeByIds(warehouseIds);
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
								 warehouses = warehousesRepository.getWarehousesDeactiveExport(usersIds,search);

							}
							else {
								 warehouses = warehousesRepository.getWarehousesDeactive(usersIds,offset,search);
								 size = warehousesRepository.getWarehouseSizeDeactive(usersIds);
							}


						 }
							
						 if(active == 2) {
							if(exportData.equals("exportData")) {
								 warehouses = warehousesRepository.getWarehousesAllExport(usersIds,search);

							}
							else {
								 warehouses = warehousesRepository.getWarehousesAll(usersIds,offset,search);
								 size = warehousesRepository.getWarehouseSizeAll(usersIds);
							}

	
						 }
							
						 if(active == 1) {
							if(exportData.equals("exportData")) {
								 warehouses = warehousesRepository.getWarehousesExport(usersIds,search);

							}
							else {
								 warehouses = warehousesRepository.getWarehouses(usersIds,offset,search);
								 size = warehousesRepository.getWarehouseSize(usersIds);
							}

						 }
						 
					}
				    

					 for(Warehouse warehouse:warehouses) {
					     Map WarehousesList= new HashMap();

					     WarehousesList.put("id", warehouse.getId());
					     WarehousesList.put("name", warehouse.getName());
					     WarehousesList.put("city", warehouse.getCity());
						 WarehousesList.put("phone", warehouse.getPhone());
						 WarehousesList.put("email", warehouse.getEmail());
						 WarehousesList.put("delete_date", warehouse.getDeleteDate());
						 WarehousesList.put("userId", warehouse.getUserId());
						 WarehousesList.put("referenceKey", warehouse.getReferenceKey());

						 WarehousesList.put("create_date", warehouse.getCreate_date());
						 WarehousesList.put("regestration_to_elm_date", warehouse.getRegestration_to_elm_date());
						 WarehousesList.put("delete_from_elm_date", warehouse.getDelete_from_elm_date());
						 WarehousesList.put("update_date_in_elm", warehouse.getUpdate_date_in_elm());

						 
						 WarehousesList.put("userName", null);
						
						 User us = userRepository.findOne(warehouse.getUserId());
						 if(us != null) {
							 WarehousesList.put("userName", us.getName());

						 }
						 data.add(WarehousesList);

					 }
						

					 
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",data,size);
					logger.info("************************ getWarehousesList ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);

				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",warehouses);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
				
			}

		}
		else{
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",warehouses);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
	}

	@Override
	public ResponseEntity<?> createWarehouses(String TOKEN, Warehouse warehouse, Long userId) {
		logger.info("************************ createWarehouses STARTED ***************************");
		Date now = new Date();
		SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String nowTime = isoFormat.format(now);
		
		warehouse.setCreate_date(nowTime);
		warehouse.setDeleteDate(null);

		String image = warehouse.getPhoto();
		warehouse.setPhoto("not_available.png");
		
		List<Warehouse> warehouses= new ArrayList<Warehouse>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",warehouses);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId != 0) {
			User user = userServiceImpl.findById(userId);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",warehouses);
				return ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(user.getAccountType()!= 1) {
					if(!userRoleService.checkUserHasPermission(userId, "WAREHOUSE", "create")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to create",null);
						 logger.info("************************ createWarehouse ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				if(user.getDeleteDate()==null) {
					if(warehouse.getName()== null || warehouse.getName().equals("")
					   || warehouse.getActivity()== null || warehouse.getActivity().equals("")
					   || warehouse.getCity()== null || warehouse.getCity().equals("")
					   || warehouse.getAddress()== null || warehouse.getAddress().equals("")
					   || warehouse.getLatitude()== null || warehouse.getLatitude().equals("")
					   || warehouse.getLongitude()== null || warehouse.getLongitude().equals("")
					   || warehouse.getLandCoordinates()== null || warehouse.getLandCoordinates().equals("")
					   || warehouse.getLicenseNumber()== null || warehouse.getLicenseNumber().equals("")
					   || warehouse.getLicenseIssueDate()== null || warehouse.getLicenseIssueDate().equals("")
					   || warehouse.getLicenseExpiryDate()== null || warehouse.getLicenseExpiryDate().equals("")
					   || warehouse.getPhone()== null || warehouse.getPhone().equals("")
					   ) {
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "[name ,activity ,city ,address , lat ,long ,landCoordinates , licenseNumber ,LicenseIssueDate ,phone and LicenseExpiryDate] are Required",null);
						return ResponseEntity.badRequest().body(getObjectResponse);

					}

					 User parent = null;

					if(warehouse.getId()==null || warehouse.getId()==0) {
						if(user.getAccountType().equals(4)) {
							 Set<User> parentClients = user.getUsersOfUser();
							 if(parentClients.isEmpty()) {
								 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are account type 4 and not has parent",null);
								 return  ResponseEntity.badRequest().body(getObjectResponse);
							 }else {
								 for(User object : parentClients) {
									 parent = object;
								 }
								 warehouse.setUserId(parent.getId());


							 }
						 }
						 else {
							 warehouse.setUserId(userId);

						 }
						
						List<Warehouse> res = warehousesRepository.checkDublicateAdd(warehouse.getUserId(), warehouse.getEmail(), warehouse.getName(),warehouse.getPhone() , warehouse.getLicenseNumber());
					    List<Integer> duplictionList =new ArrayList<Integer>();

						if(!res.isEmpty()) {
							for(int i=0;i<res.size();i++) {
								if(res.get(i).getName() != null) {
									if(res.get(i).getName().equals(warehouse.getName())) {
										duplictionList.add(1);				
					
									}
								}
								if(res.get(i).getPhone() != null) {
									if(res.get(i).getPhone().equals(warehouse.getPhone())) {
										duplictionList.add(2);				
					
									}
								}
								
								if(res.get(i).getLicenseNumber() != null) {
									if(res.get(i).getLicenseNumber().equals(warehouse.getLicenseNumber())) {
										duplictionList.add(3);				
					
									}
								}
								
								if(res.get(i).getEmail() != null) {
									if(res.get(i).getEmail().equals(warehouse.getEmail())) {
										duplictionList.add(4);				
					
									}
								}
								
								
								
							}
							getObjectResponse = new GetObjectResponse( 201, "This warehouse was found before",duplictionList);
							return ResponseEntity.ok().body(getObjectResponse);

						}
						
						
						DecodePhotoSFDA decodePhoto=new DecodePhotoSFDA();
				    	if(image !=null) {
					    	if(image !="") {
					    		if(image.startsWith("data:image")) {
					    			warehouse.setPhoto(decodePhoto.Base64_Image(image,"warehouse"));				

					    		}
					    	}
						}
						warehousesRepository.save(warehouse);
						AuditGObject<Warehouse> newEntity = new AuditGObject<>();
						newEntity.setEntity(warehouse);
						assistantServiceImpl.saveOnAuditChanges(userId,null,newEntity,"create",warehouse.getId());
						warehouses.add(warehouse);
						
						if(user.getAccountType().equals(4)) {
				    		userClientWarehouse saveData = new userClientWarehouse();
					        
					        Long wareId = warehousesRepository.getWarehouseIdByName(parent.getId(),warehouse.getName(),warehouse.getLicenseNumber());
				    		if(wareId != null) {
					    		saveData.setUserid(userId);
					    		saveData.setWarehouseid(wareId);
					    		userClientWarehouseRepository.save(saveData);
				    		}
				    		
				    	}

						getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"sucsess",warehouses);
						logger.info("************************ createWarehouses ENDED ***************************");

						return ResponseEntity.ok().body(getObjectResponse);

					}
					else {
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not allow to update warehouse Id",warehouses);
						return ResponseEntity.badRequest().body(getObjectResponse);

					}
					
					
				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User ID is not Found",warehouses);
					return ResponseEntity.status(404).body(getObjectResponse);

				}

			}
           			
			
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",warehouses);
			return ResponseEntity.badRequest().body(getObjectResponse);

			
		}
	}

	@Override
	public ResponseEntity<?> editWarehouses(String TOKEN, Warehouse warehouse, Long userId) {

		// TODO Auto-generated method stub
		List<Warehouse> warehouses = new ArrayList<Warehouse>();
		
    	String newPhoto= warehouse.getPhoto();
    	warehouse.setPhoto("not_available.png");
		
    	if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",warehouses);
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
			if(!userRoleService.checkUserHasPermission(userId, "WAREHOUSE", "edit")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to edit warehouses",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		 
		if(warehouse.getId() == null) {
			getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "warehouses ID is Required",warehouses);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		Warehouse warehouseCheck = warehousesRepository.findOne(warehouse.getId());
		if(warehouseCheck == null) {
			getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "warehouses is not found",warehouses);
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		if(warehouseCheck.getDeleteDate() != null) {
			getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "warehouses is not found or deleted",warehouses);
			return ResponseEntity.status(404).body(getObjectResponse);
		}

		warehouse.setUserId(warehouseCheck.getUserId());
		Long createdBy=warehouse.getUserId();
		Boolean isParent=false;

		if(createdBy.toString().equals(userId.toString())) {
			isParent=true;
		}
		
		List<User>childs = new ArrayList<User>();
		if(loggedUser.getAccountType().equals(4)) {
			 List<User> parents=userServiceImpl.getAllParentsOfuser(loggedUser,loggedUser.getAccountType());
			 if(parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this warehouses.",null);
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
			List<Long> warehousesData = userClientWarehouseRepository.getWarehouse(userId,warehouse.getId());
			if(warehousesData.isEmpty()) {
				isParent = false;
			}
			else {
				isParent = true;
			}
		}
		
		
		if(isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get warehouses",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(warehouse.getName()== null || warehouse.getName().equals("")
				   || warehouse.getActivity()== null || warehouse.getActivity().equals("")
				   || warehouse.getCity()== null || warehouse.getCity().equals("")
				   || warehouse.getAddress()== null || warehouse.getAddress().equals("")
				   || warehouse.getLatitude()== null || warehouse.getLatitude().equals("")
				   || warehouse.getLongitude()== null || warehouse.getLongitude().equals("")
				   || warehouse.getLandCoordinates()== null || warehouse.getLandCoordinates().equals("")
				   || warehouse.getLicenseNumber()== null || warehouse.getLicenseNumber().equals("")
				   || warehouse.getLicenseIssueDate()== null || warehouse.getLicenseIssueDate().equals("")
				   || warehouse.getLicenseExpiryDate()== null || warehouse.getLicenseExpiryDate().equals("")
				   || warehouse.getPhone()== null || warehouse.getPhone().equals("")
				   ) {
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "[name ,activity ,city ,address , lat ,long ,landCoordinates , licenseNumber ,LicenseIssueDate ,phone and LicenseExpiryDate] are Required",null);
					return ResponseEntity.badRequest().body(getObjectResponse);
		}
		else {
			
			
			List<Warehouse> res = warehousesRepository.checkDublicateEdit(warehouse.getId(),warehouse.getUserId(), warehouse.getEmail(), warehouse.getName(),warehouse.getPhone() , warehouse.getLicenseNumber());
		    List<Integer> duplictionList =new ArrayList<Integer>();

			if(!res.isEmpty()) {
				for(int i=0;i<res.size();i++) {
					if(res.get(i).getName() != null) {
						if(res.get(i).getName().equals(warehouse.getName())) {
							duplictionList.add(1);				
		
						}
					}
					
					if(res.get(i).getPhone() != null) {
						if(res.get(i).getPhone().equals(warehouse.getPhone())) {
							duplictionList.add(2);				
		
						}
					}
					
					if(res.get(i).getLicenseNumber() != null) {
						if(res.get(i).getLicenseNumber().equals(warehouse.getLicenseNumber())) {
							duplictionList.add(3);				
		
						}
					}
					
					if(res.get(i).getEmail() != null) {
						if(res.get(i).getEmail().equals(warehouse.getEmail())) {
							duplictionList.add(4);				
		
						}
					}
					
					
					
				}
				getObjectResponse = new GetObjectResponse( 201, "This warehouse was found before",duplictionList);
				return ResponseEntity.ok().body(getObjectResponse);

			}

			
			DecodePhotoSFDA decodePhoto=new DecodePhotoSFDA();
        	String oldPhoto=warehouseCheck.getPhoto();

			if(oldPhoto != null) {
	        	if(!oldPhoto.equals("")) {
					if(!oldPhoto.equals("not_available.png")) {
						decodePhoto.deletePhoto(oldPhoto, "warehouse");
					}
				}
			}
			
			
			if(newPhoto == null) {
				warehouse.setPhoto("not_available.png");
			}
			else {
				if(newPhoto.equals("")) {
					
					warehouse.setPhoto("not_available.png");				
				}
				else {
					if(newPhoto.equals(oldPhoto)) {
						warehouse.setPhoto(oldPhoto);				
					}
					else{
			    		if(newPhoto.startsWith("data:image")) {

			    			warehouse.setPhoto(decodePhoto.Base64_Image(newPhoto,"warehouse"));
			    		}
					}

			    }
			}
			AuditGObject<Warehouse> oldEntity = new AuditGObject<>();
			AuditGObject<Warehouse> newEntity = new AuditGObject<>();
			oldEntity.setEntity(warehouseCheck);
			newEntity.setEntity(warehouse);
			assistantServiceImpl.saveOnAuditChanges(userId,oldEntity,newEntity,"update",warehouse.getId());
			warehousesRepository.save(warehouse);
			

			warehouses.add(warehouse);
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Updated Successfully",warehouses);
			logger.info("************************ editWarehouse ENDED ***************************");
			return ResponseEntity.ok().body(getObjectResponse);
			
		}
			    
	}

	@Override
	public ResponseEntity<?> getWarehouseById(String TOKEN, Long WarehouseId, Long userId) {
		// TODO Auto-generated method stub

		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(WarehouseId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No WarehouseId  to return",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		if(userId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No loggedId",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		Warehouse warehouse= warehousesRepository.findOne(WarehouseId);
		if(warehouse == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this warehouse not found",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}
		if(warehouse.getDeleteDate() != null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this warehouse not found or deleted",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}
		
		User loggedUser = userServiceImpl.findById(userId);
		if(loggedUser == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this user not found",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}
		
		
		Long createdBy=warehouse.getUserId();
		Boolean isParent=false;

		if(createdBy.toString().equals(userId.toString())) {
			isParent=true;
		}
		List<User>childs = new ArrayList<User>();
		if(loggedUser.getAccountType().equals(4)) {
			 List<User> parents=userServiceImpl.getAllParentsOfuser(loggedUser,loggedUser.getAccountType());
			 if(parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this warehouse.",null);
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
			List<Long> warehousesData = userClientWarehouseRepository.getWarehouse(userId,warehouse.getId());
			if(warehousesData.isEmpty()) {
				isParent = false;
			}
			else {
				isParent = true;
			}
		}
		
		if(isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get warehouse",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		
		
		List<Warehouse> warehouses = new ArrayList<Warehouse>();
		warehouses.add(warehouse);
		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",warehouses);
		return  ResponseEntity.ok().body(getObjectResponse);
	}

	@Override
	public ResponseEntity<?> deleteWarehouse(String TOKEN, Long WarehouseId, Long userId) {
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
			if(!userRoleService.checkUserHasPermission(userId, "WAREHOUSE", "delete")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to delete warehouse",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		if(WarehouseId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No WarehouseId  to delete",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		Warehouse warehouse= warehousesRepository.findOne(WarehouseId);
		if(warehouse == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this WarehouseId not found",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}

		if(warehouse.getDeleteDate() != null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this Warehouse not found or deleted",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}
				


		Long createdBy=warehouse.getUserId();
		Boolean isParent=false;

		if(createdBy.toString().equals(userId.toString())) {
			isParent=true;
		}
		List<User>childs = new ArrayList<User>();
		if(loggedUser.getAccountType().equals(4)) {
			 List<User> parents=userServiceImpl.getAllParentsOfuser(loggedUser,loggedUser.getAccountType());
			 if(parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this warehouse.",null);
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
			List<Long> warehousesData = userClientWarehouseRepository.getWarehouse(userId,warehouse.getId());
			if(warehousesData.isEmpty()) {
				isParent = false;
			}
			else {
				isParent = true;
			}
		}
		
		
		if(isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get warehouse",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DATE);
	    int month = cal.get(Calendar.MONTH) + 1;
	    int year = cal.get(Calendar.YEAR);
	    String date = year +"-"+ month +"-"+ day;
	    warehouse.setDeleteDate(date);
		AuditGObject<Warehouse> oldEntity = new AuditGObject<>();
		oldEntity.setEntity(warehouse);
		assistantServiceImpl.saveOnAuditChanges(userId,oldEntity,null,"delete",WarehouseId);
	    warehousesRepository.save(warehouse);

	    
	    
		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",null);
		return  ResponseEntity.ok().body(getObjectResponse);


	}

	@Override
	public ResponseEntity<?> getListSelectWarehouse(String TOKEN, Long userId) {
		
		logger.info("************************ getWarehousesList STARTED ***************************");
		 List<Map> data = new ArrayList<>();
		 
		List<Warehouse> warehouses = new ArrayList<Warehouse>();
		
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",warehouses);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId != 0) {
			
			User user = userServiceImpl.findById(userId);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",warehouses);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(user.getAccountType()!= 1) {
					if(!userRoleService.checkUserHasPermission(userId, "WAREHOUSE", "list")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get WAREHOUSE list",null);
						 logger.info("************************ getWarehouseList ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				if(user.getDeleteDate() == null) {
					 List<Long>usersIds= new ArrayList<>();

					userServiceImpl.resetChildernArray();
				    if(user.getAccountType().equals(4)) {
						 

						 
						 List<Long> warehouseIds = userClientWarehouseRepository.getWarhouseIds(userId);
						 if(warehouseIds.size()>0) {
							 warehouses = warehousesRepository.getAllWarehousesSelectByIds(warehouseIds);
					
							 if(warehouses.size() >0) {
	
								 for(Warehouse warehouse:warehouses) {
								     Map WarehousesList= new HashMap();
	
								     
								     WarehousesList.put("id", warehouse.getId());
								     WarehousesList.put("name", warehouse.getName());
									
									 data.add(WarehousesList);
	
								 }
								
	
							 }
							 
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
						 warehouses = warehousesRepository.getAllWarehousesSelect(usersIds);
						 if(warehouses.size() >0) {

							 for(Warehouse warehouse:warehouses) {
							     Map WarehousesList= new HashMap();

							     
							     WarehousesList.put("id", warehouse.getId());
							     WarehousesList.put("name", warehouse.getName());
								
								 data.add(WarehousesList);

							 }
							

						 }
					}
				    

					
					
					 
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",data);
					logger.info("************************ getWarehousesList ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);

				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",warehouses);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
				
			}

		}
		else{
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",warehouses);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
	}

	@Override
	public ResponseEntity<?> getListWarehouseMap(String TOKEN, Long userId) {
        logger.info("************************ getWarehousesList STARTED ***************************");
		List<Warehouse> warehouses = new ArrayList<Warehouse>();
		
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",warehouses);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId != 0) {
			
			User user = userServiceImpl.findById(userId);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",warehouses);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(user.getAccountType()!= 1) {
					if(!userRoleService.checkUserHasPermission(userId, "WAREHOUSE", "list")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get WAREHOUSE list",null);
						 logger.info("************************ getWarehouseList ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				if(user.getDeleteDate() == null) {
					 List<Long>usersIds= new ArrayList<>();

					userServiceImpl.resetChildernArray();
				    if(user.getAccountType().equals(4)) {
				    	 List<Long> warehouseIds = userClientWarehouseRepository.getWarhouseIds(userId);
						 if(warehouseIds.size()>0) {
							 warehouses = warehousesRepository.getAllWarehousesSelectByIds(warehouseIds);

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
						 warehouses = warehousesRepository.getAllWarehousesSelect(usersIds);

					}
				    

					
					
					 
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",warehouses);
					logger.info("************************ getWarehousesList ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);

				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",warehouses);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
				
			}

		}
		else{
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",warehouses);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
	}

	@Override
	public ResponseEntity<?> activeWarehouse(String TOKEN, Long WarehouseId, Long userId) {
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
			if(!userRoleService.checkUserHasPermission(userId, "WAREHOUSE", "delete")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to delete warehouse",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		if(WarehouseId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No WarehouseId  to delete",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		Warehouse warehouse= warehousesRepository.findOne(WarehouseId);
		if(warehouse == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this WarehouseId not found",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}


		Long createdBy=warehouse.getUserId();
		Boolean isParent=false;

		if(createdBy.toString().equals(userId.toString())) {
			isParent=true;
		}
		List<User>childs = new ArrayList<User>();
		if(loggedUser.getAccountType().equals(4)) {
			 List<User> parents=userServiceImpl.getAllParentsOfuser(loggedUser,loggedUser.getAccountType());
			 if(parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this warehouse.",null);
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
			List<Long> warehousesData = userClientWarehouseRepository.getWarehouse(userId,warehouse.getId());
			if(warehousesData.isEmpty()) {
				isParent = false;
			}
			else {
				isParent = true;
			}
		}
		
		if(loggedUser.getAccountType().equals(4)) {
			List<Long> warehousesData = userClientWarehouseRepository.getWarehouse(userId,warehouse.getId());
			if(warehousesData.isEmpty()) {
				isParent = false;
			}
			else {
				isParent = true;
			}
		}
		
		
		if(isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get warehouse",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		
		
        warehouse.setDeleteDate(null);
	     
	    warehousesRepository.save(warehouse);
	    
	    
		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",null);
		return  ResponseEntity.ok().body(getObjectResponse);
	}

	@Override
	public ResponseEntity<?> getWarehouseUnSelectOfClient(String TOKEN, Long loggedUserId, Long userId) {
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
		

		drivers = warehousesRepository.getWarehouseUnSelectOfClient(loggedUserId,userId);
		List<DriverSelect> selectedWarehouses = userClientWarehouseRepository.getWarehousesOfUserList(userId);

		
		List<Map> data = new ArrayList<>();
	    Map obj = new HashMap();
	    
	    obj.put("selectedWarehouses", selectedWarehouses);
	    obj.put("warehouses", drivers);

	    data.add(obj);
	    
		getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data);
		logger.info("************************ getNotificationSelect ENDED ***************************");
		return ResponseEntity.ok().body(getObjectResponse);
		
				
			
	}

	@Override
	public ResponseEntity<?> assignClientWarehouses(String TOKEN, Long loggedUserId, Long userId, Long[] warehouseIds) {
		// TODO Auto-generated method stub
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
		
        if(warehouseIds.length > 0 && warehouseIds[0] != 0) {
			
        	List<userClientWarehouse> checkData = userClientWarehouseRepository.getWarehouseByWarIds(warehouseIds,userId);
        	if(checkData.size()>0) {
        		getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "There is warehouse assigned to another user before it should only share with one user",null);
				return ResponseEntity.badRequest().body(getObjectResponse);
        	}
        	
			for(Long id:warehouseIds) {
				if(id == 0) {
					
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "assigned ID is Required",null);
					return ResponseEntity.badRequest().body(getObjectResponse);
				}
				Warehouse assignedWarehouse = warehousesRepository.findOne(id);
				if(assignedWarehouse == null) {
					
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "assigned warehouse is not found",null);
					return ResponseEntity.status(404).body(getObjectResponse);
				}
		        
				
			}
			
			userClientWarehouseRepository.deleteWarehousesByUserId(userId);
			for(Long assignedId:warehouseIds) {
				userClientWarehouse userWarehouse = new userClientWarehouse();
				userWarehouse.setUserid(userId);
				userWarehouse.setWarehouseid(assignedId);
				userClientWarehouseRepository.save(userWarehouse);
			}

			


			getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Assigend Successfully",null);
			return ResponseEntity.ok().body(getObjectResponse);

		}
		else {
			List<userClientWarehouse> warehouses = userClientWarehouseRepository.getWarehousesOfUser(userId);
			
			if(warehouses.size() > 0) {

				userClientWarehouseRepository.deleteWarehousesByUserId(userId);
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
	public ResponseEntity<?> getClientWarehouses(String TOKEN, Long loggedUserId, Long userId) {
		// TODO Auto-generated method stub
		
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

		List<DriverSelect> warehouses = userClientWarehouseRepository.getWarehousesOfUserList(userId);

		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "Success",warehouses);
		logger.info("************************ assignClientUsers ENDED ***************************");
		return ResponseEntity.ok().body(getObjectResponse);
	}

	@Override
	public ResponseEntity<?> getInventoryListOfWarehouseMap(String TOKEN, Long userId, Long WarehouseId) {
		// TODO Auto-generated method stub

		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(WarehouseId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No WarehouseId  to return",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		if(userId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No loggedId",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		Warehouse warehouse= warehousesRepository.findOne(WarehouseId);
		if(warehouse == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this warehouse not found",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}
		if(warehouse.getDeleteDate() != null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this warehouse not found or deleted",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}
		
		User loggedUser = userServiceImpl.findById(userId);
		if(loggedUser == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this user not found",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}
		
		
		Long createdBy=warehouse.getUserId();
		Boolean isParent=false;

		if(createdBy.toString().equals(userId.toString())) {
			isParent=true;
		}
		List<User>childs = new ArrayList<User>();
		if(loggedUser.getAccountType().equals(4)) {
			 List<User> parents=userServiceImpl.getAllParentsOfuser(loggedUser,loggedUser.getAccountType());
			 if(parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this warehouse.",null);
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
			List<Long> warehousesData = userClientWarehouseRepository.getWarehouse(userId,warehouse.getId());
			if(warehousesData.isEmpty()) {
				isParent = false;
			}
			else {
				isParent = true;
			}
		}
		
		if(isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get warehouse",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		
		
		List<Inventory> inventories = new ArrayList<Inventory>();
		
		inventories = inventoryRepository.getAllInventoriesOfWarehouseList(WarehouseId);
		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",inventories);
		return  ResponseEntity.ok().body(getObjectResponse);
	}

	@Override
	public ResponseEntity<?> assignWarehouseToUser(String TOKEN, Long userId, Long warehouseId, Long toUserId) {
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
		if(userId == 0 || warehouseId == 0 || toUserId == 0 ) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "userId , warehouseId and toUserId  are required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		else {
			User loggedUser = userServiceImpl.findById(userId);
			
			if(loggedUser == null) {
				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "loggedUser is not found",null);
				
				return ResponseEntity.status(404).body(getObjectResponse);
			}else {
				if(!loggedUser.getAccountType().equals(1)) {
					if(!userRoleService.checkUserHasPermission(userId, "WAREHOUSE", "assignToUser")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to assignToWarehouse",null);
						 logger.info("************************ assignToUser ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				if(loggedUser.getAccountType().equals(3) || loggedUser.getAccountType().equals(4)) {
					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to assign warehouse to any user",null);
					
					return ResponseEntity.status(404).body(getObjectResponse);
				}
				
				User toUser = userServiceImpl.findById(toUserId);
			    if(toUser == null) {
			    	getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "user you want to assign to  is not found",null);
					return ResponseEntity.status(404).body(getObjectResponse);
			    }
			    else {
			    	
			    	 if(toUser.getAccountType().equals(4)) {
			    		  getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to assign warehouse to this user type 4 assign to his parents",null);
						  return ResponseEntity.status(404).body(getObjectResponse);
			    	 }
			    	  
			    	  
			    	 List<User>toUserParents = userServiceImpl.getAllParentsOfuser(toUser, toUser.getAccountType());
			    	 if(toUserParents.isEmpty()) {
			    		 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to assign warehouse to this user",null);
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
			    			 if(warehouseId == 0) {
			 					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No WarehouseId  to delete",null);
			 					 return  ResponseEntity.badRequest().body(getObjectResponse);
			 				}
			 				Warehouse warehouse= warehousesRepository.findOne(warehouseId);
			 				if(warehouse == null) {
			 					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this WarehouseId not found",null);
			 					 return  ResponseEntity.status(404).body(getObjectResponse);
			 				}

			 				if(warehouse.getDeleteDate() != null) {
			 					getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this Warehouse not found or deleted",null);
			 					 return  ResponseEntity.status(404).body(getObjectResponse);
			 				}
			 				warehouse.setUserId(toUserId);
			 			     
			 			    warehousesRepository.save(warehouse);
			 			    
			 			    
			 				getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",null);
			 				return  ResponseEntity.ok().body(getObjectResponse);
			    		 }
			    		 else {
			    			 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you are not allowed to assign warehouse to this user",null);
							 return ResponseEntity.status(404).body(getObjectResponse);
			    		 }
			    	 }
				
			    }
			    
			}
		}

	}

	@Override
	public ResponseEntity<?> getListWarehouseDataGraph(String TOKEN, Long userId) {
		logger.info("************************ getListWarehouseDataGraph STARTED ***************************");
		List<Object[]> sqlData = new ArrayList<>();

		if(TOKEN.equals("")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",sqlData);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}

		if(super.checkActive(TOKEN)!= null) {
			return super.checkActive(TOKEN);
		}
		if(userId != 0) {
			User user = userServiceImpl.findById(userId);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",sqlData);
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
			if(user.getDeleteDate() != null){
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(),
						"This User Was Delete at : "
								+user.getDeleteDate().toString(),sqlData);
				return  ResponseEntity.status(404).body(getObjectResponse);
			}


			List<Long>usersIds= new ArrayList<>();

			userServiceImpl.resetChildernArray();
			if(user.getAccountType().equals(4)) {
				usersIds.add(userId);
				sqlData = warehousesRepository.getInventoryWarehouseDataByUserIds(usersIds);
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
				sqlData = warehousesRepository.getInventoryWarehouseDataByUserIds(usersIds);
			}
			List<InventoryWarehouseDataByUserIdsDataWrapper> mappedSQLData = new ArrayList<>();
			String pattern = "HH:mm";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);



			for( Object[] datas : sqlData){

				List<MonogoInventoryLastData> lastDataList = mongoInventoryLastDataRepository
						.findFirst20ByInventoryIdOrderByCreateDateDesc((Integer) datas[1]);
				List<GraphObject> series = new ArrayList<>();
				for(MonogoInventoryLastData data:lastDataList){
					series.add(GraphObject.builder()
							.name(simpleDateFormat.format(data.getCreateDate()))
							.value(data.getTemperature()).build());
				}
				mappedSQLData.add(InventoryWarehouseDataByUserIdsDataWrapper
						.builder()
								.userId((Integer) datas[0])
								.inventoryId((Integer) datas[1])
								.wareHouseName((String) datas[2])
								.inventoryName((String) datas[3])
								.storingCategory((String) datas[4])
								.lastUpdate((String) datas[5])
								.lastDataId((String) datas[6])
								.warehouseId((Integer) datas[7])
								.lastData(mongoInventoryLastDataRepository.findById((String) datas[6]))
								.graphData(GraphDataWrapper.builder()
										.name("Temperature")
										.series(series)
										.build())
						.build());
			}
			List<WarehouseWrapperGraphData> warehouseWrapperGraphData = new ArrayList<>();
			List<Object[]> warehouses = warehousesRepository.getWarehouseForUser(usersIds);
			for (Object[] warehouseId : warehouses){
				List<InventoryWarehouseDataByUserIdsDataWrapper> invs = mappedSQLData
						.stream().filter(c -> c.getWarehouseId() == ((Integer) warehouseId[0]))
						.collect(Collectors.toList());
				warehouseWrapperGraphData.add(
						WarehouseWrapperGraphData.builder()
								.inventories(invs)
								.warehouseName((String) warehouseId[1])
								.build());
			}

			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",warehouseWrapperGraphData);
			logger.info("************************ getListWarehouseDataGraph ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
		}

		else{

			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",sqlData);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
	}

	@Override
	public ResponseEntity<GetObjectResponse<WarehouseInventoriesListMobileWrapper>> getListWarehouseInventoriesListByWahrehouseId(String TOKEN, Long userId, Long warehouseId) {
		logger.info("************************ getListWarehouseInventoriesListByWahrehouseId Started ***************************");
		ResponseEntity responseEntity = userServiceImplSFDA.
				userAndTokenErrorChecker(
						TOKEN,
						userId);
		if (responseEntity.getStatusCode()!=HttpStatus.OK){
			return  responseEntity;
		}
		Warehouse warehouse = warehousesRepository.findOne(warehouseId);
		List<Inventory> warehouseInventories = inventoryRepository.findAllByWarehouseId(warehouseId);
		List<InventoryWarehouseDataByUserIdsDataWrapper> warehouseInventoriesMappedData = new ArrayList<>();
		for(Inventory inventory : warehouseInventories){
			warehouseInventoriesMappedData.add(
					InventoryWarehouseDataByUserIdsDataWrapper
							.builder()
							.userId(Math.toIntExact(userId))
							.inventoryId(Math.toIntExact(inventory.getId()))
							.inventoryName(inventory.getName())
							.storingCategory(inventory.getStoringCategory())
							.lastUpdate(inventory.getLastUpdate())
							.lastDataId(inventory.getLastDataId())
							.warehouseId(Math.toIntExact(inventory.getWarehouseId()))
							.lastData(mongoInventoryLastDataRepository.findById(inventory.getLastDataId()))
							.build());
		}
		List<WarehouseInventoriesListMobileWrapper> warehouseInventoriesListMobileWrappers = new ArrayList<>();
		warehouseInventoriesListMobileWrappers.add(
				WarehouseInventoriesListMobileWrapper
						.builder()
						.warehouse(warehouse)
						.warehouseInventoiresList(warehouseInventoriesMappedData)
						.build());

		getObjectResponse= new GetObjectResponse<WarehouseInventoriesListMobileWrapper>(HttpStatus.OK.value(), "Success",warehouseInventoriesListMobileWrappers);
		logger.info("************************ getListWarehouseInventoriesListByWahrehouseId ENDED ***************************");
		return  ResponseEntity.ok().body(getObjectResponse);

	}



}
