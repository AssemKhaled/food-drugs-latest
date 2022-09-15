package com.example.food_drugs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.example.examplequerydslspringdatajpamaven.entity.Schedule;

/**
 * Queries realated to tc_schedule
 * @author fuinco
 *
 */
@Service
public interface ScheduledRepositorySFDA extends JpaRepository<Schedule, Long>, QueryDslPredicateExecutor<Schedule>{

	@Query(value = "SELECT tc_schedule.* FROM tc_schedule"
			+ " WHERE tc_schedule.userId IN(:userIds) "
			+ " and ( (tc_schedule.date_type Like %:search%) ) " + 
			" LIMIT :offset,10 ", nativeQuery = true)
	public List<Schedule> getAllScheduledAll(@Param("userIds")List<Long> userIds,@Param("offset") int offset,@Param("search") String search);
	
	@Query(value = "SELECT tc_schedule.* FROM tc_schedule"
			+ " WHERE tc_schedule.userId IN(:userIds) "
			+ " and ( (tc_schedule.date_type Like %:search%) ) " , nativeQuery = true)
	public List<Schedule> getAllScheduledAllExport(@Param("userIds")List<Long> userIds,@Param("search") String search);
	
	
	@Query(value = "SELECT count(*) FROM tc_schedule  " + 
			"  WHERE tc_schedule.userId IN(:userIds) ", nativeQuery = true)
	public Integer getAllScheduledSizeAll(@Param("userIds")List<Long> userIds);
	
	
	@Query(value = "SELECT tc_schedule.* FROM tc_schedule"
			+ " WHERE tc_schedule.userId IN(:userIds) and tc_schedule.delete_date is not null"
			+ " and ( (tc_schedule.date_type Like %:search%) ) " + 
			" LIMIT :offset,10 ", nativeQuery = true)
	public List<Schedule> getAllScheduledDeactive(@Param("userIds")List<Long> userIds,@Param("offset") int offset,@Param("search") String search);
	
	@Query(value = "SELECT tc_schedule.* FROM tc_schedule"
			+ " WHERE tc_schedule.userId IN(:userIds) and tc_schedule.delete_date is not null"
			+ " and ( (tc_schedule.date_type Like %:search%) ) " , nativeQuery = true)
	public List<Schedule> getAllScheduledDeactiveExport(@Param("userIds")List<Long> userIds,@Param("search") String search);
	
	@Query(value = "SELECT count(*) FROM tc_schedule  " + 
			"  WHERE tc_schedule.userId IN(:userIds) and tc_schedule.delete_date is not null", nativeQuery = true)
	public Integer getAllScheduledSizeDeactive(@Param("userIds")List<Long> userIds);
	
}
