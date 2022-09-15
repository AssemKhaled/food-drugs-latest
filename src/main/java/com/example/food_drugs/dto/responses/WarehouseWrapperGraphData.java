package com.example.food_drugs.dto.responses;
import lombok.*;

import java.util.*;
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class WarehouseWrapperGraphData {
    String warehouseName;
    List<InventoryWarehouseDataByUserIdsDataWrapper> inventories ;
}
