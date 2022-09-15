package com.example.food_drugs.rest.mobileRestSFDA;

import java.util.List;
import java.util.Map;

import com.example.food_drugs.dto.ApiResponse;
import com.example.food_drugs.dto.ApiResponseBuilder;
import com.example.food_drugs.dto.responses.mobile.WareHouseInvLastDataResponse;
import com.example.food_drugs.service.impl.WarehouseServiceImpl;
import com.example.food_drugs.service.mobile.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
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
import com.example.examplequerydslspringdatajpamaven.entity.Driver;
import com.example.examplequerydslspringdatajpamaven.entity.Geofence;
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.service.AppServiceImpl;
import com.example.examplequerydslspringdatajpamaven.service.DeviceServiceImpl;
import com.example.food_drugs.entity.DeviceSFDA;
import com.example.food_drugs.entity.Inventory;
import com.example.food_drugs.entity.Warehouse;
import com.example.food_drugs.service.mobile.Impl.AppServiceImplSFDA;

/**
 * Services related to app
 * @author fuinco
 *
 */
@CrossOrigin
@Component
@RequestMapping(path = "/appSFDA")
public class AppRestControllerSFDA {
	
	@Autowired
	private AppServiceImpl appService;
	
	@Autowired
	private AppServiceImplSFDA appServiceSFDA;
	
	@Autowired
	private DeviceServiceImpl deviceServiceImpl;

	private final WarehouseServiceImpl warehouseService;

	private final DashboardService dashboardService;

	public AppRestControllerSFDA(WarehouseServiceImpl warehouseService, DashboardService dashboardService) {
		this.warehouseService = warehouseService;
		this.dashboardService = dashboardService;
	}

	@GetMapping(path = "/loginApp")
	public 	ResponseEntity<?> loginApp(@RequestHeader(value = "Authorization", defaultValue = "")String authtorization ){

		return appService.loginApp(authtorization);
	}
	
