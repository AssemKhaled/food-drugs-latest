package com.example.food_drugs.dto.responses;

import lombok.*;
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder

public class DeviceResponseDataWrapper {
    java.lang.Integer id ;
    java.lang.String deviceName;
    java.lang.Double lastTemp;
    java.lang.Double lastHum;
    java.lang.String lastUpdate;
    java.lang.String storingCategory;
    GraphDataWrapper graphData ;
}



