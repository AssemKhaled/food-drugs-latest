package com.example.food_drugs.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
import com.example.food_drugs.dto.ApiResponse;
import com.example.food_drugs.dto.ApiResponseBuilder;
import com.example.food_drugs.dto.Request.NotificationSettingRequest;
import com.example.food_drugs.entity.EmailsMongo;
import com.example.food_drugs.exception.ApiGetException;
import com.example.food_drugs.repository.EmailMongoRepository;
import com.example.food_drugs.service.NotificationServiceSFDA;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.example.examplequerydslspringdatajpamaven.entity.Notification;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.repository.NotificationRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;
import com.example.examplequerydslspringdatajpamaven.service.NotificationServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.UserRoleService;
import com.example.examplequerydslspringdatajpamaven.service.UserServiceImpl;
import com.example.food_drugs.repository.NotificationRepositorySFDA;

import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;

/**
 * services functionality related to notifications
 * @author fuinco
 *
 */
@Component
@Service
@Transactional
public class NotificationServiceImplSFDA extends RestServiceController implements NotificationServiceSFDA {

	private static final Log logger = LogFactory.getLog(NotificationServiceImpl.class);

	private GetObjectResponse getObjectResponse;
	private final   UserServiceImpl userService;
	private final UserRoleService userRoleService;
	private final NotificationRepository notificationRepository;
	private final NotificationRepositorySFDA notificationRepositorySFDA;
	private final NotificationServiceImpl notificationServiceImpl;
	private final   JavaMailSender javaMailSender;
	private final EmailMongoRepository emailMongoRepository;
	private final UserRepository userRepository;

	public NotificationServiceImplSFDA(UserServiceImpl userService, UserRoleService userRoleService, NotificationRepository notificationRepository, NotificationRepositorySFDA notificationRepositorySFDA, NotificationServiceImpl notificationServiceImpl, JavaMailSender javaMailSender, EmailMongoRepository emailMongoRepository, UserRepository userRepository) {
		this.userService = userService;
		this.userRoleService = userRoleService;
		this.notificationRepository = notificationRepository;
		this.notificationRepositorySFDA = notificationRepositorySFDA;
		this.notificationServiceImpl = notificationServiceImpl;
		this.javaMailSender = javaMailSender;
		this.emailMongoRepository = emailMongoRepository;
		this.userRepository = userRepository;
	}

