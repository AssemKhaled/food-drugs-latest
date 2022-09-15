package com.example.food_drugs.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.example.food_drugs.service.DriverServiceSFDA;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.examplequerydslspringdatajpamaven.entity.CustomDriverList;
import com.example.examplequerydslspringdatajpamaven.entity.Driver;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.repository.DriverRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserClientDriverRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;
import com.example.examplequerydslspringdatajpamaven.service.DriverServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.UserRoleService;
import com.example.examplequerydslspringdatajpamaven.service.UserServiceImpl;
import com.example.food_drugs.repository.DriverRepositorySFDA;

/**
 * services functionality related to driver SFDA
 * @author fuinco
 *
 */
@Component
@Service
public class DriverServiceImplSFDA  extends RestServiceController implements DriverServiceSFDA {

	private static final Log logger = LogFactory.getLog(DriverServiceImpl.class);

	@Autowired
	private DriverServiceImpl driverServiceImpl;
	
	@Autowired
	private DriverRepository driverRepository;
	
	@Autowired
	private DriverRepositorySFDA driverRepositorySFDA;
	
	@Autowired
	private UserRoleService userRoleService;
	
	@Autowired
	private UserServiceImpl userServiceImpl;
	
	
	private GetObjectResponse getObjectResponse;
	
