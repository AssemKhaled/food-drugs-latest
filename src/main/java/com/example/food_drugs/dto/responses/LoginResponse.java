package com.example.food_drugs.dto.responses;

import com.example.examplequerydslspringdatajpamaven.entity.UserRole;
import lombok.*;

import java.util.List;

/**
 * @author Assem
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LoginResponse {

    private Long userId;
    private String name;
    private String email;
    private String photo;
    private Integer accountType;
    private Long leftDays;
    private String token;
    private UserRole userRole;


}
