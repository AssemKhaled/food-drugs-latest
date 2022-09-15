package com.example.food_drugs.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.example.food_drugs.dto.ApiResponse;
import com.example.food_drugs.dto.responses.InventoryListDashBoardResponse;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import com.example.food_drugs.entity.Inventory;
import com.example.food_drugs.entity.SensorsInventories;

public interface InventoryService {

	public ResponseEntity<?> getInventoriesList(String TOKEN,Long id,int offset,String search,int active,String exportData);
	public ResponseEntity<?> createInventories(String TOKEN,Inventory inventory,Long userId);
	public ResponseEntity<?> editInventories(String TOKEN,Inventory inventory,Long userId);
	public ResponseEntity<?> getInventoryById(String TOKEN,Long InventoryId,Long userId);
	public ResponseEntity<?> deleteInventory(String TOKEN,Long InventoryId,Long userId);
	public ResponseEntity<?> activeInventory(String TOKEN,Long InventoryId,Long userId);
	public ResponseEntity<?> removeWarehouseFromInventory(String TOKEN,Long userId,Long inventoryId,Long warehouseId);
	public ResponseEntity<?> assignWarehouseToInventory(String TOKEN,Long userId,Long inventoryId,Long warehouseId);
	public ResponseEntity<?> getSelectListInventories(String TOKEN,Long id);

	public ResponseEntity<?> getInventoriesNotifications(String TOKEN,Long userId,int offset,String search);
	public ResponseEntity<?> getAllInventoriesLastInfo(String TOKEN,Long userId,int offset,String search);
	ResponseEntity<?> getAllInventoriesLastInfoNew(String TOKEN,Long userId,int offset,String search,String timeOffset);
	
	public ResponseEntity<?> getInventoryUnSelectOfClient(String TOKEN,Long loggedUserId,Long userId);
	public ResponseEntity<?> assignClientInventories(String TOKEN,Long loggedUserId,Long userId,Long [] inventoryId);
	public ResponseEntity<?> getClientInventories(String TOKEN,Long loggedUserId,Long userId);
	
	public ResponseEntity<?> getSelectedAndListWarehouse(String TOKEN,Long loggedUserId,Long userId,Long inventoryId);

	public ResponseEntity<?> getInventoryStatus(String TOKEN,Long userId);
	
	public ResponseEntity<?> getEasyCloudData();
	public ResponseEntity<?> callApiEasyClould(Inventory inv);

	public ResponseEntity<?> getDataProtocols(ArrayList<Map<Object,Object>> data,String type,String email);
	public ResponseEntity<?> getCsvProtocols(Long userId,ArrayList<Map<Object,Object>> data);
	public ResponseEntity<?> getEasyCloudProtocols(Inventory inv,ArrayList<Map<Object,Object>> data);
	public ResponseEntity<?> getDataProtocolsSkarpt(Map<Object,Object> data);

	public ObjectId saveLastDataHandler(Inventory inventory,Date dateTime,Double AvgTemp,Double AvgHum);
	public ResponseEntity<?> saveTemperatureHandler(Inventory inventory,Date dateTime,Double AvgTemp,Double oldTemp);
	public ResponseEntity<?> saveHumidityHandler(Inventory inventory,Date dateTime,Double AvgHum,Double oldTHum);

	
	public ResponseEntity<?> getSensorsInventoriesList(String TOKEN,Long InventoryId,Long userId);
	public ResponseEntity<?> addSensor(String TOKEN,SensorsInventories sensorsInventories,Long InventoryId,Long userId);
	public ResponseEntity<?> removeSensor(String TOKEN,Long sensorInventoryId,Long InventoryId,Long userId);
	
	public ResponseEntity<?> assignInventoryToUser(String TOKEN,Long userId,Long inventoryId , Long toUserId);

	ResponseEntity<?> inventoryMonthelyView(String endDate);

	ApiResponse<List<InventoryListDashBoardResponse>> inventoryListDashBoard(String TOKEN,Long userId);

	
}
