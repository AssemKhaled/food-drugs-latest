package com.example.food_drugs.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.example.food_drugs.service.GeofenceServiceSFDA;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.examplequerydslspringdatajpamaven.entity.Geofence;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.repository.GeofenceRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserClientGeofenceRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;
import com.example.examplequerydslspringdatajpamaven.service.GeofenceServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.UserRoleService;
import com.example.examplequerydslspringdatajpamaven.service.UserServiceImpl;
import com.example.food_drugs.repository.GeofenceRepositorySFDA;

/**
 * services functionality related to geofence SFDA
 * @author fuinco
 *
 */
@Component
@Service
public class GeofenceServiceImplSFDA extends RestServiceController implements GeofenceServiceSFDA {

	private static final Log logger = LogFactory.getLog(GeofenceServiceImpl.class);

	private GetObjectResponse getObjectResponse;

	@Autowired
	private GeofenceRepository geofenceRepository;
	
	@Autowired
	private GeofenceRepositorySFDA geofenceRepositorySFDA;
	
	@Autowired
	private GeofenceServiceImpl geofenceServiceImpl;
	
	@Autowired
	private UserRoleService userRoleService;

	@Autowired
	private UserServiceImpl userServiceImpl;
	
	@Autowired
	private UserClientGeofenceRepository userClientGeofenceRepository;	
	
	@Override
	public ResponseEntity<?> activeGeofence(String TOKEN, Long geofenceId, Long userId) {

		logger.info("************************ activeGeofence STARTED ***************************");

		
		List<Geofence> geofences = new ArrayList<Geofence>();
		User user = userServiceImpl.findById(userId);
		if(user == null ) {
			getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",geofences);
			return  ResponseEntity.status(404).body(getObjectResponse);

		}
		
		if(user.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "GEOFENCE", "delete")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to active",null);
				 logger.info("************************ activeGeofence ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",geofences);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(geofenceId != 0) {
			Geofence geofence= geofenceRepository.findOne(geofenceId);
			if(geofence != null) {
				
				 boolean isParent = false;
				 if(user.getAccountType().equals(4)) {
					 Set<User> parentClients = user.getUsersOfUser();
					 if(parentClients.isEmpty()) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to active this geofnece",geofences);
						 return  ResponseEntity.badRequest().body(getObjectResponse);
					 }else {
						 User parent = null;
						 for(User object : parentClients) {
							 parent = object;
						 }
						 Set<User>geofneceParent = geofence.getUserGeofence();
						 if(geofneceParent.isEmpty()) {
							 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to active this geofnece",geofences);
							 return  ResponseEntity.badRequest().body(getObjectResponse);
						 }else {
							 for(User parentObject : geofneceParent) {
								 if(parentObject.getId().equals(parent.getId())) {
									 isParent = true;
									 break;
								 }
							 }
						 }
					 }

				 }
				 if(!geofenceServiceImpl.checkIfParent(geofence , user) && ! isParent) {
						getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to active this geofnece ",geofences);
						logger.info("************************ activeGeofence ENDED ***************************");
						return ResponseEntity.badRequest().body(getObjectResponse);
				}
				 
				geofence.setDelete_date(null);
				geofenceRepository.save(geofence);
				
				
				getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",geofences);
				logger.info("************************ activeGeofence ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);
				
					
				
				
			}
			else {

				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This Geofence ID was not found",geofences);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
						
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Geofence ID is Required",geofences);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}



	}

	@Override
	public ResponseEntity<?> getAllGeofencesSFDA(String TOKEN, Long id, int offset, String search, int active,String exportData) {

		logger.info("************************ getAllUserGeofences STARTED ***************************");
		
		List<Geofence> geofences = new ArrayList<Geofence>();
		
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",geofences);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(id != 0) {
			
			User user = userServiceImpl.findById(id);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",geofences);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(user.getAccountType()!= 1) {
					if(!userRoleService.checkUserHasPermission(id, "GEOFENCE", "list")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get geofences list",null);
						 logger.info("************************ getAllUserDevices ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				if(user.getDeleteDate() == null) {
					
				    userServiceImpl.resetChildernArray();
				    if(user.getAccountType().equals(4)) {
						 

				    	
				    	List<Long> geofenceIds = userClientGeofenceRepository.getGeofneceIds(id);
				    	Integer size = 0;
						 if(geofenceIds.size()>0) {

							 if(active == 0) {

								if(exportData.equals("exportData")) {
									geofences = geofenceRepositorySFDA.getAllGeofencesByIdsDeactiveExport(geofenceIds,search);
								}
								else {
									geofences = geofenceRepositorySFDA.getAllGeofencesByIdsDeactive(geofenceIds,offset,search);
									size=geofenceRepositorySFDA.getAllGeofencesSizeByIdsDeactive(geofenceIds,search);
								}


							 }
							 
				             if(active == 2) {
					             
								if(exportData.equals("exportData")) {
									 geofences = geofenceRepositorySFDA.getAllGeofencesByIdsAllExport(geofenceIds,search);
								}
								else {
									 geofences = geofenceRepositorySFDA.getAllGeofencesByIdsAll(geofenceIds,offset,search);
									 size=geofenceRepositorySFDA.getAllGeofencesSizeByIdsAll(geofenceIds,search);
								}


								
							 }
				             
				             if(active == 1) {
					             
								if(exportData.equals("exportData")) {
									geofences = geofenceRepository.getAllGeofencesByIdsExport(geofenceIds,search);

								}
								else {
									geofences = geofenceRepository.getAllGeofencesByIds(geofenceIds,offset,search);
									size=geofenceRepository.getAllGeofencesSizeByIds(geofenceIds,search);
								}


								
							 }
						 }
						getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",geofences,size);
						logger.info("************************ getAllUserGeofences ENDED ***************************");
						return  ResponseEntity.ok().body(getObjectResponse);
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
			            	geofences = geofenceRepositorySFDA.getAllGeofencesDeactiveExport(usersIds,search);

						}
						else {
			            	geofences = geofenceRepositorySFDA.getAllGeofencesDeactive(usersIds,offset,search);
							size=geofenceRepositorySFDA.getAllGeofencesSizeDeactive(usersIds,search);
						}


					 }
					 
		             if(active == 2) {
						if(exportData.equals("exportData")) {
							geofences = geofenceRepositorySFDA.getAllGeofencesAllExport(usersIds,search);
	
						}
						else {
							geofences = geofenceRepositorySFDA.getAllGeofencesAll(usersIds,offset,search);
							size=geofenceRepositorySFDA.getAllGeofencesSizeAll(usersIds,search);
						}


						
					 }
		             
		             if(active == 1) {
						if(exportData.equals("exportData")) {
							geofences = geofenceRepository.getAllGeofencesExport(usersIds,search);

						}
						else {
							geofences = geofenceRepository.getAllGeofences(usersIds,offset,search);
							size=geofenceRepository.getAllGeofencesSize(usersIds,search);	
						}


						
					 }
					
					
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",geofences,size);
					logger.info("************************ getAllUserGeofences ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);

				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",geofences);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
				
			}

		}
		else{
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",geofences);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}

	
	}

}