	@Override
	public ResponseEntity<?> activeNotification(String TOKEN, Long notificationId, Long userId) {
		logger.info("************************ activeNotification STARTED ***************************");

		List<Notification> notifications = new ArrayList<Notification>();
		User user = userService.findById(userId);
		if(user == null ) {
			getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",notifications);
			return  ResponseEntity.status(404).body(getObjectResponse);

		}
		
		if(user.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "NOTIFICATION", "delete")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to active",null);
				 logger.info("************************ activeNotification ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",notifications);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(notificationId != 0) {
			Notification notification = notificationRepository.findOne(notificationId);
			if(notification != null) {
				
				
				 if(!notificationServiceImpl.checkIfParent(notification , user)) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to active this notification ",notifications);
						logger.info("************************ activeNotification ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
					}
				 
				    notification.setDelete_date(null);
				    notificationRepository.save(notification);
				    
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",notifications);
					logger.info("************************ activeNotification ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);
				
				
					

				
				
			}
			else {

				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This notification ID was not found",notifications);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
						
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "notification ID is Required",notifications);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
	}

	@Override
	public ResponseEntity<?> getAllNotificationsSFDA(String TOKEN, Long id, int offset, String search, int active,String exportData) {
		
		logger.info("************************ getAllNotifications STARTED ***************************");
		
		List<Notification> notifications = new ArrayList<Notification>();
		
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",notifications);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(id != 0) {
			
			User user = userService.findById(id);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",notifications);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(user.getAccountType()!= 1) {
					if(!userRoleService.checkUserHasPermission(id, "NOTIFICATION", "list")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get notifications list",null);
						 logger.info("************************ getAllNotifications ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				if(user.getDeleteDate() == null) {
					
					userService.resetChildernArray();
				    if(user.getAccountType().equals(4)) {
					 
						 
						 List<Long>usersIds= new ArrayList<>();
						 usersIds.add(user.getId());
						 

						 Integer size = 0;
							
						 if(active == 0) {

							if(exportData.equals("exportData")) {
								notifications = notificationRepositorySFDA.getAllNotificationsDeactiveExport(usersIds,search);

							}
							else {
								notifications = notificationRepositorySFDA.getAllNotificationsDeactive(usersIds,offset,search);
								size = notificationRepositorySFDA.getAllNotificationsSizeDeactive(usersIds);
							}


						 }
						 
			             if(active == 2) {

			            	if(exportData.equals("exportData")) {
								notifications = notificationRepositorySFDA.getAllNotificationsAllExport(usersIds,search);

			            	}
			            	else {
								notifications = notificationRepositorySFDA.getAllNotificationsAll(usersIds,offset,search);
								size = notificationRepositorySFDA.getAllNotificationsSizeAll(usersIds);
			            	}

						 }
			             
			             if(active == 1) {
				            if(exportData.equals("exportData")) {
								notifications = notificationRepository.getAllNotificationsExport(usersIds,search);

				            }
				            else {

								notifications = notificationRepository.getAllNotifications(usersIds,offset,search);
								size = notificationRepository.getAllNotificationsSize(usersIds);
				            }

						 }
							
						 
                        getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",notifications,size);
						logger.info("************************ getAllNotifications ENDED ***************************");
						return  ResponseEntity.ok().body(getObjectResponse);
						 
					 }
					 List<Long>usersIds= new ArrayList<>();
					 usersIds.add(id);

					 Integer size = 0;
					
					 if(active == 0) {
						 if(exportData.equals("exportData")) {
							notifications = notificationRepositorySFDA.getAllNotificationsDeactiveExport(usersIds,search);

						 }
						 else {
							notifications = notificationRepositorySFDA.getAllNotificationsDeactive(usersIds,offset,search);
							size = notificationRepositorySFDA.getAllNotificationsSizeDeactive(usersIds);
						 }
					 

					 }
					 
		             if(active == 2) {

		            	if(exportData.equals("exportData")) {
							notifications = notificationRepositorySFDA.getAllNotificationsAllExport(usersIds,search);

		            	}
		            	else {
							notifications = notificationRepositorySFDA.getAllNotificationsAll(usersIds,offset,search);
							size = notificationRepositorySFDA.getAllNotificationsSizeAll(usersIds);
		            	}
					 }
		             
		             if(active == 1) {
		            	if(exportData.equals("exportData")) {
							notifications = notificationRepository.getAllNotificationsExport(usersIds,search);

			            }
			            else {

							notifications = notificationRepository.getAllNotifications(usersIds,offset,search);
							size = notificationRepository.getAllNotificationsSize(usersIds);
			            }
					 }
					
					
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",notifications,size);
					logger.info("************************ getAllNotifications ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);

				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",notifications);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
				
			}

		}
		else{
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",notifications);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
	}

	@Override
	public ApiResponse<NotificationSettingRequest> notificationSetting(String TOKEN,Long userId, NotificationSettingRequest notificationSettingRequest) {

		ApiResponseBuilder<NotificationSettingRequest> builder = new ApiResponseBuilder<>();
		List<String> nType;
		List<Notification> ids;
		List<Long> notiIds;
		String notifyBy;
		String emails;
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

		User user ;
		user = userRepository.findOne(userId);
		notifyBy = notificationSettingRequest.getNotifyBy().stream()
				.map(String::valueOf)
				.collect(Collectors.joining(","));
		emails = notificationSettingRequest.getEmails().stream()
				.map(String::valueOf)
				.collect(Collectors.joining(",","{","}"));
		Set<User> users = new HashSet<>();
		users.add(user);
		ids = notificationRepository.getNotifications(userId);
		if (notificationSettingRequest.getNotificationType().isEmpty()
				&& notificationSettingRequest.getNotifyBy().isEmpty()
				&& notificationSettingRequest.getEmails().isEmpty()){
			if (!ids.isEmpty()){
				notiIds = ids.stream().map(Notification::getId).collect(Collectors.toList());
				notificationRepository.deleteNotificationByIdIn(notiIds);

			}
		}else {
			if (!ids.isEmpty()){
				notiIds = ids.stream().map(Notification::getId).collect(Collectors.toList());
				notificationRepository.deleteNotificationByIdIn(notiIds);
			}
			if (user != null) {
				user.setEmails(emails);
				nType = notificationSettingRequest.getNotificationType();

				for (int i = 0; i < nType.size(); i++) {
					Notification notification = Notification
							.builder()
							.notificators(notifyBy)
							.type(nType.get(i))
							.always(true)
							.userNotification(users)
							.build();
					notificationRepository.save(notification);
				}
				builder.setMessage("success");
				builder.setStatusCode(HttpStatus.OK.value());
				builder.setEntity(null);
				builder.setSize(0);
				builder.setSuccess(true);
				return builder.build();
			} else {
				throw new ApiGetException("User Not Found");
			}
		}
		builder.setMessage("Notifications Deleted");
		builder.setStatusCode(HttpStatus.OK.value());
		builder.setEntity(null);
		builder.setSize(0);
		builder.setSuccess(true);
		return builder.build();
	}

	@Override
	public ApiResponse<NotificationSettingRequest> getNotificationSetting(String TOKEN, Long userId) {
		ApiResponseBuilder<NotificationSettingRequest> builder = new ApiResponseBuilder<>();
		List<Notification> result ;
		User user;
		List<String> nType;
		List<String> notifyBy;
		List<String> notifcators;
		List<String> emails = new ArrayList<>();
		NotificationSettingRequest notificationSettingRequest;
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
		user = userRepository.findOne(userId);
		if (user != null){
			result = notificationRepository.getNotifications(userId);
			if (!result.isEmpty()){
				nType = result.stream()
						.map(Notification::getType)
						.distinct()
						.collect(Collectors.toList());
				emails.add(user.getEmails());


				notifyBy = result.stream()
						.map(Notification::getNotificators)
						.distinct().collect(Collectors.toList());
				String [] items = notifyBy.get(0).split("\\s*,\\s*");
				notifcators = Arrays.asList(items);

				notificationSettingRequest = NotificationSettingRequest
						.builder()
						.notificationType(nType)
						.notifyBy(notifcators)
						.emails(emails)
						.build();


			}else {
				notificationSettingRequest = NotificationSettingRequest
						.builder()
						.notificationType(null)
						.notifyBy(null)
						.emails(null)
						.build();
			}
			builder.setMessage("success");
			builder.setStatusCode(HttpStatus.OK.value());
			builder.setEntity(notificationSettingRequest);
			builder.setSize(1);
			builder.setSuccess(true);
			return builder.build();
		}else {
			throw new ApiGetException("NO SUCH USER Exist");
		}

	}

	@Override
	public ApiResponse<?> sendEmail() {
		logger.info("**************************** SEND EMAILS STARTED ****************************");

		ApiResponseBuilder<ApiResponse<?>> builder = new ApiResponseBuilder<>();
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
		List<ObjectId> successIds = new ArrayList<>();
		List<Long> userIds;
		Optional<List<EmailsMongo>> optionalEmailsMongoList = emailMongoRepository.findAllByIsSent(false , new PageRequest(0,10));

		if (optionalEmailsMongoList.isPresent()){
			List<EmailsMongo> emailsMongoList = optionalEmailsMongoList.get();
			userIds = emailsMongoList.stream()
					.map(EmailsMongo::getUserId)
					.distinct()
					.filter(Objects::nonNull).collect(Collectors.toList());
			Optional<List<User>> optionalUserList = userRepository.findAllByIdIn(userIds);
			if (optionalUserList.isPresent()) {
				List<User> userList = optionalUserList.get();
				for (EmailsMongo emails: emailsMongoList) {
//				try {
//					List<User> users = userList.stream()
//							.filter(user -> user.getId().equals(emails.getUserId()))
//							.collect(Collectors.toList());
//					String emailTo = users.get(0).getEmails();
//					if (!users.isEmpty()){
//						if (users.get(0).getEmails() != null){
//							msg.setT(emailTo);
//							msg.setSubject(emails.getSubject());
//							msg.setText(emails.getBody());
//							javaMailSender.send(msg);
//						}
//					}
//				}catch (Exception e){
//					throw new ApiGetException(e.getLocalizedMessage());
//				}
						try {
							List<User> users = userList.stream()
							.filter(user -> user.getId().equals(emails.getUserId()))
							.collect(Collectors.toList());
							String emailTo = users.get(0).getEmails();
							if (!users.isEmpty()){
								if (emailTo != null && !Objects.equals(emailTo, "")){
									try {
										helper.setText(emails.getBody(), true); // Use this or above line.
										helper.setTo(emailTo);
										helper.setSubject(emails.getSubject());
										helper.setFrom("Fuinco.com");
										javaMailSender.send(mimeMessage);
									}catch (MailException e){
										throw new ApiGetException(e.getLocalizedMessage());
									}
									emails.setIsSent(true);
									emailMongoRepository.save(emails);
//									successIds.add(emails.get_id());
								}else {
									logger.info("***********"+ emails.getUserId() +"User Don`t have an Email to Sent To"+"***********");
								}
							}
						} catch (Exception e) {
							throw new ApiGetException(e.getLocalizedMessage());
						}
				}
			}else {
				throw new ApiGetException("USERS NOT FOUND");
			}

			logger.info("**************************** SEND EMAILS ENDED ****************************");

			builder.setEntity(null);
			builder.setSuccess(true);
			builder.setMessage("DONE");
			builder.setSize(0);
			builder.setStatusCode(200);
			return builder.build();
		}else {
			throw new ApiGetException("NO EMAILS FOUND TO BE SENT");
		}
	}

//	public void sendUsersEmails() throws MessagingException {
//		List<EmailsMongo> emails = emailRepository.findAllByIsSentOrderByTime(false);
//
//		System.out.println(emails.size() + "----------------------------------------------------------------");
//
//		Map<Long, List<String>> users = new HashMap<>();
//		for(MongoEmail email:emails){
//			if(!users.containsKey(email.getUserId())){
//				User user = userRepository.findOne(email.getUserId());
//				if(user == null){
//					continue;
//				}
//				List<String> userEmails = new ArrayList<>();
//				if(user.getEmail() != null){
//					userEmails.add(user.getEmail());
//				}
//				if(user.getEmail1() != null){
//					userEmails.add(user.getEmail1());
//				}
//				if(user.getEmail2() != null){
//					userEmails.add(user.getEmail2());
//				}
//				if(user.getEmail3() != null){
//					userEmails.add(user.getEmail3());
//				}
//				if(user.getEmails()!= null){
//					JSONArray jsonEmails = new JSONArray(user.getEmails());
//					for(int i = 0; i < jsonEmails.length(); i++){
//						userEmails.add(jsonEmails.getJSONObject(i).getString("email"));
//					}
//				}
//				users.put(user.getId(), userEmails);
//			}
//
//			//send email to users.get(user.getId())
//
//
//			int n = users.get(email.getUserId()).size();
//			String[] to = new String[n];
//			for(int  i = 0; i < n; i++){
//				to[i] = users.get(email.getUserId()).get(i);
//			}
//
////            String[] to = {"a000wael@gmail.com", "ahm.wael0@gmail.com", "hossamragab1997@gmail.com"};
//
//			MimeMessage mimeMessage = mailSender.createMimeMessage();
//			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
//			helper.setText(email.getBody(), true);
//			helper.setTo(to);
//			helper.setSubject(email.getSubject());
//			mailSender.send(mimeMessage);
//			email.setSent(true);
//			emailRepository.save(email);
//		}
//	}
}
