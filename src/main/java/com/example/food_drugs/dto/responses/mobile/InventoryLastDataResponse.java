package com.example.food_drugs.dto.responses.mobile;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class InventoryLastDataResponse {

    private Long wareHouseId;
    private String inventoryName;
    private String lastUpdated;
    private Double lastTemperature;
    private Double lastHumidity;

}
