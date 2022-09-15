package com.example.food_drugs.dto.responses;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Bean;

/**
 * @author Assem
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
public class allInventoriesLastInfoResponse {

    private Double temperature;
    private Double humidity ;
    private Long inventoryId ;
    private String createDate ;
    private String inventoryName ;
    private ObjectId _id;
    private String wareHouseName;
    private String storingCategory;
    private Long leftDays;
}
