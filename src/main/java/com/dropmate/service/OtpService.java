package com.dropmate.service;

import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dropmate.dto.Email;
import com.dropmate.entity.User;
import com.dropmate.repository.UserRepository;
import com.dropmate.utils.EmailSender;
import com.dropmate.utils.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OtpService {

	private final UserRepository userRepo;
	private final JavaMailSender mailSender;;
	private final PasswordEncoder passwordEncoder;
	// Twilio client can be injected if used

	private final EmailService emailService;

	private static final Duration OTP_VALID_DURATION = Duration.ofMinutes(5);
	private static final int MAX_ATTEMPTS = 5;

	public OtpService(UserRepository userRepo, JavaMailSender mailSender, PasswordEncoder passwordEncoder,
			EmailService emailService) {
		this.userRepo = userRepo;
		this.mailSender = mailSender;
		this.passwordEncoder = passwordEncoder;
		this.emailService = emailService;
	}

	public void generateAndSendOtp(User user, boolean sendSms) throws UnsupportedEncodingException, MessagingException {
		String otp = String.format("%06d", ThreadLocalRandom.current().nextInt(0, 1_000_000));
		String otpHash = passwordEncoder.encode(otp);
		user.setOtpHash(otpHash);
		user.setOtpRequestedAt(Instant.now());
		user.setOtpAttempts(0);
		userRepo.save(user);

		// Send by email
		emailService.sendOtpEmail(user.getEmail(), otp);

		// Or use SMS (Twilio) if sendSms true
		// sendOtpSms(user.getPhone(), otp);
	}

	public boolean validateOtp(User user, String rawOtp) {
		if (user.getOtpHash() == null || user.getOtpRequestedAt() == null)
			return false;
		if (user.getOtpAttempts() >= MAX_ATTEMPTS)
			return false;
		if (user.getOtpRequestedAt().plus(OTP_VALID_DURATION).isBefore(Instant.now()))
			return false;
		boolean matches = passwordEncoder.matches(rawOtp.strip(), user.getOtpHash());
		user.setOtpAttempts(user.getOtpAttempts() + 1);
		if (matches) {
			// clear OTP on success
			user.setOtpHash(null);
			user.setOtpRequestedAt(null);
			user.setOtpAttempts(0);
			userRepo.save(user);
			return true;
		} else {
			userRepo.save(user);
			return false;
		}
	}

	// Example stub for SMS (use Twilio)
	private void sendOtpSms(String phone, String otp) {
		// Twilio code here: Message.creator(new PhoneNumber(phone), new
		// PhoneNumber(from), "OTP: "+otp).create();
	}
}
