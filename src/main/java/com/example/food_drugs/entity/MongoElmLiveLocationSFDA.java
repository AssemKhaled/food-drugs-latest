package com.example.food_drugs.entity;

import com.example.examplequerydslspringdatajpamaven.entity.MongoElmLiveLocation;

public class MongoElmLiveLocationSFDA extends MongoElmLiveLocation {


	private String temperature;
	private String humidity;

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	public String getHumidity() {
		return humidity;
	}

	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}


	
	
}
