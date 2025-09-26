package com.dropmate.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.dropmate.entity.Booking;
import com.dropmate.entity.Payment;
import com.dropmate.enums.PaymentMethod;
import com.dropmate.enums.PaymentStatus;
import com.dropmate.enums.PaymentTransactionStatus;
import com.dropmate.repository.BookingRepository;
import com.dropmate.repository.PaymentRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {

	private final PaymentRepository paymentRepository;
	private final BookingRepository bookingRepository;

	public Payment initiatePayment(String bookingId, BigDecimal amount, PaymentMethod method) {
		Booking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new RuntimeException("Booking not found"));

		Payment payment = new Payment();
		payment.setBooking(booking);
		payment.setAmount(amount);
		payment.setMethod(method);
		payment.setStatus(PaymentTransactionStatus.INITIATED);

		return paymentRepository.save(payment);
	}

	public void updatePaymentStatus(String paymentId, PaymentTransactionStatus status) {
		Payment payment = paymentRepository.findById(paymentId)
				.orElseThrow(() -> new RuntimeException("Payment not found"));

		payment.setStatus(status);
		paymentRepository.save(payment);

		if (status == PaymentTransactionStatus.SUCCESS) {
			Booking booking = payment.getBooking();
			booking.setPaymentStatus(PaymentStatus.PAID);
			bookingRepository.save(booking);
		}
	}

	
	public void refundPayment(String paymentId) {
		Payment payment = paymentRepository.findById(paymentId)
				.orElseThrow(() -> new RuntimeException("Payment not found"));

		payment.setStatus(PaymentTransactionStatus.REFUNDED);
		paymentRepository.save(payment);

		Booking booking = payment.getBooking();
		booking.setPaymentStatus(PaymentStatus.PENDING);
		bookingRepository.save(booking);
	}
}
