package com.example.food_drugs.entity;

import java.util.Date;

import javax.persistence.Id;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "tc_inventory_last_data_live_elm")
public class MonogoInventoryLastDataElmSend {

	@Id
	private ObjectId _id;
	private Double temperature;
	private Double humidity;
	private String activity;
	private String name;
	private Long inventory_id;
	private String referenceKey;
	
	
	public MonogoInventoryLastDataElmSend() {
		super();
	}
	
	public MonogoInventoryLastDataElmSend(ObjectId _id, Double temperature, Double humidity, String activity,
			String name, Long inventory_id, String referenceKey) {
		super();
		this._id = _id;
		this.temperature = temperature;
		this.humidity = humidity;
		this.activity = activity;
		this.name = name;
		this.inventory_id = inventory_id;
		this.referenceKey = referenceKey;
	}
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
	public String getActivity() {
		return activity;
	}
	public void setActivity(String activity) {
		this.activity = activity;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getInventory_id() {
		return inventory_id;
	}
	public void setInventory_id(Long inventory_id) {
		this.inventory_id = inventory_id;
	}
	public String getReferenceKey() {
		return referenceKey;
	}
	public void setReferenceKey(String referenceKey) {
		this.referenceKey = referenceKey;
	}

	
	
}
