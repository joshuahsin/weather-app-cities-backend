package com.service;

import com.dto.AIRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AIService {
    private static final String ANTHROPIC_URL = "https://api.anthropic.com/v1/messages";
    private static final String MODEL = "claude-haiku-4-5-20251001";

    @Value("${anthropic.api.key}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> getItinerary(AIRequest request) {
        AIRequest.LocationContext loc = request.getContext().getLocation();
        AIRequest.WeatherContext w = request.getContext().getWeather();
        String prompt = buildItineraryPrompt(loc.getLat(), loc.getLon(), w);
        JsonNode result = callClaude(prompt, 4096);
        Map<String, Object> response = new HashMap<>();
        response.put("itinerary", result);
        response.put("model", MODEL);
        response.put("generated_at", Instant.now().toString());
        return response;
    }

    public Map<String, Object> getRecommendations(AIRequest request) {
        AIRequest.LocationContext loc = request.getContext().getLocation();
        AIRequest.WeatherContext w = request.getContext().getWeather();
        String prompt = buildRecommendationsPrompt(loc.getLat(), loc.getLon(), w);
        JsonNode result = callClaude(prompt, 2048);
        Map<String, Object> response = new HashMap<>();
        response.put("recommendations", result);
        response.put("model", MODEL);
        response.put("generated_at", Instant.now().toString());
        return response;
    }

    private JsonNode callClaude(String prompt, int maxTokens) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        headers.set("anthropic-version", "2023-06-01");
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("model", MODEL);
        body.put("max_tokens", maxTokens);
        body.put("messages", List.of(Map.of("role", "user", "content", prompt)));

        ResponseEntity<JsonNode> response = restTemplate.exchange(
            ANTHROPIC_URL, HttpMethod.POST, new HttpEntity<>(body, headers), JsonNode.class
        );

        String text = response.getBody().path("content").get(0).path("text").asText();
        text = text.replaceAll("```json\\n?", "").replaceAll("```\\n?", "").trim();

        try {
            return objectMapper.readTree(text);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("AI returned invalid JSON: " + e.getMessage());
        }
    }

    private String buildItineraryPrompt(double lat, double lon, AIRequest.WeatherContext w) {
        return String.format(
            "Create a detailed, personalized travel itinerary for someone currently at coordinates %f, %f on %s at %s.\n\n"
            + "Current Weather Conditions:\n"
            + "- Temperature: %.0f°F (feels like %.0f°F)\n"
            + "- Weather: %s\n"
            + "- Humidity: %.0f%%\n"
            + "- Cloud Coverage: %.0f%%\n"
            + "- Rain: %.2f in\n"
            + "- Snow: %.2f in\n"
            + "- Wind Speed: %.1f mph\n"
            + "- Visibility: %.1f km\n"
            + "- Sunrise: %s\n"
            + "- Sunset: %s\n\n"
            + "Requirements:\n"
            + "1. Create a full-day itinerary (morning, afternoon, evening, night) appropriate for the current weather\n"
            + "2. Include 8-12 activities/attractions that are weather-appropriate\n"
            + "3. Consider the current time and remaining daylight hours\n"
            + "4. Mix indoor and outdoor activities based on weather conditions\n"
            + "5. Include practical details like travel time, costs, and tips\n"
            + "6. Suggest local restaurants, cafes, and food options\n"
            + "7. Include cultural, historical, and entertainment options\n"
            + "8. Consider accessibility and family-friendly options\n\n"
            + "Return ONLY a valid JSON object with this structure:\n"
            + "{\n"
            + "  \"itinerary_title\": \"string\",\n"
            + "  \"weather_summary\": \"string\",\n"
            + "  \"recommendations\": \"string\",\n"
            + "  \"morning\": [{\"time\": \"string\", \"activity\": \"string\", \"location\": \"string\", \"description\": \"string\", \"duration\": \"string\", \"cost\": \"string\", \"weather_appropriate\": true, \"tips\": \"string\"}],\n"
            + "  \"afternoon\": [...],\n"
            + "  \"evening\": [...],\n"
            + "  \"night\": [...],\n"
            + "  \"total_estimated_cost\": \"string\",\n"
            + "  \"packing_suggestions\": [\"string\"],\n"
            + "  \"emergency_contacts\": [\"string\"]\n"
            + "}\n\n"
            + "Do not include any markdown formatting, code blocks, or additional text. Just the raw JSON object.",
            lat, lon,
            w.getCurrent_day(), w.getCurrent_time(),
            w.getTemperature(), w.getFeels_like(),
            w.getDescription(),
            w.getHumidity(), w.getCloud_coverage(),
            w.getRain(), w.getSnow(),
            w.getWind_speed(), w.getVisibility(),
            w.getSunrise(), w.getSunset()
        );
    }

    private String buildRecommendationsPrompt(double lat, double lon, AIRequest.WeatherContext w) {
        String weatherJson;
        try {
            weatherJson = objectMapper.writeValueAsString(w);
        } catch (JsonProcessingException e) {
            weatherJson = w.toString();
        }
        return String.format(
            "Based on the weather forecast data for today: %s, "
            + "suggest 5-10 appropriate points of interest near %f, %f. "
            + "Consider the full day's weather patterns - morning, afternoon, evening, and night conditions. "
            + "Suggest indoor activities for bad weather periods, outdoor activities for good weather periods, and flexible activities for moderate weather. "
            + "Return ONLY a valid JSON array with: name, type, lat, lng, reason, weather_appropriate, best_time_to_visit. "
            + "Do not include any markdown formatting, code blocks, or additional text. Just the raw JSON array.",
            weatherJson, lat, lon
        );
    }
}
