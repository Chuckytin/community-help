package com.help.community.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Servicio para interactuar con la API de OpenRouteService y calcular tiempos de viaje.
 */
@Service
public class OpenRouteService {

    @Value("${openroute.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Calcula el tiempo estimado de viaje en segundos entre dos coordenadas usando OpenRouteService.
     *
     * @param fromLat Latitud del origen.
     * @param fromLon Longitud del origen.
     * @param toLat Latitud del destino.
     * @param toLon Longitud del destino.
     * @return Duración del viaje en segundos.
     */
    public long getEstimatedTravelTimeInSeconds(double fromLat, double fromLon, double toLat, double toLon) throws Exception {

        try {

            String url = String.format(
                    "https://api.openrouteservice.org/v2/directions/foot-walking?api_key=%s&start=%f,%f&end=%f,%f",
                    apiKey, fromLon, fromLat, toLon, toLat
            );

            String response = restTemplate.getForObject(url, String.class);

            JSONObject json = new JSONObject(response);
            return json.getJSONArray("features")
                    .getJSONObject(0)
                    .getJSONObject("properties")
                    .getJSONArray("segments")
                    .getJSONObject(0)
                    .getLong("duration");

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

}
