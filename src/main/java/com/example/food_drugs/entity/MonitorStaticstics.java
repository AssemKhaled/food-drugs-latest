package com.example.food_drugs.entity;

import java.util.List;

public class MonitorStaticstics {

	String name;
	List<Series> series;
	
	
	
	public MonitorStaticstics() {
		super();
	}
	public MonitorStaticstics(String name, List<Series> series) {
		super();
		this.name = name;
		this.series = series;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Series> getSeries() {
		return series;
	}
	public void setSeries(List<Series> series) {
		this.series = series;
	}
	
	
	
}
