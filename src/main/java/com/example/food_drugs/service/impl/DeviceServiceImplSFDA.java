package com.example.food_drugs.service.impl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import com.example.food_drugs.dto.ApiResponse;
import com.example.food_drugs.dto.ApiResponseBuilder;
import com.example.food_drugs.dto.AttributesWrapper;
import com.example.food_drugs.dto.responses.DeviceResponseDataWrapper;
import com.example.food_drugs.dto.responses.GraphDataWrapper;
import com.example.food_drugs.dto.responses.GraphObject;
import com.example.food_drugs.dto.responses.VehicleListDashBoardResponse;
import com.example.food_drugs.exception.ApiGetException;
import com.example.food_drugs.helpers.Impl.AssistantServiceImpl;
import com.example.food_drugs.helpers.Impl.Dictionary;
import com.example.food_drugs.helpers.Impl.Utilities;
import com.example.food_drugs.service.DeviceServiceSFDA;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.examplequerydslspringdatajpamaven.entity.CustomDeviceList;
import com.example.examplequerydslspringdatajpamaven.entity.Device;
import com.example.examplequerydslspringdatajpamaven.entity.MongoPositions;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.repository.DeviceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.MongoPositionsRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserClientDeviceRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;
import com.example.examplequerydslspringdatajpamaven.service.DeviceServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.UserRoleServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.UserServiceImpl;
import com.example.food_drugs.repository.DeviceRepositorySFDA;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * services functionality related to devices SFDA
 * @author fuinco
 *
 */
@Component
@Service
public class DeviceServiceImplSFDA extends RestServiceController implements DeviceServiceSFDA {

	private static final Log logger = LogFactory.getLog(DeviceServiceImplSFDA.class);

	private final AssistantServiceImpl assistantServiceImpl;
	private final Dictionary dictionary ;
	
	private GetObjectResponse getObjectResponse;
	
	@Autowired
	private UserServiceImpl userServiceImpl;
	
	@Autowired
	private UserRoleServiceImpl userRoleServiceImpl;
	
	@Autowired
	private DeviceServiceImpl deviceServiceImpl;
	
	@Autowired
	private DeviceRepositorySFDA deviceRepositorySFDA;
	
	@Autowired
	private MongoPositionsRepository mongoPositionsRepository;
	
	@Autowired
	private DeviceRepository deviceRepository;
	
	@Autowired
	private UserClientDeviceRepository userClientDeviceRepository;
	private final Utilities utilities;
	public DeviceServiceImplSFDA(AssistantServiceImpl assistantServiceImpl, Dictionary dictionary, Utilities utilities) {
		this.assistantServiceImpl = assistantServiceImpl;
		this.dictionary = dictionary;
		this.utilities = utilities;
	}

