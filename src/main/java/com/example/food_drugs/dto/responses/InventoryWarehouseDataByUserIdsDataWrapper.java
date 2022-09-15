package com.example.food_drugs.dto.responses;

import com.example.food_drugs.entity.MonogoInventoryLastData;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class InventoryWarehouseDataByUserIdsDataWrapper {
    java.lang.Integer userId;
    java.lang.Integer inventoryId;
    java.lang.String wareHouseName;
    java.lang.String inventoryName;
    java.lang.String storingCategory;
    java.lang.String lastUpdate;
    java.lang.String lastDataId;
    java.lang.Integer warehouseId;
    MonogoInventoryLastData lastData;
    GraphDataWrapper graphData ;
}
