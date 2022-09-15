package com.example.food_drugs.dto.responses;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class KeyAndValueObject {
    String key;
    double value;
}
