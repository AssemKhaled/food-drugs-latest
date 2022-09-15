package com.example.food_drugs.entity;

public class TripDetailsRequest {
	
//	public TripDetailsRequest(Long vehilceId, String startTime, String endTime) {
//		super();
//		this.vehilceId = vehilceId;
//		this.startTime = startTime;
//		this.endTime = endTime;
//	}
	private Long vehilceId;
	private String startTime;
	private String endTime;
	
	public Long getVehilceId() {
		return vehilceId;
	}
	public void setVehilceId(Long vehilceId) {
		this.vehilceId = vehilceId;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	
}
