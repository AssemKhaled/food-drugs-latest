package com.example.food_drugs.dto.responses.mobile;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class WareHouseInvLastDataResponse {

    private String wareHouseName;
    private List<InventoryLastDataResponse> inventoryData ;

}
