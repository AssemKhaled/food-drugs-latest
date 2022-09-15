
package com.example.food_drugs.rest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
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
import com.example.examplequerydslspringdatajpamaven.service.ElmServiceImpl;
import com.example.food_drugs.service.impl.ElmServiceImplSFDA;


/**
 * Service of elm connections
 * @author fuinco
 *
 */
@CrossOrigin
@Component
@RequestMapping(path = "/elmSFDA")
public class ElmConnectionsRestControllerSFDA {

	
	@Autowired
	private ElmServiceImpl elmServiceImpl;
	
	@Autowired
	private ElmServiceImplSFDA elmServiceImplSFDA;
	
	@GetMapping(path ="/companyRegistrtaion")
	public ResponseEntity<?> companyRegistrtaion(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                     @RequestParam (value = "userId", defaultValue = "0") Long userId,
			                                     @RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId){
		
		return elmServiceImplSFDA.companyRegistrtaionSFDA(TOKEN,userId,loggedUserId);
	}
	
	@PostMapping(path ="/companyUpdate")
	public ResponseEntity<?> companyUpdate(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                     @RequestBody Map<String, String> data,
			                                     @RequestParam (value = "userId", defaultValue = "0") Long userId,
			                                     @RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId){
		
		return elmServiceImpl.companyUpdate(TOKEN,data,userId,loggedUserId);
	}
	
	@GetMapping(path ="/companyDelete")
	public ResponseEntity<?> companyDelete(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                     @RequestParam (value = "userId", defaultValue = "0") Long userId,
			                                     @RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId){
		
		return elmServiceImpl.companyDelete(TOKEN,userId,loggedUserId);
	}
	
	@GetMapping(path ="/deviceRegistrtaion")
	public ResponseEntity<?> deviceRegistrtaion(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                     @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
			                                     @RequestParam (value = "userId", defaultValue = "0") Long userId){
		
		return elmServiceImplSFDA.deviceRegistrtaionSFDA(TOKEN,deviceId,userId);
	}
	
	@GetMapping(path ="/deviceDelete")
	public ResponseEntity<?> deviceDelete(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                     @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
			                                     @RequestParam (value = "userId", defaultValue = "0") Long userId){
		
		return elmServiceImpl.deviceDelete(TOKEN,deviceId,userId);
	}
	
	
	@PostMapping(path ="/deviceUpdate")
	public ResponseEntity<?> deviceUpdate(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                     @RequestBody Map<String, String> data,
			                                     @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
			                                     @RequestParam (value = "userId", defaultValue = "0") Long userId){
		
		return elmServiceImpl.deviceUpdate(TOKEN,data,deviceId,userId);
	}
	
	@GetMapping(path ="/driverRegistrtaion")
	public ResponseEntity<?> driverRegistrtaion(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                    @RequestParam (value = "driverId", defaultValue = "0") Long driverId,
			                                    @RequestParam (value = "userId", defaultValue = "0") Long userId){
		
		return elmServiceImpl.driverRegistrtaion(TOKEN,driverId,userId);
	}
	
	@PostMapping(path ="/driverUpdate")
	public ResponseEntity<?> driverUpdate(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                     @RequestBody Map<String, String> data,
			                                     @RequestParam (value = "driverId", defaultValue = "0") Long driverId,
			                                     @RequestParam (value = "userId", defaultValue = "0") Long userId){
		
		return elmServiceImpl.driverUpdate(TOKEN,data,driverId,userId);
	}
	
	
	
	@GetMapping(path ="/deviceInquery")
	public ResponseEntity<?> deviceInquery(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                     @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
			                                     @RequestParam (value = "userId", defaultValue = "0") Long userId){
		
		return elmServiceImpl.deviceInquery(TOKEN,deviceId,userId);
	}
	
	@GetMapping(path ="/companyInquery")
	public ResponseEntity<?> companyInquery(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                     @RequestParam (value = "userId", defaultValue = "0") Long userId,
			                                     @RequestParam (value = "loggedUserId", defaultValue = "0") Long loggedUserId){
		
		return elmServiceImpl.companyInquery(TOKEN,userId,loggedUserId);
	}
	
