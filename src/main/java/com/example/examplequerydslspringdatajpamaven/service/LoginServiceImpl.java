package com.example.examplequerydslspringdatajpamaven.service;

import java.nio.charset.StandardCharsets;
import java.util.*;

import com.example.examplequerydslspringdatajpamaven.repository.DeviceRepository;
import com.example.food_drugs.helpers.Impl.AssistantServiceImpl;
import com.example.food_drugs.repository.InventoryRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.example.examplequerydslspringdatajpamaven.Validator.JWKValidator;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.entity.UserRole;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;
import com.example.examplequerydslspringdatajpamaven.tokens.TokenSecurity;

/**
 * services functionality related to login
 * @author fuinco
 *
 */
@Component
@Service
public class LoginServiceImpl extends RestServiceController implements LoginService  {
	
	private static final Log logger = LogFactory.getLog(LoginServiceImpl.class);

	private final TokenSecurity tokenSecurity;
	
	private final UserRepository userRepository;
	 
	private final UserServiceImpl userServiceImpl;
	
	private final JWKValidator jwkValidator;

	private final UserRoleService userRoleService;

	private final DeviceRepository deviceRepository;

	private final AssistantServiceImpl assistantServiceImpl;

	private GetObjectResponse getObjectResponse;
	private  final InventoryRepository inventoryRepository;

	public LoginServiceImpl(TokenSecurity tokenSecurity, UserRepository userRepository, UserServiceImpl userServiceImpl, JWKValidator jwkValidator, UserRoleService userRoleService, DeviceRepository deviceRepository, AssistantServiceImpl assistantServiceImpl, InventoryRepository inventoryRepository) {
		this.tokenSecurity = tokenSecurity;
		this.userRepository = userRepository;
		this.userServiceImpl = userServiceImpl;
		this.jwkValidator = jwkValidator;
		this.userRoleService = userRoleService;
		this.deviceRepository = deviceRepository;
		this.assistantServiceImpl = assistantServiceImpl;
		this.inventoryRepository = inventoryRepository;
	}

	/**
	 * login using email and password return data of user with token
	 */

	@Override
	public ResponseEntity<?> login(String authorization) {
		List<Long> userIds;
		logger.info("************************ Login STARTED ***************************");
		if(authorization != "" && authorization.toLowerCase().startsWith("basic")) {

			String base64Credentials = authorization.substring("Basic".length()).trim();
			byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
			String credentials = new String(credDecoded, StandardCharsets.UTF_8);

			final String[] values = credentials.split(":", 2);
			String email = values[0].toString();
			String password = values[1].toString();
			String hashedPassword = userServiceImpl.getMd5(password);
			User user = userRepository.getUserByEmailAndPassword(email, hashedPassword);
			if (user == null) {
				List<Map> loggedUser = null;

				getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "Invalid email or Password", loggedUser);
				logger.info("************************ Login ENDED ***************************");
				return ResponseEntity.status(404).body(getObjectResponse);

			} else {

				String loggedEmail = user.getEmail();
				String token = jwkValidator.createJWT(loggedEmail, null);
				Map userInfo = new HashMap();
				userInfo.put("userId", user.getId());
				userInfo.put("name", user.getName());
				userInfo.put("email", user.getEmail());
				userInfo.put("photo", user.getPhoto());
				userInfo.put("accountType", user.getAccountType());
				userInfo.put("token", token);

				if (user.getAccountType() != 1) {
					if (user.getRoleId() == null) {
						getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "No roles assigned to this user yet", null);
						logger.info("************************ Login ENDED ***************************");
						return ResponseEntity.status(404).body(getObjectResponse);
					} else {
						UserRole userRole = userRoleService.findById(user.getRoleId());
						userInfo.put("userRole", userRole);
					}
				}
				if (user.getAccountType() != 1 && user.getAccountType() != 2) {

					if (user.getExp_date() == null || user.getCreate_date() == null) {
						getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "your account is expired , contact the admin ,please", null);
						logger.info("************************ Login ENDED ***************************");
						return ResponseEntity.status(404).body(getObjectResponse);
					} else if (user.getAccountType() == 3 || user.getAccountType() == 4) {
						userIds = assistantServiceImpl.getChildrenOfUser(user.getId());
//						Optional<List<Device>> optionalDeviceList = deviceRepository.findAllByUserIdInAndDeleteDate(userIds, null);
//
//						if (optionalDeviceList.isPresent()) {
//							List<Device> deviceList = optionalDeviceList.get();
//							Date maxDate = deviceList.stream().map(Device::getEndDate).max(Date::compareTo).get();
//							Date date = new Date();
//							long diff = maxDate.getTime() - date.getTime();
//							long days;
//							days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
//							if (days > 0) {
//								userInfo.put("leftDays", days);
//							} else {
//								getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "your account is expired , contact the admin ,please", null);
//								logger.info("************************ Login ENDED ***************************");
//								return ResponseEntity.status(404).body(getObjectResponse);
//							}
//						}
//						else {
//							Date date2 = null;
//							Date date1 = new Date();
//							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//							try {
//								date2 = format.parse(user.getExp_date());
//
//							} catch (ParseException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//							long diff1 = date2.getTime() - date1.getTime();
//							long days1;
//							days1 = TimeUnit.DAYS.convert(diff1, TimeUnit.MILLISECONDS);
//							userInfo.put("leftDays", days1);
//
//							if (days1 <= 0) {
//								getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "your account is expired , contact the admin ,please", null);
//								logger.info("************************ Login ENDED ***************************");
//								return ResponseEntity.status(404).body(getObjectResponse);
//							}
//						}

//						List<Inventory> inventories = inventoryRepository.getAllInventoriesIdss(userIds);
//						if (inventories!=null) {
//							Date maxDate = inventories.stream().map(Inventory::getEndDate).max(Date::compareTo).get();
//							Date date = new Date();
//							long diff = maxDate.getTime() - date.getTime();
//							long days;
//							days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
//							if (days > 0) {
//								userInfo.put("leftDays", days);
//							} else {
//								getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "your account is expired , contact the admin ,please", null);
//								logger.info("************************ Login ENDED ***************************");
//								return ResponseEntity.status(404).body(getObjectResponse);
//							}
//						}
//						else {
//							Date date2 = null;
//							Date date1 = new Date();
//							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//							try {
//								date2 = format.parse(user.getExp_date());
//
//							} catch (ParseException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//							long diff1 = date2.getTime() - date1.getTime();
//							long days1;
//							days1 = TimeUnit.DAYS.convert(diff1, TimeUnit.MILLISECONDS);
//							userInfo.put("leftDays", days1);
//
//							if (days1 <= 0) {
//								getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "your account is expired , contact the admin ,please", null);
//								logger.info("************************ Login ENDED ***************************");
//								return ResponseEntity.status(404).body(getObjectResponse);
//							}
//						}
//


				}
			}


