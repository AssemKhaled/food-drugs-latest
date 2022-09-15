package com.example.food_drugs.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceTempHum {

	private String id;
	private Long deviceId;
	private String deviceName;
	private String driverName;
	private Long driverId;
	private String servertime;
	private String fixTime;
	private String deviceTime;
	private Object attributes;
	private Double speed;
	private Double weight;
	private Double temperature;
	private Double humidity;
	private Double latitude;
	private Double longitude;
	private String address;

	
	
//	public DeviceTempHum(String id, Long deviceId, String deviceName, String driverName, Long driverId,
//			String servertime, String fixTime, String deviceTime, Object attributes, Double speed, Double weight,
//			Double temperature, Double humidity, Double latitude, Double longitude, String address) {
//		super();
//		this.id = id;
//		this.deviceId = deviceId;
//		this.deviceName = deviceName;
//		this.driverName = driverName;
//		this.driverId = driverId;
//		this.servertime = servertime;
//		this.fixTime = fixTime;
//		this.deviceTime = deviceTime;
//		this.attributes = attributes;
//		this.speed = speed;
//		this.weight = weight;
//		this.temperature = temperature;
//		this.humidity = humidity;
//		this.latitude = latitude;
//		this.longitude = longitude;
//		this.address = address;
//	}
//	public DeviceTempHum() {
//		super();
//	}
//	public DeviceTempHum(String id, Long deviceId, String deviceName, String driverName, String servertime,
//			Object attributes, Double speed, Double weight, Double temperature, Double humidity) {
//		super();
//		this.id = id;
//		this.deviceId = deviceId;
//		this.deviceName = deviceName;
//		this.driverName = driverName;
//		this.servertime = servertime;
//		this.attributes = attributes;
//		this.speed = speed;
//		this.weight = weight;
//		this.temperature = temperature;
//		this.humidity = humidity;
//	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Long getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(Long deviceId) {
		this.deviceId = deviceId;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	public String getServertime() {
		return servertime;
	}
	public void setServertime(String servertime) {
		this.servertime = servertime;
	}
	public Object getAttributes() {
		return attributes;
	}
	public void setAttributes(Object attributes) {
		this.attributes = attributes;
	}
	public Double getSpeed() {
		return speed;
	}
	public void setSpeed(Double speed) {
		this.speed = speed;
	}
	public Double getWeight() {
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
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
	public Long getDriverId() {
		return driverId;
	}
	public void setDriverId(Long driverId) {
		this.driverId = driverId;
	}
	public String getFixTime() {
		return fixTime;
	}
	public void setFixTime(String fixTime) {
		this.fixTime = fixTime;
	}
	public String getDeviceTime() {
		return deviceTime;
	}
	public void setDeviceTime(String deviceTime) {
		this.deviceTime = deviceTime;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	
	
}
