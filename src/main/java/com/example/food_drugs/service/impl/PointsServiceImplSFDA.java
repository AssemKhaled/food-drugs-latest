package com.example.food_drugs.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.examplequerydslspringdatajpamaven.entity.Points;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.repository.PointsRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserClientPointRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;
import com.example.examplequerydslspringdatajpamaven.service.PointsServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.UserRoleService;
import com.example.examplequerydslspringdatajpamaven.service.UserServiceImpl;
import com.example.food_drugs.repository.PointsRepositorySFDA;
import com.example.food_drugs.service.PointsServiceSFDA;

/**
 * services functionality related to points
 * @author fuinco
 *
 */

@Component
@Service
public class PointsServiceImplSFDA extends RestServiceController implements PointsServiceSFDA{


	private static final Log logger = LogFactory.getLog(PointsServiceImpl.class);

	private GetObjectResponse getObjectResponse;
	
	@Autowired
	private UserServiceImpl userServiceImpl;
	
	@Autowired
	private UserRoleService userRoleService;
	
	@Autowired
	private PointsRepository pointsRepository;
	
	@Autowired
	private PointsRepositorySFDA pointsRepositorySFDA;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserClientPointRepository userClientPointRepository;
	
	
	
	@Override
	public ResponseEntity<?> activePoints(String TOKEN, Long PointId, Long userId) {
		// TODO Auto-generated method stub
		if(TOKEN.equals("")) {
			 List<Points> points= null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",points);
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
			if(!userRoleService.checkUserHasPermission(userId, "POINTS", "delete")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to active point",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		if(PointId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No PointId  to active",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		Points point= pointsRepository.findOne(PointId);
		if(point == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this PointId not found",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}
	

		Long createdBy=point.getUserId();
		Boolean isParent=false;

		if(createdBy.toString().equals(userId.toString())) {
			isParent=true;
		}
		List<User>childs = new ArrayList<User>();
		if(loggedUser.getAccountType().equals(4)) {
			 List<User> parents=userServiceImpl.getAllParentsOfuser(loggedUser,loggedUser.getAccountType());
			 if(parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this point.",null);
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
			List<Long> points = userClientPointRepository.getPoint(userId,PointId);
			if(points.isEmpty()) {
					isParent = false;
			}
			else {
					isParent = true;
			}
		}
		
		if(isParent == false) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "Not creater or parent of creater to get point",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		
	    point.setDelete_date(null);
	    pointsRepository.save(point);
	    
		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",null);
		return  ResponseEntity.ok().body(getObjectResponse);
	}

	@Override
	public ResponseEntity<?> getPointsListSFDA(String TOKEN, Long id, int offset, String search, int active,String exportData) {
		logger.info("************************ getPointsList STARTED ***************************");
		
		List<Points> points = new ArrayList<Points>();
		
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",points);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(id != 0) {
			
			User user = userServiceImpl.findById(id);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",points);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(user.getAccountType()!= 1) {
					if(!userRoleService.checkUserHasPermission(id, "POINTS", "list")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get POINTS list",null);
						 logger.info("************************ getPointsList ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				if(user.getDeleteDate() == null) {
					 List<Long>usersIds= new ArrayList<>();

					userServiceImpl.resetChildernArray();
				    if(user.getAccountType().equals(4)) {
						 
				    	 List<Long> pointIds = userClientPointRepository.getPointIds(id);
						 Integer size = 0;
						 List<Map> data = new ArrayList<>();
						 if(pointIds.size()>0) {
					    	
					    	
							if(active == 0) {
								
								if(exportData.equals("exportData")) {
									points = pointsRepositorySFDA.getAllPointsByIdsDeactiveExport(pointIds,search);

								}
								else {
									points = pointsRepositorySFDA.getAllPointsByIdsDeactive(pointIds,offset,search);
									size = pointsRepositorySFDA.getAllPointsSizeByIdsDeactive(pointIds,search);
								}
							 }
								 
				             if(active == 2) {
								if(exportData.equals("exportData")) {
					            	points = pointsRepositorySFDA.getAllPointsByIdsAllExport(pointIds,search);

								}
								else {
					            	points = pointsRepositorySFDA.getAllPointsByIdsAll(pointIds,offset,search);
									size = pointsRepositorySFDA.getAllPointsSizeByIdsAll(pointIds,search);
								}

							 }
				             
				             if(active == 1) {
								if(exportData.equals("exportData")) {
					            	points = pointsRepository.getAllPointsByIdsExport(pointIds,search);

								}
								else {

					            	points = pointsRepository.getAllPointsByIds(pointIds,offset,search);
									size = pointsRepository.getAllPointsSizeByIds(pointIds,search);
								}


							 }
				             
							 for(Points point:points) {
							     Map PointsList= new HashMap();
	
							     
								 PointsList.put("id", point.getId());
								 PointsList.put("name", point.getName());
								 PointsList.put("latitude", point.getLatitude());
								 PointsList.put("longitude", point.getLongitude());
								 PointsList.put("delete_date", point.getDelete_date());
								 PointsList.put("photo", point.getPhoto());
								 PointsList.put("userId", point.getUserId());
								 PointsList.put("userName", null);
	
								 User us = userRepository.findOne(point.getUserId());
								 if(us != null) {
									 PointsList.put("userName", us.getName());
	
								 }
								 data.add(PointsList);
	
							 }
							
	
						 }
							
						 
						getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",data,size);
						logger.info("************************ getPointsList ENDED ***************************");
						return  ResponseEntity.ok().body(getObjectResponse);
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
						
					    
	
						
						

						 Integer size = 0;
						 List<Map> data = new ArrayList<>();
						 


	
						 if(active == 0) {
							if(exportData.equals("exportData")) {
								points = pointsRepositorySFDA.getAllPointsDeactiveExport(usersIds,search);

							}
							else {
								points = pointsRepositorySFDA.getAllPointsDeactive(usersIds,offset,search);
								size = pointsRepositorySFDA.getAllPointsSizeDeactive(usersIds,search);
							}
	
						 }
							 
			             if(active == 2) {
							if(exportData.equals("exportData")) {
								points = pointsRepositorySFDA.getAllPointsAllExport(usersIds,search);

							}
							else {
								points = pointsRepositorySFDA.getAllPointsAll(usersIds,offset,search);
								size = pointsRepositorySFDA.getAllPointsSizeAll(usersIds,search);
							}

								
						 }
			             
			             if(active == 1) {
							if(exportData.equals("exportData")) {
								points = pointsRepository.getAllPointsExport(usersIds,search);

							}
							else {
								points = pointsRepository.getAllPoints(usersIds,offset,search);
								size = pointsRepository.getAllPointsSize(usersIds,search);
							}

								
						 }
						 
						 for(Points point:points) {
						     Map PointsList= new HashMap();

						     
							 PointsList.put("id", point.getId());
							 PointsList.put("name", point.getName());
							 PointsList.put("latitude", point.getLatitude());
							 PointsList.put("longitude", point.getLongitude());
							 PointsList.put("delete_date", point.getDelete_date());
							 PointsList.put("photo", point.getPhoto());
							 PointsList.put("userId", point.getUserId());
							 PointsList.put("userName", null);

							 User us = userRepository.findOne(point.getUserId());
							 if(us != null) {
								 PointsList.put("userName", us.getName());

							 }
							 data.add(PointsList);

						 }
							
	
						 
						getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",data,size);
						logger.info("************************ getPointsList ENDED ***************************");
						return  ResponseEntity.ok().body(getObjectResponse);
					}

				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",points);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
				
			}

		}
		else{
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",points);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
	}

}
