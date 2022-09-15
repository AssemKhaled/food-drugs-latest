package com.example.food_drugs.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;

import com.example.examplequerydslspringdatajpamaven.entity.*;
import com.example.examplequerydslspringdatajpamaven.repository.*;
import com.example.food_drugs.dto.responses.ElmInquiryResponse;
import com.example.food_drugs.service.ElmServiceSFDA;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;
import com.example.food_drugs.entity.MongoElmLogsSFDA;
import com.example.food_drugs.entity.MonogoInventoryLastDataElmSend;

import org.json.JSONArray;
import com.example.examplequerydslspringdatajpamaven.service.DeviceServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.ElmServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.UserRoleService;
import com.example.examplequerydslspringdatajpamaven.service.UserServiceImpl;
import com.example.food_drugs.entity.CompanyElmDataSFDA;
import com.example.food_drugs.entity.Coordinates;
import com.example.food_drugs.entity.DeviceElmDataSFDA;
import com.example.food_drugs.entity.IndividualGregorianElmDataSFDA;
import com.example.food_drugs.entity.IndividualHijriElmDataSFDA;
import com.example.food_drugs.entity.Inventory;
import com.example.food_drugs.entity.MongoElmLiveLocationSFDA;
import com.example.food_drugs.entity.Warehouse;
import com.example.food_drugs.repository.DeviceRepositorySFDA;
import com.example.food_drugs.repository.InventoryRepository;
import com.example.food_drugs.repository.MongoElmLiveLocationRepositorySFDA;
import com.example.food_drugs.repository.MongoInventoryLastDataRepo;
import com.example.food_drugs.repository.MongoInventoryLastDataRepositoryElmData;
import com.example.food_drugs.repository.WarehousesRepository;

/**
 * services functionality related to elm
 * @author fuinco
 *
 */
@Component
@Service
public class ElmServiceImplSFDA  extends RestServiceController implements ElmServiceSFDA {

	
	private static final Log logger = LogFactory.getLog(ElmServiceImpl.class);
	GetObjectResponse getObjectResponse;
	
	@Value("${elmCompanies}")
	private String elmCompanies;
	
	@Value("${elmLocations}")
	private String elmLocations;
	
	@Value("${elmVehicles}")
	private String elmVehicles;
	
	@Value("${elmDrivers}")
	private String elmDrivers;
	
	@Value("${middleWare}")
	private String middleWare;
	
	@Value("${elmWarehouses}")
	private String elmWarehouses;
	
	@Value("${elmInventories}")
	private String elmInventories;
	
	@Value("${elm}")
	private String elm;
	
	@Autowired
	private MongoElmLogsRepository elmLogsRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private MongoInventoryLastDataRepo mongoInventoryLastDataRepo;
	
	@Autowired
	private UserRoleService userRoleService;
	
	@Autowired
	private UserServiceImpl userServiceImpl;
	
	@Autowired
	private MongoElmLiveLocationRepositorySFDA mongoElmLiveLocationRepositorySFDA;
	
	@Autowired
	private MongoInventoryLastDataRepositoryElmData mongoInventoryLastDataRepositoryElmData;
	
	@Autowired
	private InventoryRepository inventoryRepository;
	
	@Autowired
	private DeviceServiceImpl deviceServiceImpl;
	
	@Autowired
	private UserClientDeviceRepository userClientDeviceRepository;
	
	@Autowired
	private DeviceRepository deviceRepository;
	
	@Autowired
	private DeviceRepositorySFDA deviceRepositorySFDA;
	
	@Autowired
	private WarehousesRepository warehousesRepository;

	private final UserServiceImplSFDA userServiceImplSFDA;

	private final MongoPositionRepo mongoPositionRepo;
	private final MongoPositionsRepository mongoPositionsRepository;

	public ElmServiceImplSFDA(UserServiceImplSFDA userServiceImplSFDA, MongoPositionRepo mongoPositionRepo, MongoPositionsRepository mongoPositionsRepository) {
		this.userServiceImplSFDA = userServiceImplSFDA;
		this.mongoPositionRepo = mongoPositionRepo;
		this.mongoPositionsRepository = mongoPositionsRepository;
	}

	@Override
	public ResponseEntity<?> warehouseRegistrtaion(String TOKEN, Long WarehouseId, Long userId) {
		
		// TODO Auto-generated method stub
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = formatter.format(date);
		String type = "Register Warehouse";
		Map requet = new HashMap();
		Map response = new HashMap();
		
		
		SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 

		String currentDate=formatter.format(date);
		
		Date dateReg = null;
		try {
			dateReg = output.parse(currentDate);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
		
		if(loggedUser.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "WAREHOUSE", "connectToElm")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to connectToElm",null);
				 logger.info("************************ deviceRegistrtaion ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
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
		if(isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get warehouse",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		

		  User parent = userRepository.findOne(createdBy);
		
		  Map<Object,Object> warehouseData = new HashMap();

		  Map<Object,Object>  bodyToMiddleWare = new HashMap();
		  warehouseData.put("activity", warehouse.getActivity());
		  warehouseData.put("name", warehouse.getName());
		  warehouseData.put("phone", warehouse.getPhone());
		  warehouseData.put("address", warehouse.getAddress());
		  warehouseData.put("city", warehouse.getCity());
		  warehouseData.put("longitude", warehouse.getLongitude());
		  warehouseData.put("latitude", warehouse.getLatitude());
		  warehouseData.put("landAreaInSquareMeter", warehouse.getLandAreaInSquareMeter());
		  warehouseData.put("licenseNumber", warehouse.getLicenseNumber());
		  warehouseData.put("licenseIssueDate", warehouse.getLicenseIssueDate());
		  warehouseData.put("licenseExpiryDate", warehouse.getLicenseExpiryDate());
		  warehouseData.put("managerMobile", warehouse.getManagerMobile());
		  warehouseData.put("email", warehouse.getEmail());
		  
		  JSONArray jsonArray = new JSONArray(warehouse.getLandCoordinates()); 
		  ArrayList<Coordinates> arrayOfJson = new ArrayList<Coordinates>(); 

		  for(Object jsonObj:jsonArray) {
			  JSONObject js = new JSONObject(jsonObj.toString());

			  Coordinates coor = new Coordinates(js.getDouble("y"),js.getDouble("x"));
			  arrayOfJson.add(coor);
		  }

		  warehouseData.put("landCoordinates", arrayOfJson);

		  String url = elmCompanies+"/"+parent.getReference_key()+"/warehouses";

		  bodyToMiddleWare.put("dataObject", warehouseData);
		  bodyToMiddleWare.put("url",url);
		  bodyToMiddleWare.put("methodType","POST");

		  requet = bodyToMiddleWare;
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
			
			
			  restTemplate.getMessageConverters()
		        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
			  
		  HttpEntity<Object> entity = new HttpEntity<Object>(bodyToMiddleWare);

		  
		  
		  ResponseEntity<ElmReturn> rateResponse;
		  
		  List<ElmReturn> data = new ArrayList<ElmReturn>();

		  try {
			  rateResponse = restTemplate.exchange(middleWare, HttpMethod.POST, entity, ElmReturn.class);
          } catch (Exception e) {
	        getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Can't Request To Elm Error in Elm Server",data);
			logger.info("************************ companyRegistrtaion ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
	     }

		 
		 if(rateResponse.getStatusCode().OK == null) {
			  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Not Requested To Elm",data);
			  logger.info("************************ companyRegistrtaion ENDED ***************************");
			  return  ResponseEntity.ok().body(getObjectResponse);
		  }
			 
		  ElmReturn elmReturn = rateResponse.getBody();


		  response.put("body", elmReturn.getBody());
		  response.put("statusCode", elmReturn.getStatusCode());
		  response.put("message", elmReturn.getMessage());

          // send Logs
		  MongoElmLogsSFDA elmLogs = new MongoElmLogsSFDA();
		  elmLogs.setUserId(parent.getId());
		  elmLogs.setUserName(parent.getName());
		  elmLogs.setTime(time);
		  elmLogs.setType(type);
		  elmLogs.setRequet(requet);
		  elmLogs.setResponse(response);
		  elmLogs.setWarehouseId(WarehouseId);
		  elmLogs.setWarehouseName(warehouse.getName());

		  elmLogsRepository.save(elmLogs);
		  
		  data.add(elmReturn);

		 if(rateResponse.getStatusCode().OK == null) {
			  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),elmReturn.getMessage(),data);
			  logger.info("************************ deviceRegistrtaion ENDED ***************************");
			  return  ResponseEntity.ok().body(getObjectResponse);
		  }
		  
		  Map resp = new HashMap();
		  resp = elmReturn.getBody();
		  
          if(resp.containsKey("errorCode")) {
			  
        	  warehouse.setReject_reason(resp.get("errorMsg").toString());
			  warehousesRepository.save(warehouse);
			  
			  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("errorMsg").toString(),data);
			  logger.info("************************ deviceRegistrtaion ENDED ***************************");
			  return  ResponseEntity.ok().body(getObjectResponse);
		  }
		  else if(resp.containsKey("resultCode")) {
			  if(resp.get("resultCode").toString().equals("success")) {
				  Map res = (Map) resp.get("result");	
				  
				  warehouse.setReject_reason(null);
				  warehouse.setReferenceKey(res.get("referenceKey").toString());
				  warehouse.setRegestration_to_elm_date(dateReg);
				  warehouse.setUpdate_date_in_elm(dateReg);
				  warehouse.setDelete_from_elm_date(null);
				  
				  
				  warehousesRepository.save(warehouse);
				  
				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"success",data);
				  logger.info("************************ deviceRegistrtaion ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
				
			  }
			  else if(resp.get("resultCode").toString().equals("duplicate")) {
				  Map res = (Map) resp.get("result");	
					
					  warehouse.setReject_reason(null);
					  warehouse.setReferenceKey(res.get("referenceKey").toString());
					  warehouse.setRegestration_to_elm_date(dateReg);
					  warehouse.setUpdate_date_in_elm(dateReg);
					  warehouse.setDelete_from_elm_date(null);
					  
					  warehousesRepository.save(warehouse);


					  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"duplicate",data);
					  logger.info("************************ deviceRegistrtaion ENDED ***************************");
					  return  ResponseEntity.ok().body(getObjectResponse);
			  }
			  else {

				  warehouse.setReject_reason(resp.get("resultCode").toString());
				  warehousesRepository.save(warehouse);
				  
				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("resultCode").toString(),data);
				  logger.info("************************ deviceRegistrtaion ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
			  }
			 
		  }
		  else {
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"cann't request to elm",data);
			logger.info("************************ deviceRegistrtaion ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);	
		  }

	}



