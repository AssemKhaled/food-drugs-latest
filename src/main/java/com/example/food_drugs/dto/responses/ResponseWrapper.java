package com.example.food_drugs.dto.responses;

import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseWrapper<T> {
    Boolean success;
    String message;
    T body;
    int size;
}
