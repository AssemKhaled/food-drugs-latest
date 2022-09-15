package com.example.examplequerydslspringdatajpamaven.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.example.examplequerydslspringdatajpamaven.entity.MongoEvents;

/**
 * Built-in queries related to Mongo collection
 * @author fuinco
 *
 */
public interface MongoEventsRepository extends MongoRepository<MongoEvents, String>{

	@Query(value="{ '_id' : { $in: ?0 } }", delete = true)
	List<MongoEvents> deleteByIdIn(List<String> positionIds);

	List<MongoEvents> findAllByDeviceidAndServertimeBetweenAndType(Long deviceId, Date start,Date end , String type);

	List<MongoEvents> findAllByDeviceidInAndServertimeBetweenAndTypeOrderByServertimeDesc(List<Long> deviceId, Date start,Date end , String type);

	List<MongoEvents> findAllByDeviceidInAndServertimeBetweenAndTypeOrderByServertimeDesc(List<Long> deviceId, Date start, Date end , String type, Pageable pageable);

	Integer countAllByDeviceidInAndServertimeBetweenAndType(List<Long> deviceId, Date start,Date end , String type);

	List<MongoEvents> findAllByDeviceidInAndServertimeBetweenOrderByServertimeDesc(List<Long> deviceId, Date start,Date end);

	List<MongoEvents> findAllByDeviceidInAndServertimeBetweenOrderByServertimeDesc(List<Long> deviceId, Date start, Date end ,  Pageable pageable);

	Integer countAllByDeviceidInAndServertimeBetween(List<Long> deviceId, Date start,Date end);

}
