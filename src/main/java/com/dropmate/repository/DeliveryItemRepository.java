package com.dropmate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dropmate.entity.DeliveryItem;

@Repository
public interface DeliveryItemRepository extends JpaRepository<DeliveryItem, String> {
    List<DeliveryItem> findByBooking_Id(String bookingId);
}
