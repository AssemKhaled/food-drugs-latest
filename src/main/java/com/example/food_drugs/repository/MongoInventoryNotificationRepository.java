package com.example.food_drugs.repository;


import com.example.food_drugs.dto.responses.NotificationWrapper;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.food_drugs.entity.MonogInventoryNotification;
import java.util.*;

public interface MongoInventoryNotificationRepository extends MongoRepository<MonogInventoryNotification, String>{

    List<MonogInventoryNotification> findAllByInventoryId(Long inventoryId);

    List<NotificationWrapper> findAllByInventoryIdAndCreatedDateBetween(Long inventoryId , Date start, Date end);

}
