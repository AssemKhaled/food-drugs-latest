package com.example.food_drugs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import com.example.examplequerydslspringdatajpamaven.entity.Notification;

/**
 * Queries on table tc_notifications with SFDA
 * @author fuinco
 *
 */
@Component
public interface NotificationRepositorySFDA extends  JpaRepository<Notification, Long>, QueryDslPredicateExecutor<Notification> {

	@Query(value = "SELECT tc_notifications.* FROM tc_notifications INNER JOIN tc_user_notification ON tc_user_notification.notificationid = tc_notifications.id"
			+ " WHERE tc_user_notification.userid IN(:userIds)  "
			+ " and ((tc_notifications.type Like %:search%) )" + 
			" LIMIT :offset,10 ", nativeQuery = true)
	public List<Notification> getAllNotificationsAll(@Param("userIds")List<Long> userIds,@Param("offset") int offset,@Param("search") String search);
	
	@Query(value = "SELECT tc_notifications.* FROM tc_notifications INNER JOIN tc_user_notification ON tc_user_notification.notificationid = tc_notifications.id"
			+ " WHERE tc_user_notification.userid IN(:userIds)  "
			+ " and ((tc_notifications.type Like %:search%) )" , nativeQuery = true)
	public List<Notification> getAllNotificationsAllExport(@Param("userIds")List<Long> userIds,@Param("search") String search);
	
	
	@Query(value = "SELECT count(*) FROM tc_notifications INNER JOIN tc_user_notification ON tc_user_notification.notificationid = tc_notifications.id "
	+  " WHERE tc_user_notification.userid IN(:userIds)  ", nativeQuery = true)
	public Integer getAllNotificationsSizeAll(@Param("userIds")List<Long> userIds);
		

	@Query(value = "SELECT tc_notifications.* FROM tc_notifications INNER JOIN tc_user_notification ON tc_user_notification.notificationid = tc_notifications.id"
			+ " WHERE tc_user_notification.userid IN(:userIds) and  tc_notifications.delete_date is not null"
			+ " and ((tc_notifications.type Like %:search%) )" + 
			" LIMIT :offset,10 ", nativeQuery = true)
	public List<Notification> getAllNotificationsDeactive(@Param("userIds")List<Long> userIds,@Param("offset") int offset,@Param("search") String search);
	
	@Query(value = "SELECT tc_notifications.* FROM tc_notifications INNER JOIN tc_user_notification ON tc_user_notification.notificationid = tc_notifications.id"
			+ " WHERE tc_user_notification.userid IN(:userIds) and  tc_notifications.delete_date is not null"
			+ " and ((tc_notifications.type Like %:search%) )" , nativeQuery = true)
	public List<Notification> getAllNotificationsDeactiveExport(@Param("userIds")List<Long> userIds,@Param("search") String search);
	
	@Query(value = "SELECT count(*) FROM tc_notifications INNER JOIN tc_user_notification ON tc_user_notification.notificationid = tc_notifications.id "
	+  " WHERE tc_user_notification.userid IN(:userIds) and  tc_notifications.delete_date is not null", nativeQuery = true)
	public Integer getAllNotificationsSizeDeactive(@Param("userIds")List<Long> userIds);
		

}
