package com.example.food_drugs.entity;

public class BindData {
	
	
	Double temperature;
	Double humidity; 
	String create_date; 
	String inventoryNumber;
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
	public String getCreate_date() {
		return create_date;
	}
	public void setCreate_date(String create_date) {
		this.create_date = create_date;
	}
	public String getInventoryNumber() {
		return inventoryNumber;
	}
	public void setInventoryNumber(String inventoryNumber) {
		this.inventoryNumber = inventoryNumber;
	}
	public BindData(Double temperature, Double humidity, String create_date, String inventoryNumber) {
		super();
		this.temperature = temperature;
		this.humidity = humidity;
		this.create_date = create_date;
		this.inventoryNumber = inventoryNumber;
	}
	public BindData() {
		super();
	}
	
	
}
