package com.example.food_drugs.rest;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
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
import com.example.food_drugs.entity.Inventory;
import com.example.food_drugs.entity.SensorsInventories;
import com.example.food_drugs.service.impl.InventoryServiceImpl;

@RestController
@RequestMapping(path = "/inventoriesSFDA")
@CrossOrigin
public class InventoryController {
	
	@Autowired
	private InventoryServiceImpl inventoryServiceImpl;

	@GetMapping("/getInventoriesList")
	public ResponseEntity<?> getInventoriesList(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
		                                       @RequestParam (value = "exportData", defaultValue = "") String exportData,                                                                     
					                           @RequestParam (value = "userId",defaultValue = "0") Long userId,
											   @RequestParam(value = "offset", defaultValue = "0") int offset,
									           @RequestParam(value = "search", defaultValue = "") String search,
									           @RequestParam(value = "active", defaultValue = "1") int active) {
	 
		return inventoryServiceImpl.getInventoriesList(TOKEN,userId,offset,search,active,exportData);
		
	}
	
	@PostMapping(path ="/createInventories")
	public ResponseEntity<?> createInventories(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                              @RequestParam (value = "userId",defaultValue = "0") Long userId,
			                              @RequestBody(required = false) Inventory inventory) {
		
			 return inventoryServiceImpl.createInventories(TOKEN,inventory,userId);				
	}
	
