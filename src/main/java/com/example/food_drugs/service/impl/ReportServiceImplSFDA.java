package com.example.food_drugs.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.example.examplequerydslspringdatajpamaven.entity.*;
import com.example.examplequerydslspringdatajpamaven.repository.*;
import com.example.examplequerydslspringdatajpamaven.responses.AlarmSectionWrapperResponse;
import com.example.food_drugs.dto.responses.*;
import com.example.food_drugs.entity.*;
import com.example.food_drugs.dto.DeviceTempHum;
import com.example.food_drugs.helpers.Impl.ReportsHelper;
import com.example.food_drugs.helpers.Impl.Utilities;
import com.example.food_drugs.mappers.PositionMapper;
import com.example.food_drugs.repository.*;
import com.example.food_drugs.service.ReportServiceSFDA;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;
import com.example.examplequerydslspringdatajpamaven.service.DeviceServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.GroupsServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.UserRoleService;
import com.example.examplequerydslspringdatajpamaven.service.UserServiceImpl;

import com.example.food_drugs.repository.InventoryRepository;
import com.example.food_drugs.repository.MongoInventoryLastDataRepo;
import com.example.food_drugs.repository.MongoInventoryNotificationRepo;
import com.example.food_drugs.repository.MongoPositionRepoSFDA;
import com.example.food_drugs.repository.PositionMongoSFDARepository;
import com.example.food_drugs.repository.WarehousesRepository;


/**
 * services functionality related to reports SFDA
 * @author fuinco
 *
 */
@Component
@Service
public class ReportServiceImplSFDA extends RestServiceController implements ReportServiceSFDA {

    private static final Log logger = LogFactory.getLog(DeviceServiceImplSFDA.class);
	
	private GetObjectResponse getObjectResponse;

//	@Autowired
//	private PositionMapper positionMapper;

	@Autowired
	private UserClientDeviceRepository userClientDeviceRepository;
	
	@Autowired
	private InventoryRepository inventoryRepository;
	
	@Autowired
	private WarehousesRepository warehousesRepository;

	@Autowired
	private UserRoleService userRoleService;
	
	@Autowired
	private GroupRepository groupRepository;
	
	@Autowired
	private UserServiceImpl userServiceImpl;

	@Autowired
	private UserClientGroupRepository userClientGroupRepository;	
	
	@Autowired
	private MongoInventoryLastDataRepo mongoInventoryLastDataRepo;
	
	@Autowired
	private MongoInventoryNotificationRepo mongoInventoryNotificationRepo;

	@Autowired
	private DeviceServiceImpl deviceServiceImpl;
	
	@Autowired
	private GroupsServiceImpl groupsServiceImpl;
	
	@Autowired
	private MongoPositionRepoSFDA mongoPositionRepoSFDA;

	private final MongoInventoryNotificationRepository mongoInventoryNotificationRepository;

	private final PositionMongoSFDARepository positionMongoSFDARepository ;

	private final DeviceRepositorySFDA deviceRepositorySFDA ;

	private final MongoEventsRepository mongoEventsRepository;

	private final MongoPositionsRepository mongoPositionsRepository;

	private final ReportsHelper reportsHelper;
	private final Utilities utilities;

	public ReportServiceImplSFDA(MongoInventoryNotificationRepository mongoInventoryNotificationRepository, PositionMongoSFDARepository positionMongoSFDARepository,
								 DeviceRepositorySFDA deviceRepositorySFDA,
								 MongoEventsRepository mongoEventsRepository, MongoPositionsRepository mongoPositionsRepository, ReportsHelper reportsHelper, Utilities utilities) {
		this.mongoInventoryNotificationRepository = mongoInventoryNotificationRepository;
		this.positionMongoSFDARepository = positionMongoSFDARepository;
		this.deviceRepositorySFDA = deviceRepositorySFDA;
		this.mongoEventsRepository = mongoEventsRepository;
		this.mongoPositionsRepository = mongoPositionsRepository;
		this.reportsHelper = reportsHelper;
		this.utilities = utilities;
	}

