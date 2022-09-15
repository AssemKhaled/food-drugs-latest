package com.example.food_drugs.entity;

import com.example.examplequerydslspringdatajpamaven.entity.MongoElmLogs;

public class MongoElmLogsSFDA extends MongoElmLogs{


	private Long inventoryId;
	
	private String inventoryName;
	
	private Long warehouseId;
	
	private String warehouseName;

	
	public MongoElmLogsSFDA() {
		super();
	}

	public Long getInventoryId() {
		return inventoryId;
	}

	public void setInventoryId(Long inventoryId) {
		this.inventoryId = inventoryId;
	}

	public String getInventoryName() {
		return inventoryName;
	}

	public void setInventoryName(String inventoryName) {
		this.inventoryName = inventoryName;
	}

	public Long getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Long warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	
	
}
