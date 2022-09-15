package com.example.food_drugs.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoringCategoryCondition {
    String temperatureCondition;
    String humidityContition;
    double minTemperature;
    double minHumidity;
    double maxTemperature;
    double maxHumidity;
}