	@Override
	public ResponseEntity<?> warehouseUpdate(String TOKEN,Map<String, String> dataObject ,Long WarehouseId, Long userId) {
		// TODO Auto-generated method stub
		
		
		// TODO Auto-generated method stub
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = formatter.format(date);
		String type = "Update Warehouse";
		Map requet = new HashMap();
		Map response = new HashMap();
		
		
		SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 

		String currentDate=formatter.format(date);
		
		Date dateReg = null;
		try {
			dateReg = output.parse(currentDate);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
		
		if(loggedUser.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "WAREHOUSE", "updateInElm")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to connectToElm",null);
				 logger.info("************************ deviceRegistrtaion ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
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
		if(isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get warehouse",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		
		if(!dataObject.containsKey("activity")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "activity shouldn't be null",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		 }
		 if(!dataObject.containsKey("name")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "name shouldn't be null",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		 }
		 if(!dataObject.containsKey("city")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "city shouldn't be null",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		 }	 
		 if(!dataObject.containsKey("address")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "address shouldn't be null",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		 }
		 if(!dataObject.containsKey("licenseNumber")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "licenseNumber shouldn't be null",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		 }
		 if(!dataObject.containsKey("licenseIssueDate")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "licenseIssueDate shouldn't be null",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		 }
		 if(!dataObject.containsKey("licenseExpiryDate")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "licenseExpiryDate shouldn't be null",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		 }
		 if(!dataObject.containsKey("phone")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "phone shouldn't be null",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		 }
		 

		  Map body = new HashMap();
		  Map bodyToMiddleWare = new HashMap();
		  
		  body.put("activity", dataObject.get("activity"));
		  body.put("name", dataObject.get("name"));
		  body.put("phone", dataObject.get("phone"));
		  body.put("address", dataObject.get("address"));
		  body.put("city", dataObject.get("city"));
		  body.put("licenseNumber", dataObject.get("licenseNumber"));
		  body.put("licenseIssueDate", dataObject.get("licenseIssueDate"));
		  body.put("licenseExpiryDate", dataObject.get("licenseExpiryDate"));

		  if(dataObject.containsKey("landAreaInSquareMeter")) {
			  body.put("landAreaInSquareMeter", dataObject.get("landAreaInSquareMeter"));

		  }
		  if(dataObject.containsKey("managerMobile")) {
			  body.put("managerMobile", dataObject.get("managerMobile"));

		  }
		  if(dataObject.containsKey("email")) {
			  body.put("email", dataObject.get("email"));

		  }
		  User parent = userRepository.findOne(createdBy);

		  String url = elmWarehouses+"/"+warehouse.getReferenceKey();
		  
		  bodyToMiddleWare.put("dataObject", body);
		  bodyToMiddleWare.put("url",url);
		  bodyToMiddleWare.put("methodType","PATCH");
		  
		  requet = bodyToMiddleWare;
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
			
			
			  restTemplate.getMessageConverters()
		        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
			  
		  HttpEntity<Object> entity = new HttpEntity<Object>(bodyToMiddleWare);

          ResponseEntity<ElmReturn> rateResponse;
		  
		  List<ElmReturn> data = new ArrayList<ElmReturn>();

		  try {
			  rateResponse = restTemplate.exchange(middleWare, HttpMethod.POST, entity, ElmReturn.class);
          } catch (Exception e) {
	        getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Can't Request To Elm Error in Elm Server",data);
			logger.info("************************ companyRegistrtaion ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
	     }

		 
		 if(rateResponse.getStatusCode().OK == null) {
			  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Not Requested To Elm",data);
			  logger.info("************************ companyRegistrtaion ENDED ***************************");
			  return  ResponseEntity.ok().body(getObjectResponse);
		  }
		  
		  ElmReturn elmReturn = rateResponse.getBody();

		  response.put("body", elmReturn.getBody());
		  response.put("statusCode", elmReturn.getStatusCode());
		  response.put("message", elmReturn.getMessage());

          // send Logs

		  
		  MongoElmLogsSFDA elmLogs = new MongoElmLogsSFDA();
		  elmLogs.setUserId(parent.getId());
		  elmLogs.setUserName(parent.getName());
		  elmLogs.setTime(time);
		  elmLogs.setType(type);
		  elmLogs.setRequet(requet);
		  elmLogs.setResponse(response);
		  elmLogs.setWarehouseId(WarehouseId);
		  elmLogs.setWarehouseName(warehouse.getName());
		  
		  elmLogsRepository.save(elmLogs);
		  
		  data.add(elmReturn);

		 if(rateResponse.getStatusCode().OK == null) {
			  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),elmReturn.getMessage(),data);
			  logger.info("************************ deviceUpdate ENDED ***************************");
			  return  ResponseEntity.ok().body(getObjectResponse);
		  }
		  
		  Map resp = new HashMap();
		  resp = elmReturn.getBody();
		 			  
          if(resp.containsKey("errorCode")) {
			  
			  warehouse.setReject_reason(resp.get("errorMsg").toString());
			  warehousesRepository.save(warehouse);
			  
			  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("errorMsg").toString(),data);
			  logger.info("************************ deviceUpdate ENDED ***************************");
			  return  ResponseEntity.ok().body(getObjectResponse);
		  }
		  else if(resp.containsKey("resultCode")) {
			  if(resp.get("success").equals(true)) {
				  warehouse.setReject_reason(null);
				  warehouse.setUpdate_date_in_elm(dateReg);
				  warehouse.setDelete_from_elm_date(null);
				  
				  
				  warehouse.setActivity(dataObject.get("activity"));
				  warehouse.setName(dataObject.get("name"));
				  warehouse.setPhone(dataObject.get("phone"));
				  warehouse.setAddress(dataObject.get("address"));
				  warehouse.setCity(dataObject.get("city"));
				  warehouse.setLicenseNumber(dataObject.get("licenseNumber"));
				  warehouse.setLicenseIssueDate(dataObject.get("licenseIssueDate"));
				  warehouse.setLicenseExpiryDate(dataObject.get("licenseExpiryDate"));

				  if(dataObject.containsKey("landAreaInSquareMeter")) {
					  warehouse.setLandAreaInSquareMeter(dataObject.get("landAreaInSquareMeter"));


				  }
				  if(dataObject.containsKey("managerMobile")) {
					  warehouse.setManagerMobile(dataObject.get("managerMobile"));

				  }
				  if(dataObject.containsKey("email")) {
					  warehouse.setEmail(dataObject.get("email"));

				  }
				  
				  
				  
				  warehousesRepository.save(warehouse);
				  
				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"success",data);
				  logger.info("************************ deviceUpdate ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
				
			  }
			  else{
					warehouse.setReject_reason(resp.get("resultCode").toString());
					warehousesRepository.save(warehouse);

					  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("resultCode").toString(),data);
					  logger.info("************************ deviceUpdate ENDED ***************************");
					  return  ResponseEntity.ok().body(getObjectResponse);
			 
			  }
			 
		  }
		  else {
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"cann't request to elm",data);
			logger.info("************************ deviceUpdate ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);	
		  }
	}




