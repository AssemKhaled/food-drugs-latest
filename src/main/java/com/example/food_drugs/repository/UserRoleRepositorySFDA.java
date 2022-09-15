package com.example.food_drugs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import com.example.examplequerydslspringdatajpamaven.entity.UserRole;


/**
 * Queries related to tc_user_roles
 * @author fuinco
 *
 */
@Component
public interface UserRoleRepositorySFDA extends  JpaRepository<UserRole, Long>, QueryDslPredicateExecutor<UserRole>{

	@Query(value = "SELECT * from tc_user_roles where tc_user_roles.userId IN(:userIds) "
			+ " and tc_user_roles.name LIKE %:search% limit :offset,10 ",nativeQuery = true)
	public List<UserRole>getAllRolesCreatedByUserOffsetAll(@Param("userIds")List<Long> userIds,@Param("offset")int offset,@Param("search")String search);
	
	@Query(value = "SELECT * from tc_user_roles where tc_user_roles.userId IN(:userIds) "
			+ " and tc_user_roles.name LIKE %:search% ",nativeQuery = true)
	public List<UserRole>getAllRolesCreatedByUserOffsetAllExport(@Param("userIds")List<Long> userIds,@Param("search")String search);
	
	
	@Query(value = "SELECT count(*) from tc_user_roles where tc_user_roles.userId IN(:userIds) ", nativeQuery = true)
	public Integer getRolesSizeAll(@Param("userIds")List<Long> userIds);
	
	
	@Query(value = "SELECT * from tc_user_roles where tc_user_roles.userId IN(:userIds) AND delete_date IS not NULL"
			+ " and tc_user_roles.name LIKE %:search% limit :offset,10 ",nativeQuery = true)
	public List<UserRole>getAllRolesCreatedByUserOffsetDeactive(@Param("userIds")List<Long> userIds,@Param("offset")int offset,@Param("search")String search);
	
	@Query(value = "SELECT * from tc_user_roles where tc_user_roles.userId IN(:userIds) AND delete_date IS not NULL"
			+ " and tc_user_roles.name LIKE %:search% ",nativeQuery = true)
	public List<UserRole>getAllRolesCreatedByUserOffsetDeactiveExport(@Param("userIds")List<Long> userIds,@Param("search")String search);
	
	
	@Query(value = "SELECT count(*) from tc_user_roles where tc_user_roles.userId IN(:userIds) AND delete_date IS not NULL", nativeQuery = true)
	public Integer getRolesSizeDeactive(@Param("userIds")List<Long> userIds);
	
}
