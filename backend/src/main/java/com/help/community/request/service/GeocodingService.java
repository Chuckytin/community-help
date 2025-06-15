package com.help.community.request.service;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Map;

@Service
public class GeocodingService {

    private final RestTemplate restTemplate;

    public GeocodingService(RestTemplateBuilder builder) {
        this.restTemplate = builder
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(10))
                .defaultHeader("User-Agent", "HelpCommunityApp/1.0")
                .build();
    }

    @Cacheable("geocoding")
    public double[] getCoordinates(String city, String postalCode) {
        String query = postalCode != null ? postalCode + ", " + city : city;
        String url = "https://nominatim.openstreetmap.org/search?format=json&q=" + query;

        ResponseEntity<Map[]> response = restTemplate.getForEntity(url, Map[].class);
        if (response.getBody() == null || response.getBody().length == 0) {
            throw new IllegalArgumentException("Ubicación no encontrada");
        }

        Map<String, String> firstResult = response.getBody()[0];
        return new double[]{
                Double.parseDouble(firstResult.get("lat")),
                Double.parseDouble(firstResult.get("lon"))
        };
    }
}
