package com.example.food_drugs.dto;

import lombok.*;

/**
 * @author Assem
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class AuditGObject<T> {
    private T entity;
}
