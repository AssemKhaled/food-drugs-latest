package com.example.food_drugs.entity;

public class NotificationAttributes {

	private String key;
	private Double value;
	
	
	
	public NotificationAttributes(String key, Double value) {
		super();
		this.key = key;
		this.value = value;
	}
	
	public NotificationAttributes() {
		super();
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	
	
}