				List<Map> loggedUser = new ArrayList<>();
				loggedUser.add(userInfo);
//				SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd  HH:MM:ss");
//				TimeZone etTimeZone = TimeZone.getTimeZone("Asia/Riyadh");
//
//				Date currentDate = new Date();
//
//
//				String requestLastUpdate = FORMATTER.format(currentDate);
				//TokenSecurity.getInstance().addActiveUser(user.getId(),token,requestLastUpdate);
				tokenSecurity.addActiveUser(user.getId(),token);
				getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",loggedUser);
				logger.info("************************ Login ENDED ***************************");

				return  ResponseEntity.ok().body(getObjectResponse);
			}
		}
		else
		{
			List<User> loggedUser = null ;
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request",loggedUser);
			logger.info("************************ Login ENDED ***************************");
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
	}

	/**
	 * logout and remove token of user 
	 */
	@Override
	public ResponseEntity<?> logout(String token) {

		
		if(super.checkActive(token)!= null)
		{
			return super.checkActive(token);
		}
		if(token == "") {
			List<User> loggedUser = null ;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id required",loggedUser);
			 logger.info("************************ Login ENDED ***************************");
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		else {
			  Boolean removed = tokenSecurity.removeActiveUser(token);
			  if(removed) {
				  List<User> loggedUser = null ;
				  getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "loggedOut successfully",loggedUser);
					 logger.info("************************ Login ENDED ***************************");
					 return  ResponseEntity.ok().body(getObjectResponse);
			  }else {
				  List<User> loggedUser = null ;
				  getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this logged token is not Found",loggedUser);
					 logger.info("************************ Login ENDED ***************************");
					 return  ResponseEntity.status(404).body(getObjectResponse);
			  }
			
			 
		}
		
	}

