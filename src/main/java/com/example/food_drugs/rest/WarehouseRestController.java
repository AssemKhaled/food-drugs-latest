package com.example.food_drugs.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.food_drugs.entity.Warehouse;
import com.example.food_drugs.service.impl.WarehouseServiceImpl;

/**
 * Services of Warehouses component 
 * @author fuinco
 *
 */
@RestController
@RequestMapping(path = "/warehousesSFDA")
@CrossOrigin
public class WarehouseRestController {

	@Autowired
	private WarehouseServiceImpl warehouseServiceImpl;
	
	
	@GetMapping("/getWarehousesList")
	public ResponseEntity<?> getWarehousesList(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
								               @RequestParam (value = "exportData", defaultValue = "") String exportData,                                                                     
				                               @RequestParam (value = "userId",defaultValue = "0") Long userId,
											   @RequestParam(value = "offset", defaultValue = "0") int offset,
									           @RequestParam(value = "search", defaultValue = "") String search,
									           @RequestParam(value = "active", defaultValue = "1") int active) {
	 
		return warehouseServiceImpl.getWarehousesList(TOKEN,userId,offset,search,active,exportData);
		
	}
	
	@PostMapping(path ="/createWarehouses")
	public ResponseEntity<?> createWarehouses(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                              @RequestParam (value = "userId",defaultValue = "0") Long userId,
			                              @RequestBody(required = false) Warehouse warehouse) {
		
		return warehouseServiceImpl.createWarehouses(TOKEN,warehouse,userId);				
	}
	@RequestMapping(value = "/editWarehouses", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> editWarehouses(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                          @RequestBody(required = false) Warehouse warehouse,
			                                          @RequestParam (value = "userId", defaultValue = "0") Long id) {
		
		
		return warehouseServiceImpl.editWarehouses(TOKEN,warehouse,id);

	}
	
	@RequestMapping(value = "/getWarehouseById", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getWarehouseById(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                               @RequestParam (value = "warehouseId", defaultValue = "0") Long warehouseId,
			                                               @RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
    	return  warehouseServiceImpl.getWarehouseById(TOKEN,warehouseId,userId);

	}
	
	@RequestMapping(value = "/deleteWarehouse", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> deleteWarehouse(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                            @RequestParam (value = "warehouseId", defaultValue = "0") Long warehouseId,
			                                            @RequestParam (value = "userId", defaultValue = "0") Long userId) {

    	return  warehouseServiceImpl.deleteWarehouse(TOKEN,warehouseId,userId);


	}
	
	@RequestMapping(value = "/activeWarehouse", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> activeWarehouse(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                            @RequestParam (value = "warehouseId", defaultValue = "0") Long warehouseId,
			                                            @RequestParam (value = "userId", defaultValue = "0") Long userId) {

    	return  warehouseServiceImpl.activeWarehouse(TOKEN,warehouseId,userId);


	}
	
	
	@RequestMapping(value = "/getListSelectWarehouse", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getListSelectWarehouse(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                            @RequestParam (value = "userId", defaultValue = "0") Long  userId) {

    	return warehouseServiceImpl.getListSelectWarehouse(TOKEN,userId);

	}
	
	@RequestMapping(value = "/getListWarehouseMap", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getListWarehouseMap(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                                      @RequestParam (value = "userId", defaultValue = "0") Long  userId) {

    	return warehouseServiceImpl.getListWarehouseMap(TOKEN,userId);

	}

	@RequestMapping(value = "/getListWarehouseInventoryGraph", method = RequestMethod.GET)
	public ResponseEntity<?> getListWarehouseInventoryGraph(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
															@RequestParam (value = "userId", defaultValue = "0") Long  userId) {

		return warehouseServiceImpl.getListWarehouseDataGraph(TOKEN,userId);

	}


	@RequestMapping(value = "/getInventoryListOfWarehouseMap", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getInventoryListOfWarehouseMap(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                                   @RequestParam (value = "userId", defaultValue = "0") Long  userId,
			                                                   @RequestParam (value = "warehouseId", defaultValue = "0") Long  warehouseId) {

    	return warehouseServiceImpl.getInventoryListOfWarehouseMap(TOKEN,userId,warehouseId);

	}
	
	@RequestMapping(value = "/getWarehouseUnSelectOfClient", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getWarehouseUnSelectOfClient(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
																	@RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId,                                             
																	@RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
    	return  warehouseServiceImpl.getWarehouseUnSelectOfClient(TOKEN,loggedUserId,userId);

	}
	
	@GetMapping("/assignClientWarehouses")
	public ResponseEntity<?> assignClientWarehouses(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
		                           			   @RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId,
											   @RequestParam (value = "userId", defaultValue = "0") Long userId,
											   @RequestParam (value = "warehouseIds", defaultValue = "0") Long [] warehouseIds) {
		return warehouseServiceImpl.assignClientWarehouses(TOKEN,loggedUserId,userId,warehouseIds);
	}
	
	@GetMapping("/getClientWarehouses")
	public ResponseEntity<?> getClientWarehouses(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
		                           			@RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId,
											@RequestParam (value = "userId", defaultValue = "0") Long userId) {
		return warehouseServiceImpl.getClientWarehouses(TOKEN,loggedUserId,userId);
	}
	
	@GetMapping(value = "/assignWarehouseToUser")
	public ResponseEntity<?> assignWarehouseToUser(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                     @RequestParam (value = "userId", defaultValue = "0") Long userId,
												 @RequestParam (value = "warehouseId", defaultValue = "0") Long warehouseId,
												 @RequestParam (value = "toUserId", defaultValue = "0") Long toUserId){
		return warehouseServiceImpl.assignWarehouseToUser(TOKEN,userId,warehouseId,toUserId);
	}

}
