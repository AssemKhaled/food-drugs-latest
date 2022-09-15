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
public class VehicleListDashBoardResponse {

    private String vehicleName;
    private String lastUpdate;
    private Double temp;
    private Double humidity;
    private RangeInTempAndHum storingCategory;
    private Integer cooler;
    private Boolean ignition;
    private Boolean valid;
    private Long leftDays;

}
