package com.example.food_drugs.entity;

import lombok.Builder;

@Builder
public class InventoryNotification {

	private String _id;
	private Double temperature;
	private Double humidity;
	private Long inventory_id;
	private String create_date;
	private String message;
	private String inventoryName;
	private Long warehouseId;
	private String warehouseName;
	private Object attributes;
	private String type;
	
	
	
	public InventoryNotification() {
		super();
	}
	public InventoryNotification(String _id, Double temperature, Double humidity, Long inventory_id, String create_date,
			String message, String inventoryName, Object attributes, String type) {
		super();
		this._id = _id;
		this.temperature = temperature;
		this.humidity = humidity;
		this.inventory_id = inventory_id;
		this.create_date = create_date;
		this.message = message;
		this.inventoryName = inventoryName;
		this.attributes = attributes;
		this.type = type;
	}
	
	
	public InventoryNotification(String _id, Double temperature, Double humidity, Long inventory_id, String create_date,
			String message, String inventoryName, Long warehouseId, String warehouseName, Object attributes,
			String type) {
		super();
		this._id = _id;
		this.temperature = temperature;
		this.humidity = humidity;
		this.inventory_id = inventory_id;
		this.create_date = create_date;
		this.message = message;
		this.inventoryName = inventoryName;
		this.warehouseId = warehouseId;
		this.warehouseName = warehouseName;
		this.attributes = attributes;
		this.type = type;
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
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public Double getTemperature() {
		return temperature;
	}
	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}
	public Double getHumidity() {
		return humidity;
	}
	public void setHumidity(Double humidity) {
		this.humidity = humidity;
	}
	public Long getInventory_id() {
		return inventory_id;
	}
	public void setInventory_id(Long inventory_id) {
		this.inventory_id = inventory_id;
	}
	public String getCreate_date() {
		return create_date;
	}
	public void setCreate_date(String create_date) {
		this.create_date = create_date;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getInventoryName() {
		return inventoryName;
	}
	public void setInventoryName(String inventoryName) {
		this.inventoryName = inventoryName;
	}
	public Object getAttributes() {
		return attributes;
	}
	public void setAttributes(Object attributes) {
		this.attributes = attributes;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	
	
	
}
