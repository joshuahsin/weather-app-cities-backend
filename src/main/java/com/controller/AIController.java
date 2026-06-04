package com.controller;

import com.dto.AIRequest;
import com.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/weatherCities/ai")
@CrossOrigin(origins = "http://localhost:3000")
public class AIController {

    @Autowired
    private AIService aiService;

    @PostMapping("/itinerary")
    public Map<String, Object> getItinerary(@RequestBody AIRequest request) {
        return aiService.getItinerary(request);
    }

    @PostMapping("/recommendations")
    public Map<String, Object> getRecommendations(@RequestBody AIRequest request) {
        return aiService.getRecommendations(request);
    }
}
