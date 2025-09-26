package com.dropmate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dropmate.entity.Payment;
import com.dropmate.enums.PaymentMethod;
import com.dropmate.enums.PaymentTransactionStatus;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    List<Payment> findByBooking_Id(String bookingId);
    List<Payment> findByStatus(PaymentTransactionStatus status);
    List<Payment> findByMethod(PaymentMethod method);
}
