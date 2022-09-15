package com.example.food_drugs.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;
/**
 * @author Assem
 */
@Getter
@Setter
@Builder
public class ApiException<T> {
    private final String message;
    private final HttpStatus httpStatus;
    private final T body;
    private final ZonedDateTime zonedDateTime;

    public ApiException(String message, HttpStatus httpStatus, T body, ZonedDateTime zonedDateTime) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.body = body;
        this.zonedDateTime = zonedDateTime;
    }

}