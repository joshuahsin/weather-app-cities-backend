package com.app.WeatherCities;

import com.dto.GeolocationResponse;
import com.dto.WeatherResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.service.GeolocationService;
import com.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WeatherCitiesExternalServiceTests {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private WeatherService weatherService;

    @InjectMocks
    private GeolocationService geolocationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(weatherService, "apiKey", "test-key");
        ReflectionTestUtils.setField(geolocationService, "apiKey", "test-key");
    }

    // --- helpers ---

    private JsonNode buildWeatherJson(boolean includeRain, boolean includeSnow, boolean includeGust) {
        ObjectNode root = mapper.createObjectNode();
        root.putObject("coord").put("lat", 37.39).put("lon", -122.08);
        ArrayNode weatherArr = root.putArray("weather");
        weatherArr.addObject()
            .put("id", 800)
            .put("main", "Clear")
            .put("description", "clear sky");
        root.putObject("main")
            .put("temp", 72.5)
            .put("feels_like", 70.1)
            .put("humidity", 60)
            .put("pressure", 1013);
        root.put("visibility", 10000);
        ObjectNode wind = root.putObject("wind")
            .put("speed", 5.5)
            .put("deg", 180);
        if (includeGust) wind.put("gust", 8.2);
        root.putObject("clouds").put("all", 5);
        root.putObject("sys")
            .put("country", "US")
            .put("sunrise", 1609484400L)
            .put("sunset", 1609520400L);
        root.put("name", "Mountain View");
        if (includeRain) root.putObject("rain").put("1h", 0.5);
        if (includeSnow) root.putObject("snow").put("1h", 0.2);
        return root;
    }

    private JsonNode buildForecastJson(int itemCount) {
        ObjectNode root = mapper.createObjectNode();
        ArrayNode list = root.putArray("list");
        for (int i = 0; i < itemCount; i++) {
            list.addObject()
                .put("dt", 1609484400L + (i * 10800L))
                .put("dt_txt", "2021-01-01 0" + i + ":00:00");
        }
        return root;
    }

    private JsonNode buildGeolocationJson() {
        ObjectNode root = mapper.createObjectNode();
        ArrayNode features = root.putArray("features");
        features.addObject()
            .putObject("properties")
            .put("city", "Mountain View")
            .put("state_code", "CA")
            .put("country", "United States");
        return root;
    }

    // --- WeatherService: getCurrentWeather (3 URI vars: lat, lon, apiKey) ---

    @Test
    void testGetCurrentWeather() {
        when(restTemplate.getForObject(anyString(), eq(JsonNode.class), any(), any(), any()))
            .thenReturn(buildWeatherJson(false, false, true));

        WeatherResponse r = weatherService.getCurrentWeather(37.39, -122.08);

        assertThat(r.getLocation().getLat()).isEqualTo(37.39);
        assertThat(r.getLocation().getLon()).isEqualTo(-122.08);
        assertThat(r.getLocation().getName()).isEqualTo("Mountain View");
        assertThat(r.getLocation().getCountry()).isEqualTo("US");
        assertThat(r.getWeather().getTemp()).isEqualTo(73);          // Math.round(72.5)
        assertThat(r.getWeather().getFeels_like()).isEqualTo(70);    // Math.round(70.1)
        assertThat(r.getWeather().getHumidity()).isEqualTo(60);
        assertThat(r.getWeather().getPressure()).isEqualTo(1013);
        assertThat(r.getWeather().getDescription()).isEqualTo("clear sky");
        assertThat(r.getWeather().getMain()).isEqualTo("Clear");
        assertThat(r.getWeather().getVisibility()).isEqualTo(10.0);  // 10000 / 1000
        assertThat(r.getWeather().getCloud_coverage()).isEqualTo(5);
        assertThat(r.getWeather().getRain()).isEqualTo(0.0);
        assertThat(r.getWeather().getSnow()).isEqualTo(0.0);
        assertThat(r.getWind().getSpeed()).isEqualTo(5.5);
        assertThat(r.getWind().getDeg()).isEqualTo(180.0);
        assertThat(r.getWind().getGust()).isEqualTo("8.2");
        assertThat(r.getTime()).isNotNull();
        assertThat(r.getTime().getSunrise()).isNotBlank();
        assertThat(r.getTime().getSunset()).isNotBlank();
        assertThat(r.getTime().getTimestamp()).isNotBlank();
    }

    @Test
    void testGetCurrentWeatherWithRain() {
        when(restTemplate.getForObject(anyString(), eq(JsonNode.class), any(), any(), any()))
            .thenReturn(buildWeatherJson(true, false, true));

        WeatherResponse r = weatherService.getCurrentWeather(37.39, -122.08);

        assertThat(r.getWeather().getRain()).isEqualTo(0.5);
        assertThat(r.getWeather().getSnow()).isEqualTo(0.0);
    }

    @Test
    void testGetCurrentWeatherWithSnow() {
        when(restTemplate.getForObject(anyString(), eq(JsonNode.class), any(), any(), any()))
            .thenReturn(buildWeatherJson(false, true, true));

        WeatherResponse r = weatherService.getCurrentWeather(37.39, -122.08);

        assertThat(r.getWeather().getRain()).isEqualTo(0.0);
        assertThat(r.getWeather().getSnow()).isEqualTo(0.2);
    }

    @Test
    void testGetCurrentWeatherNoGust() {
        when(restTemplate.getForObject(anyString(), eq(JsonNode.class), any(), any(), any()))
            .thenReturn(buildWeatherJson(false, false, false));

        WeatherResponse r = weatherService.getCurrentWeather(37.39, -122.08);

        assertThat(r.getWind().getGust()).isEqualTo("N/A");
    }

    @Test
    void testGetForecast() {
        when(restTemplate.getForObject(anyString(), eq(JsonNode.class), any(), any(), any()))
            .thenReturn(buildForecastJson(3));

        Map<String, Object> result = weatherService.getForecast(37.39, -122.08);

        assertThat(result).containsKey("forecast");
        assertThat(result).containsKey("generated_at");
        assertThat(((JsonNode) result.get("forecast")).size()).isEqualTo(3);
    }

    // --- WeatherService: by-city calls (2 URI vars: q, apiKey) ---

    @Test
    void testGetCurrentWeatherByCity() {
        when(restTemplate.getForObject(anyString(), eq(JsonNode.class), any(), any()))
            .thenReturn(buildWeatherJson(false, false, true));

        WeatherResponse r = weatherService.getCurrentWeatherByCity("Mountain View", "CA", "USA");

        assertThat(r.getLocation().getName()).isEqualTo("Mountain View");
        assertThat(r.getWeather().getTemp()).isEqualTo(73);
        assertThat(r.getWeather().getDescription()).isEqualTo("clear sky");
        assertThat(r.getTime()).isNull(); // city endpoint omits time
    }

    @Test
    void testGetForecastByCity() {
        when(restTemplate.getForObject(anyString(), eq(JsonNode.class), any(), any()))
            .thenReturn(buildForecastJson(5));

        Map<String, Object> result = weatherService.getForecastByCity("Mountain View", "CA", "USA");

        assertThat(result).containsKey("forecast");
        assertThat(result).containsKey("generated_at");
        assertThat(((JsonNode) result.get("forecast")).size()).isEqualTo(5);
    }

    // --- GeolocationService (3 URI vars: lat, lon, apiKey) ---

    @Test
    void testLocate() {
        when(restTemplate.getForObject(anyString(), eq(JsonNode.class), any(), any(), any()))
            .thenReturn(buildGeolocationJson());

        GeolocationResponse r = geolocationService.locate(37.39, -122.08);

        assertThat(r.getCity()).isEqualTo("Mountain View");
        assertThat(r.getState()).isEqualTo("CA");
        assertThat(r.getCountry()).isEqualTo("United States");
    }
}
