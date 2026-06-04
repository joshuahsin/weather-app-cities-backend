package com.service;

import com.dto.WeatherResponse;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class WeatherService {
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5";

    @Value("${openweather.api.key}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    public WeatherResponse getCurrentWeather(double lat, double lon) {
        String url = BASE_URL + "/weather?lat={lat}&lon={lon}&appid={key}&units=imperial";
        JsonNode data = restTemplate.getForObject(url, JsonNode.class, lat, lon, apiKey);
        return transform(data, true);
    }

    public WeatherResponse getCurrentWeatherByCity(String city, String state, String country) {
        String url = BASE_URL + "/weather?q={q}&appid={key}&units=imperial";
        JsonNode data = restTemplate.getForObject(url, JsonNode.class, city + "," + state + "," + country, apiKey);
        return transform(data, false);
    }

    public Map<String, Object> getForecast(double lat, double lon) {
        String url = BASE_URL + "/forecast?lat={lat}&lon={lon}&appid={key}&units=imperial";
        JsonNode data = restTemplate.getForObject(url, JsonNode.class, lat, lon, apiKey);
        Map<String, Object> result = new HashMap<>();
        result.put("forecast", data.path("list"));
        result.put("generated_at", Instant.now().toString());
        return result;
    }

    public Map<String, Object> getForecastByCity(String city, String state, String country) {
        String url = BASE_URL + "/forecast?q={q}&appid={key}&units=imperial";
        JsonNode data = restTemplate.getForObject(url, JsonNode.class, city + "," + state + "," + country, apiKey);
        Map<String, Object> result = new HashMap<>();
        result.put("forecast", data.path("list"));
        result.put("generated_at", Instant.now().toString());
        return result;
    }

    private WeatherResponse transform(JsonNode data, boolean includeTime) {
        WeatherResponse response = new WeatherResponse();

        WeatherResponse.LocationData location = new WeatherResponse.LocationData();
        location.setLat(data.path("coord").path("lat").asDouble());
        location.setLon(data.path("coord").path("lon").asDouble());
        location.setName(data.path("name").asText());
        location.setCountry(data.path("sys").path("country").asText());
        response.setLocation(location);

        JsonNode weatherArr = data.path("weather");
        WeatherResponse.WeatherData weather = new WeatherResponse.WeatherData();
        weather.setId(weatherArr.get(0).path("id").asInt());
        weather.setTemp((int) Math.round(data.path("main").path("temp").asDouble()));
        weather.setFeels_like((int) Math.round(data.path("main").path("feels_like").asDouble()));
        weather.setHumidity(data.path("main").path("humidity").asInt());
        weather.setPressure(data.path("main").path("pressure").asInt());
        weather.setDescription(weatherArr.get(0).path("description").asText());
        weather.setMain(weatherArr.get(0).path("main").asText());
        weather.setVisibility(data.path("visibility").asDouble() / 1000.0);
        weather.setCloud_coverage(data.path("clouds").path("all").asInt());

        JsonNode rain = data.path("rain");
        if (!rain.isMissingNode()) {
            weather.setRain(rain.has("1h") ? rain.path("1h").asDouble()
                          : rain.has("3h") ? rain.path("3h").asDouble() : 0.0);
        }

        JsonNode snow = data.path("snow");
        if (!snow.isMissingNode()) {
            weather.setSnow(snow.has("1h") ? snow.path("1h").asDouble()
                           : snow.has("3h") ? snow.path("3h").asDouble() : 0.0);
        }
        response.setWeather(weather);

        WeatherResponse.WindData wind = new WeatherResponse.WindData();
        wind.setSpeed(data.path("wind").path("speed").asDouble());
        wind.setDeg(data.path("wind").path("deg").asDouble());
        JsonNode gust = data.path("wind").path("gust");
        wind.setGust(gust.isMissingNode() ? "N/A" : String.valueOf(gust.asDouble()));
        response.setWind(wind);

        if (includeTime) {
            WeatherResponse.TimeData time = new WeatherResponse.TimeData();
            time.setSunrise(Instant.ofEpochMilli(data.path("sys").path("sunrise").asLong() * 1000).toString());
            time.setSunset(Instant.ofEpochMilli(data.path("sys").path("sunset").asLong() * 1000).toString());
            time.setTimestamp(Instant.now().toString());
            response.setTime(time);
        }

        return response;
    }
}
