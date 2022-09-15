package com.example.food_drugs.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.example.food_drugs.service.UserRoleServiceSFDA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.entity.UserRole;
import com.example.examplequerydslspringdatajpamaven.repository.UserRoleRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;
import com.example.examplequerydslspringdatajpamaven.service.UserRoleServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.UserServiceImpl;
import com.example.food_drugs.repository.UserRoleRepositorySFDA;

/**
 * services functionality related to roles
 * @author fuinco
 *
 */
@Component
@Service
public class UserRoleServiceImplSFDA extends RestServiceController implements UserRoleServiceSFDA {
	
	@Autowired
	private UserRoleRepository userRoleRepository;
	
	@Autowired
	private UserRoleRepositorySFDA userRoleRepositorySFDA;
	
	@Autowired
	private UserServiceImpl userServiceImpl;
	
	
	@Autowired
	private UserRoleServiceImpl userRoleServiceImpl;
	
	private GetObjectResponse getObjectResponse;

	@Override
	public ResponseEntity<?> activeRole(String TOKEN, Long roleId, Long userId) {
		// TODO Auto-generated method stub
		if(TOKEN.equals("")) {
			 List<UserRole> roles = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",roles);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		User loggedUser = userServiceImpl.findById(userId);
		 if(loggedUser == null) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "logged User is not found ",null);
			 return  ResponseEntity.status(404).body(getObjectResponse); 
		 }
		 if(loggedUser.getAccountType()!= 1) {
			if(!userRoleServiceImpl.checkUserHasPermission(userId, "ROLE", "delete")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to active role",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		if(roleId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "No roleId  to active ",null);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		UserRole role = userRoleRepository.findOne(roleId);
		if(role == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this role not found ",null);
			 return  ResponseEntity.status(404).body(getObjectResponse);
		}
		
		Long createdByUserId=role.getUserId();
		
		if(loggedUser.getAccountType().equals(4)) {
			 List<User> parents=userServiceImpl.getAllParentsOfuser(loggedUser,loggedUser.getAccountType());
			 if(parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this role.",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 else {
				 User parentClient = new User() ;
				 boolean isParent = false;

				 for(User object : parents) {
					 parentClient = object;
					 break;
					 
				 }
				 if(createdByUserId.equals(parentClient.getId())) {
				 		isParent =true;
				 }
				if(isParent == false) {
					getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "As you are account type 4 and this role not created by direct parent not allow to edit.",null);
					return  ResponseEntity.badRequest().body(getObjectResponse);
				}
				 
			 }
			 
		}
		
		 
		 
         role.setDelete_date(null);
         userRoleRepository.save(role);
	     
         getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",null);
		 return  ResponseEntity.ok().body(getObjectResponse);
	}

	@Override
	public ResponseEntity<?> getAllRolesCreatedByUserSFDA(String TOKEN, Long userId, int offset, String search,
			int active,String exportData) {
		// TODO Auto-generated method stub
		if(TOKEN.equals("")) {
			 List<UserRole> roles = null;
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",roles);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(userId == 0) {
			getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "  userId is required",null);
			return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		User user = userServiceImpl.findById(userId);
		if(user == null) {
			getObjectResponse = new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "this user not found ",null);
			return  ResponseEntity.status(404).body(getObjectResponse);
		}
		
		
		if(user.getAccountType()!= 1) {
			if(!userRoleServiceImpl.checkUserHasPermission(userId, "ROLE", "list")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this loggedId doesnot has permission to get list role",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		

		List<User> childernUsers= new ArrayList<User>();
		List<Long>usersIds= new ArrayList<>();

		if(user.getAccountType().equals(4)) {
			 Set<User> parents = user.getUsersOfUser();
			 if(parents.isEmpty()) {
				getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "as you are not have parent you cannot allow to edit this role.",null);
				return  ResponseEntity.badRequest().body(getObjectResponse);
			 }
			 else {
				 User parentClient = new User() ;
				 for(User object : parents) {

					 parentClient = object;
					 break;
				 }

				 childernUsers=userServiceImpl.getActiveAndInactiveChildern(parentClient.getId());
				 usersIds.add(parentClient.getId());
				 
				 
			 }
			 
		}
		else {

			childernUsers=userServiceImpl.getActiveAndInactiveChildern(userId);
		}
		
		
		
		 if(childernUsers.isEmpty()) {

			 usersIds.add(userId);
		 }
		 else {

			 usersIds.add(userId);
			 for(User object : childernUsers) {

				 usersIds.add(object.getId());

			 }
		 }
		 
		Integer size=0;
		List<UserRole> roles = new ArrayList<UserRole>();
		
		if(active == 0) {
			if(exportData.equals("exportData")) {
				roles = userRoleRepositorySFDA.getAllRolesCreatedByUserOffsetDeactiveExport(usersIds,search);

			}
			else {
				roles = userRoleRepositorySFDA.getAllRolesCreatedByUserOffsetDeactive(usersIds,offset,search);
				size = userRoleRepositorySFDA.getRolesSizeDeactive(usersIds);
			}
		}
		if(active == 2) {
			if(exportData.equals("exportData")) {
				roles = userRoleRepositorySFDA.getAllRolesCreatedByUserOffsetAllExport(usersIds,search);

			}
			else {
				roles = userRoleRepositorySFDA.getAllRolesCreatedByUserOffsetAll(usersIds,offset,search);
				size = userRoleRepositorySFDA.getRolesSizeAll(usersIds);
			}
	
		}
		if(active == 1) {
			if(exportData.equals("exportData")) {
				roles = userRoleRepository.getAllRolesCreatedByUserOffsetExport(usersIds,search);

			}
			else {
				roles = userRoleRepository.getAllRolesCreatedByUserOffset(usersIds,offset,search);
				size = userRoleRepository.getRolesSize(usersIds);
			}


		}
		
		getObjectResponse = new GetObjectResponse(HttpStatus.OK.value(), "success",roles,size);
		
		return ResponseEntity.ok().body(getObjectResponse);
	}
	
}