	@GetMapping(path ="/driverInquery")
	public ResponseEntity<?> driverInquery(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                    @RequestParam (value = "driverId", defaultValue = "0") Long driverId,
			                                    @RequestParam (value = "userId", defaultValue = "0") Long userId){
		
		return elmServiceImpl.driverInquery(TOKEN,driverId,userId);
	}
	
	@GetMapping(path ="/driverDelete")
	public ResponseEntity<?> driverDelete(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                    @RequestParam (value = "driverId", defaultValue = "0") Long driverId,
			                                    @RequestParam (value = "userId", defaultValue = "0") Long userId){
		
		return elmServiceImpl.driverDelete(TOKEN,driverId,userId);
	}
	
	
	@GetMapping(path ="/warehouseRegistrtaion")
	public ResponseEntity<?> warehouseRegistrtaion(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                       @RequestParam (value = "warehouseId", defaultValue = "0") Long warehouseId,
			                                       @RequestParam (value = "userId", defaultValue = "0") Long userId){
		
		return elmServiceImplSFDA.warehouseRegistrtaion(TOKEN,warehouseId,userId);
	}
	@GetMapping(path ="/warehouseDelete")
	public ResponseEntity<?> warehouseDelete(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                     @RequestParam (value = "warehouseId", defaultValue = "0") Long warehouseId,
			                                     @RequestParam (value = "userId", defaultValue = "0") Long userId){
		
		return elmServiceImplSFDA.warehouseDelete(TOKEN,warehouseId,userId);
	}
	
	@GetMapping(path ="/warehouseUpdate")
	public ResponseEntity<?> warehouseUpdate(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
                                                 @RequestBody Map<String, String> data,
			                                     @RequestParam (value = "warehouseId", defaultValue = "0") Long warehouseId,
			                                     @RequestParam (value = "userId", defaultValue = "0") Long userId){
		
		return elmServiceImplSFDA.warehouseUpdate(TOKEN,data,warehouseId,userId);
	}
	
