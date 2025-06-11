package com.help.community.integration;

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
 * Servicio para interactuar con la API de OpenRouteService y calcular tiempos de viaje y distancias entre ubicaciones.
 */
@Service
public class OpenRouteService {

    @Value("${openroute.api.key}")
    private String apiKey;

    /**
     * Cliente REST para realizar peticiones HTTP
     */
    private final RestTemplate restTemplate;

    /**
     * Constructor que inicializa el RestTemplate con tiempos de espera configurados.
     */
    public OpenRouteService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(10))
                .build();
    }

    /**
     * Modos de transporte soportados por la API
     * Cada modo tiene un radio máximo recomendado.
     */
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

    /**
     * Obtiene el tiempo de viaje y distancia entre dos puntos geográficos.
     * - Valida la distancia.
     * - Llama a la API.
     *
     * @param fromLat Latitud del punto de origen
     * @param fromLon Longitud del punto de origen
     * @param toLat Latitud del punto de destino
     * @param toLon Longitud del punto de destino
     * @param mode Modo de transporte a utilizar
     * @return Objeto TravelTimeResponse con la distancia y duración del viaje
     */
    public TravelTimeResponse getTravelTime(double fromLat, double fromLon,
                                            double toLat, double toLon,
                                            TransportMode mode) {
        try {
            double distance = calculateHaversineDistance(fromLat, fromLon, toLat, toLon);
            if (distance > mode.maxRadius) {
                throw new IllegalArgumentException("Distance exceeds maximum for " + mode);
            }

            String url = buildApiUrl(fromLat, fromLon, toLat, toLon, mode);

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return parseResponse(response.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Error calling OpenRouteService: " + e.getMessage(), e);
        }
    }

    /**
     * Construye la URL para la API de OpenRouteService.
     */
    private String buildApiUrl(double fromLat, double fromLon,
                               double toLat, double toLon,
                               TransportMode mode) {
        return String.format(
                Locale.US,
                "https://api.openrouteservice.org/v2/directions/%s?api_key=%s&start=%f,%f&end=%f,%f",
                mode.apiValue, apiKey, fromLon, fromLat, toLon, toLat
        );
    }

    /**
     * Calcula la distancia entre dos puntos geográficos usando la fórmula de Haversine.
     *
     * @param lat1 Latitud del primer punto
     * @param lon1 Longitud del primer punto
     * @param lat2 Latitud del segundo punto
     * @param lon2 Longitud del segundo punto
     * @return Distancia en metros entre los dos puntos
     */
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

    /**
     * Parsea la respuesta JSON de la API a un objeto TravelTimeResponse.
     */
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

    /**
     * Modelo para representar la respuesta de tiempo de viaje.
     * Contiene la distancia y duración del recorrido.
     */
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