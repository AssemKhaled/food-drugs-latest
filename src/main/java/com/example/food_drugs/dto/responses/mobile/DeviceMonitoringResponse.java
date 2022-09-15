package com.example.food_drugs.dto.responses.mobile;

import com.example.food_drugs.dto.StoringCategoryCondition;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class DeviceMonitoringResponse {
    Integer id;
    String deviceName;
    Double lastTemp;
    Double lastHum;
    String lastUpdate;
    StoringCategoryCondition storingCategory;
    Double power;
    Boolean ignition;
    Double speed;
    Long cooler;
    String status;
    Boolean gpsStatus;

}
