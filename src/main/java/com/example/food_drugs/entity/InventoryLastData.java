package com.example.food_drugs.entity;

import java.util.Date;

public class InventoryLastData {

	private String _id;
	private Double temperature;
	private Double humidity;
	private Long inventory_id;
	private String create_date;
	private Integer is_sent;
	private String inventoryName;
	private String WarehouseName;
	private Long warehouseId;


	
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
	public Integer getIs_sent() {
		return is_sent;
	}
	public void setIs_sent(Integer is_sent) {
		this.is_sent = is_sent;
	}
	public String getInventoryName() {
		return inventoryName;
	}
	public void setInventoryName(String inventoryName) {
		this.inventoryName = inventoryName;
	}
	public InventoryLastData(String _id, Double temperature, Double humidity, Long inventory_id,  String create_date,
			Integer is_sent, String inventoryName) {
		super();
		this._id = _id;
		this.temperature = temperature;
		this.humidity = humidity;
		this.inventory_id = inventory_id;
		this.create_date = create_date;
		this.is_sent = is_sent;
		this.inventoryName = inventoryName;
	}
	public InventoryLastData() {


	}
	
	public InventoryLastData(String _id, Double temperature, Double humidity, Long inventory_id, String create_date,
			Integer is_sent, String inventoryName, String warehouseName, Long warehouseId) {
		super();
		this._id = _id;
		this.temperature = temperature;
		this.humidity = humidity;
		this.inventory_id = inventory_id;
		this.create_date = create_date;
		this.is_sent = is_sent;
		this.inventoryName = inventoryName;
		this.WarehouseName = warehouseName;
		this.warehouseId = warehouseId;

	}
	public String getWarehouseName() {
		return WarehouseName;
	}
	public void setWarehouseName(String warehouseName) {
		WarehouseName = warehouseName;
	}
	public Long getWarehouseId() {
		return warehouseId;
	}
	public void setWarehouseId(Long warehouseId) {
		this.warehouseId = warehouseId;
	}


}