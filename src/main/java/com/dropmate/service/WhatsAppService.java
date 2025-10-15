package com.dropmate.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class WhatsAppService {

    @Value("${whatsapp.api.url}")
    private String apiUrl;

    @Value("${whatsapp.phone.number.id}")
    private String phoneNumberId;

    @Value("${whatsapp.access.token}")
    private String accessToken;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendOtp(String recipientNumber, String otpCode) {
        String url = String.format("%s/%s/messages", apiUrl, phoneNumberId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        // JSON body structure
        Map<String, Object> body = new HashMap<>();
        body.put("messaging_product", "whatsapp");
        body.put("to", recipientNumber);
        body.put("type", "template");

        Map<String, Object> template = new HashMap<>();
        template.put("name", "otp_message"); // your approved template name
        template.put("language", Map.of("code", "en_US"));

        // Pass OTP as a parameter
        template.put("components", List.of(
            Map.of(
                "type", "body",
                "parameters", List.of(
                    Map.of("type", "text", "text", otpCode)
                )
            )
        ));

        body.put("template", template);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            System.out.println("WhatsApp response: " + response.getBody());
        } catch (Exception e) {
            System.err.println("Error sending WhatsApp OTP: " + e.getMessage());
        }
    }
    
    public void sendTemplate(String recipientNumber, String templateName, List<String> parameters) {
        String url = String.format("%s/%s/messages", apiUrl, phoneNumberId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        Map<String, Object> body = new HashMap<>();
        body.put("messaging_product", "whatsapp");
        body.put("to", recipientNumber);
        body.put("type", "template");

        Map<String, Object> template = new HashMap<>();
        template.put("name", templateName);
        template.put("language", Map.of("code", "en_US"));

        // convert parameters to WhatsApp format
        List<Map<String, Object>> paramList = new ArrayList<>();
        for (String p : parameters) {
            paramList.add(Map.of("type", "text", "text", p));
        }

        template.put("components", List.of(
                Map.of("type", "body", "parameters", paramList)
        ));

        body.put("template", template);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(url, entity, String.class);
    }

    
    public String sendAnyTemplate(String to, String templateName, Map<String, String> parameters) {
        String url = String.format("%s/%s/messages", apiUrl, phoneNumberId);

        StringBuilder components = new StringBuilder();
        if (parameters != null && !parameters.isEmpty()) {
            components.append("\"components\": [{\"type\":\"body\",\"parameters\":[");
            parameters.forEach((k, v) -> {
                components.append(String.format("{\"type\":\"text\",\"text\":\"%s\"},", v));
            });
            components.deleteCharAt(components.length() - 1); // remove last comma
            components.append("]}],");
        }

        String body = String.format("""
            {
              "messaging_product": "whatsapp",
              "to": "%s",
              "type": "template",
              "template": {
                "name": "%s",
                "language": { "code": "en_US" },
                %s
                "namespace": ""
              }
            }
            """, to, templateName, components.toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        return response.getBody();
    }
    
    
    public String sendMediaTemplate(String to, String templateName, String mediaType, String mediaId, Map<String, String> parameters) {
        String url = String.format("%s/%s/messages", apiUrl, phoneNumberId);

        StringBuilder components = new StringBuilder();
        if (parameters != null && !parameters.isEmpty()) {
            components.append("\"components\": [{\"type\":\"body\",\"parameters\":[");
            parameters.forEach((k, v) -> components.append(String.format("{\"type\":\"text\",\"text\":\"%s\"},", v)));
            components.deleteCharAt(components.length() - 1);
            components.append("]},");
        } else {
            components.append("\"components\": [{");
        }

        // Add header with media attachment
        components.append(String.format("""
            {"type":"header","parameters":[{"type":"%s","%s":{"id":"%s"}}]}],
            """, mediaType, mediaType, mediaId));

        String body = String.format("""
            {
              "messaging_product": "whatsapp",
              "to": "%s",
              "type": "template",
              "template": {
                "name": "%s",
                "language": { "code": "en_US" },
                %s
                "namespace": ""
              }
            }
            """, to, templateName, components.toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        return response.getBody();
    }

}
