package com.example.food_drugs.entity;

import java.util.Date;

import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "tc_inventory_last_data")
public class MonogoInventoryLastData {
	
	@Id
	@JsonIgnore
	private ObjectId _id;
	private Double temperature;
	private Double humidity;
	@Field("inventory_id")
	private Long inventoryId;
	@Field("create_date")
	private Date createDate;
	
	public ObjectId get_id() {
		return _id;
	}
	public void set_id(ObjectId _id) {
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
	public Long getInventoryId() {
		return inventoryId;
	}
	public void setInventoryId(Long inventoryId) {
		this.inventoryId = inventoryId;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	
	
	
	public MonogoInventoryLastData() {

	}
	public MonogoInventoryLastData(ObjectId _id, Double temperature, Double humidity, Long inventoryId, Date createDate) {
		super();
		this._id = _id;
		this.temperature = temperature;
		this.humidity = humidity;
		this.inventoryId = inventoryId;
		this.createDate = createDate;
	}
	
	
}
