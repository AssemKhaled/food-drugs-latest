package com.example.food_drugs.service.mobile;

import com.example.food_drugs.dto.ApiResponse;
import com.example.food_drugs.dto.responses.mobile.WareHouseInvLastDataResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.food_drugs.entity.Inventory;
import com.example.food_drugs.entity.Warehouse;

import java.util.List;

@Service
public interface AppServiceSFDA {

	public  ResponseEntity<?> activeDeviceAppSFDA(String TOKEN,Long userId, Long deviceId);

	public ResponseEntity<?> activeDriverApp(String Token,Long driverId, Long userId);
	public ResponseEntity<?> getAllDriversAppSFDA(String TOKEN,Long id,int offset,String search,int active,String exportData);
	
	public ResponseEntity<?> activeGeofenceApp(String TOKEN,Long geofenceId,Long userId);
	public ResponseEntity<?> getAllGeofencesAppSFDA(String TOKEN,Long id,int offset,String search,int active,String exportData);

	public ResponseEntity<?> getInventoriesListApp(String TOKEN,Long id,int offset,String search,int active,String exportData);
	public ResponseEntity<?> createInventoriesApp(String TOKEN,Inventory inventory,Long userId);
	public ResponseEntity<?> editInventoriesApp(String TOKEN,Inventory inventory,Long userId);
	public ResponseEntity<?> getInventoryByIdApp(String TOKEN,Long InventoryId,Long userId);
	public ResponseEntity<?> deleteInventoryApp(String TOKEN,Long InventoryId,Long userId);
	public ResponseEntity<?> activeInventoryApp(String TOKEN,Long InventoryId,Long userId);
	public ResponseEntity<?> removeWarehouseFromInventoryApp(String TOKEN,Long userId,Long inventoryId,Long warehouseId);
	public ResponseEntity<?> assignWarehouseToInventoryApp(String TOKEN,Long userId,Long inventoryId,Long warehouseId);
	public ResponseEntity<?> getSelectListInventoriesApp(String TOKEN,Long id);
	public ResponseEntity<?> getSelectedAndListWarehouseApp(String TOKEN,Long loggedUserId,Long userId,Long inventoryId);
	
	public ResponseEntity<?> getWarehousesListApp(String TOKEN,Long id,int offset,String search,int active,String exportData);
	public ResponseEntity<?> createWarehousesApp(String TOKEN,Warehouse warehouse,Long userId);
	public ResponseEntity<?> editWarehousesApp(String TOKEN,Warehouse warehouse,Long userId);
	public ResponseEntity<?> getWarehouseByIdApp(String TOKEN,Long WarehouseId,Long userId);
	public ResponseEntity<?> deleteWarehouseApp(String TOKEN,Long WarehouseId,Long userId);
	public ResponseEntity<?> activeWarehouseApp(String TOKEN,Long WarehouseId,Long userId);
	public ResponseEntity<?> getListSelectWarehouseApp(String TOKEN,Long userId);
	public ResponseEntity<?> getListWarehouseMapApp(String TOKEN,Long userId);
	public ResponseEntity<?> getInventoryListOfWarehouseMapApp(String TOKEN,Long userId,Long WarehouseId);
	
	public ResponseEntity<?> getInventoryStatusApp(String TOKEN,Long userId);
	public ResponseEntity<?> getInventoriesNotificationsApp(String TOKEN,Long userId,int offset,String search);
	public ResponseEntity<?> getAllInventoriesLastInfoApp(String TOKEN,Long userId,int offset,String search);
	
	public ResponseEntity<?> getInventoriesReportApp(String TOKEN,Long [] inventoryId,int offset,String start,String end,String search,Long userId,String exportData);
	public ResponseEntity<?> getWarehousesReportApp(String TOKEN,Long [] warehouseId,int offset,String start,String end,String search,Long userId,String exportData);
	public ResponseEntity<?> getNotificationReportApp(String TOKEN,Long [] inventoryId,Long [] warehouseId,int offset,String start,String end,String search,Long userId,String exportData);
	public ResponseEntity<?> getVehicleTempHumApp(String TOKEN,Long [] deviceId,Long [] groupId,int offset,String start,String end,String search,Long userId,String exportData);

	ApiResponse<List<WareHouseInvLastDataResponse>>getWareHouseInvLastData(String TOKEN, Long userId, int whSize, int offset, String search);
}
