package com.example.food_drugs.rest;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import com.example.food_drugs.dto.responses.GraphDataWrapper;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.examplequerydslspringdatajpamaven.service.DeviceServiceImpl;
import com.example.food_drugs.entity.DeviceSFDA;
import com.example.food_drugs.service.impl.DeviceServiceImplSFDA;

/**
 * Services of Device SFDA Component
 * @author fuinco
 *
 */


@CrossOrigin
@Component
@RequestMapping(path = "/devicesSFDA")
public class DeviceRestControllerSFDA {
	
	@Autowired
	private DeviceServiceImpl deviceServiceImpl;
	
	@Autowired
	private DeviceServiceImplSFDA deviceServiceImplSFDA;
	
	@GetMapping("/getUserDevices")
	public ResponseEntity<?> devicesListSFDA(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
		                                     @RequestParam (value = "exportData", defaultValue = "") String exportData,                           
				                             @RequestParam (value = "userId",defaultValue = "0") Long userId,
											 @RequestParam(value = "offset", defaultValue = "0") int offset,
								             @RequestParam(value = "search", defaultValue = "") String search,
								             @RequestParam(value = "active",defaultValue = "1") int active) {
 
		return deviceServiceImplSFDA.getAllUserDevicesSFDA(TOKEN,userId,offset,search,active,exportData);
		
	}
	
	@PostMapping(path ="/createDevice")
	public ResponseEntity<?> createDeviceSFDA(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
				                              @RequestParam (value = "userId",defaultValue = "0") Long userId,
				                              @RequestBody(required = false) DeviceSFDA device) {


	    return deviceServiceImpl.createDevice(TOKEN,device,userId);				
	}

	@PostMapping(path ="/editDevice")
	public ResponseEntity<?> editDeviceSFDA(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                            @RequestParam (value = "userId",defaultValue = "0") Long userId,
			                            @RequestBody(required = false) DeviceSFDA device) {
		
		return deviceServiceImpl.editDevice(TOKEN,device,userId);	
	}
	
	@GetMapping(path ="/deactiveDevice")
	public ResponseEntity<?> deactiveDeviceSFDA(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                              @RequestParam  (value = "userId",defaultValue = "0") Long userId,
			                              @RequestParam (value = "deviceId",defaultValue = "0") Long deviceId ) {
			
		return deviceServiceImpl.deleteDevice(TOKEN,userId,deviceId);			
	}
	
	@GetMapping(path ="/activeDevice")
	public ResponseEntity<?> activeDeviceSFDA(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                              @RequestParam  (value = "userId",defaultValue = "0") Long userId,
			                              @RequestParam (value = "deviceId",defaultValue = "0") Long deviceId ) {
			
		return deviceServiceImplSFDA.activeDeviceSFDA(TOKEN,userId,deviceId);			
	}
	
	@GetMapping(path ="/getDevicebyId")
	public ResponseEntity<?> getDevicebyId(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                               @RequestParam (value = "deviceId",defaultValue = "0") Long deviceId,
			                               @RequestParam(value = "userId",defaultValue = "0") Long userId) {

		return deviceServiceImpl.findDeviceById(TOKEN,deviceId,userId);
	}
	
	@GetMapping(path = "/assignDeviceToDriver")
	public ResponseEntity<?> assignDeviceToDriver(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
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

		return deviceServiceImpl.assignDeviceToDriver(TOKEN,deviceId,Long.parseLong(driverId),userId);	
		
	}
	
	@GetMapping(path = "/assignGeofencesToDevice")
	public ResponseEntity<?> assignGeofencesToDevice(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                         @RequestParam(value = "deviceId" ,defaultValue = "0") Long deviceId,
			                                         @RequestParam(value = "userId" , defaultValue = "0")Long userId,
			                                         @RequestParam (value = "geoIds", defaultValue = "")Long [] geoIds) {
	
	     return deviceServiceImpl.assignDeviceToGeofences(TOKEN,deviceId,geoIds,userId);	
				
	}
	
	
	@GetMapping(value = "/getDeviceDriver")
	public @ResponseBody ResponseEntity<?> getDeviceDriver(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                               @RequestParam (value = "deviceId",defaultValue = "0") Long deviceId) {
		return deviceServiceImpl.getDeviceDriver(TOKEN,deviceId);
	}
	