	@Override
	public ResponseEntity<?> warehouseDelete(String TOKEN, Long WarehouseId, Long userId) {
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = formatter.format(date);
		String type = "Delete Warehouse";
		Map requet = new HashMap();
		Map response = new HashMap();
		
		
		SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 

		String currentDate=formatter.format(date);
		
		Date dateReg = null;
		try {
			dateReg = output.parse(currentDate);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
		
		if(loggedUser.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "WAREHOUSE", "connectToElm")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to connectToElm",null);
				 logger.info("************************ deviceRegistrtaion ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
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
		if(isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get warehouse",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		

		  User parent = userRepository.findOne(createdBy);
		
		  Map<Object,Object>  bodyToMiddleWare = new HashMap();

		  String url = elmWarehouses+"/"+warehouse.getReferenceKey();


		  bodyToMiddleWare.put("dataObject", null);
		  bodyToMiddleWare.put("url",url);
		  bodyToMiddleWare.put("methodType","DELETE");


		  requet = bodyToMiddleWare;
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
			
			
			  restTemplate.getMessageConverters()
		        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
			  
		  HttpEntity<Object> entity = new HttpEntity<Object>(bodyToMiddleWare);
          ResponseEntity<ElmReturn> rateResponse;
		  
		  List<ElmReturn> data = new ArrayList<ElmReturn>();

		  try {
			  rateResponse = restTemplate.exchange(middleWare, HttpMethod.POST, entity, ElmReturn.class);
          } catch (Exception e) {
	        getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Can't Request To Elm Error in Elm Server",data);
			logger.info("************************ companyRegistrtaion ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
	     }

		 
		 if(rateResponse.getStatusCode().OK == null) {
			  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Not Requested To Elm",data);
			  logger.info("************************ companyRegistrtaion ENDED ***************************");
			  return  ResponseEntity.ok().body(getObjectResponse);
		  }
		  
		  ElmReturn elmReturn = rateResponse.getBody();


		  response.put("body", elmReturn.getBody());
		  response.put("statusCode", elmReturn.getStatusCode());
		  response.put("message", elmReturn.getMessage());

          //send Logs
		  
		  MongoElmLogsSFDA elmLogs = new MongoElmLogsSFDA();
		  elmLogs.setUserId(parent.getId());
		  elmLogs.setUserName(parent.getName());
		  elmLogs.setTime(time);
		  elmLogs.setType(type);
		  elmLogs.setRequet(requet);
		  elmLogs.setResponse(response);
		  elmLogs.setWarehouseId(WarehouseId);
		  elmLogs.setWarehouseName(warehouse.getName());
		  
		  elmLogsRepository.save(elmLogs);
		  
		  data.add(elmReturn);

		 if(rateResponse.getStatusCode().OK == null) {
			  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),elmReturn.getMessage(),data);
			  logger.info("************************ deviceRegistrtaion ENDED ***************************");
			  return  ResponseEntity.ok().body(getObjectResponse);
		  }
		  
		  Map resp = new HashMap();
		  resp = elmReturn.getBody();
		  
          if(resp.containsKey("errorCode")) {
			  
        	  warehouse.setReject_reason(resp.get("errorMsg").toString());
			  warehousesRepository.save(warehouse);
			  
			  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("errorMsg").toString(),data);
			  logger.info("************************ deviceRegistrtaion ENDED ***************************");
			  return  ResponseEntity.ok().body(getObjectResponse);
		  }
		  else if(resp.containsKey("resultCode")) {
			  if(resp.get("resultCode").toString().equals("success")) {
				  
				  warehouse.setReject_reason(null);
				  warehouse.setDelete_from_elm_date(dateReg);
				  
				  warehousesRepository.save(warehouse);
				  
				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"success",data);
				  logger.info("************************ deviceRegistrtaion ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
				
			  }
			  else {

				  warehouse.setReject_reason(resp.get("resultCode").toString());
				  warehousesRepository.save(warehouse);
				  
				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("resultCode").toString(),data);
				  logger.info("************************ deviceRegistrtaion ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
			  }
			 
		  }
		  else {
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"cann't request to elm",data);
			logger.info("************************ deviceRegistrtaion ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);	
		  }

	}




	@Override
	public ResponseEntity<?> inventoryRegistrtaion(String TOKEN, Long InventoryId, Long userId) {
		// TODO Auto-generated method stub
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = formatter.format(date);
		String type = "Register Inventory";
		Map requet = new HashMap();
		Map response = new HashMap();
		
		
		SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 

		String currentDate=formatter.format(date);
		
		Date dateReg = null;
		try {
			dateReg = output.parse(currentDate);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
			if(!userRoleService.checkUserHasPermission(userId, "INVENTORY", "connectToElm")) {
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
		if(isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get inventory",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		

		User parent = userRepository.findOne(createdBy);
		
		  Map<Object,Object> inventoryData = new HashMap();

		  Map<Object,Object>  bodyToMiddleWare = new HashMap();
		  inventoryData.put("activity", inventory.getActivity());
		  inventoryData.put("name", inventory.getName());
		  inventoryData.put("inventoryNumber", inventory.getInventoryNumber());
		  inventoryData.put("storingCategory", inventory.getStoringCategory());

		  Warehouse warehouse = new Warehouse();
		  if(inventory.getWarehouseId() !=null ) {
			  warehouse = warehousesRepository.findOne(inventory.getWarehouseId());
			  if(warehouse == null) {
				  getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no warehouse for this inventory",null);
				  return  ResponseEntity.badRequest().body(getObjectResponse);
			  }
		  }
		  if(inventory.getWarehouseId() ==null ) {
			  getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "no warehouse for this inventory",null);
			  return  ResponseEntity.badRequest().body(getObjectResponse);
			  
		  }
		  
		  
		  
		  String url = elmCompanies+"/"+parent.getReference_key()+"/warehouses/"+warehouse.getReferenceKey()+"/inventories";


		  bodyToMiddleWare.put("dataObject", inventoryData);
		  bodyToMiddleWare.put("url",url);
		  bodyToMiddleWare.put("methodType","POST");


		  requet = bodyToMiddleWare;
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
			
			
			  restTemplate.getMessageConverters()
		        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
			  
		  HttpEntity<Object> entity = new HttpEntity<Object>(bodyToMiddleWare);

		  
          ResponseEntity<ElmReturn> rateResponse;
		  
		  List<ElmReturn> data = new ArrayList<ElmReturn>();

		  try {
			  rateResponse = restTemplate.exchange(middleWare, HttpMethod.POST, entity, ElmReturn.class);
          } catch (Exception e) {
	        getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Can't Request To Elm Error in Elm Server",data);
			logger.info("************************ companyRegistrtaion ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
	     }

		 
		 if(rateResponse.getStatusCode().OK == null) {
			  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Not Requested To Elm",data);
			  logger.info("************************ companyRegistrtaion ENDED ***************************");
			  return  ResponseEntity.ok().body(getObjectResponse);
		  }
		  
		  ElmReturn elmReturn = rateResponse.getBody();


		  response.put("body", elmReturn.getBody());
		  response.put("statusCode", elmReturn.getStatusCode());
		  response.put("message", elmReturn.getMessage());

          //send Logs

		  MongoElmLogsSFDA elmLogs = new MongoElmLogsSFDA();
		  elmLogs.setUserId(parent.getId());
		  elmLogs.setUserName(parent.getName());
		  elmLogs.setTime(time);
		  elmLogs.setType(type);
		  elmLogs.setRequet(requet);
		  elmLogs.setResponse(response);
		  elmLogs.setInventoryId(InventoryId);
		  elmLogs.setInventoryName(inventory.getName());
		  
		  elmLogsRepository.save(elmLogs);
		  
		  data.add(elmReturn);

		 if(rateResponse.getStatusCode().OK == null) {
			  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),elmReturn.getMessage(),data);
			  logger.info("************************ deviceRegistrtaion ENDED ***************************");
			  return  ResponseEntity.ok().body(getObjectResponse);
		  }
		  
		  Map resp = new HashMap();
		  resp = elmReturn.getBody();
		  
        if(resp.containsKey("errorCode")) {
			  
      	      inventory.setReject_reason(resp.get("errorMsg").toString());
			  inventoryRepository.save(inventory);
			  
			  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("errorMsg").toString(),data);
			  logger.info("************************ deviceRegistrtaion ENDED ***************************");
			  return  ResponseEntity.ok().body(getObjectResponse);
	  }
	  else if(resp.containsKey("resultCode")) {
		  if(resp.get("resultCode").toString().equals("success")) {
			  Map res = (Map) resp.get("result");	
			  
				inventory.setReject_reason(null);
				inventory.setReferenceKey(res.get("referenceKey").toString());
				inventory.setRegestration_to_elm_date(dateReg);
				inventory.setUpdate_date_in_elm(dateReg);
				inventory.setDelete_from_elm_date(null);
				
			  inventoryRepository.save(inventory);
			  
			  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"success",data);
			  logger.info("************************ deviceRegistrtaion ENDED ***************************");
			  return  ResponseEntity.ok().body(getObjectResponse);
			
		  }
		  else if(resp.get("resultCode").toString().equals("inventory_already_registered")) {
			  Map res = (Map) resp.get("result");	
				
				inventory.setReject_reason(null);
				
				if(res.containsKey("referenceKey")) {
					inventory.setReferenceKey(res.get("referenceKey").toString());
				}
				inventory.setRegestration_to_elm_date(dateReg);
				inventory.setUpdate_date_in_elm(dateReg);
				inventory.setDelete_from_elm_date(null);
				
				inventoryRepository.save(inventory);


				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"duplicate",data);
				  logger.info("************************ deviceRegistrtaion ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
		  }
		  else {

			  inventory.setReject_reason(resp.get("resultCode").toString());
			  inventoryRepository.save(inventory);
			  
			  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("resultCode").toString(),data);
			  logger.info("************************ deviceRegistrtaion ENDED ***************************");
			  return  ResponseEntity.ok().body(getObjectResponse);
		  }
		 
	  }
	  else {
		getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"cann't request to elm",data);
		logger.info("************************ deviceRegistrtaion ENDED ***************************");
		return  ResponseEntity.ok().body(getObjectResponse);	
	  }

	}




	@Override
	public ResponseEntity<?> inventoryUpdate(String TOKEN, Map<String, String> dataObject, Long InventoryId, Long userId) {
		// TODO Auto-generated method stub
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = formatter.format(date);
		String type = "Update Inventory";
		Map requet = new HashMap();
		Map response = new HashMap();
		
		
		SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 

		String currentDate=formatter.format(date);
		
		Date dateReg = null;
		try {
			dateReg = output.parse(currentDate);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
			if(!userRoleService.checkUserHasPermission(userId, "INVENTORY", "updateInElm")) {
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
		if(isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get inventory",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}

		if(!dataObject.containsKey("activity")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "activity shouldn't be null",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		 }
		 if(!dataObject.containsKey("name")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "name shouldn't be null",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		 }
		 if(!dataObject.containsKey("storingCategory")) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "storingCategory shouldn't be null",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		 }	 
		 

		  Map body = new HashMap();
		  Map bodyToMiddleWare = new HashMap();
		  
		  body.put("activity", dataObject.get("activity"));
		  body.put("name", dataObject.get("name"));
		  body.put("storingCategory", dataObject.get("storingCategory"));
		  
		  User parent = userRepository.findOne(createdBy);

		  String url = elmInventories+"/"+inventory.getReferenceKey();
		  
		  bodyToMiddleWare.put("dataObject", body);
		  bodyToMiddleWare.put("url",url);
		  bodyToMiddleWare.put("methodType","PATCH");
		  
		  requet = bodyToMiddleWare;
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
			
			
			  restTemplate.getMessageConverters()
		        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
			  
		  HttpEntity<Object> entity = new HttpEntity<Object>(bodyToMiddleWare);

          ResponseEntity<ElmReturn> rateResponse;
		  
		  List<ElmReturn> data = new ArrayList<ElmReturn>();

		  try {
			  rateResponse = restTemplate.exchange(middleWare, HttpMethod.POST, entity, ElmReturn.class);
          } catch (Exception e) {
	        getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Can't Request To Elm Error in Elm Server",data);
			logger.info("************************ companyRegistrtaion ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
	     }

		 
		 if(rateResponse.getStatusCode().OK == null) {
			  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Not Requested To Elm",data);
			  logger.info("************************ companyRegistrtaion ENDED ***************************");
			  return  ResponseEntity.ok().body(getObjectResponse);
		  }
		  
		  ElmReturn elmReturn = rateResponse.getBody();

		  response.put("body", elmReturn.getBody());
		  response.put("statusCode", elmReturn.getStatusCode());
		  response.put("message", elmReturn.getMessage());

          // send Logs

		  
		  MongoElmLogsSFDA elmLogs = new MongoElmLogsSFDA();
		  elmLogs.setUserId(parent.getId());
		  elmLogs.setUserName(parent.getName());
		  elmLogs.setTime(time);
		  elmLogs.setType(type);
		  elmLogs.setRequet(requet);
		  elmLogs.setResponse(response);
		  elmLogs.setInventoryId(InventoryId);
		  elmLogs.setInventoryName(inventory.getName());
		  
		  elmLogsRepository.save(elmLogs);
		  
		  data.add(elmReturn);

		 if(rateResponse.getStatusCode().OK == null) {
			  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),elmReturn.getMessage(),data);
			  logger.info("************************ deviceUpdate ENDED ***************************");
			  return  ResponseEntity.ok().body(getObjectResponse);
		  }
		  
		  Map resp = new HashMap();
		  resp = elmReturn.getBody();
		 			  
          if(resp.containsKey("errorCode")) {
			  
			  inventory.setReject_reason(resp.get("errorMsg").toString());
			  inventoryRepository.save(inventory);
			  
			  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("errorMsg").toString(),data);
			  logger.info("************************ deviceUpdate ENDED ***************************");
			  return  ResponseEntity.ok().body(getObjectResponse);
		  }
		  else if(resp.containsKey("resultCode")) {
			  if(resp.get("success").equals(true)) {
				  inventory.setReject_reason(null);
				  inventory.setDelete_from_elm_date(null);
				  inventory.setUpdate_date_in_elm(dateReg);
				  
				  inventory.setActivity(dataObject.get("activity"));
				  inventory.setName(dataObject.get("name"));
				  inventory.setStoringCategory(dataObject.get("storingCategory"));

				  
				  
				  
				  inventoryRepository.save(inventory);
				  
				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"success",data);
				  logger.info("************************ deviceUpdate ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
				
			  }
			  else{
				    inventory.setReject_reason(resp.get("resultCode").toString());
					inventoryRepository.save(inventory);

					  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("resultCode").toString(),data);
					  logger.info("************************ deviceUpdate ENDED ***************************");
					  return  ResponseEntity.ok().body(getObjectResponse);
			 
			  }
			 
		  }
		  else {
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"cann't request to elm",data);
			logger.info("************************ deviceUpdate ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);	
		  }
	}




	@Override
	public ResponseEntity<?> inventoryDelete(String TOKEN, Long InventoryId, Long userId) {
		// TODO Auto-generated method stub
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = formatter.format(date);
		String type = "Delete Inventory";
		Map requet = new HashMap();
		Map response = new HashMap();
		
		
		SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 

		String currentDate=formatter.format(date);
		
		Date dateReg = null;
		try {
			dateReg = output.parse(currentDate);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
			if(!userRoleService.checkUserHasPermission(userId, "INVENTORY", "deleteFromElm")) {
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
		if(isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get inventory",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		

		User parent = userRepository.findOne(createdBy);
		

		  Map<Object,Object>  bodyToMiddleWare = new HashMap();
		  
		  
		  String url = elmInventories+"/"+inventory.getReferenceKey();


		  bodyToMiddleWare.put("dataObject", null);
		  bodyToMiddleWare.put("url",url);
		  bodyToMiddleWare.put("methodType","DELETE");


		  requet = bodyToMiddleWare;
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
			
			
			  restTemplate.getMessageConverters()
		        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
			  
		  HttpEntity<Object> entity = new HttpEntity<Object>(bodyToMiddleWare);

		  
          ResponseEntity<ElmReturn> rateResponse;
		  
		  List<ElmReturn> data = new ArrayList<ElmReturn>();

		  try {
			  rateResponse = restTemplate.exchange(middleWare, HttpMethod.POST, entity, ElmReturn.class);
          } catch (Exception e) {
	        getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Can't Request To Elm Error in Elm Server",data);
			logger.info("************************ companyRegistrtaion ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
	     }

		 
		 if(rateResponse.getStatusCode().OK == null) {
			  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Not Requested To Elm",data);
			  logger.info("************************ companyRegistrtaion ENDED ***************************");
			  return  ResponseEntity.ok().body(getObjectResponse);
		  }
		  
		  ElmReturn elmReturn = rateResponse.getBody();


		  response.put("body", elmReturn.getBody());
		  response.put("statusCode", elmReturn.getStatusCode());
		  response.put("message", elmReturn.getMessage());

          //send Logs

		  
		  MongoElmLogsSFDA elmLogs = new MongoElmLogsSFDA();
		  elmLogs.setUserId(parent.getId());
		  elmLogs.setUserName(parent.getName());
		  elmLogs.setTime(time);
		  elmLogs.setType(type);
		  elmLogs.setRequet(requet);
		  elmLogs.setResponse(response);
		  elmLogs.setInventoryId(InventoryId);
		  elmLogs.setInventoryName(inventory.getName());
		  
		  elmLogsRepository.save(elmLogs);
		  
		  data.add(elmReturn);

		 if(rateResponse.getStatusCode().OK == null) {
			  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),elmReturn.getMessage(),data);
			  logger.info("************************ deviceRegistrtaion ENDED ***************************");
			  return  ResponseEntity.ok().body(getObjectResponse);
		  }
		  
		  Map resp = new HashMap();
		  resp = elmReturn.getBody();
		  
        if(resp.containsKey("errorCode")) {
			  
      	      inventory.setReject_reason(resp.get("errorMsg").toString());
			  inventoryRepository.save(inventory);
			  
			  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("errorMsg").toString(),data);
			  logger.info("************************ deviceRegistrtaion ENDED ***************************");
			  return  ResponseEntity.ok().body(getObjectResponse);
		  }
		  else if(resp.containsKey("resultCode")) {
			  if(resp.get("resultCode").toString().equals("success")) {
				  
				  inventory.setReject_reason(null);
				  inventory.setDelete_from_elm_date(dateReg);
				  
				  inventoryRepository.save(inventory);
				  
				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"success",data);
				  logger.info("************************ deviceRegistrtaion ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
				
			  }
			  else {

				  inventory.setReject_reason(resp.get("resultCode").toString());
				  inventoryRepository.save(inventory);
				  
				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("resultCode").toString(),data);
				  logger.info("************************ deviceRegistrtaion ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
			  }
			 
		  }
		  else {
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"cann't request to elm",data);
			logger.info("************************ deviceRegistrtaion ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);	
		  }

	}

	@Override
	public ResponseEntity<?> deviceUpdateStoring(String TOKEN, Map<String, String> dataObject, Long deviceId,Long userId) {
		logger.info("************************ deviceUpdate STARTED ***************************");

		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = formatter.format(date);
		String type = "Update Vehicle Storing Category";
		Map requet = new HashMap();
		Map response = new HashMap();
		
		SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 

		String currentDate=formatter.format(date);
		
		Date dateRegUpdate = null;
		try {
			dateRegUpdate = output.parse(currentDate);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}

        if(userId == 0) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "User Id is Required",null);
			logger.info("************************ deviceUpdate ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userRepository.findOne(userId);
		
        if(loggedUser == null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is not found",null);
			logger.info("************************ deviceUpdate ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}


		if(loggedUser.getDeleteDate() != null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is deleted",null);
			logger.info("************************ deviceUpdate ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}

		if(loggedUser.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "DEVICE", "updateInElm")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to updateInElm",null);
				 logger.info("************************ deviceUpdate ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
       if(deviceId == 0) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "Device Id is Required",null);
			logger.info("************************ deviceUpdate ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
	   Device device =  deviceRepository.findOne(deviceId);
		
       if(device == null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This Device is not found",null);
			logger.info("************************ deviceUpdate ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}


		if(device.getDeleteDate() != null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This Device is deleted",null);
			logger.info("************************ deviceUpdate ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		
		 Set<User> parentClients = device.getUser();
		 User parent =null;

		 for(User object : parentClients) {
			 parent = object;
			 break;
		 }
		 if(parent == null) {
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This Device is not have parent company",null);
			logger.info("************************ deviceUpdate ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		 }

		 boolean isParent = false;

		 if(loggedUser.getAccountType().equals(4)) {
			 User parentUser =new User();

			 Set<User> parentClient = loggedUser.getUsersOfUser();
			 if(parentClient.isEmpty()) {
				    getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is type 4 not have parent can't request to elm.",null);
					logger.info("************************ deviceUpdate ENDED ***************************");
					return ResponseEntity.badRequest().body(getObjectResponse); 
			 }else {
				 for(User object : parentClient) {
					 parentUser = object ;
					 break;
				 }
				 isParent = deviceServiceImpl.checkIfParent( device ,  parentUser);
				 if(parent.getId() == userId) {
					 isParent=true;
				 }


			 }
				 
			 
		 }
		 else {
			 isParent = deviceServiceImpl.checkIfParent(device ,  loggedUser);
			 if(loggedUser.getAccountType().equals(1)) {
				 isParent=true;
			 }
			 if(parent.getId() == userId) {
				 isParent=true;
			 }
		 }
		 
		 if(isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the parent of this creater user you cannot allow to register in elm.",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		 }
	 
		 
		     if(!dataObject.containsKey("storingCategory")) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "storingCategory shouldn't be null",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }


			  Map body = new HashMap();
			  Map bodyToMiddleWare = new HashMap();
			  body.put("storingCategory", dataObject.get("storingCategory"));
			  
			  String url = elmVehicles+"/"+device.getReference_key()+"/storingCategory";
			  
			  bodyToMiddleWare.put("dataObject", body);
			  bodyToMiddleWare.put("url",url);
			  bodyToMiddleWare.put("methodType","PATCH");
			  
			  requet = bodyToMiddleWare;
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
				
				
				  restTemplate.getMessageConverters()
			        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
				  
			  HttpEntity<Object> entity = new HttpEntity<Object>(bodyToMiddleWare);

			  ResponseEntity<ElmReturn> rateResponse;
			  
			  List<ElmReturn> data = new ArrayList<ElmReturn>();

			  try {
				  rateResponse = restTemplate.exchange(middleWare, HttpMethod.POST, entity, ElmReturn.class);
	          } catch (Exception e) {
		        getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Can't Request To Elm Error in Elm Server",data);
				logger.info("************************ companyRegistrtaion ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);
		     }

			 
			 if(rateResponse.getStatusCode().OK == null) {
				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Not Requested To Elm",data);
				  logger.info("************************ companyRegistrtaion ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
			  }
			  
			  ElmReturn elmReturn = rateResponse.getBody();

			  response.put("body", elmReturn.getBody());
			  response.put("statusCode", elmReturn.getStatusCode());
			  response.put("message", elmReturn.getMessage());

	          // send Logs
			  MongoElmLogs elmLogs = new MongoElmLogs(null,parent.getId(),parent.getName(),null,null,deviceId,device.getName(),time,type,requet,response);
			  elmLogsRepository.save(elmLogs);
			  
			  
			  
			  data.add(elmReturn);

			 if(rateResponse.getStatusCode().OK == null) {
				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),elmReturn.getMessage(),data);
				  logger.info("************************ deviceUpdate ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
			  }
			  
			  Map resp = new HashMap();
			  resp = elmReturn.getBody();
			 			  
	          if(resp.containsKey("errorCode")) {
				  
				  device.setReject_reason(resp.get("errorMsg").toString());
				  deviceRepository.save(device);
				  
				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("errorMsg").toString(),data);
				  logger.info("************************ deviceUpdate ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
			  }
			  else if(resp.containsKey("resultCode")) {
				  if(resp.get("success").equals(true)) {
					  device.setReject_reason(null);
					  device.setUpdate_date_in_elm(dateRegUpdate);
					  device.setDelete_from_elm_date(null);
					  
					  device.setExpired(0);
					  
					  deviceRepository.save(device);
					  
					  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"success",data);
					  logger.info("************************ deviceUpdate ENDED ***************************");
					  return  ResponseEntity.ok().body(getObjectResponse);
					
				  }
				  else{
						device.setReject_reason(resp.get("resultCode").toString());
						deviceRepository.save(device);

						  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("resultCode").toString(),data);
						  logger.info("************************ deviceUpdate ENDED ***************************");
						  return  ResponseEntity.ok().body(getObjectResponse);
				 
				  }
				 
			  }
			  else {
				getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"cann't request to elm",data);
				logger.info("************************ deviceUpdate ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);	
			  }
	}




	@Override
	public ResponseEntity<?> warehouseInquiry(String TOKEN, Long loggedUserId , Long userId) {
		try {
			ResponseEntity loggedUserResponse = userServiceImplSFDA.userAndTokenErrorCheckerForElm(TOKEN,loggedUserId);
			if (loggedUserResponse.getStatusCode().value()!=HttpStatus.OK.value()){
				return loggedUserResponse;
			}
			ResponseEntity userResponse = userServiceImplSFDA.userErrorChecker(userId);
			if(userResponse.getStatusCode().value()!=HttpStatus.OK.value()){
				return userResponse;
			}
			Map response = new HashMap();
			User user = userServiceImpl.findById(userId);
			String userReferenceKey = user.getReference_key();
			String type = "Get Warehouse Inquiry";
			String url = elmCompanies+"/"+userReferenceKey+"/warehouses/inquiry?activity=SFDA";
			System.out.println(url);

			Map<Object,Object>  bodyToMiddleWare = new HashMap();
			Date date = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = formatter.format(date);
			SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

			String currentDate=formatter.format(date);

			Date dateReg = null;
			try {
				dateReg = output.parse(currentDate);
			} catch (ParseException e) {

				e.printStackTrace();
			}

			bodyToMiddleWare.put("url",url);
			bodyToMiddleWare.put("methodType","GET");


			Map requet = bodyToMiddleWare;
			TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

			SSLContext sslContext = null;
			try {
				sslContext = org.apache.http.ssl.SSLContexts.custom()
						.loadTrustMaterial(null, acceptingTrustStrategy)
						.build();
			} catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
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


			restTemplate.getMessageConverters()
					.add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));


			HttpEntity<Object> entity = new HttpEntity<Object>(bodyToMiddleWare);
			ResponseEntity<ElmInquiryResponse> rateResponse;

			List<ElmInquiryResponse> data = new ArrayList<ElmInquiryResponse>();

			try {
				rateResponse = restTemplate.exchange(middleWare, HttpMethod.POST, entity, ElmInquiryResponse.class);
			} catch (Exception e) {
				getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),
						"Can't Request To Elm Error in Elm Server "+e.getMessage() ,data);
				logger.info("************************ warehouseInquiry ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);
			}

			if(rateResponse.getStatusCode().OK == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Not Requested To Elm",data);
				logger.info("************************ warehouseInquiry ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);
			}

			ElmInquiryResponse elmReturn = rateResponse.getBody();


			response.put("body", elmReturn.getBody());
			response.put("statusCode", elmReturn.getStatusCode());
			response.put("message", elmReturn.getMessage());

			//send Logs

			MongoElmLogsSFDA elmLogs = new MongoElmLogsSFDA();
			elmLogs.setUserId(userId);
			elmLogs.setUserName(user.getName());
			elmLogs.setTime(time);
			elmLogs.setType(type);
			elmLogs.setRequet(requet);
			elmLogs.setResponse(response);

			elmLogsRepository.save(elmLogs);

			data.add(elmReturn);

			if(rateResponse.getStatusCode().OK == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),elmReturn.getMessage(),data);
				logger.info("************************ warehouseInquiry ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);
			}

			if(elmReturn!=null) {
				if(elmReturn.getStatusCode()==200) {
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"success",data);
					logger.info("************************ warehouseInquiry ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);

				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),elmReturn.getStatusCode().toString(),data);
					logger.info("************************ warehouseInquiry ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);
				}

			}
			else {
				getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"cann't request to elm",data);
				logger.info("************************ warehouseInquiry ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);
			}



		}catch (Exception | Error e){
			getObjectResponse = new GetObjectResponse(HttpStatus.EXPECTATION_FAILED.value(),e.getMessage(),null);
			return ResponseEntity.badRequest().body(getObjectResponse);
		}

	}

	@Override
	public ResponseEntity<?> companyRegistrtaionSFDA(String TOKEN, Long userId, Long loggedUserId) {
		// TODO Auto-generated method stub
		logger.info("************************ companyRegistrtaion STARTED ***************************");

		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = formatter.format(date);
		String type = "Register Company";
		Map requet = new HashMap();
		Map response = new HashMap();
		
		SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 

		String currentDate=formatter.format(date);
		Date dateReg = null;
		try {
			dateReg = output.parse(currentDate);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}

        if(userId == 0) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "User Id is Required",null);
			logger.info("************************ companyRegistrtaion ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User user = userRepository.findOne(userId);
		
        if(user == null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is not found",null);
			logger.info("************************ companyRegistrtaion ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}


		if(user.getDeleteDate() != null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is deleted",null);
			logger.info("************************ companyRegistrtaion ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		
		
        if(loggedUserId == 0) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "loggedUserId is Required",null);
			logger.info("************************ companyRegistrtaion ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUserToCheck = userRepository.findOne(loggedUserId);
		
        if(loggedUserToCheck == null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This logged user is not found",null);
			logger.info("************************ companyRegistrtaion ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}


		if(loggedUserToCheck.getDeleteDate() != null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This logged user is deleted",null);
			logger.info("************************ companyRegistrtaion ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}

		if(loggedUserToCheck.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(loggedUserId, "USER", "connectToElm")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this logged user doesnot has permission to connectToElm",null);
				 logger.info("************************ companyRegistrtaion ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		if(user.getAccountType().equals(4)) {
			 Set<User> parentClients = user.getUsersOfUser();
			 if(parentClients.isEmpty()) {
				
				 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "you cannot get geofences of this user",null);
				 logger.info("************************ companyRegistrtaion ENDED ***************************");
				 return  ResponseEntity.status(404).body(getObjectResponse);
			 }else {
				 User parentClient = new User() ;
				 for(User object : parentClients) {
					 parentClient = object;
					 break;
				 }
				 
				 boolean isParent = false; 

				 
				 if(parentClient.getId() == loggedUserId) {
					 isParent =true;
				 }
				 
				 List<User> parents=userServiceImpl.getAllParentsOfuser(parentClient,parentClient.getAccountType());
				 
				 User parentCli = new User();
				 for(User object : parents) {
					 parentCli = object;
					 if(loggedUserId == parentCli.getId()) {
						isParent =true;
						break;
					 }
					 
				 }
				 
				 if(isParent == false) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the parent of this creater user you cannot allow to register in elm.",null);
					return  ResponseEntity.badRequest().body(getObjectResponse);
				 }
				 
				 
			 }
		}
		else {
			 boolean isParent = false; 

			if(loggedUserToCheck.getAccountType().equals(1)) {
				isParent =true;
			}
			 List<User> parents=userServiceImpl.getAllParentsOfuser(user,user.getAccountType());
			 User parentClient = new User();
			 for(User object : parents) {
				 parentClient = object;
				 if(loggedUserId == parentClient.getId()) {
					isParent =true;
					break;
				 }
			 }
			 if(userId == loggedUserId) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "You can't register your self in elm.",null);
				 return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 if(isParent == false) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the parent of this creater user you cannot allow to register in elm.",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
		}
		
		
		

		  Map bodyToMiddleWare = new HashMap();

		  bodyToMiddleWare.put("url",elmCompanies);
		  bodyToMiddleWare.put("methodType","POST");
		  
		  if(user.getIsCompany().equals(1)) {
			  CompanyElmDataSFDA companyElmData = new CompanyElmDataSFDA();

			  companyElmData.setCommercialRecordIssueDateHijri(user.getCommercial_reg());
			  companyElmData.setCommercialRecordNumber(user.getCommercial_num());
			  companyElmData.setManagerMobileNumber(user.getManager_mobile());
			  companyElmData.setManagerName(user.getManager_name());
			  companyElmData.setManagerPhoneNumber( user.getManager_phone());
		
			  companyElmData.setEmailAddress(user.getEmail());
			  companyElmData.setExtensionNumber(user.getPhone());
			  companyElmData.setIdentityNumber(user.getIdentity_num());
			  companyElmData.setPhoneNumber(user.getCompany_phone());

			  JSONObject obj = new JSONObject();
			  if(user.getAttributes() != null) {
				  if(user.getAttributes().toString().startsWith("{")) {
					  obj = new JSONObject(user.getAttributes());
		          	  if(obj.has("sfdaCompanyActivity")) {
		              	  companyElmData.setSfdaCompanyActivity(obj.getString("sfdaCompanyActivity"));

		          	  }
		          	  if(obj.has("activity")) {
		              	  companyElmData.setActivity(obj.getString("activity"));
						  System.out.println(obj.getString("activity"));

		        	  }
				  }

	          	 
			  }
          	  
          	  
          	  
			  bodyToMiddleWare.put("dataObject", companyElmData);

		  }
		  else {

			  if(user.getDateType().equals(1)) {
				  
				  IndividualHijriElmDataSFDA  individualHijriElmData = new IndividualHijriElmDataSFDA();
				  individualHijriElmData.setEmailAddress(user.getEmail());
				  individualHijriElmData.setExtensionNumber(user.getPhone());
				  individualHijriElmData.setIdentityNumber(user.getIdentity_num());
				  individualHijriElmData.setPhoneNumber(user.getCompany_phone());
				  individualHijriElmData.setDateOfBirthHijri(user.getDateOfBirth());
				  
				  JSONObject obj = new JSONObject();
				  if(user.getAttributes() != null) {
					  if(user.getAttributes().toString().startsWith("{")) {
						  obj = new JSONObject(user.getAttributes().toString());
			          	  if(obj.has("sfdaCompanyActivity")) {
			          		individualHijriElmData.setSfdaCompanyActivity(obj.getString("sfdaCompanyActivity"));

			          	  }
			          	  if(obj.has("activity")) {
			          		individualHijriElmData.setActivity(obj.getString("activity"));

			        	  }
					  }
				  }
	          	  
				  bodyToMiddleWare.put("dataObject", individualHijriElmData);


			  }
			  else {
				  IndividualGregorianElmDataSFDA  individualGregorianElmData = new IndividualGregorianElmDataSFDA();
				  
				  individualGregorianElmData.setEmailAddress(user.getEmail());
				  individualGregorianElmData.setExtensionNumber(user.getPhone());
				  individualGregorianElmData.setIdentityNumber(user.getIdentity_num());
				  individualGregorianElmData.setPhoneNumber(user.getCompany_phone());
				  individualGregorianElmData.setDateOfBirthGregorian(user.getDateOfBirth());
				 	          	  

				 
				  JSONObject obj = new JSONObject();
				  if(user.getAttributes() != null) {
					  if(user.getAttributes().toString().startsWith("{")) {
						  obj = new JSONObject(user.getAttributes().toString());
			          	  if(obj.has("sfdaCompanyActivity")) {
			          		individualGregorianElmData.setSfdaCompanyActivity(obj.getString("sfdaCompanyActivity"));

			          	  }
			          	  if(obj.has("activity")) {
			          		individualGregorianElmData.setActivity(obj.getString("activity"));

			        	  }
					  }

		          	  
				  }
	          	  
	          	  
	          	  
				  bodyToMiddleWare.put("dataObject", individualGregorianElmData);


			  }

		  }
		  

		  
		  requet = bodyToMiddleWare;
		  		  
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
			
			
			  restTemplate.getMessageConverters()
		        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
			  
		  HttpEntity<Object> entity = new HttpEntity<Object>(bodyToMiddleWare);
		  ResponseEntity<ElmReturn> rateResponse ;
		  List<ElmReturn> data = new ArrayList<ElmReturn>();

		 try {
			 rateResponse = restTemplate.exchange(middleWare, HttpMethod.POST, entity, ElmReturn.class);
	     } catch (Exception e) {
	        getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Can't Request To Elm Error in Elm Server",data);
			logger.info("************************ companyRegistrtaion ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
	     }

		 
		 if(rateResponse.getStatusCode().OK == null) {
			  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Not Requested To Elm",data);
			  logger.info("************************ companyRegistrtaion ENDED ***************************");
			  return  ResponseEntity.ok().body(getObjectResponse);
		  }
		 
		 
		  ElmReturn elmReturn = rateResponse.getBody();

		  response.put("body", elmReturn.getBody());
		  response.put("statusCode", elmReturn.getStatusCode());
		  response.put("message", elmReturn.getMessage());


		  MongoElmLogs elmLogs = new MongoElmLogs(null,userId,user.getName(),null,null,null,null,time,type,requet,response);
		  elmLogsRepository.save(elmLogs);
		  
		  
		  data.add(elmReturn);
		 
		  
		  Map resp = new HashMap();
		  resp = elmReturn.getBody();

		  if(resp.containsKey("errorCode")) {
			  
			  user.setReject_reason(resp.get("errorMsg").toString());
			  userRepository.save(user);
			  
			  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("errorMsg").toString(),data);
			  logger.info("************************ companyRegistrtaion ENDED ***************************");
			  return  ResponseEntity.ok().body(getObjectResponse);
		  }
		  else if(resp.containsKey("resultCode")) {
			  if(resp.get("resultCode").toString().equals("success")) {
				  Map res = (Map) resp.get("result");	
				  user.setReject_reason(null);
				  user.setReference_key(res.get("referenceKey").toString());
				  user.setRegestration_to_elm_date(dateReg);
				  user.setUpdate_date_in_elm(dateReg);
				  user.setDelete_from_elm_date(null);

				  userRepository.save(user);

					  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"success",data);
					  logger.info("************************ companyRegistrtaion ENDED ***************************");
					  return  ResponseEntity.ok().body(getObjectResponse);
					
			  }
			  else if(resp.get("resultCode").toString().equals("duplicate")) {
					
				  Map res = (Map) resp.get("result");	
				  user.setReject_reason(null);
				  user.setReference_key(res.get("referenceKey").toString());
				  user.setRegestration_to_elm_date(dateReg);
				  user.setUpdate_date_in_elm(dateReg);
				  user.setDelete_from_elm_date(null);
				  
				  userRepository.save(user);

				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"duplicate",data);
				  logger.info("************************ companyRegistrtaion ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
			  }
			  else {

				  user.setReject_reason(resp.get("resultMsg").toString());
				  userRepository.save(user);
				  
				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("resultMsg").toString(),data);
				  logger.info("************************ companyRegistrtaion ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
			  }
			 
		  }
		  else {
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"cann't request to elm",data);
			logger.info("************************ companyRegistrtaion ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);	
		  }
			
	}
	
	/**
	 * register device in elm using id to get data 
	 */
	@Override
	public ResponseEntity<?> deviceRegistrtaionSFDA(String TOKEN, Long deviceId, Long userId) {
		logger.info("************************ deviceRegistrtaion STARTED ***************************");

		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = formatter.format(date);
		String type = "Register Vehicle";
		Map requet = new HashMap();
		Map response = new HashMap();
		
		
		SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 

		String currentDate=formatter.format(date);
		
		Date dateReg = null;
		try {
			dateReg = output.parse(currentDate);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}

		
        if(userId == 0) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "User Id is Required",null);
			logger.info("************************ deviceRegistrtaion ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userRepository.findOne(userId);
		
        if(loggedUser == null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is not found",null);
			logger.info("************************ deviceRegistrtaion ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}


		if(loggedUser.getDeleteDate() != null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This user is deleted",null);
			logger.info("************************ deviceRegistrtaion ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}

		if(loggedUser.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "DEVICE", "connectToElm")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to connectToElm",null);
				 logger.info("************************ deviceRegistrtaion ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		
		
        if(deviceId == 0) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "Device Id is Required",null);
			logger.info("************************ deviceRegistrtaion ENDED ***************************");
			return ResponseEntity.badRequest().body(getObjectResponse);
		}
		Device device = deviceRepositorySFDA.findOne(deviceId);
		
        if(device == null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This Device is not found",null);
			logger.info("************************ deviceRegistrtaion ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}


		if(device.getDeleteDate() != null) {
			
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This Device is deleted",null);
			logger.info("************************ deviceRegistrtaion ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		}
		
		 Set<User> parentClients = device.getUser();
		 User parent =new User();

		 for(User object : parentClients) {
			 parent = object;
			 break;
		 }
		 if(parent == null) {
			getObjectResponse = new GetObjectResponse( HttpStatus.NOT_FOUND.value(), "This Device is not have parent company",null);
			logger.info("************************ deviceRegistrtaion ENDED ***************************");
			return ResponseEntity.status(404).body(getObjectResponse);
		 }

		 boolean isParent = false;

		 if(loggedUser.getAccountType().equals(4)) {
			 User parentUser =new User();

			 Set<User> parentClient = loggedUser.getUsersOfUser();
			 if(parentClient.isEmpty()) {
				    getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user is type 4 not have parent can't request to elm.",null);
					logger.info("************************ deviceRegistrtaion ENDED ***************************");
					return ResponseEntity.badRequest().body(getObjectResponse); 
			 }else {
				 for(User object : parentClient) {
					 parentUser = object ;
					 break;
				 }
				 isParent = deviceServiceImpl.checkIfParent( device ,  parentUser);
				 if(parent.getId() == userId) {
					 isParent=true;
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
		 else {
			 isParent = deviceServiceImpl.checkIfParent(device ,  loggedUser);
			 if(loggedUser.getAccountType().equals(1)) {
				 isParent=true;
			 }
			 if(parent.getId() == userId) {
				 isParent=true;
			 }
		 }
		 
		 if(isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not the parent of this creater user you cannot allow to register in elm.",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		 }
	 
		 
//		 
//		  Map device_data = new HashMap();
//		  Map vehiclePlate = new HashMap();
//		  Map bodyToMiddleWare = new HashMap();
//
////		  device_data.put("activity", device.getActivity());
////		  device_data.put("storingCategory", device.getStoringCategory());
////		  
//		  device_data.put("sequenceNumber", device.getSequence_number());
//		  device_data.put("plateType", device.getPlate_type());
//		  device_data.put("imeiNumber", device.getUniqueid());
//
//		  vehiclePlate.put("number", device.getPlate_num());
//		  vehiclePlate.put("rightLetter", device.getRight_letter());
//		  vehiclePlate.put("middleLetter", device.getMiddle_letter());
//		  vehiclePlate.put("leftLetter", device.getLeft_letter());
//		  
//		  device_data.put("vehiclePlate",vehiclePlate);

		  Map bodyToMiddleWare = new HashMap();

		  DeviceElmDataSFDA deviceElmData = new DeviceElmDataSFDA();
		  VehiclePlate vehiclePlate = new VehiclePlate();

		  vehiclePlate.setNumber(device.getPlate_num());
		  vehiclePlate.setRightLetter(device.getRight_letter());
		  vehiclePlate.setMiddleLetter(device.getMiddle_letter());
		  vehiclePlate.setLeftLetter(device.getLeft_letter());
		  
		  deviceElmData.setImeiNumber(device.getUniqueid());
		  deviceElmData.setPlateType(device.getPlate_type());
		  deviceElmData.setSequenceNumber(device.getSequence_number());
		  deviceElmData.setVehiclePlate(vehiclePlate);

		  JSONObject obj = new JSONObject();
		  
		  if(device.getAttributes() != null) {
			  if(device.getAttributes().toString().startsWith("{")) {
				  obj = new JSONObject(device.getAttributes().toString());		
		      	  
		      	  if(obj.has("activity")) {
		          	  deviceElmData.setActivity(obj.getString("activity"));

		      	  }
		      	  if(obj.has("storingCategory")) {
		          	  deviceElmData.setStoringCategory(obj.getString("storingCategory"));

		    	  }
			  }
			  
		  }
      	 
      	  
		  String url = elmCompanies+"/"+parent.getReference_key()+"/vehicles";


		  bodyToMiddleWare.put("dataObject", deviceElmData);
		  bodyToMiddleWare.put("url",url);
		  bodyToMiddleWare.put("methodType","POST");

		  
		  requet = bodyToMiddleWare;
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
			
			
			  restTemplate.getMessageConverters()
		        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
			  
		  HttpEntity<Object> entity = new HttpEntity<Object>(bodyToMiddleWare);

		  ResponseEntity<ElmReturn> rateResponse;
 
		 List<ElmReturn> data = new ArrayList<ElmReturn>();

		  try {
			  rateResponse = restTemplate.exchange(middleWare, HttpMethod.POST, entity, ElmReturn.class);
          } catch (Exception e) {
	        getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Can't Request To Elm Error in Elm Server",data);
			logger.info("************************ companyRegistrtaion ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);
	     }

		
		 if(rateResponse.getStatusCode().OK == null) {
			  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Not Requested To Elm",data);
			  logger.info("************************ companyRegistrtaion ENDED ***************************");
			  return  ResponseEntity.ok().body(getObjectResponse);
		  }
		  
		  ElmReturn elmReturn = rateResponse.getBody();

		  
		  response.put("body", elmReturn.getBody());
		  response.put("statusCode", elmReturn.getStatusCode());
		  response.put("message", elmReturn.getMessage());


		  MongoElmLogs elmLogs = new MongoElmLogs(null,parent.getId(),parent.getName(),null,null,deviceId,device.getName(),time,type,requet,response);
		  elmLogsRepository.save(elmLogs);
		  
		  data.add(elmReturn);

		
		  Map resp = new HashMap();
		  resp = elmReturn.getBody();
		  
          if(resp.containsKey("errorCode")) {
			  
			  device.setReject_reason(resp.get("errorMsg").toString());
			  deviceRepository.save(device);
			  
			  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("errorMsg").toString(),data);
			  logger.info("************************ deviceRegistrtaion ENDED ***************************");
			  return  ResponseEntity.ok().body(getObjectResponse);
		  }
		  else if(resp.containsKey("resultCode")) {
			  if(resp.get("resultCode").toString().equals("success")) {
				  Map res = (Map) resp.get("result");	
				  
				  device.setReject_reason(null);
				  device.setReference_key(res.get("referenceKey").toString());
				  device.setRegestration_to_elm_date(dateReg);
				  device.setUpdate_date_in_elm(dateReg);
				  device.setDelete_from_elm_date(null);

				  device.setExpired(0);
				 
				  deviceRepository.save(device);
				  
				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"success",data);
				  logger.info("************************ deviceRegistrtaion ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
				
			  }
			  else if(resp.get("resultCode").toString().equals("duplicate")) {
				  Map res = (Map) resp.get("result");	
					
				  device.setReject_reason(null);
				  device.setReference_key(res.get("referenceKey").toString());
				  device.setRegestration_to_elm_date(dateReg);
				  device.setUpdate_date_in_elm(dateReg);
				  device.setExpired(0);
				  device.setDelete_from_elm_date(null);
					
				  deviceRepository.save(device);

				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"duplicate",data);
				  logger.info("************************ deviceRegistrtaion ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
			  }
			  else {

				  device.setReject_reason(resp.get("resultCode").toString());
				  deviceRepository.save(device);
				  
				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),resp.get("resultCode").toString(),data);
				  logger.info("************************ deviceRegistrtaion ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
			  }
			 
		  }
		  else {
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"cann't request to elm",data);
			logger.info("************************ deviceRegistrtaion ENDED ***************************");
			return  ResponseEntity.ok().body(getObjectResponse);	
		  }
	}

	@Override
	public ResponseEntity<?> statsInventories() {
		

		// TODO Auto-generated method stub
		List<ElmReturn> data = new ArrayList<ElmReturn>();

		List<MonogoInventoryLastDataElmSend> inventories = mongoInventoryLastDataRepositoryElmData.findByIdsIn(new PageRequest(0, 10));

		for(MonogoInventoryLastDataElmSend inventory:inventories) {
			List<String> ids = new ArrayList<String>();
			ids.add(inventory.get_id().toString());
			mongoInventoryLastDataRepositoryElmData.deleteByIdIn(ids);

			//data to send to elm
			Map requet = new HashMap();
			Map response = new HashMap();
			Map<Object,Object> inventoryData = new HashMap();
		    Map<Object,Object>  bodyToMiddleWare = new HashMap();
		    inventoryData.put("activity", inventory.getActivity());
		    inventoryData.put("name", inventory.getName());
		    inventoryData.put("temperature", inventory.getTemperature());
		    inventoryData.put("humidity", inventory.getHumidity());
		    
		    String url = elmInventories+"/"+inventory.getReferenceKey()+"/stats";

			  bodyToMiddleWare.put("dataObject", inventoryData);
			  bodyToMiddleWare.put("url",url);
			  bodyToMiddleWare.put("methodType","POST");


			  requet = bodyToMiddleWare;
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
				
				
				  restTemplate.getMessageConverters()
			        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
				  
			  HttpEntity<Object> entity = new HttpEntity<Object>(bodyToMiddleWare);

			  

			 ResponseEntity<ElmReturn> rateResponse ;
			 
			Date date = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = formatter.format(date);
			String type = "Stats Inventories";

				

			 
			 try {
				 rateResponse = restTemplate.exchange(middleWare, HttpMethod.POST, entity, ElmReturn.class);
		     } catch (Exception e) {
		        getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Can't Request To Elm Error in Elm Server",data);
				logger.info("************************ companyRegistrtaion ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);
		     }

			 
			 if(rateResponse.getStatusCode().OK == null) {
				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"Not Requested To Elm",data);
				  logger.info("************************ companyRegistrtaion ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
			  }
			  
			  ElmReturn elmReturn = rateResponse.getBody();


			  response.put("body", elmReturn.getBody());
			  response.put("statusCode", elmReturn.getStatusCode());
			  response.put("message", elmReturn.getMessage());
		    
			  data.add(elmReturn);
			  

			  MongoElmLogs elmLogs = new MongoElmLogs(null,null,null,null,null,null,null,time,type,requet,response);
			  elmLogsRepository.save(elmLogs);
			

			
		}
	    getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"success",data);
	    logger.info("************************ deviceRegistrtaion ENDED ***************************");
	    return  ResponseEntity.ok().body(getObjectResponse);
	}
	
	/**
	 * send locations of devices from mongo collection tc_positions_elm
	 */
	@Override
	public ResponseEntity<?> lastLocationsSFDA() {
		
		// TODO Auto-generated method stub
		
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = formatter.format(date);
		String type = "Location";
		Map requet = new HashMap();
		Map response = new HashMap();
		
		logger.info("************************ lastLocations STARTED ***************************");

		List<Map> dataArray = new ArrayList<Map>();
		List<String> ids = new ArrayList<String>();


		List<MongoElmLiveLocationSFDA> positions = mongoElmLiveLocationRepositorySFDA.findByIdsIn(new PageRequest(0, 1000));
		
		for(MongoElmLiveLocationSFDA position:positions) {
			Map record = new HashMap();

			if(position.getTemperature() != null && !Objects.equals(position.getTemperature(), "")){
				if(Double.parseDouble(position.getTemperature()) >= 100){
					position.setTemperature("90");
				}
			}


			record.put("referenceKey", position.getReferenceKey());
			record.put("driverReferenceKey", position.getDriverReferenceKey());
			record.put("latitude", position.getLatitude());
			record.put("longitude", position.getLongitude());
			record.put("velocity", position.getVelocity() * (1.852) );
			record.put("weight", position.getWeight());
			record.put("locationTime", position.getLocationTime());
			record.put("vehicleStatus", position.getVehicleStatus());
			record.put("address", position.getAddress());
			record.put("roleCode", position.getRoleCode());
			record.put("humidity", position.getHumidity());
			record.put("temperature", position.getTemperature());

			ids.add(position.get_id().toString());
			
			dataArray.add(record);
		}
		List<ElmReturn> data = new ArrayList<ElmReturn>();
		if(dataArray.size() > 0) {
			  Map body = new HashMap();

	    	  body.put("activity","SFDA");
			  body.put("vehicleLocations", dataArray);
			
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
				
				
				  restTemplate.getMessageConverters()
			        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
				  
				  Map bodyToMiddleWare = new HashMap();


				  

				  bodyToMiddleWare.put("dataObject", body);
				  bodyToMiddleWare.put("url",elmLocations);
				  bodyToMiddleWare.put("methodType","POST");
				  
			  HttpEntity<Object> entity = new HttpEntity<Object>(bodyToMiddleWare);

			  ResponseEntity<ElmReturn> rateResponse = restTemplate.exchange(middleWare, HttpMethod.POST, entity, ElmReturn.class);

			  
			  ElmReturn elmReturn = rateResponse.getBody();

			  data.add(elmReturn);

			 if(rateResponse.getStatusCode().OK == null) {
				  getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),elmReturn.getMessage(),data);
				  logger.info("************************ companyRegistrtaion ENDED ***************************");
				  return  ResponseEntity.ok().body(getObjectResponse);
			  }
			  
			  Map resp = new HashMap();
			  resp = elmReturn.getBody();  
			  
			  requet = bodyToMiddleWare;
			  ElmReturn elmReturnd = rateResponse.getBody();

			  response.put("body", elmReturnd.getBody());
			  response.put("statusCode", elmReturnd.getStatusCode());
			  response.put("message", elmReturnd.getMessage());
			  
			  MongoElmLogs elmLogs = new MongoElmLogs(null,null,null,null,null,null,null,time,type,requet,response);
			  elmLogsRepository.save(elmLogs);
			  

	      	mongoElmLiveLocationRepositorySFDA.deleteByIdIn(ids);
		}
    	
	    getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(),"success",data,dataArray.size());
		logger.info("************************ lastLocations ENDED ***************************");
		return  ResponseEntity.ok().body(getObjectResponse);
	}

	@Override
	public ResponseEntity<?> findLastPositionsSequenceNumberSpeedZero(String sequenceNumber) {
		logger.info("************************ findLastPositionsSequenceNumberSpeedZero Started ***************************");

		Device device = deviceRepository.getDeviceBySequenceNumber(sequenceNumber);
		List<MongoPositions> positionsZeroSpeed = mongoPositionsRepository
				.findTop10ByDeviceidAndSpeedOrderByServertimeDesc(device.getId(),0.0);
		logger.info("************************ findLastPositionsSequenceNumberSpeedZero Get Data With Size "+positionsZeroSpeed.size()+" ***************************");

		Map dataFinal= new HashMap();
		dataFinal.put("positionZeroSpeed", positionsZeroSpeed);
		List<Map<Object,Object>> result = new ArrayList<>();
		result.add(dataFinal);

		logger.info("************************ findLastPositionsSequenceNumberSpeedZero ENDED ***************************");

		getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",result);
		return  ResponseEntity.ok().body(getObjectResponse);

	}

	@Override
	public ResponseEntity<?> findLastPositionsSequenceNumberNoneSpeedZero(String sequenceNumber) {
		logger.info("************************ findLastPositionsSequenceNumberNoneSpeedZero Started ***************************");

		Device device = deviceRepository.getDeviceBySequenceNumber(sequenceNumber);
		List<MongoPositions> positionsZeroSpeed = mongoPositionsRepository
				.findTop10ByDeviceidAndSpeedAfterOrderByServertimeDesc(device.getId(),0.0);
		logger.info("************************ findLastPositionsSequenceNumberNoneSpeedZero Get Data With Size "+positionsZeroSpeed.size()+" ***************************");

		Map dataFinal= new HashMap();
		dataFinal.put("positionsGreaterZeroSpeed", positionsZeroSpeed);
		List<Map<Object,Object>> result = new ArrayList<Map<Object,Object>>();
		result.add(dataFinal);

		logger.info("************************ findLastPositionsSequenceNumberNoneSpeedZero ENDED ***************************");

		getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",result);
		return  ResponseEntity.ok().body(getObjectResponse);

	}

	@Override
	public ResponseEntity<?> findLastZeroVelocityPositionsBySequenceNumber(String sequenceNumber) {
		logger.info("************************ findLastZeroVelocityPositionsBySequenceNumber Started ***************************");

		Device device = deviceRepository.getDeviceBySequenceNumber(sequenceNumber);

		List<LastElmData> positionsZeroVelocity = new ArrayList<LastElmData>();
		if(device.getReference_key() != null) {
			positionsZeroVelocity = mongoPositionRepo.getLastPositionVelocityZero(device.getReference_key());

		}

		Map dataFinal= new HashMap();
		dataFinal.put("positionsZeroVelocity", positionsZeroVelocity);
		List<Map<Object,Object>> result = new ArrayList<Map<Object,Object>>();
		result.add(dataFinal);

		logger.info("************************ findLastZeroVelocityPositionsBySequenceNumber ENDED ***************************");

		getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",result);
		return  ResponseEntity.ok().body(getObjectResponse);

	}

	@Override
	public ResponseEntity<?> findLastNoneZeroVelocityPositionsBySequenceNumber(String sequenceNumber) {
		logger.info("************************ findLastNoneZeroVelocityPositionsBySequenceNumber Started ***************************");

		Device device = deviceRepository.getDeviceBySequenceNumber(sequenceNumber);

		List<LastElmData> positionsGreaterZeroVelocity = new ArrayList<>();

		if(device.getReference_key() != null) {
			positionsGreaterZeroVelocity = mongoPositionRepo.getLastPositionGreaterVelocityZero(device.getReference_key());

		}


		Map dataFinal= new HashMap();
		dataFinal.put("positionsGreaterZeroVelocity", positionsGreaterZeroVelocity);
		List<Map<Object,Object>> result = new ArrayList<Map<Object,Object>>();
		result.add(dataFinal);

		logger.info("************************ findLastNoneZeroVelocityPositionsBySequenceNumber ENDED ***************************");

		getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",result);
		return  ResponseEntity.ok().body(getObjectResponse);

	}

	@Override
	public ResponseEntity<?> findDeviceData(String sequenceNumber) {
		logger.info("************************ findDeviceData Started ***************************");
		if(sequenceNumber.equals("")) {

			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "No Sequence Number Selected",null);
			return  ResponseEntity.ok().body(getObjectResponse);
		}

		Device device = deviceRepository.getDeviceBySequenceNumber(sequenceNumber);

		if(device == null) {
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "No Data For This Sequence Number In DB",null);
			return  ResponseEntity.ok().body(getObjectResponse);
		}


		List<Map<Object,Object>> result = new ArrayList<>();


		Map dataFinal= new HashMap();

		Map deviceData= new HashMap();
		deviceData.put("Name", device.getName());
		deviceData.put("Unique Id", device.getUniqueid());
		deviceData.put("Sequence Number", device.getSequence_number());

		if(device.getExpired() == 1) {
			deviceData.put("Expired", "Expired");

		}
		else {
			deviceData.put("Expired", "Not Expired");
		}

		if(device.getReference_key() != null) {
			deviceData.put("Reference Key", device.getReference_key());

		}
		else {
			deviceData.put("Reference Key", "No Reference Key");
		}

		List<Map> calibrationData=new ArrayList<Map>();
		deviceData.put("Calibration Data", calibrationData);

		if(device.getCalibrationData() != null) {

			String str = device.getCalibrationData().toString();
			String arrOfStr[] = str.split(" ");

			for (String a : arrOfStr) {
				JSONObject obj =new JSONObject(a);
				Map list   = new HashMap<>();
				list.put("s1",obj.get("s1"));
				list.put("s2",obj.get("s2"));
				list.put("w",obj.get("w"));
				calibrationData.add(list);

			}
			deviceData.put("Calibration Data", calibrationData);

		}
		Map lineData   = new HashMap<>();
		deviceData.put("Line Data", lineData);

		if(device.getLineData() != null && !device.getLineData().equals("")) {
			JSONObject obj= new JSONObject(device.getLineData());
			lineData.put("slope",obj.get("slope"));
			lineData.put("factor",obj.get("factor"));

			deviceData.put("Line Data", lineData);

		}

		dataFinal.put("deviceData", deviceData);

		result.add(dataFinal);

		getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",result);
		logger.info("************************ findDeviceData Started ***************************");
		return  ResponseEntity.ok().body(getObjectResponse);

	}

	@Override
	public ResponseEntity<?> findDeviceLastPosition(String sequenceNumber) {
		// TODO Auto-generated method stub
		logger.info("************************ findDeviceLastPosition STARTED ***************************");


		if(sequenceNumber.equals("")) {

			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "No Sequence Number Selected",null);
			return  ResponseEntity.ok().body(getObjectResponse);
		}

		Device device = deviceRepository.getDeviceBySequenceNumber(sequenceNumber);

		if(device == null) {
			getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "No Data For This Sequence Number In DB",null);
			return  ResponseEntity.ok().body(getObjectResponse);
		}

		List<Map<Object,Object>> result = new ArrayList<>();

		Map dataFinal= new HashMap();

		LastPositionData position = new LastPositionData();

		position = mongoPositionRepo.getLastPosition(device.getId());

		dataFinal.put("positionData", position);

		result.add(dataFinal);

		getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",result);
		logger.info("************************ findDeviceLastPosition STARTED ***************************");

		return  ResponseEntity.ok().body(getObjectResponse);

	}
}
