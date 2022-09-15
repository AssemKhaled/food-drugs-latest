package com.example.food_drugs.dto.responses;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ReceiverDataWrapper {
    Long id;
    String name;
    String IMEI;
}
