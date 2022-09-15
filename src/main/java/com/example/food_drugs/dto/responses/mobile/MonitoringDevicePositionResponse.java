package com.example.food_drugs.dto.responses.mobile;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class MonitoringDevicePositionResponse {
    String deviceName;
    String driverName;
    String lastupdate;
    String sequence_num;
    String trackerimei;
    String mobile_num;
    Double lasttemp;
    Double lasthum;
    Double speed;
    Double temperature ;
    Double humidity ;
    Double latitude ;
    Double longitude ;
    String serverTime ;
    String status ;
    Double power;
    Boolean ignition;
    Long cooler;
    Boolean gpsStatus;
}
