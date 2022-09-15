package com.example.food_drugs.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.food_drugs.entity.Position;

@Repository
public interface PositionMongoSFDARepository extends MongoRepository<Position, String> {
	
//	@Override
	Position findBy_id(ObjectId _id);
	
	List<Position> findAllByDeviceid(long i);
	

	List<Position> findAllByDevicetimeBetweenAndDeviceidOrderByDevicetime(Date from,Date to,long i);
	List<Position> findAllByDeviceidAndDevicetimeBetweenOrderByDevicetimeDesc(long i , Date from, Date to ,Pageable pageable);
	List<Position> findAllByDeviceidAndDevicetimeBetween(long i , Date from, Date to ,Pageable pageable);
	Integer countAllByDeviceidAndDevicetimeBetween( long i ,Date from, Date to);

	Optional<Position> findFirstByDeviceidOrderByServertimeDesc(Long deviceId);




//	public static List<Position> getPositions(){
//		Query query = new Query();
//		query.addCriteria(Criteria.where("name").is("Eric"));
//		List<Position> positions = mongoTemplate.find(query, Position.class);
//	}
//	
}
