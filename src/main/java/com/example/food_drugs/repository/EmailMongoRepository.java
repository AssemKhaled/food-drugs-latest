package com.example.food_drugs.repository;

import com.example.food_drugs.entity.EmailsMongo;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author Assem
 */
public interface EmailMongoRepository extends MongoRepository<EmailsMongo, ObjectId> {


    Optional<List<EmailsMongo>> findAllByIsSent(Boolean isSent , Pageable pageable);
}
