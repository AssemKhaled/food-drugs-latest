package com.example.food_drugs.dto.responses;

import lombok.*;
import java.util.*;
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class GraphDataWrapper {
    String name;
    List<GraphObject> series;
}
