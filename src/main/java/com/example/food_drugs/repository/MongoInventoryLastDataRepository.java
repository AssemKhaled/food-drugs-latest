package com.example.food_drugs.repository;

import com.example.food_drugs.entity.MonogInventoryNotification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.*;

import com.example.food_drugs.entity.MonogoInventoryLastData;;
public interface MongoInventoryLastDataRepository extends MongoRepository<MonogoInventoryLastData, String>{

	@Query("{ '_id' : ?0 }")
	MonogoInventoryLastData findById(String lastId);
//	List<MonogoInventoryLastData> findAllByInventory(Long i);
	List<MonogoInventoryLastData> findAllByInventoryIdAndCreateDateBetween(int inventoryId , Date start, Date end);
	List <MonogoInventoryLastData> findFirst20ByInventoryIdOrderByCreateDateDesc(int inventoryId);
	List <MonogoInventoryLastData> findAllByInventoryId(int inventoryId, Pageable topTen);
//	MonogoInventoryLastData findAllby_idIn(String lastDataId);

	Optional<List<MonogoInventoryLastData>> findAllBy_idIn(List<String> ids);

	Optional<MonogoInventoryLastData> findBy_id(String id);

}
