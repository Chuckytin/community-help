package com.help.community.request.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.naming.ServiceUnavailableException;
import java.time.Duration;
import java.util.Map;

@Service
public class GeocodingService {

    private static final Logger logger = LoggerFactory.getLogger(GeocodingService.class);

    private final RestTemplate restTemplate;

    public GeocodingService(RestTemplateBuilder builder) {
        this.restTemplate = builder
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(10))
                .defaultHeader("User-Agent", "HelpCommunityApp/1.0")
                .build();
    }

    @Cacheable(value = "geocoding", unless = "#result == null")
    public double[] getCoordinates(String city) throws ServiceUnavailableException {
        try {
            String url = "https://nominatim.openstreetmap.org/search?format=json&q=" + city + ", España";

            ResponseEntity<Map[]> response = restTemplate.getForEntity(url, Map[].class);
            if (response.getBody() == null || response.getBody().length == 0) {
                throw new IllegalArgumentException("Ciudad no encontrada");
            }

            Map<String, String> firstResult = response.getBody()[0];
            return new double[]{
                    Double.parseDouble(firstResult.get("lat")),
                    Double.parseDouble(firstResult.get("lon"))
            };
        } catch (RestClientException e) {
            logger.error("Error calling Nominatim API", e);
            throw new ServiceUnavailableException("Servicio de geocodificación no disponible");
        }
    }

    /**
     * Verifica si un municipio existe usando la API de Nominatim.
     *
     * @param municipality Nombre del municipio (ej. "Madrid")
     * @return true si la ciudad existe, false en caso contrario
     */
    public boolean existsMunicipality(String municipality) {
        try {
            String url = "https://nominatim.openstreetmap.org/search?format=json&q=" + municipality + ", España";

            ResponseEntity<Map[]> response = restTemplate.getForEntity(url, Map[].class);

            return response.getBody() != null && response.getBody().length > 0;
        } catch (Exception e) {
            return false;
        }
    }

}
