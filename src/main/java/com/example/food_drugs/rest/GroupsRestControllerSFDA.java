package com.example.food_drugs.rest;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.example.examplequerydslspringdatajpamaven.entity.Group;
import com.example.examplequerydslspringdatajpamaven.service.GroupsServiceImpl;
import com.example.food_drugs.service.impl.GroupServiceImplSFDA;

/**
 * Service of group component
 * @author fuinco
 *
 */
@CrossOrigin
@Component
@RequestMapping(path = "/groupSFDA")
public class GroupsRestControllerSFDA {
	
	
	@Autowired
	private GroupsServiceImpl groupsServiceImpl;
	
	@Autowired
	private GroupServiceImplSFDA groupServiceImplSFDA;

	
	@PostMapping(path ="/createGroup")
	public ResponseEntity<?> createGroup(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
										 @RequestParam (value = "userId",defaultValue = "0") Long userId,
							             @RequestBody(required = false) Group group) {
								
		return groupsServiceImpl.createGroup(TOKEN, group,userId);				
	
	}
	
	@RequestMapping(value = "/getAllGroups", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getAllGroups(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
														@RequestParam (value = "exportData", defaultValue = "") String exportData,                                            
														@RequestParam (value = "userId", defaultValue = "0") Long id,
													 	@RequestParam (value = "offset", defaultValue = "0") int offset,
														@RequestParam (value = "search", defaultValue = "") String search,
														@RequestParam (value = "active", defaultValue = "1") int active) {
		
    	return  groupServiceImplSFDA.getAllGroupsSFDA(TOKEN,id,offset,search,active,exportData);

	}
	
	@RequestMapping(value = "/getGroupById", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getGroupById(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                               @RequestParam (value = "groupId", defaultValue = "0") Long groupId,
			                                               @RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
    	return  groupsServiceImpl.getGroupById(TOKEN,groupId,userId);

	}
	
	@RequestMapping(value = "/editGroup", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> editGroup(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                            @RequestBody(required = false) Group group,
			                                            @RequestParam (value = "userId", defaultValue = "0") Long id) {
		
		
		return groupsServiceImpl.editGroup(TOKEN,group,id);

	}
	
	@RequestMapping(value = "/deleteGroup", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> deleteGroup(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                            @RequestParam (value = "groupId", defaultValue = "0") Long groupId,
			                                            @RequestParam (value = "userId", defaultValue = "0") Long userId) {

    	return  groupsServiceImpl.deleteGroup(TOKEN,groupId,userId);


	}
	
	@RequestMapping(value = "/activeGroup", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> activeGroup(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                            @RequestParam (value = "groupId", defaultValue = "0") Long groupId,
			                                            @RequestParam (value = "userId", defaultValue = "0") Long userId) {

    	return  groupServiceImplSFDA.activeGroup(TOKEN,groupId,userId);


	}
	
	@RequestMapping(value = "/assignGroupToDriver", method = RequestMethod.POST)
	public ResponseEntity<?> assignGroupToDriver(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			@RequestBody Map<String, List> data,
			@RequestParam(value = "groupId" ,defaultValue = "0") Long groupId,
			@RequestParam(value = "userId" , defaultValue = "0")Long userId ) {
		
		return groupsServiceImpl.assignGroupToDriver(TOKEN,groupId,data,userId);	
		
	}
	
	@RequestMapping(value = "/assignGroupToGeofence", method = RequestMethod.POST)
	public ResponseEntity<?> assignGroupToGeofence(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			@RequestBody Map<String, List> data,
			@RequestParam(value = "groupId" ,defaultValue = "0") Long groupId,
			@RequestParam(value = "userId" , defaultValue = "0")Long userId ) {
		
		return groupsServiceImpl.assignGroupToGeofence(TOKEN,groupId,data,userId);	
		
	}
	
	@RequestMapping(value = "/assignGroupToDevice", method = RequestMethod.POST)
	public ResponseEntity<?> assignGroupToDevice(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			@RequestBody Map<String, List> data,
			@RequestParam(value = "groupId" ,defaultValue = "0") Long groupId,
			@RequestParam(value = "userId" , defaultValue = "0")Long userId ) {
		
		return groupsServiceImpl.assignGroupToDevice(TOKEN,groupId,data,userId);	
		
	}
	
	@GetMapping(value = "/getGroupDataSelected")
	public @ResponseBody ResponseEntity<?> getGroupDevices(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                               @RequestParam (value = "groupId",defaultValue = "0") Long groupId,
			                                               @RequestParam (value = "type", defaultValue = "") String type) {
		
			
		return groupsServiceImpl.getGroupDevices(TOKEN,groupId,type);

	}
	
	@RequestMapping(value = "/getGroupSelect", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getGroupSelect(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
												          @RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId,
			                                              @RequestParam (value = "userId", defaultValue = "0") Long userId,
			                                              @RequestParam (value = "type", defaultValue = "") List<String> type) {
		
    	return  groupsServiceImpl.getGroupSelect(TOKEN,loggedUserId,userId,type);
		
	}
	
	@RequestMapping(value = "/getGroupUnSelectOfCient", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getGroupUnSelectOfCient(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
															        @RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId,                                               
																	@RequestParam (value = "userId", defaultValue = "0") Long userId) {
																
	
    	return  groupsServiceImpl.getGroupUnSelectOfCient(TOKEN, loggedUserId,userId);

		
	}
	
	@GetMapping("/assignClientGroups")
	public ResponseEntity<?> assignClientGroups(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
		                           			   @RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId,
											   @RequestParam (value = "userId", defaultValue = "0") Long userId,
											   @RequestParam (value = "groupIds", defaultValue = "0") Long [] groupIds) {
		return groupsServiceImpl.assignClientGroups(TOKEN,loggedUserId,userId,groupIds);
	}
	
	@GetMapping("/getClientGroups")
	public ResponseEntity<?> getClientGroups(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
		                           			@RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId,
											@RequestParam (value = "userId", defaultValue = "0") Long userId) {
		return groupsServiceImpl.getClientGroups(TOKEN,loggedUserId,userId);
	}

}
