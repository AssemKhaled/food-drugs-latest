package com.example.food_drugs.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.example.food_drugs.service.ComputedServiceSFDA;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.example.examplequerydslspringdatajpamaven.entity.Attribute;
import com.example.examplequerydslspringdatajpamaven.entity.User;
import com.example.examplequerydslspringdatajpamaven.repository.ComputedRepository;
import com.example.examplequerydslspringdatajpamaven.repository.UserClientComputedRepository;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.rest.RestServiceController;
import com.example.examplequerydslspringdatajpamaven.service.ComputedServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.UserRoleService;
import com.example.examplequerydslspringdatajpamaven.service.UserServiceImpl;
import com.example.food_drugs.repository.ComputedRepositorySFDA;


/**
 * services functionality related to computed SFDA
 * @author fuinco
 *
 */
@Component
@Service
public class ComputedServiceImplSFDA extends RestServiceController implements ComputedServiceSFDA {

	private GetObjectResponse getObjectResponse;
	
	private static final Log logger = LogFactory.getLog(ComputedServiceImpl.class);

	@Autowired
	private UserServiceImpl userServiceImpl;
	
	@Autowired
	private ComputedServiceImpl computedServiceImpl;
	
	@Autowired
	private UserRoleService userRoleService;
	
	@Autowired
	private ComputedRepositorySFDA computedRepositorySFDA;
	
	@Autowired
	private ComputedRepository computedRepository;
	
	@Autowired
	private UserClientComputedRepository userClientComputedRepository;
	
	@Override
	public ResponseEntity<?> activeComputedSFDA(String TOKEN, Long attributeId, Long userId) {
		logger.info("************************ activeComputed STARTED ***************************");

		List<Attribute> attributes = new ArrayList<Attribute>();
		User user = userServiceImpl.findById(userId);
		if(user == null ) {
			getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",attributes);
			return  ResponseEntity.status(404).body(getObjectResponse);

		}
		
		if(user.getAccountType()!= 1) {
			if(!userRoleService.checkUserHasPermission(userId, "COMPUTED", "delete")) {
				 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to active",null);
				 logger.info("************************ activeComputed ENDED ***************************");
				return  ResponseEntity.badRequest().body(getObjectResponse);
			}
		}
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",attributes);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(attributeId != 0) {
			Attribute attribute = computedRepository.findOne(attributeId);
			if(attribute != null) {
			
				 boolean isParent = false;
				 if(user.getAccountType().equals(4)) {
					 Set<User> parentClients = user.getUsersOfUser();
					 if(parentClients.isEmpty()) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to active this attribute",attributes);
						 return  ResponseEntity.badRequest().body(getObjectResponse);
					 }else {
						 User parent = null;
						 for(User object : parentClients) {
							 parent = object;
						 }
						 Set<User>attributeParent = attribute.getUserAttribute();
						 if(attributeParent.isEmpty()) {
							 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "you are not allowed to active this attribute",attributes);
							 return  ResponseEntity.badRequest().body(getObjectResponse);
						 }else {
							 for(User parentObject : attributeParent) {
								 if(parentObject.getId().equals(parent.getId())) {
									 isParent = true;
									 break;
								 }
							 }
						 }
					 }
					 
				 }
				 if(!computedServiceImpl.checkIfParent(attribute , user) && ! isParent) {
					getObjectResponse = new GetObjectResponse( HttpStatus.BAD_REQUEST.value(), "you are not allowed to active this attribute ",attributes);
					logger.info("************************ activeComputed ENDED ***************************");
					return ResponseEntity.badRequest().body(getObjectResponse);
				 }
				 
			    attribute.setDelete_date(null);
			    
			    computedRepository.save(attribute);

				getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "success",attributes);
				logger.info("************************ activeComputed ENDED ***************************");
				return  ResponseEntity.ok().body(getObjectResponse);
				
			}
			else {

				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This attribute ID was not found",attributes);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
						
		}
		else {
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "attributes ID is Required",attributes);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
	}
	@Override
	public ResponseEntity<?> getAllComputedSFDA(String TOKEN, Long id, int offset, String search, int active,String exportData) {
		logger.info("************************ getAllComputed STARTED ***************************");
		List<Attribute> attrbuites = new ArrayList<Attribute>();
		
		if(TOKEN.equals("")) {
			 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "TOKEN id is required",attrbuites);
			 return  ResponseEntity.badRequest().body(getObjectResponse);
		}
		
		if(super.checkActive(TOKEN)!= null)
		{
			return super.checkActive(TOKEN);
		}
		if(id != 0) {
			
			User user = userServiceImpl.findById(id);
			if(user == null ) {
				getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",attrbuites);
				return  ResponseEntity.status(404).body(getObjectResponse);

			}
			else {
				if(user.getAccountType()!= 1) {
					if(!userRoleService.checkUserHasPermission(id, "COMPUTED", "list")) {
						 getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "this user doesnot has permission to get attrbuites list",null);
						 logger.info("************************ getAllattributes ENDED ***************************");
						return  ResponseEntity.badRequest().body(getObjectResponse);
					}
				}
				if(user.getDeleteDate() == null) {
					
				    if(user.getAccountType().equals(4)) {

				    	List<Long> computedIds = userClientComputedRepository.getComputedsIds(id);
				    	Integer size=0;
				    	if(computedIds.size() > 0) {
				    		
				    		if(active == 0) {
								if(exportData.equals("exportData")) {
									attrbuites = computedRepositorySFDA.getAllComputedByIdsDeactiveExport(computedIds,search);

								}
								else {
									attrbuites = computedRepositorySFDA.getAllComputedByIdsDeactive(computedIds,offset,search);
									size=computedRepositorySFDA.getAllComputedSizeByIdsDeactive(computedIds);
								}
				    			
							 }
							 
			                 if(active == 2) {
								if(exportData.equals("exportData")) {
				                	attrbuites = computedRepositorySFDA.getAllComputedByIdsAllExport(computedIds,search);

								}
								else {

				                	attrbuites = computedRepositorySFDA.getAllComputedByIdsAll(computedIds,offset,search);
									size=computedRepositorySFDA.getAllComputedSizeByIdsAll(computedIds);
								}
									
							 }
			                 
			                 if(active == 1) {
								if(exportData.equals("exportData")) {
				                	attrbuites = computedRepository.getAllComputedByIdsExport(computedIds,search);

								}
								else {

				                	attrbuites = computedRepository.getAllComputedByIds(computedIds,offset,search);
									size=computedRepository.getAllComputedSizeByIds(computedIds);
								}
			    			 }
			                 
				    		
							
				    	}
				    	
				    	getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",attrbuites,size);
						logger.info("************************ getAllComputed ENDED ***************************");
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
							attrbuites = computedRepositorySFDA.getAllComputedDeactiveExport(usersIds,search);

						}
						else {
							attrbuites = computedRepositorySFDA.getAllComputedDeactive(usersIds,offset,search);
							size=computedRepositorySFDA.getAllComputedSizeDeactive(usersIds);
							
						}

						 
					 }
					 
		             if(active == 2) {

						if(exportData.equals("exportData")) {
							attrbuites = computedRepositorySFDA.getAllComputedAllExport(usersIds,search);

						}
						else {
							attrbuites = computedRepositorySFDA.getAllComputedAll(usersIds,offset,search);
							size=computedRepositorySFDA.getAllComputedSizeAll(usersIds);
						}

						 
					 }
		             
		             if(active == 1) {
						if(exportData.equals("exportData")) {
							attrbuites = computedRepository.getAllComputedExport(usersIds,search);

						}
						else {
							attrbuites = computedRepository.getAllComputed(usersIds,offset,search);
							size=computedRepository.getAllComputedSize(usersIds);
						}

						 
					 }

					getObjectResponse= new GetObjectResponse(HttpStatus.OK.value(), "Success",attrbuites,size);
					logger.info("************************ getAllComputed ENDED ***************************");
					return  ResponseEntity.ok().body(getObjectResponse);

				}
				else {
					getObjectResponse= new GetObjectResponse(HttpStatus.NOT_FOUND.value(), "This User is not Found",attrbuites);
					return  ResponseEntity.status(404).body(getObjectResponse);

				}
				
			}

		}
		else{
			
			getObjectResponse= new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), "User ID is Required",attrbuites);
			return  ResponseEntity.badRequest().body(getObjectResponse);

		}
	}

}