//	@Override
//	public ApiResponse<LoginResponse> login(String authorization) {
//		List<Long> userIds;
//		ApiResponseBuilder<LoginResponse> builder = new ApiResponseBuilder<>();
//		LoginResponse results = new LoginResponse();
//		logger.info("************************ Login STARTED ***************************");
//		 if(authorization != "" && authorization.toLowerCase().startsWith("basic")) {
//
//				String base64Credentials = authorization.substring("Basic".length()).trim();
//				byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
//				String credentials = new String(credDecoded, StandardCharsets.UTF_8);
//				final String[] values = credentials.split(":", 2);
//				String email = values[0];
//				String password = values[1];
//
//				String hashedPassword = userServiceImpl.getMd5(password);
//				User user = userRepository.getUserByEmailAndPassword(email,hashedPassword);
//
//				if(user == null)
//				{
//					throw new ApiGetException("Invalid email or Password");
//
//				}
//				else {
//
//					String loggedEmail= user.getEmail();
//					String token =  jwkValidator.createJWT(loggedEmail, null);
//					Map userInfo = new HashMap();
//					results.setUserId(user.getId());
//					results.setUserId(user.getId());
//					results.setName(user.getName());
//					results.setEmail(user.getEmail());
//					results.setPhoto(user.getPhoto());
//					results.setAccountType(user.getAccountType());
//					results.setToken(token);
////					userInfo.put("userId", user.getId());
////					userInfo.put("name" ,user.getName());
////					userInfo.put("email", user.getEmail());
////					userInfo.put("photo", user.getPhoto());
////					userInfo.put("accountType", user.getAccountType());
////					userInfo.put("token",token);
//
//					if(user.getAccountType() != 1) {
//						if(user.getRoleId() == null ) {
//							throw new ApiGetException("No roles assigned to this user yet");
//
//						}else {
//							UserRole userRole = userRoleService.findById(user.getRoleId());
//							results.setUserRole(userRole);
////							userInfo.put("userRole", userRole);
//						}
//					}
//					if(user.getAccountType() != 1 && user.getAccountType() != 2 ) {
//
//						if(user.getExp_date() == null || user.getCreate_date() == null) {
//							throw new ApiGetException("your account is expired , contact the admin ,please");
//						}
//						else if (user.getAccountType() == 3 || user.getAccountType() == 4){
//							userIds=assistantServiceImpl.getChildrenOfUser(user.getId());
//							Optional<List<Device>> optionalDeviceList = deviceRepository.findAllByUserIdInAndDeleteDate(userIds,null);
//							if(optionalDeviceList.isPresent()){
//								List<Device> deviceList = optionalDeviceList.get();
//								Date maxDate = deviceList.stream().map(Device::getEndDate).max(Date::compareTo).get();
////								logger.info("this is max date" + maxDate);
//								Date date = new Date();
//								long diff = maxDate.getTime() - date.getTime();
//								long days;
//								days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) ;
////								logger.info("Numbers Of Days " + days);
//								if (days > 0) {
//									results.setLeftDays(days);
////									userInfo.put("leftDays" , days);
//
//								}else {
//									throw new ApiGetException("your account is expired , contact the admin ,please");
//									}
//							}else {
//								Date date2 = null;
//								Date date1 = new Date();
//								SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//								try {
//									date2 = format.parse(user.getExp_date());
//
//								} catch (ParseException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
//
//								long diff1 = date2.getTime() - date1.getTime();
//								long days1;
//								days1 = TimeUnit.DAYS.convert(diff1, TimeUnit.MILLISECONDS) ;
//								results.setLeftDays(days1);
////								userInfo.put("leftDays" , days1);
//
//								if(days1 <= 0) {
//									throw new ApiGetException("your account is expired , contact the admin ,please");
//								}
//							}
////							Date date2 = null;
////							Date date1 = new Date();
////							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
////
////							try {
////								date2 = format.parse(user.getExp_date());
////
////							} catch (ParseException e) {
////								// TODO Auto-generated catch block
////								e.printStackTrace();
////							}
////
////							long diff = date2.getTime() - date1.getTime();
////							long days;
////							days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) ;
////							userInfo.put("leftDays" , days);
////
////							if(days <= 0) {
////								getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "your account is expired , contact the admin ,please",null);
////								logger.info("************************ Login ENDED ***************************");
////								return  ResponseEntity.status(404).body(getObjectResponse);
////							}
//						}
//					}
//
//					List<Map> loggedUser = new ArrayList<>();
//					loggedUser.add(userInfo);
//			        tokenSecurity.addActiveUser(user.getId(),token);
//
//					builder.setEntity(results);
//					builder.setStatusCode(200);
//					builder.setMessage("success");
//					builder.setSize(0);
//					builder.setSuccess(true);
//					return builder.build();
////				    getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",loggedUser);
////					logger.info("************************ Login ENDED ***************************");
////
////					return  ResponseEntity.ok().body(getObjectResponse);
//				}
//		 }
//		 else
//		 {
//			 throw new ApiGetException("Bad Request");
//		 }
//	}
	
	
	
	

}
