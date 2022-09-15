package com.example.food_drugs.dto.responses;

import com.example.food_drugs.entity.Warehouse;
import lombok.*;

import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class WarehouseInventoriesListMobileWrapper {
    Warehouse warehouse ;
    List<InventoryWarehouseDataByUserIdsDataWrapper> warehouseInventoiresList ;
}
