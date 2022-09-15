package com.example.food_drugs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import com.example.food_drugs.entity.UserSFDA;


@Component
public interface UserRepositorySFDA extends  JpaRepository<UserSFDA, Long>, QueryDslPredicateExecutor<UserSFDA> {

	@Query(value = "SELECT tc_users.id FROM tc_users where tc_users.email=:email", nativeQuery = true)
	public Long getUserByEmail(@Param("email") String email);
}
