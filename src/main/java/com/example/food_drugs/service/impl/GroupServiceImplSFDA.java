package com.example.food_drugs.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.example.food_drugs.service.GroupServiceSFDA;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import com.example.examplequerydslspringdatajpamaven.entity.Group;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.repository.GroupRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserClientGroupRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;
import com.example.examplequerydslspringdatajpamaven.service.GroupsServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.UserRoleService;
import com.example.examplequerydslspringdatajpamaven.service.UserServiceImpl;
import com.example.food_drugs.repository.GroupRepositorySFDA;

/**
 * Queries on table tc_groups with SFDA
 * @author fuinco
 *
 */
@Component
public class GroupServiceImplSFDA extends RestServiceController implements GroupServiceSFDA {


	private static final Log logger = LogFactory.getLog(GroupsServiceImpl.class);

	private GetObjectResponse getObjectResponse;
	
	@Autowired
	private UserServiceImpl userServiceImpl;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	UserClientGroupRepository userClientGroupRepository;
	
	@Autowired
	private UserRoleService userRoleService;
	
	@Autowired 
	private GroupRepository groupRepository;
	
	@Autowired 
	private GroupRepositorySFDA groupRepositorySFDA;
	
	@Autowired
	private GroupsServiceImpl groupServiceImpl;
	