	@Override
	public ResponseEntity<?> activeDeviceSFDA(String TOKEN, Long userId, Long deviceId) {
	    logger.info("************************ activeDevice ENDED ***************************");
		if(TOKEN.equals("")) {
			 List<Device> devices = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",devices);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		 if(deviceId.equals(0) || userId.equals(0)) {
			 List<Device> devices = null;
			 getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "User ID and Device ID are Required",devices);
		     logger.info("************************ activeDevice ENDED ***************************");
		     return ResponseEntity.badRequest().body(getObjectResponse); 
		 }
		User loggedUser = userServiceImpl.findById(userId);
		if(loggedUser == null) {
			List<Device> devices = null;
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "User ID is not found",devices);
		    logger.info("************************ activeDevice ENDED ***************************");
		    return ResponseEntity.status(404).body(getObjectResponse);
		}
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleServiceImpl.checkUserHasPermission(userId, "DEVICE", "delete")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to delete device",null);
				 logger.info("************************ activeDevice ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		 Device device = deviceRepository.findOne(deviceId);
		 if(device == null)
		 {
			 List<Device> devices = null;
			 getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This device is not found",devices);
		     logger.info("************************ activeDevice ENDED ***************************");
		     return ResponseEntity.status(404).body(getObjectResponse);
		 }
		 else
		 {
			 boolean isParent = false;
			 User creater= userServiceImpl.findById(userId);
			 if(creater == null) {
				 List<Device> devices = null;
				 getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is not found",devices);
			     logger.info("************************ activeDevice ENDED ***************************");
			     return ResponseEntity.status(404).body(getObjectResponse);
			 }
			 if(creater.getAccountType().equals(4)) {
				    Set<User>parentClient = creater.getUsersOfUser();
					if(parentClient.isEmpty()) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to edit this user ",null);
						logger.info("************************ editDevice ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}else {
					  
						User parent =null;
						for(User object : parentClient) {
							parent = object;
						}
						Set<User> deviceParent = device.getUser();
						if(deviceParent.isEmpty()) {
							getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), " this device is not assigned to any user ,you are not allowed to edit this user ",null);
							logger.info("************************ editDevice ENDED ***************************");
							return ResponseEntity.badRequest().body(getObjectResponse);
						}else {
							
							for(User deviceUser : deviceParent) {
								if(deviceUser.getId().equals(parent.getId())) {
									
									isParent = true;
									break;
								}
							}
						}
					}
			 }
			 if(!deviceServiceImpl.checkIfParent(device , creater)&& ! isParent) {
					getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to active this user ",null);
					logger.info("************************ editDevice ENDED ***************************");
					return ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 
		     device.setDeleteDate(null);
		     deviceRepository.save(device);
		     
		     List<Device> devices = null;
		     
		     
		     
			 getObjectResponse = new GetObjectResponse( HttpStatus.OK.value(), "success",devices);
		     logger.info("************************ activeDevice ENDED ***************************");
		     return ResponseEntity.ok().body(getObjectResponse);
		 }
			 
	}


	@Override
	public ResponseEntity<?> getAllUserDevicesSFDA(String TOKEN, Long userId, int offset, String search, int active,String exportData) {
		// TODO Auto-generated method stub
 
		logger.info("************************ getAllUserDevices STARTED ***************************");
		if(TOKEN.equals("")) {
			 List<Device> devices = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",devices);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN) != null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId.equals(0)) {
			 List<CustomDeviceList> devices= null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",devices);
			 logger.info("************************ getAllUserDevices ENDED ***************************");
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userServiceImpl.findById(userId);
		if(loggedUser == null) {
			 List<CustomDeviceList> devices= null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This user is not found",devices);
			 logger.info("************************ getAllUserDevices ENDED ***************************");
			return  ResponseEntity.status(404).body(getObjectResponse);
		}
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleServiceImpl.checkUserHasPermission(userId, "DEVICE", "list")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get devices list",null);
				 logger.info("************************ getAllUserDevices ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		 List<Long>usersIds= new ArrayList<>();
		 Integer size=0;
		 List<CustomDeviceList> devices = new ArrayList<CustomDeviceList>();
		 if(loggedUser.getAccountType().equals(4)) {
			 List<Long> deviceIds = userClientDeviceRepository.getDevicesIds(userId);
			 if(deviceIds.size()>0) {
				 if(active == 0) {
					if(exportData.equals("exportData")) {
						devices = deviceRepositorySFDA.getDevicesListByIdsDeactiveExport(deviceIds, search,true);
					}
					else {
						devices = deviceRepositorySFDA.getDevicesListByIdsDeactive(deviceIds, offset, search ,true);
					    size = deviceRepositorySFDA.getDevicesListSizeByIdsDeactive(deviceIds, search,true);
					}
				 }
                 if(active == 2) {
 					if(exportData.equals("exportData")) {
	                	 devices = deviceRepositorySFDA.getDevicesListByIdsAllExport(deviceIds, search,true);
 					}
 					else {
 	                	 devices = deviceRepositorySFDA.getDevicesListByIdsAll(deviceIds, offset, search,true);
 					     size = deviceRepositorySFDA.getDevicesListSizeByIdsAll(deviceIds, search, true);
 					}
				 }
                 if(active == 1) {
  					if(exportData.equals("exportData")) {
  						devices= deviceRepository.getDevicesListByIdsExport(deviceIds,search,true);
  					}
  					else {
  						devices= deviceRepository.getDevicesListByIds(deviceIds,offset,search,true);
  	    				size=  deviceRepository.getDevicesListSizeByIds(deviceIds,search,true);
  					}
    			 }
			 }
		 }
		 else {
			 List<User>childernUsers = userServiceImpl.getAllChildernOfUser(userId);
			 if(childernUsers.isEmpty()) {
				 usersIds.add(userId);
			 }
			 else {
				 usersIds.add(userId);
				 for(User object : childernUsers) {
					 usersIds.add(object.getId());
				 }
			 }

			 if(active == 0) {


					if(loggedUser.getAccountType().equals(2)||loggedUser.getAccountType().equals(1)) {
						if(exportData.equals("exportData")) {
							devices= deviceRepositorySFDA.getDevicesListDeactiveExport(usersIds,search,true);
						}else {
							devices = deviceRepositorySFDA.getDevicesListDeactive(usersIds, offset, search, true);
							size = deviceRepositorySFDA.getDevicesListSizeDeactive(usersIds, search, true);
						}
					}else{
						if(exportData.equals("exportData")) {
							devices= deviceRepositorySFDA.getDevicesListDeactiveExport(usersIds,search,true);
						}else {
							devices = deviceRepositorySFDA.getDevicesListDeactive(usersIds, offset, search, true);
							size = deviceRepositorySFDA.getDevicesListSizeDeactive(usersIds, search, true);
						}
					}



			 }
			 
             if(active == 2) {
					 if(loggedUser.getAccountType().equals(2)||loggedUser.getAccountType().equals(1)){
						 if(exportData.equals("exportData")) {
							 devices= deviceRepositorySFDA.getDevicesListAllExport(usersIds,search,true);
						 }else {
							 devices = deviceRepositorySFDA.getDevicesListAll(usersIds, offset, search, true);
							 size = deviceRepositorySFDA.getDevicesListSizeAll(usersIds, search, true);
						 }
					 }else {
						 if(exportData.equals("exportData")) {
							 devices= deviceRepositorySFDA.getDevicesListAllExport(usersIds,search,true);
						 }else {
							 devices = deviceRepositorySFDA.getDevicesListAll(usersIds, offset, search, true);
							 size = deviceRepositorySFDA.getDevicesListSizeAll(usersIds, search, true);
						 }
					 }
			 }
             
             if(active == 1) {
					  if(loggedUser.getAccountType().equals(2)||loggedUser.getAccountType().equals(1)){
						  if(exportData.equals("exportData")) {
							  devices= deviceRepository.getDevicesListExport(usersIds,search,true);
						  }else {
							  devices= deviceRepository.getDevicesList(usersIds,offset,search,true);
							  size=  deviceRepository.getDevicesListSize(usersIds,search ,true);
						  }

					  }else {
						  if(exportData.equals("exportData")) {
							  devices= deviceRepository.getDevicesListExport(usersIds,search,true);
						  }else {
						  		devices= deviceRepository.getDevicesList(usersIds,offset,search,true);
						  		size=  deviceRepository.getDevicesListSize(usersIds,search ,true);
						  }
					  }
			 }
		}
		 getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",devices,size);
		 logger.info("************************ getAllUserDevices ENDED ***************************");
		 return  ResponseEntity.ok().body(getObjectResponse);
	}

	@Override
	public ResponseEntity<?> getDeviceGraphData(String TOKEN, Long userId) throws IOException {
		logger.info("************************ getDeviceGraphData STARTED ***************************");
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
				sqlData = deviceRepositorySFDA.getDeviceByUserIds(usersIds);
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
				sqlData = deviceRepositorySFDA.getDeviceByUserIds(usersIds);
			}



			List<DeviceResponseDataWrapper> mappedSQLData = new ArrayList<>();
			String pattern = "HH:mm";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

			ObjectMapper oMapper = new ObjectMapper();

			for( Object[] datas : sqlData){

				List<MongoPositions> lastDataList = mongoPositionsRepository
						.findFirst20ByDeviceidOrderByServertimeDesc((Integer) datas[1]);
				List<GraphObject> series = new ArrayList<>();

				for(MongoPositions data:lastDataList){
					series.add(GraphObject.builder()
							.name(simpleDateFormat.format(data.getServertime()))
							.value(findTemperature(oMapper.convertValue(data.getAttributes(), Map.class)))
							.build());
				}
				AttributesWrapper attributes = new ObjectMapper().readValue((String) datas[5],AttributesWrapper.class);
				mappedSQLData.add(DeviceResponseDataWrapper
						.builder()
						.deviceName((String) datas[0])
						.lastTemp((Double) datas[3])
						.lastHum((Double) datas[4])
						.storingCategory(attributes.getStoringCategory())
						.id((Integer) datas[1])
						.lastUpdate(datas[2].toString())
						.graphData(GraphDataWrapper.builder()
								.name("Temperature")
								.series(series)
								.build())
						.build());
			}


			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",mappedSQLData);
			logger.info("************************ getDeviceGraphData ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
		}


		else{

			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",sqlData);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
	}


	private static int devicesSize = 0 ;
	@Override
	public ResponseEntity<?> getDeviceGraphDataDashboard(String TOKEN, Long userId,int offset,int size) {
		logger.info("************************ getDeviceGraphData STARTED ***************************");
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
				sqlData = deviceRepositorySFDA.getDeviceByUserIdsForDashboard(usersIds, offset, size);
				if(offset==0)
					devicesSize = deviceRepositorySFDA.getDevicesSizeByUserIds(usersIds);
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
				sqlData = deviceRepositorySFDA.getDeviceByUserIdsForDashboard(usersIds,offset,size);
				;
				if(offset == 0)
					devicesSize = deviceRepositorySFDA.getDevicesSizeByUserIds(usersIds);
			}



			List<DeviceResponseDataWrapper> mappedSQLData = new ArrayList<>();
			for( Object[] datas : sqlData){
				Date dateLast = new Date();
				if(datas[2].toString() != null) {
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date now = new Date();
					String strDate = formatter.format(now);
					try {
						dateLast = formatter.parse(datas[2].toString());
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}else {
					dateLast = null;
				}
				mappedSQLData.add(DeviceResponseDataWrapper
						.builder()
						.deviceName((String) datas[0])
						.lastTemp((Double) datas[3])
						.lastHum((Double) datas[4])
						.storingCategory((String) datas[5])
						.id((Integer) datas[1])
						.lastUpdate(utilities.timeZoneConverter(dateLast,"%2B0300"))
						.build());
			}


			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",mappedSQLData,devicesSize);
			logger.info("************************ getDeviceGraphData ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
		}


		else{

			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",sqlData);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
	}


	@Override
	public ResponseEntity<GetObjectResponse<GraphDataWrapper>> getDataForGraphByDeviceID(int deviceID){
		//*************  DEPRECATED ****************
		List<MongoPositions> lastDataList = mongoPositionsRepository
				.findFirst15ByDeviceidOrderByServertimeDesc(deviceID);
		List<GraphObject> series = new ArrayList<>();
		String pattern = "HH:mm";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

		ObjectMapper oMapper = new ObjectMapper();
		for(MongoPositions data:lastDataList){
			series.add(GraphObject.builder()
					.name(simpleDateFormat.format(data.getServertime()))
					.value(findTemperature(oMapper.convertValue(data.getAttributes(), Map.class)))
					.build());
		}
		getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",
				Arrays.asList(GraphDataWrapper.builder()
				.name("Temperature")
				.series(series)
				.build()));
		logger.info("************************ getDeviceGraphData ENDED ***************************");
		return  ResponseEntity.ok().body(getObjectResponse);
	}

	double findTemperature(Map<String,Object> attributes){
		double temparature  = 0.0;
		for (Map.Entry<String, Object> set:attributes.entrySet()) {
			String key  = set.getKey() ;
			Object value = set.getValue() ;
			if(key.contains("temp")){
				if(Double.parseDouble(value.toString())>0.0
						&&Double.parseDouble(value.toString())<300.0){
					 temparature=Double.parseDouble(value.toString());
					return Math.round(temparature * 100.0) / 100.0;
				}
			}
		}
		return temparature;
	}

@Override
public void startAndEndDate() throws ParseException {
	List<Device> allDeviceList = deviceRepository.findAll();
	logger.info("**********************Experation DAte STarted******************");
	for(Device device : allDeviceList){
		Date updateToELmDate  = device.getUpdate_date_in_elm();
		if(updateToELmDate != null){
			logger.info("**********************Experation DAte STarted****************** UPdate TO eldm Date" + updateToELmDate);
			device.setStartDate(updateToELmDate);
			Calendar c = Calendar.getInstance();
			c.setTime(updateToELmDate);
			c.add(Calendar.YEAR , 1);
			device.setEndDate(c.getTime());
			deviceRepository.save(device);
		}else{
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(device.getCreate_date()!=null){
				logger.info("**********************Experation DAte STarted******************  Creation Date" + device.getCreate_date());
				Date startDate = formatter.parse(device.getCreate_date());
				device.setStartDate(startDate);
				Calendar c = Calendar.getInstance();
				c.setTime(startDate);
				c.add(Calendar.YEAR , 1);
				device.setEndDate(c.getTime());
				deviceRepository.save(device);
			}

		}
	}
}

	@Override
	public ApiResponse<List<VehicleListDashBoardResponse>> vehicleListDashBoard(String TOKEN, Long userId) {

		ApiResponseBuilder<List<VehicleListDashBoardResponse>> builder = new ApiResponseBuilder<>();
		List<VehicleListDashBoardResponse> result = new ArrayList<>();
		List<Long> userIds;
		List<String> lastDataIds;
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

		Optional<List<Device>> optionalDeviceList = deviceRepository.findAllByUserIdInAndDeleteDate(userIds,null);
		if (optionalDeviceList.isPresent()) {
			List<Device> deviceList = optionalDeviceList.get();


			lastDataIds = deviceList.stream()
					.map(Device::getPositionid).filter(Objects::nonNull).collect(Collectors.toList());
			Optional<List<MongoPositions>> mongoPositions = mongoPositionsRepository.findAllBy_idIn(lastDataIds);
			List<MongoPositions> mongoPositionsList = mongoPositions.get();

			Integer cooler;
			Boolean valid;
			Boolean ignition;

			for (Device device:deviceList) {
				Date now=new Date();
				Long leftDays = null;
				if(device.getEndDate()==null||device.getEndDate().before(now)){
					leftDays=Long.valueOf(0);
				}
				else if (device.getEndDate() !=null){
					leftDays = ChronoUnit.DAYS.between(now.toInstant(),device.getEndDate().toInstant());
				}
//				Date date=new Date();
//				Date endDate=device.getEndDate();
//				Long leftDays;
//				if(endDate==null){
//					leftDays= Long.valueOf(0);
//				} else if (endDate.before(date)){
//					leftDays= Long.valueOf(0);
//				}else {
//					long enddateinms= endDate.getTime();
//					long dateinms=date.getTime();
//					long timediff=enddateinms-dateinms;
//
//					leftDays= TimeUnit.DAYS.convert(timediff,TimeUnit.MILLISECONDS);
//				}
				try {
					List<MongoPositions> mongoPositionsList1 = mongoPositionsList.stream()
							.filter(mongoPositions1 -> mongoPositions1.getDeviceid().equals(device.getId()))
							.collect(Collectors.toList());
					AttributesWrapper attributes = new ObjectMapper().readValue(device.getAttributes(),AttributesWrapper.class);
					if (!mongoPositionsList1.isEmpty()) {
						valid = mongoPositionsList1.get(0).getValid();
						ObjectMapper mapper = new ObjectMapper();
						String json = null;
						try {
							json = mapper.writeValueAsString(mongoPositionsList1.get(0).getAttributes());
						} catch (JsonProcessingException e) {
							e.printStackTrace();
						}
						JSONObject obj = new JSONObject(json);
						if (obj.has("AC")){
							cooler = obj.getInt("AC");
						}else {
							cooler= null;
						}
						if (obj.has("ignition")){
						ignition = obj.getBoolean("ignition");
						}else {
							ignition = null;
						}
					}else {
						valid = null;
						cooler= null;
						ignition = null;
					}
					result.add(
							VehicleListDashBoardResponse
									.builder()
									.vehicleName(device.getName())
									.lastUpdate(device.getLastupdate())
									.temp(device.getLastTemp())
									.humidity(device.getLastHum())
									.storingCategory(dictionary.RangeInTempAndHumValid(attributes.getStoringCategory(),device.getLastTemp(),device.getLastHum()))
									.cooler(cooler)
									.ignition(ignition)
									.valid(valid)
									.leftDays(leftDays)
									.build()
					);
				}
				catch (Exception e){
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
			throw new ApiGetException("No Vehicles Found");
		}

	}


}