	@Override
	public ResponseEntity<?> getInventoriesReport(String TOKEN, Long[] inventoryIds, int offset, String start,
			String end, String search, Long userId,String exportData) {
		logger.info("************************ getInventoriesReport STARTED ***************************");		
		List<InventoryLastData> inventoriesReport = new ArrayList<InventoryLastData>();
		if(TOKEN.equals("")) {
			
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",inventoriesReport);
				logger.info("************************ getInventoriesReport ENDED ***************************");		
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(!TOKEN.equals("Schedule")) {
			if(super.checkActive(TOKEN)!= null)
			{
				return super.checkActive(TOKEN);
			}
		}

		User loggedUser = new User();
		if(userId != 0) {
			
			loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",inventoriesReport);
				logger.info("************************ getInventoriesReport ENDED ***************************");		
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
		}	
		
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "INVENTORYTEMPHUMD", "list")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get inventoriesReport list",inventoriesReport);
					logger.info("************************ getInventoriesReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}

		List<Long>allInventories= new ArrayList<>();
		
		if(inventoryIds.length != 0 ) {
			for(Long inventoryId:inventoryIds) {
				if(inventoryId !=0) {
					Inventory inventory =inventoryRepository.findOne(inventoryId);
					if(inventory != null) {
						
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
						
						allInventories.add(inventoryId);
					}
				}
			}
		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Inventory is not found",inventoriesReport);
			logger.info("************************ getInventoriesReport ENDED ***************************");		
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		Date dateFrom;
		Date dateTo;
		if(start.equals("0") || end.equals("0")) {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",null);
			logger.info("************************ getInventoriesReport ENDED ***************************");
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		else {

			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			inputFormat1.setLenient(false);
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);

			try {
				dateFrom = inputFormat.parse(start);
				start = outputFormat.format(dateFrom);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateFrom = inputFormat1.parse(start);
					start = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getInventoriesReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			try {
				dateTo = inputFormat.parse(end);
				end = outputFormat.format(dateTo);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateTo = inputFormat1.parse(end);
					end = outputFormat.format(dateTo);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getInventoriesReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}

			Date today=new Date();

			if(dateFrom.getTime() > dateTo.getTime()) {
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
				logger.info("************************ getInventoriesReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
				logger.info("************************ getInventoriesReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		List<InventoryLastData> data = new ArrayList<InventoryLastData>();
		Integer size=0;

		if(exportData.equals("exportData")) {
			data = mongoInventoryLastDataRepo.getInventoriesReportSchedule(allInventories, dateFrom, dateTo);

			if(data.size()>0) {
				for(int i=0;i<data.size();i++) {
					
					Inventory inventory = inventoryRepository.findOne(data.get(i).getInventory_id());
					if(inventory != null) {
						data.get(i).setInventoryName(inventory.getName());

					}
				}
					
			}
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data,size);
			logger.info("************************ getInventoriesReport ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
		}
		
		if(!TOKEN.equals("Schedule")) {
			data = mongoInventoryLastDataRepo.getInventoriesReport(allInventories, offset, dateFrom, dateTo);
			
			if(data.size()>0) {
				size= mongoInventoryLastDataRepo.getInventoriesReportSize(allInventories,dateFrom, dateTo);
				for(int i=0;i<data.size();i++) {
					
					Inventory inventory = inventoryRepository.findOne(data.get(i).getInventory_id());
					if(inventory != null) {
						data.get(i).setInventoryName(inventory.getName());

					}
				}
					
			}
			
		}
		else {
			data = mongoInventoryLastDataRepo.getInventoriesReportSchedule(allInventories, dateFrom, dateTo);
			if(data.size()>0) {
				for(int i=0;i<data.size();i++) {
					
					Inventory inventory = inventoryRepository.findOne(data.get(i).getInventory_id());
					if(inventory != null) {
						data.get(i).setInventoryName(inventory.getName());

					}
				}
					
			}

		}
		
		getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data,size);
		logger.info("************************ getInventoriesReport ENDED ***************************");
		return  ResponseEntity.ok().body(getObjectResponse);
	}

	@Override
	public ResponseEntity<?> getWarehousesReport(String TOKEN, Long[] warehouseIds, int offset, String start, String end,
			String search, Long userId,String exportData) {
		
		logger.info("************************ getWarehousesReport STARTED ***************************");		
		List<InventoryLastData> inventoriesReport = new ArrayList<InventoryLastData>();
		if(TOKEN.equals("")) {
			
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",inventoriesReport);
				logger.info("************************ getWarehousesReport ENDED ***************************");		
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		
		if(!TOKEN.equals("Schedule")) {
			if(super.checkActive(TOKEN)!= null)
			{
				return super.checkActive(TOKEN);
			}
		}
		
		
		User loggedUser = new User();
		if(userId != 0) {
			
			loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",inventoriesReport);
				logger.info("************************ getWarehousesReport ENDED ***************************");		
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
		}	
		
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "WAREHOUSETEMPHUMD", "list")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get inventoriesReport list",inventoriesReport);
					logger.info("************************ getWarehousesReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
		
		List<Long>allInventories= new ArrayList<>();
		List<Long>allWarehouses= new ArrayList<>();

		if(warehouseIds.length != 0 ) {
			for(Long warehouseId:warehouseIds) {
				if(warehouseId !=0) {
					Warehouse warehouse =warehousesRepository.findOne(warehouseId);
					if(warehouse != null) {
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
						if(isParent == false) {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get warehouses",null);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						allWarehouses.add(warehouseId);

					}
					
				}
			}
		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Warehouse is not found",inventoriesReport);
			logger.info("************************ getWarehousesReport ENDED ***************************");		
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(allWarehouses.isEmpty()) {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Warehouse is not found",inventoriesReport);
			logger.info("************************ getWarehousesReport ENDED ***************************");		
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		else {
			allInventories = inventoryRepository.getAllInventoriesOfWarehouse(allWarehouses);
			if(allInventories.isEmpty()) {
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No Inventories for those Warehouses",inventoriesReport);
				logger.info("************************ getWarehousesReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
		
		Date dateFrom;
		Date dateTo;
		if(start.equals("0") || end.equals("0")) {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",null);
			logger.info("************************ getWarehousesReport ENDED ***************************");
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		else {

			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			inputFormat1.setLenient(false);
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);

			
			try {
				dateFrom = inputFormat.parse(start);
				start = outputFormat.format(dateFrom);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateFrom = inputFormat1.parse(start);
					start = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getWarehousesReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			try {
				dateTo = inputFormat.parse(end);
				end = outputFormat.format(dateTo);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateTo = inputFormat1.parse(end);
					end = outputFormat.format(dateTo);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getWarehousesReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			
			
			
			Date today=new Date();

			if(dateFrom.getTime() > dateTo.getTime()) {
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
				logger.info("************************ getWarehousesReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
				logger.info("************************ getWarehousesReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}

		}
		List<InventoryLastData> data = new ArrayList<InventoryLastData>();
		Integer size=0;
		
		
		if(exportData.equals("exportData")) {
			data = mongoInventoryLastDataRepo.getInventoriesReportSchedule(allInventories, dateFrom, dateTo);

			if(data.size()>0) {
				for(int i=0;i<data.size();i++) {
					
					Inventory inventory = inventoryRepository.findOne(data.get(i).getInventory_id());
					if(inventory != null) {
						data.get(i).setInventoryName(inventory.getName());
						Warehouse war = warehousesRepository.findOne(inventory.getWarehouseId());
						data.get(i).setWarehouseId(war.getId());
						data.get(i).setWarehouseName(war.getName());
					}
				}
					
			}
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data,size);
			logger.info("************************ getInventoriesReport ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
		}
		
		if(!TOKEN.equals("Schedule")) {
			data = mongoInventoryLastDataRepo.getInventoriesReport(allInventories, offset, dateFrom, dateTo);
			
			if(data.size()>0) {
				size= mongoInventoryLastDataRepo.getInventoriesReportSize(allInventories,dateFrom, dateTo);
				for(int i=0;i<data.size();i++) {
					
					Inventory inventory = inventoryRepository.findOne(data.get(i).getInventory_id());
					if(inventory != null) {
						data.get(i).setInventoryName(inventory.getName());
						Warehouse war = warehousesRepository.findOne(inventory.getWarehouseId());
						data.get(i).setWarehouseId(war.getId());
						data.get(i).setWarehouseName(war.getName());
					}
				}
					
			}
			
		}
		else {
			data = mongoInventoryLastDataRepo.getInventoriesReportSchedule(allInventories, dateFrom, dateTo);
			if(data.size()>0) {
				for(int i=0;i<data.size();i++) {
					
					Inventory inventory = inventoryRepository.findOne(data.get(i).getInventory_id());
					if(inventory != null) {
						data.get(i).setInventoryName(inventory.getName());
						Warehouse war = warehousesRepository.findOne(inventory.getWarehouseId());
						data.get(i).setWarehouseId(war.getId());
						data.get(i).setWarehouseName(war.getName());
					}
				}
					
			}

		}
		
		getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data,size);
		logger.info("************************ getInventoriesReport ENDED ***************************");
		return  ResponseEntity.ok().body(getObjectResponse);
		
		

	}

	@Override
	public ResponseEntity<?> getNotificationReportNew(String TOKEN, Long[] inventoryIds, Long[] warehouseIds, int offset,
			String start, String end, String search, Long userId,String exportData) {
		logger.info("************************ getInventoriesReport STARTED ***************************");		
		List<InventoryLastData> inventoriesReport = new ArrayList<>();
		if(TOKEN.equals("")) {
			
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",inventoriesReport);
				logger.info("************************ getInventoriesReport ENDED ***************************");		
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		
		if(!TOKEN.equals("Schedule")) {
			if(super.checkActive(TOKEN)!= null)
			{
				return super.checkActive(TOKEN);
			}
		}
		
		
		User loggedUser = new User();
		if(userId != 0) {
			
			loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",inventoriesReport);
				logger.info("************************ getInventoriesReport ENDED ***************************");		
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
		}	
		
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "NOTIFICATIONTEMPHUMD", "list")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get inventoriesReport list",inventoriesReport);
					logger.info("************************ getInventoriesReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
		
		List<Long>allInventories= new ArrayList<>();
		
		if(inventoryIds.length != 0 ) {
			for(Long inventoryId:inventoryIds) {
				if(inventoryId !=0) {
					Inventory inventory =inventoryRepository.findOne(inventoryId);
					if(inventory != null) {
						
						Long createdBy=inventory.getUserId();
						Boolean isParent=false;

						if(createdBy.toString().equals(userId.toString())) {
							isParent=true;
						}
						List<User>childs;
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
						
						allInventories.add(inventoryId);
					}
					
				}
			}
		}
		
		List<Long>allWarehouses= new ArrayList<>();

		if(warehouseIds.length != 0 ) {
			for(Long warehouseId:warehouseIds) {
				if(warehouseId !=0) {
					Warehouse warehouse =warehousesRepository.findOne(warehouseId);
					if(warehouse != null) {
						Long createdBy=warehouse.getUserId();
						Boolean isParent=false;

						if(createdBy.toString().equals(userId.toString())) {
							isParent=true;
						}
						
						List<User>childs;
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
						if(isParent == false) {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get warehouses",null);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						allWarehouses.add(warehouseId);

					}
					
				}
			}
		}
		
		if(!allWarehouses.isEmpty()) {
			allInventories.addAll(inventoryRepository.getAllInventoriesOfWarehouse(allWarehouses));
			
		}

		Date dateFrom;
		Date dateTo;
		if(start.equals("0") || end.equals("0")) {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",null);
			logger.info("************************ getInventoriesReport ENDED ***************************");
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		else {

			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			inputFormat1.setLenient(false);
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);

			
			try {
				dateFrom = inputFormat.parse(start);
				start = outputFormat.format(dateFrom);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateFrom = inputFormat1.parse(start);
					start = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getInventoriesReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			try {
				dateTo = inputFormat.parse(end);
				end = outputFormat.format(dateTo);
				

			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateTo = inputFormat1.parse(end);
					end = outputFormat.format(dateTo);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getInventoriesReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
			
			
			
			Date today=new Date();

			if(dateFrom.getTime() > dateTo.getTime()) {
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
				logger.info("************************ getInventoriesReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
				logger.info("************************ getInventoriesReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}

		}
		
	
		
		List<NewInventoryNotificationResponse> data = new ArrayList<>();
		Integer size=0;
		
		
		if(exportData.equals("exportData")) {
//			data = mongoInventoryNotificationRepo.getNotificationsReportSchedule(allInventories, dateFrom, dateTo);
//			if(data.size()>0) {
//				for(int i=0;i<data.size();i++) {
//					Inventory inventory = inventoryRepository.findOne(data.get(i).getInventory_id());
//					if(inventory != null) {
//						data.get(i).setInventoryName(inventory.getName());
//						Warehouse war = warehousesRepository.findOne(inventory.getWarehouseId());
//						data.get(i).setWarehouseId(war.getId());
//						data.get(i).setWarehouseName(war.getName());
//					}
//				}
//			}
			List<InventoriesAndWarehousesWrapper> inventorisAndWarehousesList  =inventoryRepository.getAllInventoriesAndWarehouses(inventoryIds);
			InventoriesAndWarehousesWrapper inv = inventorisAndWarehousesList.get(0);
			List<NotificationWrapper> noti = mongoInventoryNotificationRepository
					.findAllByInventoryIdAndCreatedDateBetween(inventoryIds[0],dateFrom,dateTo);
			for(NotificationWrapper notificationWrapper : noti){
				if(notificationWrapper.getType().equals("temperature alarm")){
					data.add(NewInventoryNotificationResponse
							.builder()
							._id(notificationWrapper.get_id().toString())
							.type(notificationWrapper.getType())
							.create_date(notificationWrapper.getCreatedDate().toString())
							.value(notificationWrapper.getAttributes().getValue())
							.inventory_id(notificationWrapper.getInventoryId())
							.attributes(notificationWrapper.getAttributes())
									.inventoryName(inv.getInventoryName())
									.warehouseName(inv.getWarehouseName())
							.build());
				}else {
					data.add(NewInventoryNotificationResponse
							.builder()
							._id(notificationWrapper.get_id().toString())
							.type(notificationWrapper.getType())
							.create_date(notificationWrapper.getCreatedDate().toString())
							.value(notificationWrapper.getAttributes().getValue())
							.inventory_id(notificationWrapper.getInventoryId())
							.attributes(notificationWrapper.getAttributes())
							.inventoryName(inv.getInventoryName())
							.warehouseName(inv.getWarehouseName())
							.build());
				}

			}

			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data,size);
			logger.info("************************ getInventoriesReport ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
		}
		
		if(!TOKEN.equals("Schedule")) {
			data = mongoInventoryNotificationRepo.newGetNotificationsReport(allInventories, offset, dateFrom, dateTo);
			if(data.size()>0) {
				size= mongoInventoryNotificationRepo.getNotificationsReportSize(allInventories,dateFrom, dateTo);
				for(int i=0;i<data.size();i++) {
					
					Inventory inventory = inventoryRepository.findOne(data.get(i).getInventory_id());
					if(inventory != null) {
						data.get(i).setInventoryName(inventory.getName());
						Warehouse war = warehousesRepository.findOne(inventory.getWarehouseId());
						data.get(i).setWarehouseId(war.getId());
						data.get(i).setWarehouseName(war.getName());
					}
				}
					
			}
			
		}
		else {
			data = mongoInventoryNotificationRepo.newGetNotificationsReportSchedule(allInventories, dateFrom, dateTo);
			if(data.size()>0) {
				for(int i=0;i<data.size();i++) {
					
					Inventory inventory = inventoryRepository.findOne(data.get(i).getInventory_id());
					if(inventory != null) {
						data.get(i).setInventoryName(inventory.getName());
						Warehouse war = warehousesRepository.findOne(inventory.getWarehouseId());
						data.get(i).setWarehouseId(war.getId());
						data.get(i).setWarehouseName(war.getName());
					}
				}
					
			}

		}
		
		getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data,size);
		logger.info("************************ getInventoriesReport ENDED ***************************");
		return  ResponseEntity.ok().body(getObjectResponse);
	}




	@Override
	public ResponseEntity<?> getNotificationReport(String TOKEN, Long[] inventoryIds, Long[] warehouseIds, int offset,
												   String start, String end, String search, Long userId,String exportData) {
		logger.info("************************ getInventoriesReport STARTED ***************************");
		List<InventoryLastData> inventoriesReport = new ArrayList<InventoryLastData>();
		if(TOKEN.equals("")) {

			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",inventoriesReport);
			logger.info("************************ getInventoriesReport ENDED ***************************");
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}


		if(!TOKEN.equals("Schedule")) {
			if(super.checkActive(TOKEN)!= null)
			{
				return super.checkActive(TOKEN);
			}
		}


		User loggedUser = new User();
		if(userId != 0) {

			loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",inventoriesReport);
				logger.info("************************ getInventoriesReport ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
		}

		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "NOTIFICATIONTEMPHUMD", "list")) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get inventoriesReport list",inventoriesReport);
				logger.info("************************ getInventoriesReport ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}



		List<Long>allInventories= new ArrayList<>();

		if(inventoryIds.length != 0 ) {
			for(Long inventoryId:inventoryIds) {
				if(inventoryId !=0) {
					Inventory inventory =inventoryRepository.findOne(inventoryId);
					if(inventory != null) {

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

						allInventories.add(inventoryId);
					}

				}
			}
		}

		List<Long>allWarehouses= new ArrayList<>();

		if(warehouseIds.length != 0 ) {
			for(Long warehouseId:warehouseIds) {
				if(warehouseId !=0) {
					Warehouse warehouse =warehousesRepository.findOne(warehouseId);
					if(warehouse != null) {
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
						if(isParent == false) {
							getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get warehouses",null);
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}
						allWarehouses.add(warehouseId);

					}

				}
			}
		}

		if(!allWarehouses.isEmpty()) {
			allInventories.addAll(inventoryRepository.getAllInventoriesOfWarehouse(allWarehouses));

		}

		Date dateFrom;
		Date dateTo;
		if(start.equals("0") || end.equals("0")) {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",null);
			logger.info("************************ getInventoriesReport ENDED ***************************");
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		else {

			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			inputFormat1.setLenient(false);
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);


			try {
				dateFrom = inputFormat.parse(start);
				start = outputFormat.format(dateFrom);


			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateFrom = inputFormat1.parse(start);
					start = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block

					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getInventoriesReport ENDED ***************************");
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}

			}

			try {
				dateTo = inputFormat.parse(end);
				end = outputFormat.format(dateTo);


			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateTo = inputFormat1.parse(end);
					end = outputFormat.format(dateTo);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getInventoriesReport ENDED ***************************");
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}

			}




			Date today=new Date();

			if(dateFrom.getTime() > dateTo.getTime()) {
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
				logger.info("************************ getInventoriesReport ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
				logger.info("************************ getInventoriesReport ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}

		}



		List<InventoryNotification> data;
		Integer size=0;


		if(exportData.equals("exportData")) {
			data = mongoInventoryNotificationRepo.getNotificationsReportSchedule(allInventories, dateFrom, dateTo);
			if(data.size()>0) {
				for(int i=0;i<data.size();i++) {
					Inventory inventory = inventoryRepository.findOne(data.get(i).getInventory_id());
					if(inventory != null) {
						data.get(i).setInventoryName(inventory.getName());
						Warehouse war = warehousesRepository.findOne(inventory.getWarehouseId());
						data.get(i).setWarehouseId(war.getId());
						data.get(i).setWarehouseName(war.getName());
					}
				}
			}

			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data,size);
			logger.info("************************ getInventoriesReport ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
		}

		if(!TOKEN.equals("Schedule")) {
			data = mongoInventoryNotificationRepo.getNotificationsReport(allInventories, offset, dateFrom, dateTo);
			if(data.size()>0) {
				size= mongoInventoryNotificationRepo.getNotificationsReportSize(allInventories,dateFrom, dateTo);
				for(int i=0;i<data.size();i++) {

					Inventory inventory = inventoryRepository.findOne(data.get(i).getInventory_id());
					if(inventory != null) {
						data.get(i).setInventoryName(inventory.getName());
						Warehouse war = warehousesRepository.findOne(inventory.getWarehouseId());
						data.get(i).setWarehouseId(war.getId());
						data.get(i).setWarehouseName(war.getName());
					}
				}

			}

		}
		else {
			data = mongoInventoryNotificationRepo.getNotificationsReportSchedule(allInventories, dateFrom, dateTo);
			if(data.size()>0) {
				for(int i=0;i<data.size();i++) {

					Inventory inventory = inventoryRepository.findOne(data.get(i).getInventory_id());
					if(inventory != null) {
						data.get(i).setInventoryName(inventory.getName());
						Warehouse war = warehousesRepository.findOne(inventory.getWarehouseId());
						data.get(i).setWarehouseId(war.getId());
						data.get(i).setWarehouseName(war.getName());
					}
				}

			}

		}

		getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",data,size);
		logger.info("************************ getInventoriesReport ENDED ***************************");
		return  ResponseEntity.ok().body(getObjectResponse);
	}

	@Override
	public ResponseEntity<?> getVehicleTempHum(String TOKEN, Long[] deviceIds, Long[] groupIds, int offset, String start,
			String end, String search, Long userId, String exportData, String timeOffset) {
		 logger.info("************************ getSensorsReport STARTED ***************************");

			List<DeviceTempHum> positionsList = new ArrayList<DeviceTempHum>();
			if(TOKEN.equals("")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",positionsList);
				 logger.info("************************ getSensorsReport ENDED ***************************");
				 return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			
			
			if(!TOKEN.equals("Schedule")) {
				if(super.checkActive(TOKEN)!= null)
				{
					return super.checkActive(TOKEN);
				}
			}
			
			
			User loggedUser = new User();
			if(userId != 0) {
				
				loggedUser = userServiceImpl.findById(userId);
				if(loggedUser == null) {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",positionsList);
					 logger.info("************************ getSensorsReport ENDED ***************************");
					return  ResponseEntity.status(404).body(getObjectResponse);
				}
			}	
			
			if(!loggedUser.getAccountType().equals(1)) {
				if(!userRoleService.checkUserHasPermission(userId, "SENSORWEIGHT", "list")) {
					 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get SENSORWEIGHT list",positionsList);
					 logger.info("************************ getSensorsReport ENDED ***************************");
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
			}
			
			
			
			List<Long>allDevices= new ArrayList<>();

			if(groupIds.length != 0) {
				for(Long groupId:groupIds) {
					if(groupId != 0) {
				    	Group group=groupRepository.findOne(groupId);
				    	if(group != null) {
							if(group.getIs_deleted() == null) {
								boolean isParent = false;
								if(loggedUser.getAccountType().equals(4)) {
									Set<User> clientParents = loggedUser.getUsersOfUser();
									if(clientParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",positionsList);
										 logger.info("************************ getSensorsReport ENDED ***************************"); 
										return  ResponseEntity.badRequest().body(getObjectResponse);
									}else {
										User parent = null;
										for(User object : clientParents) {
											parent = object ;
										}

										Set<User>groupParents = group.getUserGroup();
										if(groupParents.isEmpty()) {
											getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",positionsList);
											 logger.info("************************ getSensorsReport ENDED ***************************");
											return  ResponseEntity.badRequest().body(getObjectResponse);
										}else {
											for(User parentObject : groupParents) {
												if(parentObject.getId().equals(parent.getId())) {
													isParent = true;
													break;
												}
											}
										}
									}
									List<Long> CheckData = userClientGroupRepository.getGroup(userId,groupId);
									if(CheckData.isEmpty()) {
											isParent = false;
									}
									else {
											isParent = true;
									}
								}
								if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
									getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",positionsList);
									 logger.info("************************ getSensorsReport ENDED ***************************");
									return ResponseEntity.badRequest().body(getObjectResponse);
								}
								if(group.getType() != null) {
									if(group.getType().equals("driver")) {
										
										allDevices.addAll(groupRepository.getDevicesFromDriver(groupId));
									

									}
									else if(group.getType().equals("device")) {
										
										allDevices.addAll(groupRepository.getDevicesFromGroup(groupId));
										
										
									}
									else if(group.getType().equals("geofence")) {
										
										allDevices.addAll(groupRepository.getDevicesFromGeofence(groupId));
										

									}
								}

								
							}
				    	}
				    	

					}
				}
			}
			if(deviceIds.length != 0 ) {
				for(Long deviceId:deviceIds) {
					if(deviceId !=0) {
						Device device =deviceServiceImpl.findById(deviceId);
						boolean isParent = false;
						if(loggedUser.getAccountType() == 4) {
							Set<User>parentClients = loggedUser.getUsersOfUser();
							if(parentClients.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",positionsList);
								 logger.info("************************ getSensorsReport ENDED ***************************");
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								User parent = null;
								for(User object : parentClients) {
									parent = object ;
								}
								Set<User>deviceParent = device.getUser();
								if(deviceParent.isEmpty()) {
									getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",positionsList);
									 logger.info("************************ getSensorsReport ENDED ***************************");
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									for(User  parentObject : deviceParent) {
										if(parent.getId() == parentObject.getId()) {
											isParent = true;
											break;
										}
									}
								}
							}
							List<Long> CheckData = userClientDeviceRepository.getDevice(userId,deviceId);
							if(CheckData.isEmpty()) {
									isParent = false;
							}
							else {
									isParent = true;
							}
						}
						if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this device",positionsList);
							 logger.info("************************ getSensorsReport ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}
						
						allDevices.add(deviceId);

						
		
					}
				}
			}

			Date dateFrom;
			Date dateTo;
			if(start.equals("0") || end.equals("0")) {
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",null);
				logger.info("************************ getEventsReport ENDED ***************************");		
				return  ResponseEntity.badRequest().body(getObjectResponse);

			}
			else {
				SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
				SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
				SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
				inputFormat1.setLenient(false);
				inputFormat.setLenient(false);
				outputFormat.setLenient(false);

				
				try {
					TimeZone etTimeZone = TimeZone.getTimeZone("UTC");
					inputFormat.setTimeZone(etTimeZone);
					dateFrom = inputFormat.parse(start);
					start = outputFormat.format(dateFrom);
					

				} catch (ParseException e2) {
					// TODO Auto-generated catch block
					try {
						TimeZone etTimeZone = TimeZone.getTimeZone("UTC");
						inputFormat.setTimeZone(etTimeZone);
						dateFrom = inputFormat1.parse(start);
						start = outputFormat.format(dateFrom);

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
						logger.info("************************ getEventsReport ENDED ***************************");		
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
					
				}
				
				try {
					TimeZone etTimeZone = TimeZone.getTimeZone("UTC");
					inputFormat.setTimeZone(etTimeZone);
					dateTo = inputFormat.parse(end);
					end = outputFormat.format(dateTo);
					

				} catch (ParseException e2) {
					// TODO Auto-generated catch block
					try {
						TimeZone etTimeZone = TimeZone.getTimeZone("UTC");
						inputFormat.setTimeZone(etTimeZone);
						dateTo = inputFormat1.parse(end);
						end = outputFormat.format(dateTo);

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
						logger.info("************************ getEventsReport ENDED ***************************");		
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
					
				}
				
				
				
				
				Date today=new Date();

				if(dateFrom.getTime() > dateTo.getTime()) {
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
//				if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
//					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
//					logger.info("************************ getEventsReport ENDED ***************************");
//					return  ResponseEntity.badRequest().body(getObjectResponse);
//				}
				
				search = "%"+search+"%";				
				
				String appendString="";
		
				if(allDevices.size()>0) {
					  for(int i=0;i<allDevices.size();i++) {
						  if(appendString != "") {
							  appendString +=","+allDevices.get(i);
						  }
						  else {
							  appendString +=allDevices.get(i);
						  }
					  }
				 }
				allDevices = new ArrayList<Long>();
				
				String[] data = {};
				if(!appendString.equals("")) {
			        data = appendString.split(",");

				}
		        

		        for(String d:data) {

		        	if(!allDevices.contains(Long.parseLong(d))) {
			        	allDevices.add(Long.parseLong(d));
		        	}
		        }
		        
		        if(allDevices.isEmpty()) {

		        	getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no data for devices of group or devices that you selected ",positionsList);
					 logger.info("************************ getSensorsReport ENDED ***************************");
		        	return  ResponseEntity.badRequest().body(getObjectResponse);
		        }
			}
			Integer size = 0;
			List<MongoPositions> mongoPositionsList;
			List<DeviceTempHum> deviceTempHumList = new ArrayList<>();

			if(exportData.equals("exportData")) {
//				positionsList = mongoPositionRepoSFDA.getVehicleTempHumListScheduled(allDevices,dateFrom, dateTo);
				mongoPositionsList = mongoPositionsRepository.
						findAllByDeviceidAndDevicetimeBetweenOrderByDevicetimeDesc(deviceIds[0],dateFrom, dateTo);
				deviceTempHumList = reportsHelper.deviceTempAndHumProcessHandler(mongoPositionsList, timeOffset);
				getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",deviceTempHumList,size);
				logger.info("************************ getSensorsReport ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);
				
			}
			if(!TOKEN.equals("Schedule")) {
				search = "%"+search+"%";
				int limit = 10;
				Pageable pageable = new PageRequest(offset, limit);
//				positionsList = mongoPositionRepoSFDA.getVehicleTempHumList(allDevices, offset, dateFrom, dateTo);
				mongoPositionsList = mongoPositionsRepository.
						findAllByDeviceidAndDevicetimeBetweenOrderByDevicetimeDesc(deviceIds[0],dateFrom, dateTo, pageable);
				deviceTempHumList = reportsHelper.deviceTempAndHumProcessHandler(mongoPositionsList, timeOffset);
				if(mongoPositionsList.size()>0) {
//					size=mongoPositionRepoSFDA.getVehicleTempHumListSize(allDevices,dateFrom, dateTo);
					size = mongoPositionsRepository.countAllByDeviceidAndDevicetimeBetween(deviceIds[0],dateFrom, dateTo);
				}
			}
			else {
				positionsList = mongoPositionRepoSFDA.getVehicleTempHumListScheduled(allDevices,dateFrom, dateTo);
			}
			
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",deviceTempHumList,size);
			logger.info("************************ getSensorsReport ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
	}




	@Override
	public ResponseEntity<?> getVehicleTempHumNew(String TOKEN, Long[] deviceIds, Long[] groupIds, int offset, String start,
											   String end, String search, Long userId, String exportData) {
		logger.info("************************ getSensorsReport STARTED ***************************");
		PositionMapper positionMapper = new PositionMapper(deviceRepositorySFDA);
		int pageSize = 10;
		int page = offset;
		List<Position> positionsList = new ArrayList<>();
		List<PositionResponse> positionResponsesList = new ArrayList<>();
		if(TOKEN.equals("")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",positionsList);
			logger.info("************************ getSensorsReport ENDED ***************************");
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}


		if(!TOKEN.equals("Schedule")) {
			if(super.checkActive(TOKEN)!= null)
			{
				return super.checkActive(TOKEN);
			}
		}


		User loggedUser = new User();
		if(userId != 0) {

			loggedUser = userServiceImpl.findById(userId);
			if(loggedUser == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",positionsList);
				logger.info("************************ getSensorsReport ENDED ***************************");
				return  ResponseEntity.status(404).body(getObjectResponse);
			}
		}

		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "SENSORWEIGHT", "list")) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get SENSORWEIGHT list",positionsList);
				logger.info("************************ getSensorsReport ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}



		List<Long>allDevices= new ArrayList<>();

		if(groupIds.length != 0) {
			for(Long groupId:groupIds) {
				if(groupId != 0) {
					Group group=groupRepository.findOne(groupId);
					if(group != null) {
						if(group.getIs_deleted() == null) {
							boolean isParent = false;
							if(loggedUser.getAccountType().equals(4)) {
								Set<User> clientParents = loggedUser.getUsersOfUser();
								if(clientParents.isEmpty()) {
									getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",positionsList);
									logger.info("************************ getSensorsReport ENDED ***************************");
									return  ResponseEntity.badRequest().body(getObjectResponse);
								}else {
									User parent = null;
									for(User object : clientParents) {
										parent = object ;
									}

									Set<User>groupParents = group.getUserGroup();
									if(groupParents.isEmpty()) {
										getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group",positionsList);
										logger.info("************************ getSensorsReport ENDED ***************************");
										return  ResponseEntity.badRequest().body(getObjectResponse);
									}else {
										for(User parentObject : groupParents) {
											if(parentObject.getId().equals(parent.getId())) {
												isParent = true;
												break;
											}
										}
									}
								}
								List<Long> CheckData = userClientGroupRepository.getGroup(userId,groupId);
								if(CheckData.isEmpty()) {
									isParent = false;
								}
								else {
									isParent = true;
								}
							}
							if(!groupsServiceImpl.checkIfParent(group , loggedUser) && ! isParent) {
								getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this group ",positionsList);
								logger.info("************************ getSensorsReport ENDED ***************************");
								return ResponseEntity.badRequest().body(getObjectResponse);
							}
							if(group.getType() != null) {
								if(group.getType().equals("driver")) {
									allDevices.addAll(groupRepository.getDevicesFromDriver(groupId));
								}
								else if(group.getType().equals("device")) {
									allDevices.addAll(groupRepository.getDevicesFromGroup(groupId));
								}
								else if(group.getType().equals("geofence")) {
									allDevices.addAll(groupRepository.getDevicesFromGeofence(groupId));
								}
							}
						}
					}


				}
			}
		}
		if(deviceIds.length != 0 ) {
			for(Long deviceId:deviceIds) {
				if(deviceId !=0) {
					Device device =deviceServiceImpl.findById(deviceId);
					boolean isParent = false;
					if(loggedUser.getAccountType() == 4) {
						Set<User>parentClients = loggedUser.getUsersOfUser();
						if(parentClients.isEmpty()) {
							getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",positionsList);
							logger.info("************************ getSensorsReport ENDED ***************************");
							return  ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							User parent = null;
							for(User object : parentClients) {
								parent = object ;
							}
							Set<User>deviceParent = device.getUser();
							if(deviceParent.isEmpty()) {
								getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is not allwed to get data of this device ",positionsList);
								logger.info("************************ getSensorsReport ENDED ***************************");
								return  ResponseEntity.badRequest().body(getObjectResponse);
							}else {
								for(User  parentObject : deviceParent) {
									if(parent.getId() == parentObject.getId()) {
										isParent = true;
										break;
									}
								}
							}
						}
						List<Long> CheckData = userClientDeviceRepository.getDevice(userId,deviceId);
						if(CheckData.isEmpty()) {
							isParent = false;
						}
						else {
							isParent = true;
						}
					}
					if(!deviceServiceImpl.checkIfParent(device , loggedUser) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to get this device",positionsList);
						logger.info("************************ getSensorsReport ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
					allDevices.add(deviceId);
				}
			}
		}

		Date dateFrom;
		Date dateTo;
		if(start.equals("0") || end.equals("0")) {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Date start and end is Required",null);
			logger.info("************************ getEventsReport ENDED ***************************");
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		else {
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			inputFormat1.setLenient(false);
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);


			try {
				dateFrom = inputFormat.parse(start);
				start = outputFormat.format(dateFrom);


			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateFrom = inputFormat1.parse(start);
					start = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block

					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}

			}

			try {
				dateTo = inputFormat.parse(end);
				end = outputFormat.format(dateTo);
			} catch (ParseException e2) {
				// TODO Auto-generated catch block
				try {
					dateTo = inputFormat1.parse(end);
					end = outputFormat.format(dateTo);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
			}

			Date today=new Date();

			if(dateFrom.getTime() > dateTo.getTime()) {
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date should be Earlier than End Date",null);
				logger.info("************************ getEventsReport ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			if(today.getTime()<dateFrom.getTime() || today.getTime()<dateTo.getTime() ){
				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start Date and End Date should be Earlier than Today",null);
				logger.info("************************ getEventsReport ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}

			search = "%"+search+"%";

			String appendString="";

			if(allDevices.size()>0) {
				for(int i=0;i<allDevices.size();i++) {
					if(appendString != "") {
						appendString +=","+allDevices.get(i);
					}
					else {
						appendString +=allDevices.get(i);
					}
				}
			}
			allDevices = new ArrayList<Long>();

			String[] data = {};
			if(!appendString.equals("")) {
				data = appendString.split(",");

			}


			for(String d:data) {
				if(!allDevices.contains(Long.parseLong(d))) {
					allDevices.add(Long.parseLong(d));
				}
			}

			if(allDevices.isEmpty()) {

				getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no data for devices of group or devices that you selected ",positionsList);
				logger.info("************************ getSensorsReport ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		Integer size = 0;

		if(exportData.equals("exportData")) {
			// First Attack
//			positionsList = mongoPositionRepoSFDA.getVehicleTempHumListScheduled(allDevices,dateFrom, dateTo);

			for(long id : allDevices){
				List<Position> positions = positionMongoSFDARepository.
						findAllByDeviceidAndDevicetimeBetweenOrderByDevicetimeDesc(id ,dateFrom,dateTo ,new PageRequest(page, pageSize));
				for(Position position : positions){
					positionResponsesList.add(positionMapper.convertToResponse(position));
				}

				size+=positionMongoSFDARepository.countAllByDeviceidAndDevicetimeBetween(id ,dateFrom,dateTo );
			}
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),
					"success",
					positionResponsesList,
					size);
			logger.info("************************ getSensorsReport ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);

		}
		if(!TOKEN.equals("Schedule")) {
			search = "%"+search+"%";
			// Second Attack
//			positionsList = mongoPositionRepoSFDA.getVehicleTempHumList(allDevices, offset, dateFrom, dateTo);
			for(long id : allDevices){
				List<Position> positions = positionMongoSFDARepository.
						findAllByDeviceidAndDevicetimeBetweenOrderByDevicetimeDesc(id ,dateFrom,dateTo ,new PageRequest(page, pageSize));
				for(Position position : positions){
					positionResponsesList.add(positionMapper.convertToResponse(position));
				}
				size+=positionMongoSFDARepository.countAllByDeviceidAndDevicetimeBetween(id ,dateFrom,dateTo );
			}

		}
		else {
			// Third Attack
//			positionsList = mongoPositionRepoSFDA.getVehicleTempHumListScheduled(allDevices,dateFrom, dateTo);
			for(long id : allDevices){
				List<Position> positions = positionMongoSFDARepository.
						findAllByDeviceidAndDevicetimeBetweenOrderByDevicetimeDesc(id ,dateFrom,dateTo,new PageRequest(page, pageSize));
				for(Position position : positions){
					positionResponsesList.add(positionMapper.convertToResponse(position));
				}
				size+=positionMongoSFDARepository.countAllByDeviceidAndDevicetimeBetween(id ,dateFrom,dateTo );
			}

		}

		getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",positionResponsesList,size);
		logger.info("************************ getSensorsReport ENDED ***************************");
		return  ResponseEntity.ok().body(getObjectResponse);
	}

	@Override
	public ResponseEntity<?> getviewTripDetails(String TOKEN, Long deviceId, String from, String to,String exportData,int offset, String timeOffset) {
		// TODO Auto-generated method stub
		

		logger.info("************************ getviewTrip STARTED ***************************");

		List<DeviceTempHum> positions = new ArrayList<DeviceTempHum>();
		List<MongoPositions> mongoPositions = new ArrayList<>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",positions);
				logger.info("************************ getviewTrip ENDED ***************************");
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
//			return super.checkActive(TOKEN);
		}
		

		Date dateFrom;
		Date dateTo;

		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		SimpleDateFormat inputFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS SSSS");
		SimpleDateFormat inputFormat1 = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");
		inputFormat1.setLenient(false);
		inputFormat.setLenient(false);
		outputFormat.setLenient(false);

		
		try {
			TimeZone etTimeZone = TimeZone.getTimeZone("UTC");
			inputFormat.setTimeZone(etTimeZone);
			outputFormat.setTimeZone(etTimeZone);
			dateFrom = inputFormat2.parse(from);
			from = outputFormat.format(dateFrom);
			

		} catch (ParseException e2) {
			try {
				TimeZone etTimeZone = TimeZone.getTimeZone("UTC");
				inputFormat.setTimeZone(etTimeZone);
				outputFormat.setTimeZone(etTimeZone);
				dateFrom = inputFormat.parse(from);
				from = outputFormat.format(dateFrom);

			} catch (ParseException e3) {
				// TODO Auto-generated catch block
				try {
					TimeZone etTimeZone = TimeZone.getTimeZone("UTC");
					inputFormat.setTimeZone(etTimeZone);
					outputFormat.setTimeZone(etTimeZone);
					dateFrom = inputFormat1.parse(from);
					from = outputFormat.format(dateFrom);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z' or yyyy-MM-dd'T'HH:mm:ss.SSS SSSS",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
		}
		
		
		try {
			TimeZone etTimeZone = TimeZone.getTimeZone("UTC");
			inputFormat2.setTimeZone(etTimeZone);
			outputFormat.setTimeZone(etTimeZone);
			dateTo = inputFormat2.parse(to);
			to = outputFormat.format(dateTo);
			

		} catch (ParseException e2) {
			try {
				TimeZone etTimeZone = TimeZone.getTimeZone("UTC");
				inputFormat.setTimeZone(etTimeZone);
				outputFormat.setTimeZone(etTimeZone);
				dateTo = inputFormat.parse(to);
				to = outputFormat.format(dateTo);
				

			} catch (ParseException e3) {
				// TODO Auto-generated catch block
				try {
					TimeZone etTimeZone = TimeZone.getTimeZone("UTC");
					inputFormat1.setTimeZone(etTimeZone);
					dateTo = inputFormat1.parse(to);
					to = outputFormat.format(dateTo);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
					logger.info("************************ getEventsReport ENDED ***************************");		
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				
			}
			
		}
		
		Device device = deviceServiceImpl.findById(deviceId);
		
		Integer size = 0;
		List<DeviceTempHum> reportDetailsList;
		
		if(device != null) {
			
			
			if(exportData.equals("exportData")) {
				
				
//				positions = mongoPositionRepoSFDA.getTripPositionsDetailsExport(deviceId, dateFrom, dateTo);
				mongoPositions = mongoPositionsRepository.
						findAllByDeviceidAndDevicetimeBetweenOrderByDevicetimeDesc(deviceId, dateFrom, dateTo);
				reportDetailsList = reportsHelper.deviceTempAndHumProcessHandler(mongoPositions, timeOffset);

				getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",reportDetailsList);
				logger.info("************************ getviewTrip ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);
			}
			int limit = 10;
			Pageable pageable = new PageRequest(offset/limit, limit);
//			positions = mongoPositionRepoSFDA.getTripPositionsDetails(deviceId, dateFrom, dateTo,offset);

			mongoPositions = mongoPositionsRepository.
					findAllByDeviceidAndDevicetimeBetweenOrderByDevicetimeDesc(deviceId, dateFrom, dateTo, pageable);

			reportDetailsList = reportsHelper.deviceTempAndHumProcessHandler(mongoPositions, timeOffset);


//			if(positions.size() > 0) {
			if(mongoPositions.size() > 0) {
//				size = mongoPositionRepoSFDA.getTripPositionsDetailsSize(deviceId, dateFrom, dateTo);
				size = mongoPositionsRepository.countAllByDeviceidAndDevicetimeBetween(deviceId, dateFrom,
						dateTo);
			}
			
			
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",reportDetailsList,size);
			logger.info("************************ getviewTrip ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);

		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Device ID is not found",positions);
			logger.info("************************ getviewTrip ENDED ***************************");
			return  ResponseEntity.status(404).body(getObjectResponse);

		}
		
	}
	
	@Override
	public ResponseEntity<?> getVehicleTempHumPDF(String TOKEN, Long deviceId, int offset, String from,
			String to, String search, Long userId, String exportData) {
		 logger.info("************************ getSensorsReport STARTED ***************************");

			List<MonitorStaticstics> positionsList = new ArrayList<MonitorStaticstics>();
			if(TOKEN.equals("")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",positionsList);
				 logger.info("************************ getSensorsReport ENDED ***************************");
				 return  ResponseEntity.badRequest().body(getObjectResponse);
			}
			
			
			if(!TOKEN.equals("Schedule")) {
				if(super.checkActive(TOKEN)!= null)
				{
					return super.checkActive(TOKEN);
				}
			}
			
			
			User loggedUser = new User();
			if(userId != 0) {
				
				loggedUser = userServiceImpl.findById(userId);
				if(loggedUser == null) {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged user is not found",positionsList);
					 logger.info("************************ getSensorsReport ENDED ***************************");
					return  ResponseEntity.status(404).body(getObjectResponse);
				}
			}	
			
			if(!loggedUser.getAccountType().equals(1)) {
				if(!userRoleService.checkUserHasPermission(userId, "SENSORWEIGHT", "list")) {
					 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get SENSORWEIGHT list",positionsList);
					 logger.info("************************ getSensorsReport ENDED ***************************");
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
			}
			
			
			
			List<Long>allDevices= new ArrayList<>();
			allDevices.add(deviceId);
			Device device =deviceServiceImpl.findById(deviceId);

			String storingCategory="";
		    if(device.getAttributes() != null) {
			   if(device.getAttributes().toString().startsWith("{")) {
				   JSONObject object = new JSONObject();

				   object = new JSONObject(device.getAttributes().toString());		
		      	  
		      	   if(object.has("storingCategory")) {
		          	  storingCategory = object.getString("storingCategory");
		    	   }
			   }
			  
		    }
			

			Date dateFrom;
			Date dateTo;

			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			SimpleDateFormat inputFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS SSSS");
			SimpleDateFormat inputFormat1 = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");
			inputFormat1.setLenient(false);
			inputFormat.setLenient(false);
			outputFormat.setLenient(false);

			
			try {
				dateFrom = inputFormat2.parse(from);
				from = outputFormat.format(dateFrom);
				

			} catch (ParseException e2) {
				try {
					dateFrom = inputFormat.parse(from);
					from = outputFormat.format(dateFrom);
					

				} catch (ParseException e3) {
					// TODO Auto-generated catch block
					try {
						dateFrom = inputFormat1.parse(from);
						from = outputFormat.format(dateFrom);

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z' or yyyy-MM-dd'T'HH:mm:ss.SSS SSSS",null);
						logger.info("************************ getEventsReport ENDED ***************************");		
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
					
				}
				
			}
			
			
			try {
				dateTo = inputFormat2.parse(to);
				to = outputFormat.format(dateTo);
				

			} catch (ParseException e2) {
				try {
					dateTo = inputFormat.parse(to);
					to = outputFormat.format(dateTo);
					

				} catch (ParseException e3) {
					// TODO Auto-generated catch block
					try {
						dateTo = inputFormat1.parse(to);
						to = outputFormat.format(dateTo);

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",null);
						logger.info("************************ getEventsReport ENDED ***************************");		
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
					
				}
				
			}
				
				
			

			positionsList = mongoPositionRepoSFDA.getVehicleTempHumListDigram(allDevices,dateFrom, dateTo);
			
			List<Map> list = new ArrayList<>();
		    Map obj = new HashMap();
		    

            double high=0.0;
            double low=0.0;
            double avg=0.0;

		    Map rec = new HashMap();
		    rec.put("sequenceNumber",device.getSequence_number());
		    rec.put("uniqueId",device.getUniqueid());
		    rec.put("low",null);
		    rec.put("high",null);
		    rec.put("lowExtreme",null);
		    rec.put("highExtreme",null);
		    rec.put("size", null);
		    rec.put("start", null);
		    rec.put("end", null);
		    rec.put("avg",null);
		    rec.put("lowCheck", false);
		    rec.put("highCheck", false);
		    rec.put("lowAlarm", false);
		    rec.put("highAlarm", false);
		    rec.put("lowLimit", null);
		    rec.put("highLimit", null);
		    rec.put("storingCategory", storingCategory);

			for(MonitorStaticstics position:positionsList) {
				if(position.getName().equals("Temperature")) {
				    rec.put("size", position.getSeries().size());
				    if(position.getSeries().size() > 0) {
					    rec.put("end", position.getSeries().get(0).getName());
					    rec.put("start", position.getSeries().get(position.getSeries().size()-1).getName());
					    low=position.getSeries().get(0).getValue();
					    high=position.getSeries().get(0).getValue();

				    }

					for(Series series:position.getSeries()) {
						
						if(low>series.getValue()) {
							low = series.getValue();
						}
						
						if(high<series.getValue()) {
							high = series.getValue();
						}
						avg +=series.getValue();

					    rec = checkTemp(storingCategory,series.getValue(),rec);

						
					}
					
				    rec.put("avg",Math.round( (avg/position.getSeries().size()) * 100.0) / 100.0);

				}
			}
		    rec.put("low",low);
		    rec.put("high",high);

		    

		    
		    
		    obj.put("digram", positionsList);
		    obj.put("data", rec);
		    list.add(obj);
			
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",list);
			logger.info("************************ getSensorsReport ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
			
			
	}

	public Map checkTemp(String category,Double AvgTemp,Map record) {
		// TODO Auto-generated method stub
        
	
		
		//SCD1 -20°C to -10°C
		if(category.equals("SCD1")) {
			
			record.put("lowAlarm", true);
			record.put("highAlarm", true);
			
			record.put("lowLimit", -20);
			record.put("highLimit", -10);
			

			if(AvgTemp < -20) {
				record.put("lowCheck", true);

				if(record.get("lowExtreme") != null) {
					if((double)record.get("lowExtreme") > AvgTemp) {
						record.put("lowExtreme",AvgTemp);

					}
				}
				else {
					record.put("lowExtreme",AvgTemp);

				}

			}
			
			if(AvgTemp > -10) {
				record.put("highCheck", true);

				if(record.get("highExtreme") != null) {
					if((double)record.get("highExtreme") < AvgTemp) {
						record.put("highExtreme",AvgTemp);

					}
				}
				else {
					record.put("highExtreme",AvgTemp);

				}

			}


		}
		
        //SCD2 2°C to 8°C
		else if(category.equals("SCD2")) {
			
			record.put("lowAlarm", true);
			record.put("highAlarm", true);
			
			record.put("lowLimit", 2);
			record.put("highLimit", 8);
			
			if(AvgTemp < 2) {
				record.put("lowCheck", true);

				if(record.get("lowExtreme") != null) {
					if((double)record.get("lowExtreme") > AvgTemp) {
						record.put("lowExtreme",AvgTemp);

					}
				}
				else {
					record.put("lowExtreme",AvgTemp);

				}

			}
			
			if(AvgTemp > 8) {
				record.put("highCheck", true);

				if(record.get("highExtreme") != null) {
					if((double)record.get("highExtreme") < AvgTemp) {
						record.put("highExtreme",AvgTemp);

					}
				}
				else {
					record.put("highExtreme",AvgTemp);

				}

			}
		}
		
        //SCD3 Less than 25°C
		else if(category.equals("SCD3")) {
			
			record.put("highAlarm", true);
			record.put("highLimit", 25);
			
			
			if(AvgTemp >= 25) {
				record.put("highCheck", true);

				if(record.get("highExtreme") != null) {
					if((double)record.get("highExtreme") < AvgTemp) {
						record.put("highExtreme",AvgTemp);

					}
				}
				else {
					record.put("highExtreme",AvgTemp);

				}

			}

		}
		
        //SCC1 Less than 25°C
		else if(category.equals("SCC1")) {
			
			record.put("highAlarm", true);
			record.put("highLimit", 25);
			
			if(AvgTemp >= 25) {
				record.put("highCheck", true);

				if(record.get("highExtreme") != null) {
					if((double)record.get("highExtreme") < AvgTemp) {
						record.put("highExtreme",AvgTemp);

					}
				}
				else {
					record.put("highExtreme",AvgTemp);

				}

			}

		}
		
        //SCM1 -20°C to -10°C
		else if(category.equals("SCM1")) {
			
			record.put("lowAlarm", true);
			record.put("highAlarm", true);
			
			record.put("lowLimit", -20);
			record.put("highLimit", -10);
			
			if(AvgTemp < -20) {
				record.put("lowCheck", true);

				if(record.get("lowExtreme") != null) {
					if((double)record.get("lowExtreme") > AvgTemp) {
						record.put("lowExtreme",AvgTemp);

					}
				}
				else {
					record.put("lowExtreme",AvgTemp);

				}

			}
			
			if(AvgTemp > -10) {
				record.put("highCheck", true);

				if(record.get("highExtreme") != null) {
					if((double)record.get("highExtreme") < AvgTemp) {
						record.put("highExtreme",AvgTemp);

					}
				}
				else {
					record.put("highExtreme",AvgTemp);

				}

			}
		}
	
        //SCM2 2°C to 8°C
		else if(category.equals("SCM2")) {
			
			record.put("lowAlarm", true);
			record.put("highAlarm", true);
			
			record.put("lowLimit", 2);
			record.put("highLimit", 8);
			
			if(AvgTemp < 2) {
				record.put("lowCheck", true);

				if(record.get("lowExtreme") != null) {
					if((double)record.get("lowExtreme") > AvgTemp) {
						record.put("lowExtreme",AvgTemp);

					}
				}
				else {
					record.put("lowExtreme",AvgTemp);

				}

			}
			
			if(AvgTemp > 8) {
				record.put("highCheck", true);

				if(record.get("highExtreme") != null) {
					if((double)record.get("highExtreme") < AvgTemp) {
						record.put("highExtreme",AvgTemp);

					}
				}
				else {
					record.put("highExtreme",AvgTemp);

				}

			}
		}
		
		//SCM3 8°C to 15°C
		else if(category.equals("SCM3")) {
            
			record.put("lowAlarm", true);
			record.put("highAlarm", true);
			
			record.put("lowLimit", 8);
			record.put("highLimit", 15);
			
			if(AvgTemp < 8) {
				record.put("lowCheck", true);

				if(record.get("lowExtreme") != null) {
					if((double)record.get("lowExtreme") > AvgTemp) {
						record.put("lowExtreme",AvgTemp);

					}
				}
				else {
					record.put("lowExtreme",AvgTemp);

				}

			}
			
			if(AvgTemp > 15) {
				record.put("highCheck", true);

				if(record.get("highExtreme") != null) {
					if((double)record.get("highExtreme") < AvgTemp) {
						record.put("highExtreme",AvgTemp);

					}
				}
				else {
					record.put("highExtreme",AvgTemp);

				}

			}
		}
		
		//SCM4 15°C to 30°C
		else if(category.equals("SCM4")) {
            
			record.put("lowAlarm", true);
			record.put("highAlarm", true);
			
			record.put("lowLimit", 15);
			record.put("highLimit", 30);
			
			if(AvgTemp < 15) {
				record.put("lowCheck", true);

				if(record.get("lowExtreme") != null) {
					if((double)record.get("lowExtreme") > AvgTemp) {
						record.put("lowExtreme",AvgTemp);

					}
				}
				else {
					record.put("lowExtreme",AvgTemp);

				}

			}
			
			if(AvgTemp > 30) {
				record.put("highCheck", true);

				if(record.get("highExtreme") != null) {
					if((double)record.get("highExtreme") < AvgTemp) {
						record.put("highExtreme",AvgTemp);

					}
				}
				else {
					record.put("highExtreme",AvgTemp);

				}

			}

		}
		
        //SCM5 Should not exceed 40°C
		else if(category.equals("SCM5")) {
			
			record.put("highAlarm", true);
			record.put("highLimit", 40);	

			
			if(AvgTemp > 40) {
				record.put("highCheck", true);

				if(record.get("highExtreme") != null) {
					if((double)record.get("highExtreme") < AvgTemp) {
						record.put("highExtreme",AvgTemp);

					}
				}
				else {
					record.put("highExtreme",AvgTemp);

				}

			}

		}
		
		//SCF1 Should not exceed 25°C
		else if(category.equals("SCF1")) {
            
			record.put("highAlarm", true);
			record.put("highLimit", 25);		
			
			if(AvgTemp > 25) {
				record.put("highCheck", true);

				if(record.get("highExtreme") != null) {
					if((double)record.get("highExtreme") < AvgTemp) {
						record.put("highExtreme",AvgTemp);

					}
				}
				else {
					record.put("highExtreme",AvgTemp);

				}

			}
		}
		
		//SCF2 -1.5°C to 10°C
		else if(category.equals("SCF2")) {

			record.put("lowAlarm", true);
			record.put("highAlarm", true);
			
			record.put("lowLimit", -1.5);
			record.put("highLimit", 10);
			
			
			if(AvgTemp < -1.5) {
				record.put("lowCheck", true);

				if(record.get("lowExtreme") != null) {
					if((double)record.get("lowExtreme") > AvgTemp) {
						record.put("lowExtreme",AvgTemp);

					}
				}
				else {
					record.put("lowExtreme",AvgTemp);

				}

			}
			
			if(AvgTemp > 10) {
				record.put("highCheck", true);

				if(record.get("highExtreme") != null) {
					if((double)record.get("highExtreme") < AvgTemp) {
						record.put("highExtreme",AvgTemp);

					}
				}
				else {
					record.put("highExtreme",AvgTemp);

				}

			}
		}
		
        //SCF3 -1.5°C to 21°C 
		else if(category.equals("SCF3")) {
			
			record.put("lowAlarm", true);
			record.put("highAlarm", true);
			
			record.put("lowLimit", -1.5);
			record.put("highLimit", 21);
			
			
			if(AvgTemp < -1.5) {
				record.put("lowCheck", true);

				if(record.get("lowExtreme") != null) {
					if((double)record.get("lowExtreme") > AvgTemp) {
						record.put("lowExtreme",AvgTemp);

					}
				}
				else {
					record.put("lowExtreme",AvgTemp);

				}

			}
			
			if(AvgTemp > 21) {
				record.put("highCheck", true);

				if(record.get("highExtreme") != null) {
					if((double)record.get("highExtreme") < AvgTemp) {
						record.put("highExtreme",AvgTemp);

					}
				}
				else {
					record.put("highExtreme",AvgTemp);

				}

			}

		}
		
		//SCF4 Should not exceed (-18)°C
		else if(category.equals("SCF4")) {
            
			record.put("highAlarm", true);
			record.put("highLimit", -18);
			

			if(AvgTemp > -18) {
				record.put("highCheck", true);

				if(record.get("highExtreme") != null) {
					if((double)record.get("highExtreme") < AvgTemp) {
						record.put("highExtreme",AvgTemp);

					}
				}
				else {
					record.put("highExtreme",AvgTemp);

				}

			}

		}
		
		//SCA1 Should not exceed 30°C
		else if(category.equals("SCA1")) {
			record.put("highAlarm", true);
			record.put("highLimit", 30);
			
			if(AvgTemp > 30) {
				record.put("highCheck", true);

				if(record.get("highExtreme") != null) {
					if((double)record.get("highExtreme") < AvgTemp) {
						record.put("highExtreme",AvgTemp);

					}
				}
				else {
					record.put("highExtreme",AvgTemp);

				}

			}

		}
		
        //SCP1 Should not exceed 35°C
		else if(category.equals("SCP1")) {
			
			record.put("highAlarm", true);
			record.put("highLimit", 35);
			
			if(AvgTemp > 35) {
				record.put("highCheck", true);

				if(record.get("highExtreme") != null) {
					if((double)record.get("highExtreme") < AvgTemp) {
						record.put("highExtreme",AvgTemp);

					}
				}
				else {
					record.put("highExtreme",AvgTemp);

				}

			}

		}
		
		
		return record;
	}

	@Override
	public ResponseEntity<?> getTripPdfDetails(TripDetailsRequest request,String timeOffset) {
		//get trip summary data --->maryam

//		SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");
//		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

		formatter.setLenient(false);
		PdfSummaryData summaryData = new PdfSummaryData();
		List<List<ReportDetails>> reportDetails = new ArrayList();
		List<Position> positions = new ArrayList<>();
		logger.info(request.getStartTime()+"************"+request.getEndTime());
		try {
//			TimeZone etTimeZone = TimeZone.getTimeZone("UTC");
//			formatter.setTimeZone(etTimeZone);
			Date from = formatter.parse(request.getStartTime());
			Date to = formatter.parse(request.getEndTime());
			positions = getDevicePositionsWithinDateRange(from , to , request.getVehilceId());
			summaryData = getSummaryData(positions);
			reportDetails = getReportDetails(positions,timeOffset);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ArrayList<Object> response = new ArrayList();
		Object[][] deviceNames = deviceRepositorySFDA.deviceNamesData(Arrays.asList(request.getVehilceId()));
		AlarmsReportResponseWrapper alarmsReport = new AlarmsReportResponseWrapper();
		alarmsReport = getAlarmSection(request.getVehilceId(),request.getStartTime(),request.getEndTime(),timeOffset,positions);
		alarmsReport.setReportDetailsData(reportDetails);
		summaryData.setDeviceName((String) deviceNames[0][0]);
		summaryData.setDriverName((String) deviceNames[0][1]);
		summaryData.setCompanyName((String) deviceNames[0][2]);
		alarmsReport.setSummaryData(summaryData);
		response.add(alarmsReport);
		
		getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",response);
		return  ResponseEntity.ok().body(getObjectResponse);
	}


	@Override
	public ResponseEntity<?> getDeviceCFRReport(TripDetailsRequest request,String timeOffset)  {

		//Request Formatting

		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		LocalDateTime from = LocalDateTime.parse(request.getStartTime(), inputFormatter);
		LocalDateTime to = LocalDateTime.parse(request.getEndTime(), inputFormatter);

//		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy, HH:mm:ss a");
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//		fromt = inputFormatter.parse(request.);
		request.setStartTime(from.format(outputFormatter));
		request.setEndTime(to.format(outputFormatter));

		return getTripPdfDetails(request,timeOffset);
	}


	public PdfSummaryData getSummaryData(List<Position> positions) {
		int count = 0;
		Double avg = 0.0;
		Double max = 0.0;
		Double min = 0.0;
		Double avgHum = 0.0;
		Double maxHum = 0.0;
		Double minHum = 1000.0;
		Double sumAvg = 0.0;
		Double sumTemp = 0.0;
		int countHum = 0;
		PdfSummaryData pdfSummary = new PdfSummaryData() ;
		
			for(Position position :positions) {

				Map attributesMap = position.getAttributes();
				Iterator<Map.Entry<String, Integer>> iterator = attributesMap.entrySet().iterator();
				Double recordAvg = getAvgTemp(attributesMap);
				Double humAvg = getHumAvg(attributesMap);
				if(humAvg!=0.0){
					if(humAvg<minHum){
						minHum = humAvg;
					}
					if(humAvg>maxHum){
						maxHum=humAvg;
					}
					sumAvg+=humAvg;
					countHum++;
				}
//				avgHum=sumAvg/countHum;

				if(recordAvg != 0.0) {
					if(count == 0) {
						max = recordAvg;
						min = recordAvg;
//						avg = recordAvg;
						sumTemp = recordAvg;
						count ++;
					}
					else {
						if(recordAvg > max) {
							max = recordAvg;
						}
						if(recordAvg < min) {
							min = recordAvg;
						}
//						avg += recordAvg;
						sumTemp += recordAvg;
						count ++;
						
					}
					
				}

				
			}
			if(count >0) {
//				avg = sumAvg/count;
				avg = sumTemp/count;
				avgHum=sumAvg/countHum;
			}

//		SimpleDateFormat formatTime = new SimpleDateFormat("dd:hh:mm:ss");
		SimpleDateFormat formatTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String duration = "00:00:00:00" ;
		long date;
			if(positions.size()>1){
//				try{
////					Date start = formatTime.parse(positions.get(0).getDevicetime().toString());
////					Date end = formatTime.parse(positions.get(positions.size()-1).getDevicetime().toString());
//
//				}
//				catch (ParseException e){
//					e.printStackTrace();
//				}
				Date start = positions.get(0).getDevicetime();
				Date end = positions.get(positions.size()-1).getDevicetime();
				long difference_In_Time = end.getTime() - start.getTime();
				long difference_In_Seconds = (difference_In_Time / 1000) % 60;

				long difference_In_Minutes = (difference_In_Time / (1000 * 60)) % 60;

				long difference_In_Hours = (difference_In_Time / (1000 * 60 * 60)) % 24;

				long difference_In_Days = (difference_In_Time / (1000 * 60 * 60 * 24)) % 365;

				duration = + difference_In_Days + " days, " + difference_In_Hours + " hours, " + difference_In_Minutes + " minutes, "
						+ difference_In_Seconds + " seconds";
//				duration = formatTime.format(positions.get(positions.size()-1).getDevicetime().getTime()-positions.get(0).getDevicetime().getTime());
			}


			double mkt = calcMKT(positions);
			pdfSummary = PdfSummaryData
					.builder()
					.avgTemp(avg)
					.maxTemp(max)
					.minTemp(min)
					.totalLength(positions.size())
					.mkt(mkt)
					.maxHum(maxHum)
					.minHum(minHum==1000.0?0.0:minHum)
					.averageHum(avgHum==null?0.0:avgHum)
					.duration(duration)
					.build();

		return pdfSummary;

	}

	public AlarmsReportResponseWrapper getAlarmSection(long deviceID,String start,String end,String timeOffset,List<Position> positionList){
		try {

			String storingCategory = new ObjectMapper().readValue(
					deviceRepositorySFDA.findOne(deviceID).getAttributes()
					, DeviceAttributes.class).getStoringCategory();
//			SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			formatter.setLenient(false);
			Date startDate = formatter.parse(start);
			Date endDate = formatter.parse(end);
			String tempAlarmConditionOver = "";
			String tempAlarmConditionBelow = "";
			String humAlarmConditionOver = "";
			String humAlarmConditionBelow = "";
			switch (storingCategory){
				case "SCD1":
				case "SCM1":
					tempAlarmConditionOver = "Temperature Over -10°C";
					tempAlarmConditionBelow = "Temperature  Below -20°C";
					break;
				case "SCD2":
				case "SCM2":
					tempAlarmConditionOver = "Temperature Over 8°C";
					tempAlarmConditionBelow = "Temperature Below 2°C";
					break;
				case "SCD3":
				case "SCC1":
					tempAlarmConditionOver = "Temperature Over 25°C";
					humAlarmConditionOver = "Humidity Over 60%";
					break;
				case "SCM3":
					tempAlarmConditionOver = "Temperature Over 15°C";
					tempAlarmConditionBelow = "Temperature Below 8°C";
					humAlarmConditionOver = "Humidity Over 60%";
					break;
				case "SCM4":
					tempAlarmConditionOver = "Temperature Over 30°C";
					tempAlarmConditionBelow ="Temperature Below 15°C";
					humAlarmConditionOver = "Humidity Over 60%";
					break;
				case "SCM5":
					tempAlarmConditionOver = "Temperature Over 40°C";
					break;
				case "SCF1":
					tempAlarmConditionOver = "Temperature Over 25°C ";
					humAlarmConditionOver = "Humidity Over 60%";
					break;
				case "SCF2":
					tempAlarmConditionOver = "Temperature over 10°C";
					tempAlarmConditionBelow = "Temperature Below -1.5°C";
					humAlarmConditionOver = "Humidity Over 90%";
					humAlarmConditionBelow = "Humidity Below 75%";
					break;
				case "SCF3":
					tempAlarmConditionOver = "Temperature over 21°C";
					tempAlarmConditionBelow = "Temperature Below -1.5°C";
					humAlarmConditionOver = "Humidity Over 95%";
					humAlarmConditionBelow = "Humidity Below 85%";
					break;
				case "SCF4":
					tempAlarmConditionOver = "Temperature over -18°C";
					humAlarmConditionOver = "Humidity Over 75%";
					humAlarmConditionBelow = "Humidity Below 99%";
					break;
				case "SCA1":
					tempAlarmConditionOver = "Temperature over 30°C";
					humAlarmConditionOver = "Humidity Over 60%";
					break;
				case "SCP1":
					tempAlarmConditionOver = "Temperature over 35°C";
					break;
				default:
					tempAlarmConditionOver = "";
					humAlarmConditionOver = "";
			}

			List<MongoEvents> tempOverAlarms = mongoEventsRepository
					.findAllByDeviceidAndServertimeBetweenAndType(deviceID,startDate,
							endDate,"tepmperatureIncreasedAlarm");
			if(tempOverAlarms.size()>1){
				tempOverAlarms.sort(Comparator.comparing(MongoEvents::getServertime));
			}


			List<MongoEvents> tempBelowAlarms = mongoEventsRepository
					.findAllByDeviceidAndServertimeBetweenAndType(deviceID,startDate,
							endDate,"tepmperatureDecreasedAlarm");
			if(tempBelowAlarms.size()>1){
				tempBelowAlarms.sort(Comparator.comparing(MongoEvents::getServertime));
			}


			List<MongoEvents> humidityOverAlarms = mongoEventsRepository
					.findAllByDeviceidAndServertimeBetweenAndType(deviceID,startDate,
							endDate,"humidityIncreasedAlarm");
			if(humidityOverAlarms.size()>1){
				humidityOverAlarms.sort(Comparator.comparing(MongoEvents::getServertime));
			}

			List<MongoEvents> humidityBelowAlarms = mongoEventsRepository
					.findAllByDeviceidAndServertimeBetweenAndType(deviceID,startDate,
							endDate,"humidityDecreasedAlarm");
			if(humidityBelowAlarms.size()>1){
				humidityBelowAlarms.sort(Comparator.comparing(MongoEvents::getServertime));
			}


			SimpleDateFormat formatForGraph = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");


			List<AlarmSectionWrapperResponse> alarmSectionWrapperList = new ArrayList<>();

			if(!tempAlarmConditionOver.equals("")&&tempOverAlarms.size()>0){

				alarmSectionWrapperList.add(
						AlarmSectionWrapperResponse.builder()
						.alarmCondition(tempAlarmConditionOver)
						.firstAlarmTime(formatForGraph.format(tempOverAlarms.get(0).getServertime()))
						.numOfAlarms(tempOverAlarms.size())
						.build());

			}

			if(!tempAlarmConditionBelow.equals("")&&tempBelowAlarms.size()>0){
				alarmSectionWrapperList.add(
					AlarmSectionWrapperResponse.builder()
							.alarmCondition(tempAlarmConditionBelow)
							.firstAlarmTime(formatForGraph.format(tempBelowAlarms.get(0).getServertime()))
							.numOfAlarms(tempBelowAlarms.size())
							.build()
				);
			}

			if(!humAlarmConditionOver.equals("")&&humidityOverAlarms.size()>0){
				alarmSectionWrapperList.add(
						AlarmSectionWrapperResponse.builder()
								.alarmCondition(humAlarmConditionOver)
								.firstAlarmTime(formatForGraph.format(humidityOverAlarms.get(0).getServertime()))
								.numOfAlarms(humidityOverAlarms.size())
								.build()
				);
			}

			if(!humAlarmConditionBelow.equals("")&&humidityBelowAlarms.size()>0){
				alarmSectionWrapperList.add(
						AlarmSectionWrapperResponse.builder()
								.alarmCondition(humAlarmConditionBelow)
								.firstAlarmTime(formatForGraph.format(humidityBelowAlarms.get(0).getServertime()))
								.numOfAlarms(humidityBelowAlarms.size())
								.build()
				);
			}


//			List<Position> positionList =positionMongoSFDARepository.findAllByDevicetimeBetweenAndDeviceidOrderByDevicetime(startDate,endDate,deviceID);
			List<GraphObject> temperatureGraph = new ArrayList<>();
			List<GraphObject> humidityGraph = new ArrayList<>();

			for(Position position :positionList){
				Date finalDate = new Date(); String modDate;
				String beforeDate = utilities.timeZoneConverter(position.getDevicetime(),timeOffset);
				try {
					finalDate = formatForGraph.parse(beforeDate);
				}catch (ParseException e){
					e.printStackTrace();
				}
				modDate = formatForGraph.format(finalDate);
				temperatureGraph.add(GraphObject.builder()
						.name(modDate)
						.value(getAvgTemp(position.getAttributes()))
						.build());

					humidityGraph.add(GraphObject.builder()
							.name(modDate)
							.value(getHumAvg(position.getAttributes()))
							.build());
			}




			return AlarmsReportResponseWrapper.builder()
					.alarmsSection(alarmSectionWrapperList)
					.temperatureDataGraph(
							GraphDataWrapper.builder()
							.name("temperature")
							.series(temperatureGraph).build())
					.humidityDataGraph(
							GraphDataWrapper.builder()
							.name("humidity")
							.series(humidityGraph).build())
					.build();

		}catch (Exception e){
			System.out.println(e);
			System.out.println(e.getMessage());
			return null;
		}

	}

	public Double getAvgTemp(Map attributesMap) {
//		int count = 0;
		AtomicReference<Double> avg = new AtomicReference<>(0.0);
		List<Double> avgs = new ArrayList<>();
		if(attributesMap.keySet().toString().contains("temp")){
			attributesMap.keySet().stream().filter(o ->
					o.toString().contains("temp"))
					.forEach(o -> {
						if(!attributesMap.get(o).equals(0.0) && !attributesMap.get(o).equals(300.0)) {
							avgs.add((Double) attributesMap.get(o));
						}
					});
		}
		for(Double avrage : avgs){
			avg.updateAndGet(v -> v + avrage);
		}
		if(avgs.size()>0){
			avg.updateAndGet(v -> v / avgs.size());
		}
//		if(attributesMap.containsKey("temp1")) {
//			if(!attributesMap.get("temp1").equals(0.0) && !attributesMap.get("temp1").equals(300.0)) {
//				System.out.println("temp1"+attributesMap.get("temp1"));
//				count++;
//				avg += (Double)attributesMap.get("temp1");
//			}
//		}
//		if(attributesMap.containsKey("temp2") && !attributesMap.get("temp2").equals(300.0)) {
//			if(!attributesMap.get("temp2").equals(0.0)){
//				System.out.println("temp2"+attributesMap.get("temp2"));
//				count++;
//				avg += (Double)attributesMap.get("temp2");
//			}
//		}
//		if(attributesMap.containsKey("temp3") && !attributesMap.get("temp2").equals(300.0)) {
//			if(!attributesMap.get("temp3").equals(0.0)){
//				System.out.println("temp3"+attributesMap.get("temp3"));
//				count++;
//				avg += (Double)attributesMap.get("temp3");
//			}
//		}
//		if(attributesMap.containsKey("temp4") && !attributesMap.get("temp2").equals(300.0)) {
//			if(!attributesMap.get("temp4").equals(0)){
//				System.out.println("temp4"+attributesMap.get("temp4"));
//				count++;
//				avg += (Double)attributesMap.get("temp4");
//			}
//		}
//		if(attributesMap.containsKey("temp6") && !attributesMap.get("temp6").equals(300.0)) {
//			if(!attributesMap.get("temp6").equals(0.0)){
//				System.out.println("temp6"+attributesMap.get("temp6"));
//				count++;
//				avg += (Double)attributesMap.get("temp6");
//			}
//		}
//		if(attributesMap.containsKey("temp7") && !attributesMap.get("temp7").equals(300.0)) {
//			if(!attributesMap.get("temp7").equals(0.0)){
//				System.out.println("temp7"+attributesMap.get("temp7"));
//				count++;
//				avg += (Double)attributesMap.get("temp7");
//
//			}
//		}
//		if(attributesMap.containsKey("temp8") && !attributesMap.get("temp8").equals(300.0)) {
//			if(!attributesMap.get("temp8").equals(0.0)){
//				System.out.println("temp8"+attributesMap.get("temp8"));
//				count++;
//				avg += (Double)attributesMap.get("temp8");
//			}
//		}
//		if(attributesMap.containsKey("wiretemp1") && !attributesMap.get("wiretemp1").equals(300.0)) {
//			if(!attributesMap.get("wiretemp1").equals(0.0)){
//				System.out.println("wire1"+attributesMap.get("wiretemp1"));
//				count++;
//				avg += (Double)attributesMap.get("wiretemp1");
//			}
//		}
//		if(attributesMap.containsKey("wiretemp2") && !attributesMap.get("wiretemp2").equals(300.0)) {
//			if(!attributesMap.get("wiretemp2").equals(0.0)){
//				System.out.println("wire2"+attributesMap.get("wiretemp2"));
//				count++;
//				avg += (Double)attributesMap.get("wiretemp2");
//			}
//		}
//		if(attributesMap.containsKey("wiretemp3") && !attributesMap.get("wiretemp3").equals(300.0)) {
//			if(!attributesMap.get("wiretemp3").equals(0.0)){
//				System.out.println("wire3"+attributesMap.get("wiretemp3"));
//				count++;
//				avg += (Double)attributesMap.get("wiretemp3");
//			}
//		}
//		if(attributesMap.containsKey("wiretemp4") && !attributesMap.get("wiretemp4").equals(300.0)) {
//			if(!attributesMap.get("wiretemp4").equals(0.0)){
//				System.out.println("wire4"+attributesMap.get("wiretemp4"));
//				count++;
//				avg += (Double)attributesMap.get("wiretemp4");
//			}
//		}
//		if(avg>0) {
//			avg = avg/count;
//		}
//
		return avg.get();
		
	}
	public Double getHumAvg(Map attributesMap){
		int count = 0;
		Double avg = 0.0;
		List<Double> avgs = new ArrayList<>();
		if(attributesMap.keySet().toString().contains("hum")){
			attributesMap.keySet().stream().filter(o ->
					o.toString().contains("hum"))
					.forEach(o -> {
						if(!attributesMap.get(o).equals(0.0) && !attributesMap.get(o).equals(300.0)) {
							avgs.add((Double) attributesMap.get(o));
						}

					});
		}
		for(Double avrage : avgs){
			avg+=avrage;
		}
		if(avgs.size()>0){
			avg/=avgs.size();
		}
		return avg;
	}
	
	public List<List<ReportDetails>> getReportDetails(List<Position> positions,String timeOffset) {
		List<ReportDetails> reportDetailsList = new ArrayList();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat formatDateJava = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat formatTime = new SimpleDateFormat("hh:mm:ss");
		for(Position position : positions) {
			logger.info("BEFORE******"+position.getDevicetime());
			ReportDetails reportDetails = new ReportDetails();
			Map attributesMap = position.getAttributes();
			Double recordAvgTemp = getAvgTemp(attributesMap);
			
			Double humidity = getHumAvg(attributesMap);;
			Date dateFrom = new Date();
			Date dateTo = new Date();
//			String devicetimeAsDateStr = formatDateJava.format(position.getDevicetime());
			String dateSt = utilities.timeZoneConverter(position.getDevicetime(),timeOffset);
			logger.info("AFTER*******"+dateSt);

//			String devicetimeAsTimeStr = formatTime.format(position.getDevicetime());
			String hourSt = utilities.timeZoneConverter(position.getDevicetime(),timeOffset);
			try {
				dateFrom = formatter.parse(dateSt);
				dateTo = formatter.parse(hourSt);
			}catch (ParseException e){
				e.printStackTrace();
			}

			String devicetimeAsDateStr = formatDateJava.format(dateFrom);
			String devicetimeAsTimeStr = formatTime.format(dateTo);

			reportDetails = ReportDetails
					.builder()
					.date(devicetimeAsDateStr)
					.time(devicetimeAsTimeStr)
					.temperature(recordAvgTemp)
					.humidity(humidity)
					.build();
			
			
			reportDetailsList.add(reportDetails);
			
		}
		List<List<ReportDetails>> recordsList = new ArrayList<>();
		int numOfSensorRecords = positions.size();
		if(numOfSensorRecords>240){
			int recordLengthUnit = (numOfSensorRecords/4) +1;
			recordsList.add(reportDetailsList.subList(0,recordLengthUnit));
			recordsList.add(reportDetailsList.subList(recordLengthUnit,2*recordLengthUnit));
			recordsList.add(reportDetailsList.subList((2*recordLengthUnit),3*recordLengthUnit));
			recordsList.add(reportDetailsList.subList((3*recordLengthUnit),numOfSensorRecords));
		}else if(numOfSensorRecords>118){
			int recordLengthUnit = 59;
			recordsList.add(reportDetailsList.subList(0,recordLengthUnit));
			recordsList.add(reportDetailsList.subList(recordLengthUnit,2*recordLengthUnit));
			recordsList.add(reportDetailsList.subList(2*recordLengthUnit,numOfSensorRecords));
		}else if (numOfSensorRecords>59){
			int recordLengthUnit = 59;
			recordsList.add(reportDetailsList.subList(0,recordLengthUnit));
			recordsList.add(reportDetailsList.subList(recordLengthUnit,numOfSensorRecords));
		}else{
			recordsList.add(reportDetailsList);
		}
		
		return recordsList;
	}
	
	public List<Position> getDevicePositionsWithinDateRange(Date from , Date to , long deviceid){
		List<Position> pos = positionMongoSFDARepository.findAllByDevicetimeBetweenAndDeviceidOrderByDevicetime(from,to,deviceid);
		return pos;
	}
	
	public Double 	calcMKT(List<Position> positions) {
		double allExponenials = 0.0;
		double result = 0.0;
//		for(Position position :positions) {
//			Map attributesMap = position.getAttributes();
//			Double recordAvg = getAvgTemp(attributesMap);
//			 double t1 = -(10000/(recordAvg+273.1));//t1 value is ΔH/RT, according to the formula: H/R=10000K K = 273.1 + temperature, so 10000 divided by K equals t1
//			 double e1 = Math.exp(t1);//Find the value of e to the power of t1 //Math.exp(x) e to the power of x
//			 allExponenials += e1;
//			allExponenials += position.getExpoDeltaHRTkelvins();
//			40.2887620398829\\40.23883963989806
//		}
//		allExponenials = positions.stream().filter(position -> position.getExpoDeltaHRTkelvins() != null).mapToDouble(Position::getExpoDeltaHRTkelvins).sum();
	allExponenials = positions
			.stream()
			.map(Position::getExpoDeltaHRTkelvins)
			.mapToDouble(Double::doubleValue).sum();
		int n = positions.size();
		if (n>0) {
			result = Math.log((allExponenials)/n);
			result = ((-10000/result)-273.1); //in c
		}
		return result;
	}
	
	
	
	
}
