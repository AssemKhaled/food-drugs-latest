package com.example.food_drugs.dto.responses;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomDeviceLiveDataResponse {

   int id;
   String deviceName;
   String uniqueId;
   String lastUpdate;
   Boolean expired;
   String positionId;
   String photo;
   String create_date;
   Double temperature;
   Long leftDays;
   Double humidity;
   String attributesSTR;

   Double latitude;
   Double operator;
   Double longitude;
   Object attributes;
   Double power;
   Double speed;
   String status;
   String vehicleStatus;
   Boolean valid;
   Boolean ignition;

   public CustomDeviceLiveDataResponse(int id, String deviceName, String uniqueId, String lastUpdate, Boolean expired, String positionId, String photo, String create_date, Double temperature, Long leftDays, Double humidity, String attributesSTR) {
      this.id = id;
      this.deviceName = deviceName;
      this.uniqueId = uniqueId;
      this.lastUpdate = lastUpdate;
      this.expired = expired;
      this.positionId = positionId;
      this.photo = photo;
      this.create_date = create_date;
      this.temperature = temperature;
      this.leftDays = leftDays;
      this.humidity = humidity;
      this.attributesSTR = attributesSTR;
   }

   //   public CustomDeviceLiveDataResponse(int id,
//                                       String deviceName,
//                                       String uniqueId,
//                                       String lastUpdate,
//                                       String photo,
//                                       String positionId,
//                                       int expired,
//                                       Date leftDays,
//                                       Double temperature,
//                                       Double humidity,
//                                       Date create_date,
//                                       String attributesSTR) {
////     super();
//      this.id = id;
//      this.deviceName = deviceName;
//      this.uniqueId = uniqueId;
//      this.lastUpdate = lastUpdate;
//      this.photo = photo;
//      this.positionId = positionId;
//      this.expired = expired;
//      this.leftDays = leftDays;
//      this.temperature = temperature;
//      this.humidity = humidity;
//      this.create_date = create_date;
//      this.attributesSTR = attributesSTR;
//   }
//   public CustomDeviceLiveDataResponse(String attributesSTR, int id, String deviceName, String uniqueId, String lastUpdate, String photo, String positionId, int expired, Double temperature, Double humidity, Date create_date) {
//      super();
//      this.id = id;
//      this.deviceName = deviceName;
//      this.uniqueId = uniqueId;
//      this.lastUpdate = lastUpdate;
//      this.photo = photo;
//      this.positionId = positionId;
//      this.expired = expired;
//      this.temperature = temperature;
//      this.humidity = humidity;
//      this.create_date = create_date;
//      this.attributesSTR = attributesSTR;
//   }
//   public CustomDeviceLiveDataResponse(Double latitude, Double operator, Double longitude, Object attributes, Double power, Double speed, String status, String vehicleStatus, Boolean valid, Boolean ignition) {
//      super();
//      this.latitude = latitude;
//      this.operator = operator;
//      this.longitude = longitude;
//      this.attributes = attributes;
//      this.power = power;
//      this.speed = speed;
//      this.status = status;
//      this.vehicleStatus = vehicleStatus;
//      this.valid = valid;
//      this.ignition = ignition;
//   }
//
//
//   public CustomDeviceLiveDataResponse(String attributesSTR, int id, String deviceName, String uniqueId, String lastUpdate, String photo, String positionId, int expired, Date leftDays, Double temperature, Double humidity, Date create_date, Double latitude, Double operator, Double longitude, Object attributes, Double power, Double speed, String status, String vehicleStatus, Boolean valid, Boolean ignition) {
//      super();
//      this.attributesSTR = attributesSTR;
//      this.id = id;
//      this.deviceName = deviceName;
//      this.uniqueId = uniqueId;
//      this.lastUpdate = lastUpdate;
//      this.photo = photo;
//      this.positionId = positionId;
//      this.expired = expired;
//      this.leftDays = leftDays;
//      this.temperature = temperature;
//      this.humidity = humidity;
//      this.create_date = create_date;
//      this.latitude = latitude;
//      this.operator = operator;
//      this.longitude = longitude;
//      this.attributes = attributes;
//      this.power = power;
//      this.speed = speed;
//      this.status = status;
//      this.vehicleStatus = vehicleStatus;
//      this.valid = valid;
//      this.ignition = ignition;
//   }
//
//   public CustomDeviceLiveDataResponse() {
//   }
}
