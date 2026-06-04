package com.controller;

import com.dto.WeatherResponse;
import com.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/weatherCities/weather")
@CrossOrigin(origins = "http://localhost:3000")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping("/current")
    public WeatherResponse getCurrent(@RequestParam double lat, @RequestParam double lon) {
        return weatherService.getCurrentWeather(lat, lon);
    }

    @GetMapping("/currentByCityState")
    public WeatherResponse getCurrentByCityState(
            @RequestParam String city,
            @RequestParam String state,
            @RequestParam String country) {
        return weatherService.getCurrentWeatherByCity(city, state, country);
    }

    @GetMapping("/forecast")
    public Map<String, Object> getForecast(@RequestParam double lat, @RequestParam double lon) {
        return weatherService.getForecast(lat, lon);
    }

    @GetMapping("/forecastByCityState")
    public Map<String, Object> getForecastByCityState(
            @RequestParam String city,
            @RequestParam String state,
            @RequestParam String country) {
        return weatherService.getForecastByCity(city, state, country);
    }
}
