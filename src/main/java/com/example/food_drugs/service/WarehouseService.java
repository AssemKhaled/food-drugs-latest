package com.example.food_drugs.service;

import com.example.examplequerydslspringdatajpamaven.responses.GetObjectResponse;
import com.example.food_drugs.dto.responses.WarehouseInventoriesListMobileWrapper;
import org.springframework.http.ResponseEntity;
import com.example.food_drugs.entity.Warehouse;

public interface WarehouseService {
	public ResponseEntity<?> getWarehousesList(String TOKEN,Long id,int offset,String search,int active,String exportData);
	public ResponseEntity<?> createWarehouses(String TOKEN,Warehouse warehouse,Long userId);
	public ResponseEntity<?> editWarehouses(String TOKEN,Warehouse warehouse,Long userId);
	public ResponseEntity<?> getWarehouseById(String TOKEN,Long WarehouseId,Long userId);
	public ResponseEntity<?> deleteWarehouse(String TOKEN,Long WarehouseId,Long userId);
	public ResponseEntity<?> activeWarehouse(String TOKEN,Long WarehouseId,Long userId);
	public ResponseEntity<?> getListSelectWarehouse(String TOKEN,Long userId);
	public ResponseEntity<?> getListWarehouseMap(String TOKEN,Long userId);
	public ResponseEntity<?> getInventoryListOfWarehouseMap(String TOKEN,Long userId,Long WarehouseId);

	public ResponseEntity<?> getWarehouseUnSelectOfClient(String TOKEN,Long loggedUserId,Long userId);
	public ResponseEntity<?> assignClientWarehouses(String TOKEN,Long loggedUserId,Long userId,Long [] warehouseIds);
	public ResponseEntity<?> getClientWarehouses(String TOKEN,Long loggedUserId,Long userId);
	
	public ResponseEntity<?> assignWarehouseToUser(String TOKEN,Long userId,Long warehouseId , Long toUserId);

	ResponseEntity<?> getListWarehouseDataGraph(String TOKEN,Long userId);
	ResponseEntity<GetObjectResponse<WarehouseInventoriesListMobileWrapper>> getListWarehouseInventoriesListByWahrehouseId(String TOKEN, Long userId, Long warehouseId);

}
