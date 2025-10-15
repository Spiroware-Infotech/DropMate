package com.dropmate.controller;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dropmate.service.WhatsAppService;

@RestController
@RequestMapping("/api/whatsapp")
public class WhatsAppController {

	private final WhatsAppService whatsAppService;

	public WhatsAppController(WhatsAppService whatsAppService) {
		this.whatsAppService = whatsAppService;
	}

	@GetMapping("/send-otp/{phone}")
	public String sendOtp(@PathVariable String phone) {
		String otp = String.format("%06d", new Random().nextInt(999999));
		whatsAppService.sendOtp(phone, otp);
		return "OTP sent to " + phone + " : " + otp;
	}

	@GetMapping("/send/{phone}")
	public String send(@PathVariable String phone) {
		String otp = String.format("%06d", new Random().nextInt(999999));

		// 1️ Send OTP
		whatsAppService.sendTemplate("+919876543210", "otp_message", List.of("654321"));

		// 2️⃣ Order confirmation
		whatsAppService.sendTemplate("+919876543210", "order_confirmation", List.of("12345", "Oct 15"));

		// 3️⃣ Promotional offer
		whatsAppService.sendTemplate("+919876543210", "promo_offer", List.of("20"));

		return "OTP sent to " + phone + " : " + otp;
	}

	@PostMapping("/send")
	public String sendAll(@RequestParam String to,
			@RequestParam String template,
			@RequestBody(required = false) Map<String, String> params) {
		
		return whatsAppService.sendAnyTemplate(to, template, params);
	}
	
	@PostMapping("/send/pdf")
	public String sendPdf(@RequestBody Map<String, Object> payload) {
	    String to = (String) payload.get("to");
	    String template = (String) payload.get("template");
	    String mediaType = (String) payload.get("mediaType");
	    String mediaId = (String) payload.get("mediaId");
	    Map<String, String> params = (Map<String, String>) payload.get("params");
	    return whatsAppService.sendMediaTemplate(to, template, mediaType, mediaId, params);
	}
}
