package com.example.food_drugs.rest;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import com.example.food_drugs.dto.ApiResponse;
import com.example.food_drugs.dto.Request.NotificationSettingRequest;
import com.example.food_drugs.exception.ApiGetException;
import com.example.food_drugs.exception.ApiRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import com.example.examplequerydslspringdatajpamaven.entity.Notification;
import com.example.examplequerydslspringdatajpamaven.service.NotificationServiceImpl;
import com.example.food_drugs.service.impl.NotificationServiceImplSFDA;;import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

/**
 * Services of notification component
 * @author fuinco
 *
 */
@CrossOrigin
@Component
@RequestMapping(path = "/notificationSFDA")
public class NotificationsRestControllerSFDA {

	

	@Autowired
	private NotificationServiceImpl notificationServiceImpl;

	@Autowired
	private NotificationServiceImplSFDA notificationServiceImplSFDA;
	
	@PostMapping(path ="/createNotification")
	public ResponseEntity<?> createNotification(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
												@RequestParam (value = "userId",defaultValue = "0") Long userId,
									            @RequestBody(required = false) Notification notification,
									            @RequestHeader(value = "Authorization", defaultValue = "")String authorization) {
		return notificationServiceImpl.createNotification(TOKEN,authorization,notification,userId);				
	}
	
	@RequestMapping(value = "/getAllNotifications", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getAllNotifications(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													            @RequestParam (value = "exportData", defaultValue = "") String exportData,                                                                                                          
																@RequestParam (value = "userId", defaultValue = "0") Long id,
																@RequestParam (value = "offset", defaultValue = "0") int offset,
																@RequestParam (value = "search", defaultValue = "") String search,
																@RequestParam (value = "active", defaultValue = "1") int active) {
				
    	return  notificationServiceImplSFDA.getAllNotificationsSFDA(TOKEN,id,offset,search,active,exportData);

	}
	
	@RequestMapping(value = "/getNotificationById", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getNotificationById(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                               @RequestParam (value = "notificationId", defaultValue = "0") Long notificationId,
			                                               @RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
    	return  notificationServiceImpl.getNotificationById(TOKEN,notificationId,userId);

	}
	
	@RequestMapping(value = "/editNotification", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> editNotification(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                            @RequestBody(required = false) Notification notification,
			                                            @RequestParam (value = "userId", defaultValue = "0") Long id,
			                                            @RequestHeader(value = "Authorization", defaultValue = "")String authorization) {
		
		
		return notificationServiceImpl.editNotification(TOKEN,authorization,notification,id);

	}
	
	@RequestMapping(value = "/deleteNotification", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> deleteNotification(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                            @RequestParam (value = "notificationId", defaultValue = "0") Long notificationId,
			                                            @RequestParam (value = "userId", defaultValue = "0") Long userId) {

    	return  notificationServiceImpl.deleteNotification(TOKEN,notificationId,userId);


	}
	
	@RequestMapping(value = "/activeNotification", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> activeNotification(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                            @RequestParam (value = "notificationId", defaultValue = "0") Long notificationId,
			                                            @RequestParam (value = "userId", defaultValue = "0") Long userId) {

    	return  notificationServiceImplSFDA.activeNotification(TOKEN,notificationId,userId);


	}
	
	@RequestMapping(value = "/assignNotificationToGroup", method = RequestMethod.POST)
	public ResponseEntity<?> assignNotificationToGroup(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													@RequestBody Map<String, List> data,
													@RequestParam(value = "groupId" ,defaultValue = "0") Long groupId,
													@RequestParam(value = "userId" , defaultValue = "0")Long userId ) {
		
		return notificationServiceImpl.assignNotificationToGroup(TOKEN,groupId,data,userId);	
		
	}
	
	@RequestMapping(value = "/assignNotificationToDevice", method = RequestMethod.POST)
	public ResponseEntity<?> assignNotificationToDevice(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													@RequestBody Map<String, List> data,
													@RequestParam(value = "deviceId" ,defaultValue = "0") Long deviceId,
													@RequestParam(value = "userId" , defaultValue = "0")Long userId ) {
		
		return notificationServiceImpl.assignNotificationToDevice(TOKEN,deviceId,data,userId);	
		
	}
	
	@RequestMapping(value = "/getNotificationSelect", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getNotificationSelect(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													             @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
													             @RequestParam (value = "groupId", defaultValue = "0") Long groupId,                                              
																 @RequestParam (value = "userId", defaultValue = "0") Long userId) {

    	return  notificationServiceImpl.getNotificationSelect(TOKEN,userId,deviceId,groupId);

	}

	@PostMapping("/notificationSetting")
	ResponseEntity<ApiResponse<NotificationSettingRequest>> notificationSetting(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
																				@RequestParam (value = "userId", defaultValue = "0") Long userId,
																				@RequestBody NotificationSettingRequest notificationSettingRequest) {
		try{
			return ResponseEntity.ok(
					notificationServiceImplSFDA.notificationSetting(TOKEN,userId,notificationSettingRequest));

		}catch (Exception | Error e){
			throw new ApiRequestException(e.getLocalizedMessage());
		}
	}
	@GetMapping("/getNotificationSetting")
	ResponseEntity<ApiResponse<NotificationSettingRequest>> getNotificationSetting(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
																				@RequestParam (value = "userId", defaultValue = "0") Long userId) {
		try{
			return ResponseEntity.ok(
					notificationServiceImplSFDA.getNotificationSetting(TOKEN,userId));

		}catch (Exception | Error e){
			throw new ApiRequestException(e.getLocalizedMessage());
		}
	}


	@RequestMapping(value = "/sendEmail")
	public ResponseEntity<ApiResponse<?>> sendEmail()  {

		try {
			return ResponseEntity.ok(
					notificationServiceImplSFDA.sendEmail());

		}catch (Exception e){
			throw new ApiGetException(e.getLocalizedMessage());
		}
	}

}