	@GetMapping(value = "/getDeviceGeofences")
	public @ResponseBody ResponseEntity<?> getDeviceGeofences(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                                  @RequestParam (value = "deviceId",defaultValue = "0") Long deviceId) {
		
			
		return deviceServiceImpl.getDeviceGeofences(TOKEN,deviceId);

	}
	
	@RequestMapping(value = "/getDeviceSelect", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getDeviceSelect(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
												           @RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId,                                               
														   @RequestParam (value = "userId", defaultValue = "0") Long userId) {
														
	
    	return deviceServiceImpl.getDeviceSelect(TOKEN,loggedUserId,userId);

		
	}
	@RequestMapping(value = "/getDeviceSelectGroup", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getDeviceSelectGroup(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                                    @RequestParam (value = "groupId", defaultValue = "0") Long groupId,                                               
														        @RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId,                                               
																@RequestParam (value = "userId", defaultValue = "0") Long userId) {
																
		
    	return deviceServiceImpl.getDeviceSelectGroup(TOKEN,loggedUserId,userId,groupId);

		
	}
	
	
	@RequestMapping(value = "/getDeviceUnSelectOfClient", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getDeviceUnSelect(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
                                                             @RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId,
			                                                 @RequestParam (value = "userId", defaultValue = "0") Long userId) {
		
	
    	return deviceServiceImpl.getDeviceUnSelect(TOKEN,loggedUserId,userId);

		
	}
	
	@GetMapping(value = "/assignDeviceToUser")
	public ResponseEntity<?> assignDeviceToUser(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                     @RequestParam (value = "userId", defaultValue = "0") Long userId,
												 @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
												 @RequestParam (value = "toUserId", defaultValue = "0") Long toUserId){
		return deviceServiceImpl.assignDeviceToUser(TOKEN,userId,deviceId,toUserId);
	}

	@GetMapping(value = "/getCalibrationData")
	public ResponseEntity<?> getCalibrationData( @RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN, 
			                                     @RequestParam (value = "userId", defaultValue = "0") Long userId,
												 @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId){
		return deviceServiceImpl.getCalibrationData(TOKEN,userId,deviceId);
	}
	@PostMapping(value = "/addDataToCaliberation")
	public ResponseEntity<?> addDataToCaliberation( @RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN, 
			                                        @RequestParam (value = "userId", defaultValue = "0") Long userId,
												    @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
												    @RequestBody Map<String, List> data){
		return deviceServiceImpl.addDataToCaliberation(TOKEN,userId,deviceId,data);
	}
	@GetMapping(value = "/getFuelData")
	public ResponseEntity<?> getFuelData( @RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN, 
			                                     @RequestParam (value = "userId", defaultValue = "0") Long userId,
												 @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId){
		return deviceServiceImpl.getFuelData(TOKEN,userId,deviceId);
	}
	@PostMapping(value = "/addDataToFuel")
	public ResponseEntity<?> addDataToFuel(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN, 
			                               @RequestParam (value = "userId", defaultValue = "0") Long userId,
										   @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
										   @RequestBody Map<String, Object> data){
		return deviceServiceImpl.addDataToFuel(TOKEN,userId,deviceId,data);
	}
	
	@GetMapping(value = "/getSensorSettings")
	public ResponseEntity<?> getSensorSettings( @RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN, 
			                                     @RequestParam (value = "userId", defaultValue = "0") Long userId,
												 @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId){
		return deviceServiceImpl.getSensorSettings(TOKEN,userId,deviceId);
	}

	@PostMapping(value = "/sendCommand")
	public ResponseEntity<?> sendCommand( @RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN, 
                                        @RequestParam (value = "userId", defaultValue = "0") Long userId,
									    @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
									    @RequestBody Map<String, Object> data){
		return deviceServiceImpl.sendCommand(TOKEN,userId,deviceId,data);
	}
	
	@PostMapping(value = "/addSensorSettings")
	public ResponseEntity<?> addSensorSettings( @RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN, 
			                                        @RequestParam (value = "userId", defaultValue = "0") Long userId,
												    @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
												    @RequestBody Map<String, Object> data){
		return deviceServiceImpl.addSensorSettings(TOKEN,userId,deviceId,data);
	}
	
	@PostMapping(value = "/addIcon")
	public ResponseEntity<?> addIcon( @RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN, 
			                                        @RequestParam (value = "userId", defaultValue = "0") Long userId,
												    @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
												    @RequestBody Map<String, Object> data){
		return deviceServiceImpl.addIcon(TOKEN,userId,deviceId,data);
	}
	
	@GetMapping(value = "/getIcon")
	public ResponseEntity<?> getIcon( @RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN, 
			                                     @RequestParam (value = "userId", defaultValue = "0") Long userId,
												 @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId){
		return deviceServiceImpl.getIcon(TOKEN,userId,deviceId);
	}
	
	@GetMapping(value = "/getDeviceDataSelected")
	public @ResponseBody ResponseEntity<?> getDeviceDataSelected(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                               @RequestParam (value = "deviceId",defaultValue = "0") Long deviceId,
			                                               @RequestParam (value = "type", defaultValue = "") String type) {
		
			
		return deviceServiceImpl.getDeviceDataSelected(TOKEN,deviceId,type);

	}
	
	@GetMapping("/assignClientDevices")
	public ResponseEntity<?> assignClientDevices(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
		                           			   @RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId,
											   @RequestParam (value = "userId", defaultValue = "0") Long userId,
											   @RequestParam (value = "deviceIds", defaultValue = "0") Long [] deviceIds) {
		return deviceServiceImpl.assignClientDevices(TOKEN,loggedUserId,userId,deviceIds);
	}
	
	@GetMapping("/getClientDevices")
	public ResponseEntity<?> getClientDevices(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
		                           			@RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId,
											@RequestParam (value = "userId", defaultValue = "0") Long userId) {
		return deviceServiceImpl.getClientDevices(TOKEN,loggedUserId,userId);
	}
	
	@GetMapping("/updateLineData")
	public ResponseEntity<?> updateLineData() {
		return deviceServiceImpl.updateLineData();
	}
	
	@GetMapping("/updatePositionData")
	public ResponseEntity<?> updatePositionData() {
		return deviceServiceImpl.updatePositionData();
	}

	@GetMapping(value = "/getDeviceGraphData")
	public ResponseEntity<?> getDeviceGraphData( @RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
									  @RequestParam (value = "userId", defaultValue = "0") Long userId) throws IOException {
		return deviceServiceImplSFDA.getDeviceGraphData(TOKEN,userId);
	}

	@GetMapping(value = "/getDeviceData/v2.0")
	public ResponseEntity<?> getDeviceGraphDataDashboard( @RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
														  @RequestParam (value = "userId", defaultValue = "0") Long userId,
														  @RequestParam(value = "offset") int offset ,
														  @RequestParam(value = "limit") int limit){
		return deviceServiceImplSFDA.getDeviceGraphDataDashboard(TOKEN,userId,offset,limit);
	}

	@GetMapping(value = "/getGraphData/v2.0")//******DEPRECATED*******
	public ResponseEntity<GetObjectResponse<GraphDataWrapper>> getDeviceGraphDataDashboard(@RequestParam (value = "deviceId", defaultValue = "0") Integer deviceId) {
		return deviceServiceImplSFDA.getDataForGraphByDeviceID(deviceId);
	}
	@GetMapping(value = "/startEndData")
	public void startEndDateScript() throws ParseException {
		deviceServiceImplSFDA.startAndEndDate();
	}
}
