package com.example.food_drugs.dto.responses;

import lombok.*;

/**
 * @author Assem
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class NewInventoryNotificationResponse {
    private String _id;
    private Double value;
    private Long inventory_id;
    private String create_date;
    private String message;
    private String inventoryName;
    private Long warehouseId;
    private String warehouseName;
    private Object attributes;
    private String type;
}
