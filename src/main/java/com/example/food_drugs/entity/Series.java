package com.example.food_drugs.entity;

public class Series {

	String name;
	Double value;
	
	public Series() {
		super();
	}
	public Series(String name, Double value) {
		super();
		this.name = name;
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	
	

	
}