	@RequestMapping(value = "/editInventories", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> editInventories(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                          @RequestBody(required = false) Inventory inventory,
			                                          @RequestParam (value = "userId", defaultValue = "0") Long id) {
		
		
		return inventoryServiceImpl.editInventories(TOKEN,inventory,id);

	}
	
	@RequestMapping(value = "/getInventoryById", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getInventoryById(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                               @RequestParam (value = "inventoryId", defaultValue = "0") Long inventoryId,
			                                               @RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
    	return  inventoryServiceImpl.getInventoryById(TOKEN,inventoryId,userId);

	}
	
	@RequestMapping(value = "/deleteInventory", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> deleteInventory(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                            @RequestParam (value = "inventoryId", defaultValue = "0") Long inventoryId,
			                                            @RequestParam (value = "userId", defaultValue = "0") Long userId) {

    	return  inventoryServiceImpl.deleteInventory(TOKEN,inventoryId,userId);

	}
	
	@RequestMapping(value = "/activeInventory", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> activeInventory(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                            @RequestParam (value = "inventoryId", defaultValue = "0") Long inventoryId,
			                                            @RequestParam (value = "userId", defaultValue = "0") Long userId) {

    	return  inventoryServiceImpl.activeInventory(TOKEN,inventoryId,userId);

	}
	
	@GetMapping("/assignWarehouseToInventory")
	public ResponseEntity<?>assignWarehouseToInventory(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
											@RequestParam (value = "userId",defaultValue = "0") Long userId,
											@RequestParam (value = "inventoryId",defaultValue = "0") Long inventoryId,
											@RequestParam (value = "warehouseId",defaultValue = "0") Long warehouseId){
	
		if(warehouseId == 0) {
			return inventoryServiceImpl.removeWarehouseFromInventory(TOKEN,userId,inventoryId,warehouseId);
		}else {
			return inventoryServiceImpl.assignWarehouseToInventory(TOKEN,userId,inventoryId,warehouseId);
		}
		
	}
	
	@GetMapping("/getSelectedAndListWarehouse")
	public ResponseEntity<?>getSelectedAndListWarehouse(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
											@RequestParam (value = "userId",defaultValue = "0") Long userId,
											@RequestParam (value = "inventoryId",defaultValue = "0") Long inventoryId,
											@RequestParam (value = "loggedUserId",defaultValue = "0") Long loggedUserId){
	
		return inventoryServiceImpl.getSelectedAndListWarehouse(TOKEN,loggedUserId,userId,inventoryId);
		
		
	}
	
	@GetMapping("/getSelectListInventories")
	public ResponseEntity<?> getSelectListInventories(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
				                           @RequestParam (value = "userId",defaultValue = "0") Long userId) {
	 
		return inventoryServiceImpl.getSelectListInventories(TOKEN,userId);
		
	}

	@RequestMapping(value = "/getInventoryUnSelectOfClient", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getInventoryUnSelectOfClient(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
																	@RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId,                                             
																	@RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
    	return  inventoryServiceImpl.getInventoryUnSelectOfClient(TOKEN,loggedUserId,userId);

	}
	
	@GetMapping("/assignClientInventories")
	public ResponseEntity<?> assignClientInventories(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
		                           			   @RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId,
											   @RequestParam (value = "userId", defaultValue = "0") Long userId,
											   @RequestParam (value = "inventoryIds", defaultValue = "0") Long [] inventoryIds) {
		return inventoryServiceImpl.assignClientInventories(TOKEN,loggedUserId,userId,inventoryIds);
	}
	
	@GetMapping("/getClientInventories")
	public ResponseEntity<?> getClientInventories(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
		                           			@RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId,
											@RequestParam (value = "userId", defaultValue = "0") Long userId) {
		return inventoryServiceImpl.getClientInventories(TOKEN,loggedUserId,userId);
	}
	

	@GetMapping("/getEasyCloudData")
    @Scheduled(fixedRate = 30000)
	public ResponseEntity<?> getEasyCloudData() {
		
		return inventoryServiceImpl.getEasyCloudData();
	}
	
	@PostMapping(path ="/getDataProtocols")
	public ResponseEntity<?> getDataProtocols(@RequestBody Map<Object,Object> dataObject){
		
		String email = "";
		String type = "";
		ArrayList<Map<Object,Object>> data = new ArrayList<Map<Object,Object>>();
		
		if(dataObject.containsKey("email")) {
			email = (String) dataObject.get("email");
		}
		if(dataObject.containsKey("type")) {
			type = 	(String) dataObject.get("type");
		}
		if(dataObject.containsKey("data")) {
			data = 	(ArrayList<Map<Object, Object>>) dataObject.get("data");

		}
		
		return inventoryServiceImpl.getDataProtocols(data,type,email);
	}
	
	//only for one client
	@PostMapping(path ="/getDataProtocolsCSV")
	public ResponseEntity<?> getDataProtocolsCSV(@RequestBody ArrayList<Map<Object,Object>> data){
		String email = "hzetawi@alhaya-medical.com";
		String type = "csv";
		
		return inventoryServiceImpl.getDataProtocols(data,type,email);
	}
	
	@GetMapping("/getSensorsInventoriesList")
	public ResponseEntity<?> getSensorsInventoriesList(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
				                           @RequestParam (value = "inventoryId",defaultValue = "0") Long inventoryId,
				                           @RequestParam (value = "userId",defaultValue = "0") Long userId) {
	 
		return inventoryServiceImpl.getSensorsInventoriesList(TOKEN,inventoryId,userId);
		
	}
	
	@PostMapping(path ="/addSensor")
	public ResponseEntity<?> addSensor(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                           @RequestParam (value = "userId",defaultValue = "0") Long userId,
			                           @RequestParam (value = "inventoryId",defaultValue = "0") Long InventoryId,
			                           @RequestBody(required = false) SensorsInventories sensorsInventories) {
		
		return inventoryServiceImpl.addSensor(TOKEN,sensorsInventories,InventoryId,userId);				
	}
	@GetMapping("/removeSensor")
	public ResponseEntity<?> removeSensor(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                           @RequestParam (value = "userId",defaultValue = "0") Long userId,
			                           @RequestParam (value = "inventoryId",defaultValue = "0") Long InventoryId,
			                           @RequestParam (value = "sensorInventoryId",defaultValue = "0") Long sensorInventoryId) {
		
		return inventoryServiceImpl.removeSensor(TOKEN,sensorInventoryId,InventoryId,userId);				
	}
	
	@PostMapping(path ="/getDataProtocolsSkarpt")
	public ResponseEntity<?> getDataProtocolsSkarpt(@RequestBody Map<Object,Object> data){
		
		return inventoryServiceImpl.getDataProtocolsSkarpt(data);
	}
	
	@GetMapping(value = "/assignInventoryToUser")
	public ResponseEntity<?> assignInventoryToUser(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                     @RequestParam (value = "userId", defaultValue = "0") Long userId,
												 @RequestParam (value = "inventoryId", defaultValue = "0") Long inventoryId,
												 @RequestParam (value = "toUserId", defaultValue = "0") Long toUserId){
		return inventoryServiceImpl.assignInventoryToUser(TOKEN,userId,inventoryId,toUserId);
	}

	@GetMapping(value = "/inventoryMonthelyView")
	public ResponseEntity<?> inventoryMonthelyView(@RequestParam(value = "endDate") String endDate){
		return inventoryServiceImpl.inventoryMonthelyView(endDate);
	}

	@GetMapping(value = "/assign")
	public Date assign() throws ParseException{
		Instant nowUtc = Instant.now();
		ZoneId asiaSingapore = ZoneId.of("Asia/Riyadh");
		ZonedDateTime nowAsiaSingapore = ZonedDateTime.ofInstant(nowUtc, asiaSingapore);
		System.out.println(nowAsiaSingapore);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.Z");
		String formattedString = nowAsiaSingapore.format(formatter);
		System.out.println(formattedString);
		
//		Date now = new Date();
//		System.out.println(now);
		SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		input.setTimeZone(TimeZone.getTimeZone("Aisa/Riyadh"));
//		String create_date = input.format(now);
//		System.out.println(create_date);
		Date dateTime = input.parse(formattedString) ;
				//input.parse(formattedString);
		System.out.println("finalDate"+dateTime);
		return dateTime;
//		return inventoryServiceImpl.assignInventoryToUser(TOKEN,userId,inventoryId,toUserId);
	}
	
	
}