	@Override
	public ResponseEntity<?> activeGroup(String TOKEN, Long groupId, Long userId) {
		logger.info("************************ activeGroup STARTED ***************************");
		
		List<Group> groups = new ArrayList<Group>();
		User user = userServiceImpl.findById(userId);
		if(user == null ) {
			getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",groups);
			return  ResponseEntity.status(404).body(getObjectResponse);

		}
		
		if(user.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "GROUP", "delete")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to active",null);
				 logger.info("************************ activeGroup ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",groups);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(groupId != 0) {
			Group group = groupRepository.findOne(groupId);
			if(group != null) {
				
				 boolean isParent = false;
				 if(user.getAccountType().equals(4)) {
					 Set<User> parentClients = user.getUsersOfUser();
					 if(parentClients.isEmpty()) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to active this group",groups);
						 return  ResponseEntity.badRequest().body(getObjectResponse);
					 }else {
						 User parent = null;
						 for(User object : parentClients) {
							 parent = object;
						 }
						 Set<User>groupParent = group.getUserGroup();
						 if(groupParent.isEmpty()) {
							 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to active this group",groups);
							 return  ResponseEntity.badRequest().body(getObjectResponse);
						 }else {
							 for(User parentObject : groupParent) {
								 if(parentObject.getId().equals(parent.getId())) {
									 isParent = true;
									 break;
								 }
							 }
						 }
					 }

				 }
				 if(!groupServiceImpl.checkIfParent(group , user) && ! isParent) {
					getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to active this group ",groups);
					logger.info("************************ activeGroup ENDED ***************************");
					return ResponseEntity.badRequest().body(getObjectResponse);
				}
				group.setIs_deleted(null);
				groupRepository.save(group);
				
				getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",groups);
				logger.info("************************ activeGroup ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);
				
				
			}
			else {

				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This group ID was not found",groups);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
						
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "group ID is Required",groups);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
	}

	@Override
	public ResponseEntity<?> getAllGroupsSFDA(String TOKEN, Long id, int offset, String search, int active,String exportData) {
		
		logger.info("************************ getAllUserGroups STARTED ***************************");
		
		List<Group> groups = new ArrayList<Group>();
		
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",groups);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(id != 0) {
			
			User user = userServiceImpl.findById(id);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",groups);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(user.getAccountType()!= 1) {
					if(!userRoleService.checkUserHasPermission(id, "GROUP", "list")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get geofences list",null);
						 logger.info("************************ getAllUserDevices ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				if(user.getDeleteDate() == null) {
					
				    if(user.getAccountType().equals(4)) {
						 
				    	List<Long> groupIds = userClientGroupRepository.getGroupsIds(id);
						Integer size=0;
						List<Map> data = new ArrayList<>();

						 if(groupIds.size()>0) {

							if(active == 0) {
								if(exportData.equals("exportData")) {
								    groups = groupRepositorySFDA.getAllGroupsByIdsDeactiveExport(groupIds,search);

								}
								else {
								    groups = groupRepositorySFDA.getAllGroupsByIdsDeactive(groupIds,offset,search);
									size=groupRepositorySFDA.getAllGroupsSizeByIdsDeactive(groupIds,search);	
								}


							}
							 
				             if(active == 2) {
								if(exportData.equals("exportData")) {
								    groups = groupRepositorySFDA.getAllGroupsByIdsAllExport(groupIds,search);

								}
								else {
								    groups = groupRepositorySFDA.getAllGroupsByIdsAll(groupIds,offset,search);
									size=groupRepositorySFDA.getAllGroupsSizeByIdsAll(groupIds,search);
								}


							}
				             
				             if(active == 1) {
								if(exportData.equals("exportData")) {
								    groups = groupRepository.getAllGroupsByIdsExport(groupIds,search);

								}
								else {
								    groups = groupRepository.getAllGroupsByIds(groupIds,offset,search);
									size=groupRepository.getAllGroupsSizeByIds(groupIds,search);
								}


							 }
				             
							for(Group group:groups) {
							     Map PointsList= new HashMap();
							     
								 PointsList.put("id", group.getId());
								 PointsList.put("name", group.getName());
								 PointsList.put("attributes", group.getAttributes());
								 PointsList.put("groupid", group.getGroupid());
								 PointsList.put("is_deleted", group.getIs_deleted());
								 PointsList.put("type", group.getType());
								 PointsList.put("companyName",null);
								 PointsList.put("companyId",null);

								 
								 
							    	Set<User>groupParents = group.getUserGroup();
									if(groupParents.isEmpty()) {
										

									}else {
										for(User parentObject : groupParents) {
											 PointsList.put("companyId",parentObject.getId());
											 User us = userRepository.findOne(parentObject.getId());
											 if(us != null) {
												 PointsList.put("companyName", us.getName());

											 }
											 break;
											
										}
									}
									data.add(PointsList);

							}	 
								


						 }
						 
						 getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",data,size);
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

					
					Integer size=0;
					List<Map> data = new ArrayList<>();

					if(active == 0) {
						if(exportData.equals("exportData")) {
						    groups = groupRepositorySFDA.getAllGroupsDeactiveExport(usersIds,search);

						}
						else {
						    groups = groupRepositorySFDA.getAllGroupsDeactive(usersIds,offset,search);
							size=groupRepositorySFDA.getAllGroupsSizeDeactive(usersIds,search);
						}


					 }
					 
		             if(active == 2) {
						if(exportData.equals("exportData")) {
							groups = groupRepositorySFDA.getAllGroupsAllExport(usersIds,search);

						}
						else {
							groups = groupRepositorySFDA.getAllGroupsAll(usersIds,offset,search);
							size=groupRepositorySFDA.getAllGroupsSizeAll(usersIds,search);
						}


					 }
		             
		             if(active == 1) {
						if(exportData.equals("exportData")) {
						    groups = groupRepository.getAllGroupsExport(usersIds,search);

						}
						else {
						    groups = groupRepository.getAllGroups(usersIds,offset,search);
							size=groupRepository.getAllGroupsSize(usersIds,search);
						}


					 }
		             
					for(Group group:groups) {
					     Map PointsList= new HashMap();
					     
						 PointsList.put("id", group.getId());
						 PointsList.put("name", group.getName());
						 PointsList.put("attributes", group.getAttributes());
						 PointsList.put("groupid", group.getGroupid());
						 PointsList.put("is_deleted", group.getIs_deleted());
						 PointsList.put("type", group.getType());
						 PointsList.put("companyName",null);
						 PointsList.put("companyId",null);

						 
						 
				     		Set<User>groupParents = group.getUserGroup();
							if(groupParents.isEmpty()) {
								

							}else {
								for(User parentObject : groupParents) {
									 PointsList.put("companyId",parentObject.getId());
									 User us = userRepository.findOne(parentObject.getId());
									 if(us != null) {
										 PointsList.put("companyName", us.getName());

									 }
									break;
									
								}
							}
							data.add(PointsList);

					}	 
							
					
					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",data,size);
					logger.info("************************ getAllUserGeofences ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);

				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",groups);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
				
			}

		}
		else{
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",groups);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}

	}

}
