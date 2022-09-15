package com.example.food_drugs.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
public class ApiResponseBuilder<T> {
    private Integer statusCode;
    private Boolean success;
    private String message;
    private int size;
    private T entity;

    public ApiResponse<T> build(){
        return new ApiResponse<T>(this.statusCode,this.success,this.message,this.size,this.entity);
    }
}
