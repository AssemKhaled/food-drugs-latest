package com.example.food_drugs.dto;

import lombok.*;
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ApiResponse<T> {

    private Integer statusCode;
    private Boolean success;
    private String message;
    private int size;
    private T entity;

}
