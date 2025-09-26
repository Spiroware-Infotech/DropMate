package com.dropmate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dropmate.entity.Notification;
import com.dropmate.enums.NotificationChannel;
import com.dropmate.enums.NotificationStatus;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {
	List<Notification> findByUser_UserId(Long userId);
    List<Notification> findByStatus(NotificationStatus status);
    List<Notification> findByChannel(NotificationChannel channel);
}
