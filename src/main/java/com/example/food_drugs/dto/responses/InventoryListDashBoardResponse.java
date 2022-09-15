package com.example.food_drugs.dto.responses;

import com.example.food_drugs.dto.Request.RangeInTempAndHum;
import lombok.*;

/**
 * @author Assem
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class InventoryListDashBoardResponse {

    private String inventoryName ;
    private String wareHouseName;
    private String lastUpdate;
    private Double temp;
    private Double humidity;
    private RangeInTempAndHum storingCategory;
    private Long leftDays;
}

