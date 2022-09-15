package com.example.food_drugs.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.food_drugs.entity.MongoElmLiveLocationSFDA;

/**
 * Built-in queries related to Mongo collection
 * @author fuinco
 *
 */
@Repository
public interface MongoElmLiveLocationRepositorySFDA extends MongoRepository<MongoElmLiveLocationSFDA,String>{

	@Query(value="{ '_id' : { $in: ?0 } }", delete = true)
	public List<MongoElmLiveLocationSFDA> deleteByIdIn(List<String> positionIds);
	
	@Query("{ '_id' : { $exists: true }}")
	public List<MongoElmLiveLocationSFDA> findByIdsIn(Pageable pageable);
	
}