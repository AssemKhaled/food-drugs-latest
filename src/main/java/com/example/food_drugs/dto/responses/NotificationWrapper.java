package com.example.food_drugs.dto.responses;

import com.example.food_drugs.entity.NotificationAttributes;
import lombok.*;
import org.bson.types.ObjectId;

import java.util.Date;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationWrapper {
    private ObjectId _id;
    private String type;
    private Long inventoryId;
    private Date createdDate;
    private NotificationAttributes attributes;
}
