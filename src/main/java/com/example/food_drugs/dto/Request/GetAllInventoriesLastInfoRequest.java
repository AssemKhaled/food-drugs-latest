package com.example.food_drugs.dto.Request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class GetAllInventoriesLastInfoRequest {
    Long userId ;
    String search ;
    int offset ;
}
