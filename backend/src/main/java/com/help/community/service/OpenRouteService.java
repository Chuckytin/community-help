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

    public enum TransportMode {
        FOOT_WALKING("foot-walking", 5000),  // Radio máximo recomendado: 5km
        DRIVING_CAR("driving-car", 500000),   // Radio máximo: 500km
        CYCLING("cycling-regular", 20000);   // Radio máximo: 20km

        private final String apiValue;
        @Getter
        private final int maxRadius;

        TransportMode(String apiValue, int maxRadius) {
            this.apiValue = apiValue;
            this.maxRadius = maxRadius;
        }


    }

    public TravelTimeResponse getTravelTime(double fromLat, double fromLon,
                                            double toLat, double toLon,
                                            TransportMode mode) {
        try {
            // 1. Validación de distancia
            double distance = calculateHaversineDistance(fromLat, fromLon, toLat, toLon);
            if (distance > mode.maxRadius) {
                throw new IllegalArgumentException("Distance exceeds maximum for " + mode);
            }

            // 2. Llamada a la API
            String url = String.format(
                    Locale.US,
                    "https://api.openrouteservice.org/v2/directions/%s?api_key=%s&start=%f,%f&end=%f,%f",
                    mode.apiValue, apiKey, fromLon, fromLat, toLon, toLat
            );

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return parseResponse(response.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Error calling OpenRouteService: " + e.getMessage(), e);
        }
    }

    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radio de la Tierra en km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c * 1000;
    }

    private TravelTimeResponse parseResponse(String jsonResponse) {
        JSONObject json = new JSONObject(jsonResponse);
        JSONObject summary = json.getJSONArray("features")
                .getJSONObject(0)
                .getJSONObject("properties")
                .getJSONObject("summary");

        return new TravelTimeResponse(
                summary.getDouble("distance"),
                summary.getDouble("duration")
        );
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