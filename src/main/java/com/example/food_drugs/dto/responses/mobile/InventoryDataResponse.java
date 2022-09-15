package com.example.food_drugs.dto.responses.mobile;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class InventoryDataResponse {
    String inventoryName;
    String inventoryNumber;
    String inventoryStoringCategory;
    Double lastTemp;
    Double lastHum;
    String lastUpDate;
    String assignWarehouseName;
}
