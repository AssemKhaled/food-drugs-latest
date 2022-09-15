package com.example.food_drugs.entity;

import com.example.examplequerydslspringdatajpamaven.entity.VehiclePlate;

public class DeviceElmDataSFDA {

	String sequenceNumber;
	Integer plateType;
	String imeiNumber;
	VehiclePlate vehiclePlate;
	String activity;
	String storingCategory;
	
	
	
	
	public DeviceElmDataSFDA() {
		super();
	}
	public DeviceElmDataSFDA(String sequenceNumber, Integer plateType, String imeiNumber, VehiclePlate vehiclePlate,
			String activity, String storingCategory) {
		super();
		this.sequenceNumber = sequenceNumber;
		this.plateType = plateType;
		this.imeiNumber = imeiNumber;
		this.vehiclePlate = vehiclePlate;
		this.activity = activity;
		this.storingCategory = storingCategory;
	}
	
	public String getSequenceNumber() {
		return sequenceNumber;
	}
	public void setSequenceNumber(String sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	public Integer getPlateType() {
		return plateType;
	}
	public void setPlateType(Integer plateType) {
		this.plateType = plateType;
	}
	public String getImeiNumber() {
		return imeiNumber;
	}
	public void setImeiNumber(String imeiNumber) {
		this.imeiNumber = imeiNumber;
	}
	public VehiclePlate getVehiclePlate() {
		return vehiclePlate;
	}
	public void setVehiclePlate(VehiclePlate vehiclePlate) {
		this.vehiclePlate = vehiclePlate;
	}
	public String getActivity() {
		return activity;
	}
	public void setActivity(String activity) {
		this.activity = activity;
	}
	public String getStoringCategory() {
		return storingCategory;
	}
	public void setStoringCategory(String storingCategory) {
		this.storingCategory = storingCategory;
	}
	
	
	
	
	
}