	@GetMapping(path ="/inventoryDelete")
	public ResponseEntity<?> inventoryDelete(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                     @RequestParam (value = "inventoryId", defaultValue = "0") Long inventoryId,
			                                     @RequestParam (value = "userId", defaultValue = "0") Long userId){
		
		return elmServiceImplSFDA.inventoryDelete(TOKEN,inventoryId,userId);
	}
	@GetMapping(path ="/inventoryRegistrtaion")
	public ResponseEntity<?> inventoryRegistrtaion(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                     @RequestParam (value = "inventoryId", defaultValue = "0") Long inventoryId,
			                                     @RequestParam (value = "userId", defaultValue = "0") Long userId){
		
		return elmServiceImplSFDA.inventoryRegistrtaion(TOKEN,inventoryId,userId);
	}
	@GetMapping(path ="/inventoryUpdate")
	public ResponseEntity<?> inventoryUpdate(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
                                                 @RequestBody Map<String, String> data,
			                                     @RequestParam (value = "inventoryId", defaultValue = "0") Long inventoryId,
			                                     @RequestParam (value = "userId", defaultValue = "0") Long userId){
		
		return elmServiceImplSFDA.inventoryUpdate(TOKEN,data,inventoryId,userId);
	}
	
	
	@PostMapping(path ="/deviceUpdateStoring")
	public ResponseEntity<?> deviceUpdateStoring(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                     @RequestBody Map<String, String> data,
			                                     @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
			                                     @RequestParam (value = "userId", defaultValue = "0") Long userId){
		
		return elmServiceImplSFDA.deviceUpdateStoring(TOKEN,data,deviceId,userId);
	}
	

	
	@RequestMapping(value = "/getAllLogs", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getLogs(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                       @RequestParam (value = "loggedUserId", defaultValue = "0") Long id,
			                                       @RequestParam (value = "userId", defaultValue = "0") Long userId,
			                                       @RequestParam (value = "driverId", defaultValue = "0") Long driverId,
			                                       @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId,
												   @RequestParam (value = "offset", defaultValue = "0") int offset,
												   @RequestParam (value = "search", defaultValue = "") String search) {
		
    	return  elmServiceImpl.getLogs(TOKEN,id,userId,driverId,deviceId,offset,search);

	}
	
	@GetMapping(path ="/statsInventories")
//    @Scheduled(fixedRate = 30000)
	public ResponseEntity<?> statsInventories(){
		
		return elmServiceImplSFDA.statsInventories();
	}
	
	@GetMapping(path ="/lastLocations")
    @Scheduled(fixedRate = 30000)
	public ResponseEntity<?> lastLocations(){
		
		return elmServiceImplSFDA.lastLocationsSFDA();
	}
	
	@GetMapping(path ="/getExpiredVehicles")
	@Scheduled(cron = "0 59 23 ? * *")
	public ResponseEntity<?> getExpiredVehicles(){
		
		return elmServiceImpl.getExpiredVehicles();
	}
	
	@GetMapping(path ="/getRemoveOldLogs")
	public ResponseEntity<?> getRemoveOldLogs(){
		
		return elmServiceImpl.getRemoveOldLogs();
	}
	
	@GetMapping(path ="/checkBySequenceNumber")
	public ResponseEntity<?> checkBySequenceNumber(
			@RequestParam (value = "sequenceNumber", defaultValue = "") String sequenceNumber){
		
		return elmServiceImpl.checkBySequenceNumber(sequenceNumber);
	}
	
	@PostMapping(path ="/deleteVehicleFromElm")
	public ResponseEntity<?> deleteVehicleFromElm(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
			                                     @RequestBody Map<String, String> data,
			                                     @RequestParam (value = "userId", defaultValue = "0") Long userId,
			                                     @RequestParam (value = "deviceId", defaultValue = "0") Long deviceId){
		
		return elmServiceImpl.deleteVehicleFromElm(TOKEN,deviceId,userId,data);
	}

	@GetMapping(path ="/warehouseInquiry")
	public ResponseEntity<?> warehouseInquiry(@RequestHeader(value = "TOKEN", defaultValue = "")String TOKEN,
												  @RequestParam (value = "userId", defaultValue = "0") Long userId,
												  @RequestParam (value = "loggerId", defaultValue = "0") Long loggedUserId){

		return elmServiceImplSFDA.warehouseInquiry(TOKEN,loggedUserId,userId);
	}

	@GetMapping(path = "/findLastPositionsSequenceNumberSpeedZero")
	public ResponseEntity<?> findLastPositionsSequenceNumberSpeedZero(
			@RequestParam (value = "sequenceNumber", defaultValue = "") String sequenceNumber){

		return elmServiceImplSFDA.findLastPositionsSequenceNumberSpeedZero(sequenceNumber);
	}

	@GetMapping(path = "/findLastPositionsSequenceNumberNoneSpeedZero")
	public ResponseEntity<?> findLastPositionsSequenceNumberNoneSpeedZero(
			@RequestParam (value = "sequenceNumber", defaultValue = "") String sequenceNumber){

		return elmServiceImplSFDA.findLastPositionsSequenceNumberNoneSpeedZero(sequenceNumber);
	}
	@GetMapping(path = "/findLastNoneZeroVelocityPositionsBySequenceNumber")
	public ResponseEntity<?> findLastNoneZeroVelocityPositionsBySequenceNumber(
			@RequestParam (value = "sequenceNumber", defaultValue = "") String sequenceNumber){

		return elmServiceImplSFDA.findLastNoneZeroVelocityPositionsBySequenceNumber(sequenceNumber);
	}
	@GetMapping(path = "/findLastZeroVelocityPositionsBySequenceNumber")
	public ResponseEntity<?> findLastZeroVelocityPositionsBySequenceNumber(
			@RequestParam (value = "sequenceNumber", defaultValue = "") String sequenceNumber){

		return elmServiceImplSFDA.findLastZeroVelocityPositionsBySequenceNumber(sequenceNumber);
	}
	@GetMapping(path ="/findDeviceData")
	public ResponseEntity<?> findDeviceData(
			@RequestParam (value = "sequenceNumber", defaultValue = "") String sequenceNumber){

		return elmServiceImplSFDA.findDeviceData(sequenceNumber);
	}
	@GetMapping(path ="/findDeviceLastPosition")
	public ResponseEntity<?> findDeviceLastPosition(
			@RequestParam (value = "sequenceNumber", defaultValue = "") String sequenceNumber){

		return elmServiceImplSFDA.findDeviceLastPosition(sequenceNumber);
	}

}
