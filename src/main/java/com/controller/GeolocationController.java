package com.controller;

import com.dto.GeolocationResponse;
import com.service.GeolocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/weatherCities/geolocation")
@CrossOrigin(origins = "http://localhost:3000")
public class GeolocationController {

    @Autowired
    private GeolocationService geolocationService;

    @GetMapping("/locate")
    public GeolocationResponse locate(@RequestParam double lat, @RequestParam double lon) {
        return geolocationService.locate(lat, lon);
    }
}
