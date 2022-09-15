package com.example.food_drugs.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Assem
 */
@Getter
@Setter
@AllArgsConstructor
public class InventorySummaryDataWrapperResponse {

    Long id;
    String name;
    String lastDataId;
    Long warehouseId;
    String storingCategory;
}
