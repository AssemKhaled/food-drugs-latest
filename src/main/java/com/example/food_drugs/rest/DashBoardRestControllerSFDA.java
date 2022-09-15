package com.example.food_drugs.rest;


import com.example.examplequerydslspringdatajpamaven.entity.CustomDeviceLiveData;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.food_drugs.dto.ApiResponse;
import com.example.food_drugs.dto.Request.NotificationSettingRequest;
import com.example.food_drugs.dto.responses.CustomDeviceLiveDataResponse;
import com.example.food_drugs.dto.responses.InventoryListDashBoardResponse;
import com.example.food_drugs.dto.responses.VehicleListDashBoardResponse;
import com.example.food_drugs.exception.ApiRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import com.example.examplequerydslspringdatajpamaven.service.DeviceServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.ReportServiceImpl;
import com.example.food_drugs.service.impl.DeviceServiceImplSFDA;
import com.example.food_drugs.service.impl.InventoryServiceImpl;

import java.util.List;


/**
 * Services of Dashboard component
 * @author fuinco
 *
 */
@CrossOrigin
@Component
@RequestMapping(path = "/homeSFDA")
public class DashBoardRestControllerSFDA {
	
	@Autowired
	private DeviceServiceImpl deviceService;
	
	@Autowired
	private DeviceServiceImplSFDA deviceServiceSFDA;
	
	@Autowired
	private ReportServiceImpl reportServiceImpl;
	
	@Autowired
	private InventoryServiceImpl inventoryServiceImpl;
	
	
	@GetMapping(path ="/getDevicesStatuesAndAllDrivers")
	public ResponseEntity<?> devicesStatuesAndAllDrivers(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                             @RequestParam (value = "userId", defaultValue = "0") Long userId){
		
		return deviceService.getDeviceStatus(TOKEN,userId);
	}
	
	@GetMapping(path = "/getAllDevicesLastInfo")
	public ResponseEntity<GetObjectResponse<CustomDeviceLiveDataResponse>> getAllDevicesLastInfo(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
																								 @RequestParam (value = "userId", defaultValue = "0") Long userId,
																								 @RequestParam (value = "offset", defaultValue = "0")int offset,
																								 @RequestParam (value = "search", defaultValue = "") String search,
																								 @RequestParam (value = "timeOffset", defaultValue = "") String timeOffset ){
		return deviceService.getAllDeviceLiveData(TOKEN,userId, offset, search,timeOffset);

	}
	
	@GetMapping(path = "/getAllDevicesLastInfoMap")
	public ResponseEntity<?> getAllDevicesLastInfo(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                       @RequestParam (value = "userId", defaultValue = "0") Long userId){		
		return deviceService.getAllDeviceLiveDataMap(TOKEN,userId);
	}


	@RequestMapping(value = "/getNotifications", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getNotifications(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
															@RequestParam (value = "userId", defaultValue = "0") Long userId,
															@RequestParam (value = "offset", defaultValue = "0") int offset,
															@RequestParam (value = "search", defaultValue = "") String search) {
		
    	return  reportServiceImpl.getNotifications(TOKEN,userId, offset,search);

	}
	@RequestMapping(value = "/vehicleInfo", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> vehicleInfo(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                           @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
			                                           @RequestParam(value = "userId",defaultValue = "0")Long userId){
		
    	return  deviceService.vehicleInfo(TOKEN,deviceId,userId);

	}
	
	@RequestMapping(value = "/getInventoriesNotifications", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getInventoriesNotifications(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
															@RequestParam (value = "userId", defaultValue = "0") Long userId,
															@RequestParam (value = "offset", defaultValue = "0") int offset,
															@RequestParam (value = "search", defaultValue = "") String search) {
		
    	return  inventoryServiceImpl.getInventoriesNotifications(TOKEN,userId, offset,search);

	}
	
	@GetMapping(path = "/getAllInventoriesLastInfo")
	public ResponseEntity<?> getAllInventoriesLastInfo(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                       @RequestParam (value = "userId", defaultValue = "0") Long userId,
												   @RequestParam (value = "offset", defaultValue = "0")int offset,
												   @RequestParam (value = "search", defaultValue = "") String search ){
		return inventoryServiceImpl.getAllInventoriesLastInfo(TOKEN,userId, offset, search);

	}

	@GetMapping(path = "/getAllInventoriesLastInfoNew")

	public ResponseEntity<?> getAllInventoriesLastInfoNew(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													   @RequestParam (value = "userId", defaultValue = "0") Long userId,
													   @RequestParam (value = "offset", defaultValue = "0")int offset,
													   @RequestParam (value = "search", defaultValue = "") String search,
													   @RequestParam (value = "timeOffset", defaultValue = "") String timeOffset){
		return inventoryServiceImpl.getAllInventoriesLastInfoNew(TOKEN,userId, offset, search,timeOffset);

	}
	
	@GetMapping(path ="/getInventoryStatus")
	public ResponseEntity<?> getInventoryStatus(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                             @RequestParam (value = "userId", defaultValue = "0") Long userId){
		
		return inventoryServiceImpl.getInventoryStatus(TOKEN,userId);
	}

	@GetMapping("/vehicleListDashBoard")
	ResponseEntity<ApiResponse<List<VehicleListDashBoardResponse>>> vehicleListDashBoard(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
																						 @RequestParam (value = "userId", defaultValue = "0") Long userId) {
		try{
			return ResponseEntity.ok(
					deviceServiceSFDA.vehicleListDashBoard(TOKEN,userId));

		}catch (Exception | Error e){
			throw new ApiRequestException(e.getLocalizedMessage());
		}
	}

	@GetMapping("/inventoryListDashBoard")
	ResponseEntity<ApiResponse<List<InventoryListDashBoardResponse>>> inventoryListDashBoard(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
																							 @RequestParam (value = "userId", defaultValue = "0") Long userId) {
		try{
			return ResponseEntity.ok(
					inventoryServiceImpl.inventoryListDashBoard(TOKEN,userId));

		}catch (Exception | Error e){
			throw new ApiRequestException(e.getLocalizedMessage());
		}
	}

}
