package com.example.food_drugs.rest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.examplequerydslspringdatajpamaven.service.ReportServiceImpl;
import com.example.food_drugs.entity.TripDetailsRequest;
import com.example.food_drugs.service.impl.ReportServiceImplSFDA;

/**
 * Service of reports component
 * @author fuinco
 *
 */
@RestController
@RequestMapping(path = "/reportsSFDA")
@CrossOrigin
public class ReportRestControllerSFDA {
	
	@Autowired
	private ReportServiceImpl reportServiceImpl;
	
	@Autowired
	private ReportServiceImplSFDA reportServiceImplSFDA;
	
	
	@RequestMapping(value = "/getEventsReport", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getEvents(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													 @RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
													 @RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													 @RequestParam (value = "offset", defaultValue = "0") int offset,
													 @RequestParam (value = "start", defaultValue = "0") String start,
													 @RequestParam (value = "exportData", defaultValue = "") String exportData,
													 @RequestParam (value = "end", defaultValue = "0") String end,
													 @RequestParam (value = "type", defaultValue = "") String type,
													 @RequestParam (value = "search", defaultValue = "") String search,
													 @RequestParam (value = "userId",defaultValue = "0")Long userId,
													 @RequestParam (value = "timeOffset", defaultValue = "") String timeOffset) {
    	return  reportServiceImpl.getEventsReport(TOKEN, deviceId,groupId, offset, start, end, type, search, userId ,exportData, timeOffset);

	}
	

	@RequestMapping(value = "/getDeviceWorkingHours", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getDeviceWorkingHours(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													 @RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
													 @RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													 @RequestParam (value = "offset", defaultValue = "0") int offset,
													 @RequestParam (value = "start", defaultValue = "0") String start,
													 @RequestParam (value = "exportData", defaultValue = "") String exportData,
													 @RequestParam (value = "end", defaultValue = "0") String end,
													 @RequestParam (value = "search", defaultValue = "") String search,
													 @RequestParam (value = "userId",defaultValue = "0")Long userId) {
		
		
		
    	return  reportServiceImpl.getDeviceWorkingHours(TOKEN,deviceId,groupId, offset, start, end,search,userId,exportData);

	}
	
	
	@RequestMapping(value = "/getCustomReport", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getCustomReport(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													 @RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
													 @RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													 @RequestParam (value = "offset", defaultValue = "0") int offset,
													 @RequestParam (value = "exportData", defaultValue = "") String exportData,
													 @RequestParam (value = "start", defaultValue = "0") String start,
													 @RequestParam (value = "end", defaultValue = "0") String end,
													 @RequestParam (value = "search", defaultValue = "") String search,
													 @RequestParam (value = "userId",defaultValue = "0")Long userId,
													 @RequestParam (value = "custom", defaultValue = "") String custom,
													 @RequestParam (value = "value", defaultValue = "") String value) {
		
		
		
    	return  reportServiceImpl.getCustomReport(TOKEN,deviceId,groupId, offset, start, end,search,userId,custom,value,exportData);

	}
	

	@RequestMapping(value = "/getDriverWorkingHours", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getDriverWorkingHours(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													 @RequestParam (value = "driverId", defaultValue = "0") Long [] driverId,
													 @RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													 @RequestParam (value = "exportData", defaultValue = "") String exportData,
													 @RequestParam (value = "offset", defaultValue = "0") int offset,
													 @RequestParam (value = "start", defaultValue = "0") String start,
													 @RequestParam (value = "end", defaultValue = "0") String end,
													 @RequestParam (value = "search", defaultValue = "") String search,
													 @RequestParam (value = "userId",defaultValue = "0")Long userId) {
		
		
		
    	return  reportServiceImpl.getDriverWorkingHours(TOKEN,driverId,groupId, offset,start, end,search,userId,exportData);

	}

	@RequestMapping(value = "/getStopsReport", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getStops(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													@RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
													@RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													@RequestParam (value = "type", defaultValue = "allEvents") String type,
													@RequestParam (value = "from", defaultValue = "0") String from,
													@RequestParam (value = "to", defaultValue = "0") String to,
													@RequestParam (value = "page", defaultValue = "1") int page,
													@RequestParam (value = "start", defaultValue = "0") int start,
													@RequestParam (value = "limit", defaultValue = "25") int limit,
													@RequestParam (value = "userId",defaultValue = "0")Long userId,
													@RequestParam (value = "timeOffset", defaultValue = "") String timeOffset ) {
		

		
    	return reportServiceImpl.getStopsReport(TOKEN,deviceId,groupId, type, from, to, page, start, limit,userId, timeOffset);
		
		
	}
	
	
	@RequestMapping(value = "/getTripsReport", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getTrips(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													@RequestParam (value = "deviceId", defaultValue = "0")  Long [] deviceId,
													@RequestParam (value = "groupId", defaultValue = "0")  Long [] groupId,
													@RequestParam (value = "type", defaultValue = "allEvents") String type,
													@RequestParam (value = "from", defaultValue = "0") String from,
													@RequestParam (value = "to", defaultValue = "0") String to,
													@RequestParam (value = "page", defaultValue = "1") int page,
													@RequestParam (value = "start", defaultValue = "0") int start,
													@RequestParam (value = "limit", defaultValue = "25") int limit,
													@RequestParam (value = "userId",defaultValue = "0")Long userId,
													@RequestParam (value = "timeOffset", defaultValue = "") String timeOffset) {
												

    	return  reportServiceImpl.getTripsReport(TOKEN,deviceId,groupId, type, from, to, page, start, limit,userId, timeOffset);
		 
	}
	

	@RequestMapping(value = "/getDriveMoreThanReport", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getDriveMoreThanReport(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
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
												

    	return  reportServiceImpl.getDriveMoreThanReport(TOKEN,deviceId,driverId,groupId, type, from, to, page, start, limit,userId);
		 
		
	}
	
	
	
	@RequestMapping(value = "/getEventsReportByType", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getEventsType(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													@RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
													@RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													@RequestParam (value = "type", defaultValue = "allEvents") String type,
													@RequestParam (value = "from", defaultValue = "0") String from,
													@RequestParam (value = "to", defaultValue = "0") String to,
													@RequestParam (value = "page", defaultValue = "1") int page,
													@RequestParam (value = "start", defaultValue = "0") int start,
													@RequestParam (value = "limit", defaultValue = "25") int limit,
													@RequestParam (value = "userId",defaultValue = "0")Long userId) {
												

    	return  reportServiceImpl.getEventsReportByType(TOKEN,deviceId,groupId, type, from, to, page, start, limit,userId);
		 
		
	}
	
	
	@RequestMapping(value = "/getSummaryReport", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getSummary(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													@RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
													@RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													@RequestParam (value = "type", defaultValue = "allEvents") String type,
													@RequestParam (value = "from", defaultValue = "0") String from,
													@RequestParam (value = "to", defaultValue = "0") String to,
													@RequestParam (value = "page", defaultValue = "1") int page,
													@RequestParam (value = "start", defaultValue = "0") int start,
													@RequestParam (value = "limit", defaultValue = "25") int limit,
													@RequestParam (value = "userId",defaultValue = "0")Long userId,
												    @RequestParam (value = "timeOffset", defaultValue = "") String timeOffset) {
												

    	return  reportServiceImpl.getSummaryReport(TOKEN,deviceId,groupId, type, from, to, page, start, limit,userId, timeOffset);
		 
		
	}
	
	@RequestMapping(value = "/getSensorsReport", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getSensors(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													  @RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
													  @RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													  @RequestParam (value = "exportData", defaultValue = "") String exportData,
													  @RequestParam (value = "offset", defaultValue = "0") int offset,
													  @RequestParam (value = "start", defaultValue = "0") String start,
													  @RequestParam (value = "end", defaultValue = "0") String end,
													  @RequestParam (value = "search", defaultValue = "") String search,
													  @RequestParam (value = "userId",defaultValue = "0")Long userId) {
	
    	return  reportServiceImpl.getSensorsReport(TOKEN,deviceId,groupId, offset, start, end,search,userId,exportData);

	}
	

	@RequestMapping(value = "/getNumTripsReport", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getNumTripsReport(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
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
												

    	return  reportServiceImpl.getNumTripsReport(TOKEN,deviceId,driverId,groupId, type, from, to, page, start, limit,userId);
		 
		
	}
	
	@RequestMapping(value = "/getNumStopsReport", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getNumStopsReport(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
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
		

		
    	return reportServiceImpl.getNumStopsReport(TOKEN,deviceId,driverId,groupId, type, from, to, page, start, limit,userId);
		
		
	}
	
	
	@RequestMapping(value = "/geTotalTripsReport", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> geTotalTripsReport(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
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
												

    	return  reportServiceImpl.geTotalTripsReport(TOKEN,deviceId,driverId,groupId, type, from, to, page, start, limit,userId);
		 
		
	}
	
	
	@RequestMapping(value = "/getTotalStopsReport", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getTotalStopsReport(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
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
		

		
    	return reportServiceImpl.getTotalStopsReport(TOKEN,deviceId,driverId,groupId, type, from, to, page, start, limit,userId);
		
		
	}
	
	

	@RequestMapping(value = "/getNumberDriverWorkingHours", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getNumberDriverWorkingHours(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													 @RequestParam (value = "driverId", defaultValue = "0") Long [] driverId,
													 @RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													 @RequestParam (value = "exportData", defaultValue = "") String exportData,
													 @RequestParam (value = "offset", defaultValue = "0") int offset,
													 @RequestParam (value = "start", defaultValue = "0") String start,
													 @RequestParam (value = "end", defaultValue = "0") String end,
													 @RequestParam (value = "search", defaultValue = "") String search,
													 @RequestParam (value = "userId",defaultValue = "0")Long userId) {
		
		
		
    	return  reportServiceImpl.getNumberDriverWorkingHours(TOKEN,driverId,groupId, offset,start, end,search,userId,exportData);

	}
	
	@RequestMapping(value = "/viewTrip", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> viewTrip(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													 @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
													 @RequestParam (value = "startTime", defaultValue = "0") String startTime,
													 @RequestParam (value = "endTime", defaultValue = "0") String endTime) {	
    	return  reportServiceImpl.getviewTrip(TOKEN, deviceId,startTime, endTime);

	}

	
	@RequestMapping(value = "/getInventoriesReport", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getInventoriesReport(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
																@RequestParam (value = "inventoryId", defaultValue = "0") Long [] inventoryId,
																@RequestParam (value = "exportData", defaultValue = "") String exportData,
																@RequestParam (value = "offset", defaultValue = "0") int offset,
																@RequestParam (value = "start", defaultValue = "0") String start,
																@RequestParam (value = "end", defaultValue = "0") String end,
																@RequestParam (value = "search", defaultValue = "") String search,
																@RequestParam (value = "userId",defaultValue = "0")Long userId) {	
    	return  reportServiceImplSFDA.getInventoriesReport(TOKEN, inventoryId, offset, start, end, search, userId,exportData);

	}
	
	@RequestMapping(value = "/getWarehousesReport", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getWarehousesReport(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
															   @RequestParam (value = "warehouseId", defaultValue = "0") Long [] warehouseId,
																@RequestParam (value = "exportData", defaultValue = "") String exportData,
																 @RequestParam (value = "offset", defaultValue = "0") int offset,
																 @RequestParam (value = "start", defaultValue = "0") String start,
																 @RequestParam (value = "end", defaultValue = "0") String end,
																 @RequestParam (value = "search", defaultValue = "") String search,
																 @RequestParam (value = "userId",defaultValue = "0")Long userId) {	
    	return  reportServiceImplSFDA.getWarehousesReport(TOKEN, warehouseId, offset, start, end, search, userId,exportData);

	}
	
	@RequestMapping(value = "/getNotificationReport", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getNotificationReport(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
																 @RequestParam (value = "inventoryId", defaultValue = "0") Long [] inventoryId,													 
																 @RequestParam (value = "warehouseId", defaultValue = "0") Long [] warehouseId,
																 @RequestParam (value = "exportData", defaultValue = "") String exportData,
																 @RequestParam (value = "offset", defaultValue = "0") int offset,
																 @RequestParam (value = "start", defaultValue = "0") String start,
																 @RequestParam (value = "end", defaultValue = "0") String end,
																 @RequestParam (value = "search", defaultValue = "") String search,
																 @RequestParam (value = "userId",defaultValue = "0")Long userId) {	
    	return  reportServiceImplSFDA.getNotificationReport(TOKEN,inventoryId, warehouseId, offset, start, end, search, userId,exportData);

	}


	@RequestMapping(value = "/getNotificationReport/v2.0", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getNotificationReportNew(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
																 @RequestParam (value = "inventoryId", defaultValue = "0") Long [] inventoryId,
																 @RequestParam (value = "warehouseId", defaultValue = "0") Long [] warehouseId,
																 @RequestParam (value = "exportData", defaultValue = "") String exportData,
																 @RequestParam (value = "offset", defaultValue = "0") int offset,
																 @RequestParam (value = "start", defaultValue = "0") String start,
																 @RequestParam (value = "end", defaultValue = "0") String end,
																 @RequestParam (value = "search", defaultValue = "") String search,
																 @RequestParam (value = "userId",defaultValue = "0")Long userId) {
		return  reportServiceImplSFDA.getNotificationReportNew(TOKEN,inventoryId, warehouseId, offset, start, end, search, userId,exportData);

	}
	
	@RequestMapping(value = "/getVehicleTempHum", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getVehicleTempHum(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													  @RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
													  @RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
													  @RequestParam (value = "exportData", defaultValue = "") String exportData,
													  @RequestParam (value = "offset", defaultValue = "0") int offset,
													  @RequestParam (value = "start", defaultValue = "0") String start,
													  @RequestParam (value = "end", defaultValue = "0") String end,
													  @RequestParam (value = "search", defaultValue = "") String search,
													  @RequestParam (value = "userId",defaultValue = "0")Long userId,
													  @RequestParam (value = "timeOffset", defaultValue = "") String timeOffset) {
	
    	return  reportServiceImplSFDA.getVehicleTempHum(TOKEN,deviceId,groupId, offset, start, end,search,userId,exportData,timeOffset);

	}


	@RequestMapping(value = "/getVehicleTempHum/v2.0", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getVehicleTempHumNew(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
															 @RequestParam (value = "deviceId", defaultValue = "0") Long [] deviceId,
															 @RequestParam (value = "groupId", defaultValue = "0") Long [] groupId,
															 @RequestParam (value = "exportData", defaultValue = "") String exportData,
															 @RequestParam (value = "offset", defaultValue = "0") int offset,
															 @RequestParam (value = "start", defaultValue = "0") String start,
															 @RequestParam (value = "end", defaultValue = "0") String end,
															 @RequestParam (value = "search", defaultValue = "") String search,
															 @RequestParam (value = "userId",defaultValue = "0")Long userId) {

		return  reportServiceImplSFDA.getVehicleTempHumNew(TOKEN,deviceId,groupId, offset, start, end,search,userId,exportData);

	}
	
	@RequestMapping(value = "/getVehicleTempHumPDF", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getVehicleTempHumPDF(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													  @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
													  @RequestParam (value = "exportData", defaultValue = "") String exportData,
													  @RequestParam (value = "offset", defaultValue = "0") int offset,
													  @RequestParam (value = "start", defaultValue = "0") String start,
													  @RequestParam (value = "end", defaultValue = "0") String end,
													  @RequestParam (value = "search", defaultValue = "") String search,
													  @RequestParam (value = "userId",defaultValue = "0")Long userId) {
	
    	return  reportServiceImplSFDA.getVehicleTempHumPDF(TOKEN,deviceId, offset, start, end,search,userId,exportData);

	}
	
	
	@RequestMapping(value = "/viewTripDetails", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> viewTripDetails(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
													       @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
													       @RequestParam (value = "exportData", defaultValue = "") String exportData,
													       @RequestParam (value = "offset", defaultValue = "0") int offset,
													       @RequestParam (value = "startTime", defaultValue = "0") String startTime,
													       @RequestParam (value = "endTime", defaultValue = "0") String endTime,
														   @RequestParam (value = "timeOffset", defaultValue = "") String timeOffset) {
    	return  reportServiceImplSFDA.getviewTripDetails(TOKEN, deviceId,startTime, endTime,exportData,offset, timeOffset);

	}
	
	@PostMapping(value = "/getTripPdf")
	public @ResponseBody ResponseEntity<?>getTripPdf(@RequestHeader(value = "TOKEN", defaultValue = "") String TOKEN,
													 @RequestBody(required = false) TripDetailsRequest request,
													 @RequestParam(value ="timeOffset",defaultValue = "")String timeOffset) {
		
		return reportServiceImplSFDA.getTripPdfDetails(request,timeOffset);
	}

	@PostMapping(value = "/device/cfr")
	public @ResponseBody ResponseEntity<?>getDeviceCFRReport(@RequestHeader(value = "TOKEN", defaultValue = "") String TOKEN,
															 @RequestBody(required = false) TripDetailsRequest request,
															 @RequestParam(value ="timeOffset",defaultValue = "")String timeOffset) {

		return reportServiceImplSFDA.getDeviceCFRReport(request,timeOffset);
	}


}
