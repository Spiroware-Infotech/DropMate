// GoogleMapsService.java
package com.dropmate.map;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import reactor.core.publisher.Mono;

@Service
public class GoogleMapsService {
    
    @Value("${serverkey.google.maps.api.key}")
    private String apiKey;
    
    @Value("${google.maps.api.url}")
    private String apiUrl;
    
    private final WebClient webClient;
    
    public GoogleMapsService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }
    
    public Mono<PlacesResponse> searchPlaces(SearchRequest searchRequest) {
        String url = UriComponentsBuilder.fromHttpUrl(apiUrl + "/textsearch/json")
                .queryParam("query", searchRequest.getQuery())
                .queryParam("key", apiKey)
                .queryParamIfPresent("location", java.util.Optional.ofNullable(searchRequest.getLocation()))
                .queryParamIfPresent("radius", java.util.Optional.ofNullable(searchRequest.getRadius()))
                .queryParamIfPresent("type", java.util.Optional.ofNullable(searchRequest.getType()))
                .toUriString();
        
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(PlacesResponse.class)
                .doOnSuccess(response -> {
                    if (!"OK".equals(response.getStatus())) {
                        throw new RuntimeException("Google Places API error: " + response.getStatus() + 
                                " - " + response.getErrorMessage());
                    }
                });
    }
    
    public Mono<PlacesResponse> searchNearby(String latitude, String longitude, Integer radius, String type) {
        String location = latitude + "," + longitude;
        
        String url = UriComponentsBuilder.fromHttpUrl(apiUrl + "/nearbysearch/json")
                .queryParam("location", location)
                .queryParam("key", apiKey)
                .queryParamIfPresent("radius", java.util.Optional.ofNullable(radius))
                .queryParamIfPresent("type", java.util.Optional.ofNullable(type))
                .toUriString();
        
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(PlacesResponse.class);
    }
    
   

}