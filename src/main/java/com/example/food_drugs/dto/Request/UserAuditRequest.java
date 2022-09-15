package com.example.food_drugs.dto.Request;

import lombok.*;

import java.util.List;

/**
 * @author Assem
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class UserAuditRequest {

    private List<Long> deviceIds;
    private List<Long> driverIds;
    private List<Long> inventoryIds;
    private List<Long> wareHouseIds;
}
