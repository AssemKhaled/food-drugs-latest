package com.example.food_drugs.dto.responses;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder

public class InventorySamWrapper {
    private Long id;
    private String name;
    private String lastDataId;
}
