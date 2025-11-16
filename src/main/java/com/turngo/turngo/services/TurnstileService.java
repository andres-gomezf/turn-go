package com.turngo.turngo.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class TurnstileService {

    @Value("${cloudflare.turnstile.secretKey}")
    private String secretKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean validateToken(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }

        String url = "https://challenges.cloudflare.com/turnstile/v0/siteverify";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("secret", secretKey);
        body.add("response", token);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        Map<?, ?> response = restTemplate.postForObject(url, requestEntity, Map.class);

        if (response == null) {
            return false;
        }

        Object success = response.get("success");
        return success instanceof Boolean && (Boolean) success;
    }
}



