package com.dropmate.utils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dropmate.dto.Email;
import com.dropmate.entity.User;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailService {

	@Autowired
	private EmailSender emailService;

	/**
	 * Sending Welcome Mail
	 * 
	 * @param user
	 * @param request
	 */
	public void sendWelcomeEmail(User user, HttpServletRequest request) {
		log.info("Welcome mail started !: {} ", user.getUsername());
		try {
			Map<String, Object> model = new HashMap<>();
			model.put("name", user.getFirstname());
			String siteURL = Utility.getSiteUrl(request);
			model.put("link", siteURL);
			String subject = "Welcome to DropMate, " + user.getFirstname() + "!";
			// emailService.sendHtmlEmail(user.getEmail(), subject, "welcome", model);

		} catch (Exception e) {
			log.error("Welcome mail sending fail!: {} ", e);
		}
		log.info("Welcome mail successfully Sent!....");
	}

	public void sendVerificationEmail(User user, String siteURL) {
		log.info("Verification mail started !: {} ", user.getUsername());
		try {
			Map<String, Object> model = new HashMap<>();
			model.put("name", user.getFirstname());
			model.put("link", siteURL);

			String subject = "Account Activation!";
			Email email = new Email();
			email.setTo(Arrays.asList(user.getEmail()));
			email.setSubject(subject);

			emailService.sendHtmlEmail(user.getEmail(), subject, "accountverify", model);

			// emailService.sendMail(email, "accountverify",user,siteURL);

		} catch (Exception e) {
			log.error("Verification mail sending fail!: {} ", e);
		}
		log.info("Verification mail successfully Sent!....");

	}

	
	public void sendOtpEmail(String to, String otp) {
		log.info("OTP mail started !: {} ", to);
		try {
			Map<String, Object> model = new HashMap<>();
			model.put("otp", otp);

			String subject = "DropMate verification OTP code!";
			Email email = new Email();
			email.setTo(Arrays.asList(to));
			email.setSubject(subject);
			
			emailService.sendHtmlEmail(to, subject, "otpMail", model);
			log.info("OTP mail sent !: {} ", to);
		} catch (Exception e) {
			log.error("OTP mail sending fail!: {} ", e);
		}
		log.info("Verification mail successfully Sent!....");

	}

}