	@GetMapping(path = "/logoutApp")
	public ResponseEntity<?> logoutApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN ){
		
		return appService.logoutApp(TOKEN);
	}
	
	@GetMapping(path = "/getAllDevicesMapApp")
	public ResponseEntity<?> getAllDevicesLastInfoMapApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                       @RequestParam (value = "userId", defaultValue = "0") Long userId
			                                       ){		
		return appService.getAllDeviceLiveDataMapApp(TOKEN,userId);
	}

	@GetMapping(path = "/getListWarehousesInventoriesApp")
	public ResponseEntity<?> getListWarehousesInventoriesApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
															 @RequestParam(value = "offset", defaultValue = "0") int offset,
															 @RequestParam (value = "userId", defaultValue = "0") Long  userId){
		return dashboardService.getListWarehousesInventories(TOKEN, userId, offset);
	}
	
	@RequestMapping(value = "/vehicleInfoApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> vehicleInfoApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                           @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
			                                           @RequestParam(value = "userId",defaultValue = "0")Long userId){
				
    	return  appService.vehicleInfoApp(TOKEN,deviceId,userId);

	}
	
	@GetMapping("/getDevicesListApp")
	public ResponseEntity<?> getDevicesListApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                             @RequestParam (value = "userId",defaultValue = "0") Long userId,
										 @RequestParam(value = "offset", defaultValue = "0") int offset,
							             @RequestParam(value = "search", defaultValue = "") String search) {
 
		return appService.getDevicesListApp(TOKEN,userId,offset,search);
		
	}
	
	@PostMapping(path ="/createDeviceApp")
	public ResponseEntity<?> createDeviceApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                              @RequestParam (value = "userId",defaultValue = "0") Long userId,
			                              @RequestBody(required = false) DeviceSFDA device) {

	    return deviceServiceImpl.createDevice(TOKEN,device,userId);					
	}
	
	@PostMapping(path ="/editDeviceApp")
	public ResponseEntity<?> editDeviceApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                            @RequestParam (value = "userId",defaultValue = "0") Long userId,
			                            @RequestBody(required = false) DeviceSFDA device) {
		

		return deviceServiceImpl.editDevice(TOKEN,device,userId);
	}
	
	@GetMapping(path ="/deleteDeviceApp")
	public ResponseEntity<?> deleteDeviceApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                              @RequestParam  (value = "userId",defaultValue = "0") Long userId,
			                              @RequestParam (value = "deviceId",defaultValue = "0") Long deviceId ) {
			
			 return appService.deleteDeviceApp(TOKEN,userId,deviceId);			
	}
	
	@GetMapping(path = "/assignDeviceToDriverApp")
	public ResponseEntity<?> assignDeviceToDriverApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
												  @RequestParam (value = "driverId", defaultValue = "0") String driverId,
												  @RequestParam(value = "deviceId" ,defaultValue = "0") Long deviceId,
												  @RequestParam(value = "userId" , defaultValue = "0")Long userId ) {
		try
	    {
			Long.parseLong(driverId);
	    }
	    catch(NumberFormatException ex)
	    {
	    	GetObjectResponse getObjectResponse;

	    	getObjectResponse = new GetObjectResponse(HttpStatus.BAD_REQUEST.value(), " Input is not Number or Can't Assign more than 2  Drivers to the Device",null);
			return ResponseEntity.badRequest().body(getObjectResponse);
	    }

		return appService.assignDeviceToDriverApp(TOKEN,deviceId,Long.parseLong(driverId),userId);	
		
	}
	
	@GetMapping(path = "/assignGeofencesToDeviceApp")
	public ResponseEntity<?> assignGeofencesToDeviceApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                         @RequestParam(value = "deviceId" ,defaultValue = "0") Long deviceId,
			                                         @RequestParam(value = "userId" , defaultValue = "0")Long userId,
			                                         @RequestParam (value = "geoIds", defaultValue = "")Long [] geoIds) {
	
		return appService.assignGeofencesToDeviceApp(TOKEN,deviceId,geoIds,userId);	
				
	}
	
	@GetMapping(path ="/getDevicebyIdApp")
	public ResponseEntity<?> getDevicebyIdApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                               @RequestParam (value = "deviceId",defaultValue = "0") Long deviceId,
			                               @RequestParam(value = "userId",defaultValue = "0") Long userId) {

		return  appService.findDeviceByIdApp(TOKEN,deviceId,userId);
	}
	
	@GetMapping(value = "/getDeviceDriverApp")
	public @ResponseBody ResponseEntity<?> getDeviceDriverApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                               @RequestParam (value = "deviceId",defaultValue = "0") Long deviceId) {
		return appService.getDeviceDriverApp(TOKEN,deviceId);
	}
	
	@GetMapping(value = "/getDeviceGeofencesApp")
	public @ResponseBody ResponseEntity<?> getDeviceGeofencesApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                                  @RequestParam (value = "deviceId",defaultValue = "0") Long deviceId) {
		
			
		return appService.getDeviceGeofencesApp(TOKEN,deviceId);

	}
	
	@RequestMapping(value = "/getDriversListApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getDrivers(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
											          @RequestParam (value = "exportData", defaultValue = "") String exportData,                                                                     
													  @RequestParam (value = "userId", defaultValue = "0") Long id,
													  @RequestParam (value = "offset", defaultValue = "0") int offset,
													  @RequestParam (value = "search", defaultValue = "") String search,
													  @RequestParam (value = "active", defaultValue = "1") int active) {
		
    	return  appServiceSFDA.getAllDriversAppSFDA(TOKEN,id,offset,search,active,exportData);

	}
	
	@RequestMapping(value = "/activeDriverApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> activeDriverApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                            @RequestParam (value = "driverId", defaultValue = "0") Long driverId,
			                                            @RequestParam(value = "userId",defaultValue = "0") Long userId) {
		
		
		
		return appServiceSFDA.activeDriverApp(TOKEN,driverId,userId);

	}
	
	@RequestMapping(value = "/getDriverByIdApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getDriverByIdApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                             @RequestParam (value = "driverId", defaultValue = "0") Long driverId,
			                                             @RequestParam(value = "userId",defaultValue = "0")Long userId) {
		
		
		return appService.getDriverByIdApp(TOKEN,driverId,userId);

	}
	
	@RequestMapping(value = "/deleteDriverApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> deleteDriverApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                            @RequestParam (value = "driverId", defaultValue = "0") Long driverId,
			                                            @RequestParam(value = "userId",defaultValue = "0") Long userId) {
		
		
		
		return appService.deleteDriverApp(TOKEN,driverId,userId);

	}
	
	@RequestMapping(value = "/addDriverApp", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> addDriverApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                         @RequestBody(required = false) Driver driver,
			                                         @RequestParam (value = "userId", defaultValue = "0") Long id) {
		
		
		return appService.addDriverApp(TOKEN,driver,id);

		
	}	
	@RequestMapping(value = "/editDriverApp", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> editDriverApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                          @RequestBody(required = false) Driver driver,
			                                          @RequestParam (value = "userId", defaultValue = "0") Long id) {
		
		
		
		return appService.editDriverApp(TOKEN,driver,id);


	}
	@GetMapping(path = "/getUnassignedDriversApp")
	public ResponseEntity<?> getUnassignedDriversApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                         @RequestParam (value = "userId",defaultValue = "0") Long userId){
		
		return appService.getUnassignedDriversApp(TOKEN,userId);
	}
	
	@RequestMapping(value = "/getDriverSelectApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getDriverSelectApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                                  @RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
	
    	return  appService.getDriverSelectApp(TOKEN,userId);

		
	}
	
	
	@RequestMapping(value = "/getStopsReportApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getStopsReportApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													@RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
													@RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													@RequestParam (value = "type", defaultValue = "allEvents") String type,
													@RequestParam (value = "from", defaultValue = "0") String from,
													@RequestParam (value = "to", defaultValue = "0") String to,
													@RequestParam (value = "page", defaultValue = "1") int page,
													@RequestParam (value = "start", defaultValue = "0") int start,
													@RequestParam (value = "limit", defaultValue = "25") int limit,
													@RequestParam (value = "userId",defaultValue = "0")Long userId) {
		

		
    	return appService.getStopsReportApp(TOKEN,deviceId,groupId, type, from, to, page, start, limit,userId);
		
		
	}
	
	@RequestMapping(value = "/getTripsReportApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getTripsReportApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													@RequestParam (value = "deviceId", defaultValue = "0")  Long [] deviceId,
													@RequestParam (value = "groupId", defaultValue = "0")  Long [] groupId,
													@RequestParam (value = "type", defaultValue = "allEvents") String type,
													@RequestParam (value = "from", defaultValue = "0") String from,
													@RequestParam (value = "to", defaultValue = "0") String to,
													@RequestParam (value = "page", defaultValue = "1") int page,
													@RequestParam (value = "start", defaultValue = "0") int start,
													@RequestParam (value = "limit", defaultValue = "25") int limit,
													@RequestParam (value = "userId",defaultValue = "0")Long userId) {
												

    	return  appService.getTripsReportApp(TOKEN,deviceId,groupId, type, from, to, page, start, limit,userId);
		 
		
	}
	
	@RequestMapping(value = "/getSummaryReportApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getSummaryReportApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													@RequestParam (value = "deviceId", defaultValue = "0")  Long [] deviceId,
													@RequestParam (value = "groupId", defaultValue = "0")  Long [] groupId,
													@RequestParam (value = "type", defaultValue = "allEvents") String type,
													@RequestParam (value = "from", defaultValue = "0") String from,
													@RequestParam (value = "to", defaultValue = "0") String to,
													@RequestParam (value = "page", defaultValue = "1") int page,
													@RequestParam (value = "start", defaultValue = "0") int start,
													@RequestParam (value = "limit", defaultValue = "25") int limit,
													@RequestParam (value = "userId",defaultValue = "0")Long userId) {
												

    	return  appService.getSummaryReportApp(TOKEN,deviceId,groupId, type, from, to, page, start, limit,userId);
		 
		
	}
	
	@RequestMapping(value = "/getEventsReportApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getEventsApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													 @RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
													 @RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													 @RequestParam (value = "offset", defaultValue = "0") int offset,
													 @RequestParam (value = "from", defaultValue = "0") String start,
													 @RequestParam (value = "exportData", defaultValue = "") String exportData,
													 @RequestParam (value = "to", defaultValue = "0") String end,
													 @RequestParam (value = "type", defaultValue = "") String type,
													 @RequestParam (value = "search", defaultValue = "") String search,
													 @RequestParam (value = "userId",defaultValue = "0")Long userId) {	
    	return  appService.getEventsReportApp(TOKEN, deviceId,groupId, offset, start, end, type, search, userId,exportData);

	}
	
	@RequestMapping(value = "/getDeviceWorkingHoursApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getDeviceWorkingHoursApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													 @RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
													 @RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													 @RequestParam (value = "exportData", defaultValue = "") String exportData,
													 @RequestParam (value = "offset", defaultValue = "0") int offset,
													 @RequestParam (value = "from", defaultValue = "0") String start,
													 @RequestParam (value = "to", defaultValue = "0") String end,
													 @RequestParam (value = "search", defaultValue = "") String search,
													 @RequestParam (value = "userId",defaultValue = "0")Long userId) {
		
		
		
    	return  appService.getDeviceWorkingHoursApp(TOKEN,deviceId,groupId, offset, start, end,search,userId,exportData);

	}
	@RequestMapping(value = "/getCustomReportApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getCustomReportApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													 @RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
													 @RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													 @RequestParam (value = "offset", defaultValue = "0") int offset,
													 @RequestParam (value = "from", defaultValue = "0") String start,
													 @RequestParam (value = "exportData", defaultValue = "") String exportData,
													 @RequestParam (value = "to", defaultValue = "0") String end,
													 @RequestParam (value = "search", defaultValue = "") String search,
													 @RequestParam (value = "userId",defaultValue = "0")Long userId,
													 @RequestParam (value = "custom", defaultValue = "") String custom,
													 @RequestParam (value = "value", defaultValue = "") String value) {
		
		
		
    	return  appService.getCustomReportApp(TOKEN,deviceId,groupId, offset, start, end,search,userId,custom,value,exportData);

	}
	
	@RequestMapping(value = "/getDriverWorkingHoursApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getDriverWorkingHoursApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													 @RequestParam (value = "driverId", defaultValue = "0") Long [] driverId,
													 @RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													 @RequestParam (value = "offset", defaultValue = "0") int offset,
													 @RequestParam (value = "from", defaultValue = "0") String start,
													 @RequestParam (value = "to", defaultValue = "0") String end,
													 @RequestParam (value = "exportData", defaultValue = "") String exportData,
													 @RequestParam (value = "search", defaultValue = "") String search,
													 @RequestParam (value = "userId",defaultValue = "0")Long userId) {
		
		
		
    	return  appService.getDriverWorkingHoursApp(TOKEN,driverId,groupId, offset,start, end,search,userId,exportData);

	}
	@RequestMapping(value = "/getDriveMoreThanReportApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getDriveMoreThanReportApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													@RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
													@RequestParam (value = "driverId", defaultValue = "0") Long [] driverId,
													@RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													@RequestParam (value = "type", defaultValue = "allEvents") String type,
													@RequestParam (value = "from", defaultValue = "0") String from,
													@RequestParam (value = "to", defaultValue = "0") String to,
													@RequestParam (value = "page", defaultValue = "1") int page,
													@RequestParam (value = "start", defaultValue = "0") int start,
													@RequestParam (value = "limit", defaultValue = "25") int limit,
													@RequestParam (value = "userId",defaultValue = "0")Long userId) {
												

    	return  appService.getDriveMoreThanReportApp(TOKEN,deviceId,driverId,groupId, type, from, to, page, start, limit,userId);
		 
		
	}
	@RequestMapping(value = "/getEventsReportByTypeApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getEventsTypeApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													@RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
													@RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													@RequestParam (value = "type", defaultValue = "allEvents") String type,
													@RequestParam (value = "from", defaultValue = "0") String from,
													@RequestParam (value = "to", defaultValue = "0") String to,
													@RequestParam (value = "page", defaultValue = "1") int page,
													@RequestParam (value = "start", defaultValue = "0") int start,
													@RequestParam (value = "limit", defaultValue = "25") int limit,
													@RequestParam (value = "userId",defaultValue = "0")Long userId) {
												

    	return  appService.getEventsReportByTypeApp(TOKEN,deviceId,groupId, type, from, to, page, start, limit,userId);
		 
		
	}
	
	@RequestMapping(value = "/getSensorsReportApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getSensorsApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													 @RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
													 @RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													 @RequestParam (value = "offset", defaultValue = "0") int offset,
													 @RequestParam (value = "from", defaultValue = "0") String start,
													 @RequestParam (value = "to", defaultValue = "0") String end,
													 @RequestParam (value = "exportData", defaultValue = "") String exportData,
													 @RequestParam (value = "search", defaultValue = "") String search,
													 @RequestParam (value = "userId",defaultValue = "0")Long userId) {
		
		
		
    	return  appService.getSensorsReportApp(TOKEN,deviceId,groupId, offset, start, end,search,userId,exportData);

	}
	
	@RequestMapping(value = "/getNumTripsReportApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getNumTripsReportApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													@RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
													@RequestParam (value = "driverId", defaultValue = "0") Long [] driverId,
													@RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													@RequestParam (value = "type", defaultValue = "allEvents") String type,
													@RequestParam (value = "from", defaultValue = "0") String from,
													@RequestParam (value = "to", defaultValue = "0") String to,
													@RequestParam (value = "page", defaultValue = "1") int page,
													@RequestParam (value = "start", defaultValue = "0") int start,
													@RequestParam (value = "limit", defaultValue = "25") int limit,
													@RequestParam (value = "userId",defaultValue = "0")Long userId) {
												

    	return  appService.getNumTripsReportApp(TOKEN,deviceId,driverId,groupId, type, from, to, page, start, limit,userId);
		 
		
	}
	
	@RequestMapping(value = "/getNumStopsReportApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getNumStopsReportApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													@RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
													@RequestParam (value = "driverId", defaultValue = "0") Long [] driverId,
													@RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													@RequestParam (value = "type", defaultValue = "allEvents") String type,
													@RequestParam (value = "from", defaultValue = "0") String from,
													@RequestParam (value = "to", defaultValue = "0") String to,
													@RequestParam (value = "page", defaultValue = "1") int page,
													@RequestParam (value = "start", defaultValue = "0") int start,
													@RequestParam (value = "limit", defaultValue = "25") int limit,
													@RequestParam (value = "userId",defaultValue = "0")Long userId) {
		

		
    	return appService.getNumStopsReportApp(TOKEN,deviceId,driverId,groupId, type, from, to, page, start, limit,userId);
		
		
	}
	
	@RequestMapping(value = "/geTotalTripsReportApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> geTotalTripsReportApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													@RequestParam (value = "deviceId", defaultValue = "0")  Long [] deviceId,
													@RequestParam (value = "driverId", defaultValue = "0")  Long [] driverId,
													@RequestParam (value = "groupId", defaultValue = "0")  Long [] groupId,
													@RequestParam (value = "type", defaultValue = "allEvents") String type,
													@RequestParam (value = "from", defaultValue = "0") String from,
													@RequestParam (value = "to", defaultValue = "0") String to,
													@RequestParam (value = "page", defaultValue = "1") int page,
													@RequestParam (value = "start", defaultValue = "0") int start,
													@RequestParam (value = "limit", defaultValue = "25") int limit,
													@RequestParam (value = "userId",defaultValue = "0")Long userId) {
												

    	return  appService.geTotalTripsReportApp(TOKEN,deviceId,driverId,groupId, type, from, to, page, start, limit,userId);
		 
		
	}
	
	@RequestMapping(value = "/getTotalStopsReportApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getTotalStopsReportApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													@RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
													@RequestParam (value = "driverId", defaultValue = "0") Long [] driverId,
													@RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													@RequestParam (value = "type", defaultValue = "allEvents") String type,
													@RequestParam (value = "from", defaultValue = "0") String from,
													@RequestParam (value = "to", defaultValue = "0") String to,
													@RequestParam (value = "page", defaultValue = "1") int page,
													@RequestParam (value = "start", defaultValue = "0") int start,
													@RequestParam (value = "limit", defaultValue = "25") int limit,
													@RequestParam (value = "userId",defaultValue = "0")Long userId) {
		

		
    	return appService.getTotalStopsReportApp(TOKEN,deviceId,driverId,groupId, type, from, to, page, start, limit,userId);
		
		
	}
	
	@RequestMapping(value = "/viewTripApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> viewTripApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													 @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
													 @RequestParam (value = "startTime", defaultValue = "0") String startTime,
													 @RequestParam (value = "endTime", defaultValue = "0") String endTime) {	
    	return  appService.viewTripApp(TOKEN, deviceId,startTime, endTime);

	}
	
	
	@RequestMapping(value = "/getGeoListApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getGeoListApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
												        @RequestParam (value = "exportData", defaultValue = "") String exportData,                                                                                                                
														@RequestParam (value = "userId", defaultValue = "0") Long id,
														@RequestParam (value = "offset", defaultValue = "0") int offset,
														@RequestParam (value = "search", defaultValue = "") String search,
														@RequestParam (value = "active", defaultValue = "1") int active) {
		
    	return  appServiceSFDA.getAllGeofencesAppSFDA(TOKEN,id,offset,search,active,exportData);

	}
	
	
	@RequestMapping(value = "/activeGeofenceApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> activeGeofenceApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                            @RequestParam (value = "geofenceId", defaultValue = "0") Long geofenceId,
			                                            @RequestParam (value = "userId", defaultValue = "0") Long userId) {

    	return  appServiceSFDA.activeGeofenceApp(TOKEN,geofenceId,userId);


	}
	
	
	@RequestMapping(value = "/getGeofenceByIdApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getGeofenceByIdApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                               @RequestParam (value = "geofenceId", defaultValue = "0") Long geofenceId,
			                                               @RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
    	return  appService.getGeofenceByIdApp(TOKEN,geofenceId,userId);

	}
	
	@RequestMapping(value = "/deleteGeofenceApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> deleteGeofenceApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                            @RequestParam (value = "geofenceId", defaultValue = "0") Long geofenceId,
			                                            @RequestParam (value = "userId", defaultValue = "0") Long userId) {

    	return  appService.deleteGeofenceApp(TOKEN,geofenceId,userId);


	}
	
	@RequestMapping(value = "/addGeofenceApp", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> addGeofenceApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                         @RequestBody(required = false) Geofence geofence,
			                                         @RequestParam (value = "userId", defaultValue = "0") Long id) {
		

		return appService.addGeofenceApp(TOKEN,geofence,id);

	}
	
	@RequestMapping(value = "/editGeofenceApp", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> editGeofenceApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                            @RequestBody(required = false) Geofence geofence,
			                                            @RequestParam (value = "userId", defaultValue = "0") Long id) {
		
		
		return appService.editGeofenceApp(TOKEN,geofence,id);

	}	
	
	@RequestMapping(value = "/getGeofenceSelectApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getGeofenceSelectApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                                    @RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
	
    	return  appService.getGeofenceSelectApp(TOKEN,userId);

		
	}
	
	@RequestMapping(value = "/getDeviceSelectApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getDeviceSelectApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                                  @RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
	
    	return  appService.getDeviceSelectApp(TOKEN,userId);

		
	}
	
	@RequestMapping(value = "/updatePhotoApp", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> updatePhotoApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                           @RequestBody Map<String, String> data ,
			                                           @RequestParam (value = "userId", defaultValue = "0") Long userId) {

    	return  appService.updateProfilePhotoApp(TOKEN,data,userId);

	}
	
	@RequestMapping(value = "/getProfileInfoApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getProfileInfoApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                              @RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
	
    	return  appService.getUserInfoApp(TOKEN,userId);

	}
	
	@RequestMapping(value = "/changePassowrdApp", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> changePassowrdApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                              @RequestBody Map<String, String> data ,
			                                              @RequestParam (value = "check", defaultValue = "") String check,
			                                              @RequestParam (value = "userId", defaultValue = "0") Long userId) {

		
    	return appService.updateProfilePasswordApp(TOKEN,data,check,userId);

	}
	
	@GetMapping(path ="/getStatusApp")
	public ResponseEntity<?> getStatusApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                           @RequestParam (value = "userId", defaultValue = "0") Long userId){
		
		return appService.getStatusApp(TOKEN,userId);
	}
	
	@RequestMapping(value = "/getMergeHoursIgnitionApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getMergeHoursIgnitionApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
															@RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
    	return  appService.getMergeHoursIgnitionApp(TOKEN,userId);

	}
	
	@GetMapping(path ="/getDistanceFuelEngineApp")
	public ResponseEntity<?> getDistanceFuelEngineApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                             @RequestParam (value = "userId", defaultValue = "0") Long userId){
		
		return appService.getDistanceFuelEngineApp(TOKEN,userId);
	}
	
	@RequestMapping(value = "/getNotificationsChartApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getNotificationsChartApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
															@RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
    	return  appService.getNotificationsChartApp(TOKEN,userId);

	}
	
	@RequestMapping(value = "/getNumberDriverWorkingHoursApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getNumberDriverWorkingHoursApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													 @RequestParam (value = "driverId", defaultValue = "0") Long [] driverId,
													 @RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													 @RequestParam (value = "offset", defaultValue = "0") int offset,
													 @RequestParam (value = "from", defaultValue = "0") String start,
													 @RequestParam (value = "to", defaultValue = "0") String end,
													 @RequestParam (value = "exportData", defaultValue = "") String exportData,
													 @RequestParam (value = "search", defaultValue = "") String search,
													 @RequestParam (value = "userId",defaultValue = "0")Long userId) {
		
		
		
    	return  appService.getNumberDriverWorkingHoursApp(TOKEN,driverId,groupId, offset,start, end,search,userId,exportData);

	}
	
	@GetMapping(path ="/activeDeviceApp")
	public ResponseEntity<?> activeDeviceSFDAApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                              @RequestParam  (value = "userId",defaultValue = "0") Long userId,
			                              @RequestParam (value = "deviceId",defaultValue = "0") Long deviceId ) {
			
		return appServiceSFDA.activeDeviceAppSFDA(TOKEN,userId,deviceId);			
	}
	
	@GetMapping("/getInventoriesListApp")
	public ResponseEntity<?> getInventoriesListApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
		                                       @RequestParam (value = "exportData", defaultValue = "") String exportData,                                                                     
					                           @RequestParam (value = "userId",defaultValue = "0") Long userId,
											   @RequestParam(value = "offset", defaultValue = "0") int offset,
									           @RequestParam(value = "search", defaultValue = "") String search,
									           @RequestParam(value = "active", defaultValue = "1") int active) {
	 
		return appServiceSFDA.getInventoriesListApp(TOKEN,userId,offset,search,active,exportData);
		
	}
	
	@PostMapping(path ="/createInventoriesApp")
	@CacheEvict(cacheNames = "inventory" , value = "inventory" ,allEntries = true)
	public ResponseEntity<?> createInventoriesApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                              @RequestParam (value = "userId",defaultValue = "0") Long userId,
			                              @RequestBody(required = false) Inventory inventory) {
		
			 return appServiceSFDA.createInventoriesApp(TOKEN,inventory,userId);				
	}

	@RequestMapping(value = "/editInventoriesApp", method = RequestMethod.POST)
	@CacheEvict(cacheNames = "inventory" , value = "inventory" ,allEntries = true)
	public @ResponseBody ResponseEntity<?> editInventoriesApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                          @RequestBody(required = false) Inventory inventory,
			                                          @RequestParam (value = "userId", defaultValue = "0") Long id) {
		
		
		return appServiceSFDA.editInventoriesApp(TOKEN,inventory,id);

	}
	
	@RequestMapping(value = "/getInventoryByIdApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getInventoryByIdApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                               @RequestParam (value = "inventoryId", defaultValue = "0") Long inventoryId,
			                                               @RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
    	return  appServiceSFDA.getInventoryByIdApp(TOKEN,inventoryId,userId);

	}
	
	@RequestMapping(value = "/deleteInventoryApp", method = RequestMethod.GET)
	@CacheEvict(cacheNames = "inventory" , value = "inventory" ,allEntries = true)
	public @ResponseBody ResponseEntity<?> deleteInventoryApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                            @RequestParam (value = "inventoryId", defaultValue = "0") Long inventoryId,
			                                            @RequestParam (value = "userId", defaultValue = "0") Long userId) {

    	return  appServiceSFDA.deleteInventoryApp(TOKEN,inventoryId,userId);

	}
	
	@RequestMapping(value = "/activeInventoryApp", method = RequestMethod.GET)
	@CacheEvict(cacheNames = "inventory" , value = "inventory" ,allEntries = true)
	public @ResponseBody ResponseEntity<?> activeInventoryApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                            @RequestParam (value = "inventoryId", defaultValue = "0") Long inventoryId,
			                                            @RequestParam (value = "userId", defaultValue = "0") Long userId) {

    	return  appServiceSFDA.activeInventoryApp(TOKEN,inventoryId,userId);

	}
	
	@GetMapping("/assignWarehouseToInventoryApp")
	public ResponseEntity<?>assignWarehouseToInventoryApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
											@RequestParam (value = "userId",defaultValue = "0") Long userId,
											@RequestParam (value = "inventoryId",defaultValue = "0") Long inventoryId,
											@RequestParam (value = "warehouseId",defaultValue = "0") Long warehouseId){
	
		if(warehouseId == 0) {
			return appServiceSFDA.removeWarehouseFromInventoryApp(TOKEN,userId,inventoryId,warehouseId);
		}else {
			return appServiceSFDA.assignWarehouseToInventoryApp(TOKEN,userId,inventoryId,warehouseId);
		}
		
	}
	
	@GetMapping("/getSelectedAndListWarehouseApp")
	public ResponseEntity<?>getSelectedAndListWarehouseApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
											@RequestParam (value = "userId",defaultValue = "0") Long userId,
											@RequestParam (value = "inventoryId",defaultValue = "0") Long inventoryId,
											@RequestParam (value = "loggedUserId",defaultValue = "0") Long loggedUserId){
	
		return appServiceSFDA.getSelectedAndListWarehouseApp(TOKEN,loggedUserId,userId,inventoryId);
		
		
	}
	
	@GetMapping("/getSelectListInventoriesApp")
	public ResponseEntity<?> getSelectListInventoriesApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
				                           @RequestParam (value = "userId",defaultValue = "0") Long userId) {
	 
		return appServiceSFDA.getSelectListInventoriesApp(TOKEN,userId);
		
	}
	
	@GetMapping("/getWarehousesListApp")
	public ResponseEntity<?> getWarehousesListApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
								               @RequestParam (value = "exportData", defaultValue = "") String exportData,                                                                     
				                               @RequestParam (value = "userId",defaultValue = "0") Long userId,
											   @RequestParam(value = "offset", defaultValue = "0") int offset,
									           @RequestParam(value = "search", defaultValue = "") String search,
									           @RequestParam(value = "active", defaultValue = "1") int active) {
	 
		return appServiceSFDA.getWarehousesListApp(TOKEN,userId,offset,search,active,exportData);
		
	}
	
	@PostMapping(path ="/createWarehousesApp")
	public ResponseEntity<?> createWarehousesApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                              @RequestParam (value = "userId",defaultValue = "0") Long userId,
			                              @RequestBody(required = false) Warehouse warehouse) {
		
		return appServiceSFDA.createWarehousesApp(TOKEN,warehouse,userId);				
	}
	@RequestMapping(value = "/editWarehousesApp", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> editWarehousesApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                          @RequestBody(required = false) Warehouse warehouse,
			                                          @RequestParam (value = "userId", defaultValue = "0") Long id) {
		
		
		return appServiceSFDA.editWarehousesApp(TOKEN,warehouse,id);

	}
	
	@RequestMapping(value = "/getWarehouseByIdApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getWarehouseByIdApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                               @RequestParam (value = "warehouseId", defaultValue = "0") Long warehouseId,
			                                               @RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
    	return  appServiceSFDA.getWarehouseByIdApp(TOKEN,warehouseId,userId);

	}
	
	@RequestMapping(value = "/deleteWarehouseApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> deleteWarehouseApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                            @RequestParam (value = "warehouseId", defaultValue = "0") Long warehouseId,
			                                            @RequestParam (value = "userId", defaultValue = "0") Long userId) {

    	return  appServiceSFDA.deleteWarehouseApp(TOKEN,warehouseId,userId);


	}
	
	@RequestMapping(value = "/activeWarehouseApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> activeWarehouseApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                            @RequestParam (value = "warehouseId", defaultValue = "0") Long warehouseId,
			                                            @RequestParam (value = "userId", defaultValue = "0") Long userId) {

    	return  appServiceSFDA.activeWarehouseApp(TOKEN,warehouseId,userId);


	}
	
	
	@RequestMapping(value = "/getListSelectWarehouseApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getListSelectWarehouseApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                            @RequestParam (value = "userId", defaultValue = "0") Long  userId) {

    	return appServiceSFDA.getListSelectWarehouseApp(TOKEN,userId);

	}
	
	@RequestMapping(value = "/getListWarehouseMapApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getListWarehouseMapApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                                      @RequestParam (value = "userId", defaultValue = "0") Long  userId) {

    	return appServiceSFDA.getListWarehouseMapApp(TOKEN,userId);

	}
	
	@RequestMapping(value = "/getInventoryListOfWarehouseMapApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getInventoryListOfWarehouseMapApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                                   @RequestParam (value = "userId", defaultValue = "0") Long  userId,
			                                                   @RequestParam (value = "warehouseId", defaultValue = "0") Long  warehouseId) {

    	return appServiceSFDA.getInventoryListOfWarehouseMapApp(TOKEN,userId,warehouseId);

	}
	
	@RequestMapping(value = "/getInventoriesNotificationsApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getInventoriesNotificationsApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
															@RequestParam (value = "userId", defaultValue = "0") Long userId,
															@RequestParam (value = "offset", defaultValue = "0") int offset,
															@RequestParam (value = "search", defaultValue = "") String search) {
		
    	return  appServiceSFDA.getInventoriesNotificationsApp(TOKEN,userId, offset,search);

	}
	
	@GetMapping(path = "/getAllInventoriesLastInfoApp")
	public ResponseEntity<?> getAllInventoriesLastInfoApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                       @RequestParam (value = "userId", defaultValue = "0") Long userId,
												   @RequestParam (value = "offset", defaultValue = "0")int offset,
												   @RequestParam (value = "search", defaultValue = "") String search ){
		return appServiceSFDA.getAllInventoriesLastInfoApp(TOKEN,userId, offset, search);

	}
	
	@GetMapping(path ="/getInventoryStatusApp")
	public ResponseEntity<?> getInventoryStatusApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                             @RequestParam (value = "userId", defaultValue = "0") Long userId){
		
		return appServiceSFDA.getInventoryStatusApp(TOKEN,userId);
	}
	

	@RequestMapping(value = "/getInventoriesReportApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getInventoriesReportApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
																@RequestParam (value = "inventoryId", defaultValue = "0") Long [] inventoryId,
																@RequestParam (value = "exportData", defaultValue = "") String exportData,
																@RequestParam (value = "offset", defaultValue = "0") int offset,
																@RequestParam (value = "start", defaultValue = "0") String start,
																@RequestParam (value = "end", defaultValue = "0") String end,
																@RequestParam (value = "search", defaultValue = "") String search,
																@RequestParam (value = "userId",defaultValue = "0")Long userId) {	
    	return  appServiceSFDA.getInventoriesReportApp(TOKEN, inventoryId, offset, start, end, search, userId,exportData);

	}
	
	@RequestMapping(value = "/getWarehousesReportApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getWarehousesReportApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
															   @RequestParam (value = "warehouseId", defaultValue = "0") Long [] warehouseId,
																@RequestParam (value = "exportData", defaultValue = "") String exportData,
																 @RequestParam (value = "offset", defaultValue = "0") int offset,
																 @RequestParam (value = "start", defaultValue = "0") String start,
																 @RequestParam (value = "end", defaultValue = "0") String end,
																 @RequestParam (value = "search", defaultValue = "") String search,
																 @RequestParam (value = "userId",defaultValue = "0")Long userId) {	
    	return  appServiceSFDA.getWarehousesReportApp(TOKEN, warehouseId, offset, start, end, search, userId,exportData);

	}
	
	@RequestMapping(value = "/getNotificationReportApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getNotificationReportApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
																 @RequestParam (value = "inventoryId", defaultValue = "0") Long [] inventoryId,													 
																 @RequestParam (value = "warehouseId", defaultValue = "0") Long [] warehouseId,
																 @RequestParam (value = "exportData", defaultValue = "") String exportData,
																 @RequestParam (value = "offset", defaultValue = "0") int offset,
																 @RequestParam (value = "start", defaultValue = "0") String start,
																 @RequestParam (value = "end", defaultValue = "0") String end,
																 @RequestParam (value = "search", defaultValue = "") String search,
																 @RequestParam (value = "userId",defaultValue = "0")Long userId) {	
    	return  appServiceSFDA.getNotificationReportApp(TOKEN,inventoryId, warehouseId, offset, start, end, search, userId,exportData);

	}
	
	@RequestMapping(value = "/getVehicleTempHumApp", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getVehicleTempHumApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													  @RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
													  @RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													  @RequestParam (value = "exportData", defaultValue = "") String exportData,
													  @RequestParam (value = "offset", defaultValue = "0") int offset,
													  @RequestParam (value = "start", defaultValue = "0") String start,
													  @RequestParam (value = "end", defaultValue = "0") String end,
													  @RequestParam (value = "search", defaultValue = "") String search,
													  @RequestParam (value = "userId",defaultValue = "0")Long userId) {
	
    	return  appServiceSFDA.getVehicleTempHumApp(TOKEN,deviceId,groupId, offset, start, end,search,userId,exportData);

	}
	@RequestMapping(value = "/warehouseInventories/list", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getVehicleTempHumApp(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
																@RequestParam (value = "userId", defaultValue = "0") Long  userId,
																@RequestParam (value = "warehouseId", defaultValue = "0") Long  warehouseId){
		return warehouseService.getListWarehouseInventoriesListByWahrehouseId(TOKEN,userId,warehouseId);
	}

	@GetMapping(value = "/wareHouseInvLastData")
	public @ResponseBody ResponseEntity<ApiResponse<List<WareHouseInvLastDataResponse>>> getWareHouseInvLastData(@RequestHeader(value = "TOKEN")String TOKEN,
																								 @RequestParam (value = "userId") Long userId,
																								 @RequestParam (value = "size",defaultValue = "0") int whSize,
																								 @RequestParam(value = "offset", defaultValue = "0") int offset,
																								 @RequestParam(value = "search", defaultValue = "") String search){
		try{
			return ResponseEntity.ok(appServiceSFDA
					.getWareHouseInvLastData(TOKEN,userId,whSize,offset,search));

		}catch (Exception | Error e){
			ApiResponseBuilder<List<WareHouseInvLastDataResponse>> builder = new ApiResponseBuilder<>();

			builder.setMessage(e.getLocalizedMessage());
			builder.setStatusCode(401);
			builder.setEntity(null);
			builder.setSize(0);
			builder.setSuccess(false);

			return ResponseEntity.ok(builder.build());
		}
	}
	
}
