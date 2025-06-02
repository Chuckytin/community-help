package com.help.community.service;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Locale;

/**
 * Servicio para interactuar con la API de OpenRouteService y calcular tiempos de viaje.
 */
@Service
public class OpenRouteService {
    @Value("${openroute.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public OpenRouteService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(10))
                .build();
    }


    //TODO: añadir medios de transporte
    public TravelTimeResponse getTravelTime(double fromLat, double fromLon, double toLat, double toLon) {
        try {
            String url = String.format(
                    Locale.US,
                    "https://api.openrouteservice.org/v2/directions/foot-walking?api_key=%s&start=%f,%f&end=%f,%f",
                    apiKey, fromLon, fromLat, toLon, toLat
            );

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JSONObject json = new JSONObject(response.getBody());
            JSONObject summary = json.getJSONArray("features")
                    .getJSONObject(0)
                    .getJSONObject("properties")
                    .getJSONObject("summary");

            return new TravelTimeResponse(
                    summary.getDouble("distance"),
                    summary.getDouble("duration")
            );
        } catch (Exception e) {
            throw new RuntimeException("Error calling OpenRouteService: " + e.getMessage());
        }
    }

    @Getter
    @Setter
    public static class TravelTimeResponse {
        private double distance;
        private double duration;

        public TravelTimeResponse(double distance, double duration) {
            this.distance = distance;
            this.duration = duration;
        }

        public TravelTimeResponse() {
            this(0, 0);
        }
    }
}