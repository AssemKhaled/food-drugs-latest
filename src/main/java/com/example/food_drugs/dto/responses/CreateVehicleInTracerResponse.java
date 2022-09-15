package com.example.food_drugs.dto.responses;

import lombok.*;

import java.util.List;

/**
 * @author Assem
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class CreateVehicleInTracerResponse {

    private Long id;
    private String name;
    private String uniqueId;
    private String status;
    private Boolean  disabled;
    private String  lastUpdate;
    private int positionId;
    private int groupId;
    private String phone;
    private String  model;
    private String  contact;
    private String  string;
    private List<Integer> geofenceIds;
    private Object attributes;

}
