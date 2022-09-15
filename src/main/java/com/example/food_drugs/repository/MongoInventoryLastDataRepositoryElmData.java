package com.example.food_drugs.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import com.example.food_drugs.entity.MonogoInventoryLastDataElmSend;

public interface MongoInventoryLastDataRepositoryElmData  extends MongoRepository<MonogoInventoryLastDataElmSend, String>{

	@Query(value="{ '_id' : { $in: ?0 } }", delete = true)
	public List<MonogoInventoryLastDataElmSend> deleteByIdIn(List<String> invIds);
	
	@Query("{ '_id' : { $exists: true }}")
	public List<MonogoInventoryLastDataElmSend> findByIdsIn(Pageable pageable);

}
