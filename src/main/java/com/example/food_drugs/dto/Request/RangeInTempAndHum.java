package com.example.food_drugs.dto.Request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * @author Assem
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RangeInTempAndHum {

    private String storingCategory;
    private Double minTemp;
    private Double maxTemp;
    private Double minHum;
    private Double maxHum;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean inRangeTemp;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean inRangeHum;


}