	@Autowired
	private UserClientDriverRepository userClientDriverRepository;
	
	
	@Override
	public ResponseEntity<?> activeDriver(String TOKEN, Long driverId, Long userId) {

		logger.info("************************ activeDriver STARTED ***************************");

		List<Driver> drivers = new ArrayList<Driver>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN  is required",drivers);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId.equals(0)) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "loggedUser id is required",drivers);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		User loggedUser = userServiceImpl.findById(userId);
		if(loggedUser == null) {
			getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This loggedUser  is not Found",drivers);
			return  ResponseEntity.status(404).body(getObjectResponse);
		}
		if(!loggedUser.getAccountType().equals(1)) {
			if(!userRoleService.checkUserHasPermission(userId, "DRIVER", "delete")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to active driver",null);
				 logger.info("************************ activeDriver ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		if(!driverId.equals(0)) {
			Driver driver= driverRepository.findOne(driverId);
			if(driver != null) {
				 boolean isParent = false;
				 if(loggedUser.getAccountType().equals(4)) {
					 Set<User> parentClients = loggedUser.getUsersOfUser();
					 if(parentClients.isEmpty()) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to active this driver",drivers);
						 return  ResponseEntity.badRequest().body(getObjectResponse);
					 }else {
						 User parent = null;
						 for(User object : parentClients) {
							 parent = object;
						 }
						 Set<User>driverParent = driver.getUserDriver();
						 if(driverParent.isEmpty()) {
							 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to active this driver",drivers);
							 return  ResponseEntity.badRequest().body(getObjectResponse);
						 }else {
							 for(User parentObject : driverParent) {
								 if(parentObject.getId().equals(parent.getId())) {
									 isParent = true;
									 break;
								 }
							 }
						 }
					 }
				 }
				 if(!driverServiceImpl.checkIfParent(driver , loggedUser) && ! isParent) {
					getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to active this driver ",null);
					logger.info("************************ editDevice ENDED ***************************");
					return ResponseEntity.badRequest().body(getObjectResponse);
				 }
					
				 driver.setDelete_date(null);
				 driverRepository.save(driver);
				 
				 
				getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",drivers);
				logger.info("************************ activeDriver ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);
	
				
				
				
			}
			else {

				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Driver ID is not Found",drivers);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
						
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Driver ID is Required",drivers);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
		
		
	}


	@Override
	public ResponseEntity<?> getAllDriversSFDA(String TOKEN, Long id, int offset, String search, int active,String exportData) {

		logger.info("************************ getAllDrivers STARTED ***************************");
		List<Driver> drivers = new ArrayList<Driver>();
	    List<CustomDriverList> customDrivers = new ArrayList<CustomDriverList>();
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",drivers);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(!id.equals(0)) {
			User user = userServiceImpl.findById(id);
			if(user == null) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",drivers);
				return ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(!user.getAccountType().equals(1)) {
					if(!userRoleService.checkUserHasPermission(id, "DRIVER", "list")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get driver list",null);
						 logger.info("************************ getAllUserDrivers ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				   userServiceImpl.resetChildernArray();
					if(user.getAccountType().equals(4)) {
						
						List<Long> driverIds = userClientDriverRepository.getDriverIds(id);
						Integer size = 0;
						if(driverIds.size()>0) {

							if(active == 0) {

								if(exportData.equals("exportData")) {
									customDrivers= driverRepositorySFDA.getAllDriversCustomByIdsDeactiveExport(driverIds,search);

								}
								else {
									customDrivers= driverRepositorySFDA.getAllDriversCustomByIdsDeactive(driverIds,offset,search);
								    size= driverRepositorySFDA.getAllDriversSizeByIdsDeactive(driverIds,search);
								}

								
									
							 }
							 
				             if(active == 2) {
				            	 if(exportData.equals("exportData")) {
									 customDrivers= driverRepositorySFDA.getAllDriversCustomByIdsAllExport(driverIds,search);
				            	 }
				            	 else {
									 customDrivers= driverRepositorySFDA.getAllDriversCustomByIdsAll(driverIds,offset,search);
								     size= driverRepositorySFDA.getAllDriversSizeByIdsAll(driverIds,search);
				            	 }
								
							 }
				             
				             if(active == 1) {
				            	 if(exportData.equals("exportData")) {
										customDrivers= driverRepository.getAllDriversCustomByIdsExport(driverIds,search);

				            	 }
				            	 else {
					            	 
									customDrivers= driverRepository.getAllDriversCustomByIds(driverIds,offset,search);
								    size= driverRepository.getAllDriversSizeByIds(driverIds,search);
				            		 
				            	 }
								
							 }
						}
						 
						getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",customDrivers,size);
						logger.info("************************ getAllDrivers ENDED ***************************");
						return ResponseEntity.ok().body(getObjectResponse);
					}
					List<User>childernUsers = userServiceImpl.getActiveAndInactiveChildern(id);
					 List<Long>usersIds= new ArrayList<>();
					 if(childernUsers.isEmpty()) {
						 usersIds.add(id);
					 }
					 else {
						 usersIds.add(id);
						 for(User object : childernUsers) {
							 usersIds.add(object.getId());
						 }
					 }
					 Integer size = 0;
					 if(active == 0) {
		            	 if(exportData.equals("exportData")) {
		            		 customDrivers= driverRepositorySFDA.getAllDriversCustomDeactiveExport(usersIds,search);

		            	 }
		            	 else {
		            		 customDrivers= driverRepositorySFDA.getAllDriversCustomDeactive(usersIds,offset,search);
		            		 size= driverRepositorySFDA.getAllDriversSizeDeactive(usersIds,search);
		            	 }


							
					 }
					 
		             if(active == 2) {
		            	 if(exportData.equals("exportData")) {
						     customDrivers= driverRepositorySFDA.getAllDriversCustomAllExport(usersIds,search);

		            	 }
		            	 else {
						     customDrivers= driverRepositorySFDA.getAllDriversCustomAll(usersIds,offset,search);
							 size= driverRepositorySFDA.getAllDriversSizeAll(usersIds,search); 
		            	 }


							
					 }
		             
		             if(active == 1) {
		            	 if(exportData.equals("exportData")) {
							 customDrivers= driverRepository.getAllDriversCustomExport(usersIds,search);

		            	 }
		            	 else {

							 customDrivers= driverRepository.getAllDriversCustom(usersIds,offset,search);
							 size= driverRepository.getAllDriversSize(usersIds,search); 
		            	 }

							
					 }
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",customDrivers,size);
					logger.info("************************ getAllDrivers ENDED ***************************");
					return ResponseEntity.ok().body(getObjectResponse);

				
				
			}

		}
		else {
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",drivers);
			return ResponseEntity.badRequest().body(getObjectResponse);

		}
		
		
	
	}

}
