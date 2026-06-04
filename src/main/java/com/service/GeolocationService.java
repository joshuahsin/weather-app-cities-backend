package com.service;

import com.dto.GeolocationResponse;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeolocationService {
    private static final String BASE_URL = "https://api.geoapify.com/v1/geocode/reverse";

    @Value("${geoapify.api.key}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    public GeolocationResponse locate(double lat, double lon) {
        String url = BASE_URL + "?lat={lat}&lon={lon}&apiKey={key}";
        JsonNode data = restTemplate.getForObject(url, JsonNode.class, lat, lon, apiKey);

        JsonNode props = data.path("features").get(0).path("properties");
        GeolocationResponse response = new GeolocationResponse();
        response.setCity(props.path("city").asText());
        response.setState(props.path("state_code").asText());
        response.setCountry(props.path("country").asText());
        return response;
    }
}
