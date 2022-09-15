package com.example.food_drugs.dto.responses;

import lombok.*;
import org.bson.types.ObjectId;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class MongoInventoryWrapper {

   private Double temperature;

   private Double humidity ;

   private Long inventoryId ;

   private String createDate ;

   private String inventoryName ;

   private ObjectId _id;



}
