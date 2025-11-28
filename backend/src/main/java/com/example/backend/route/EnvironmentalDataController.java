package com.example.backend.route;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class EnvironmentalDataController {

    private final EnvironmentalDataService environmentalDataService;

    @Autowired
    public EnvironmentalDataController(EnvironmentalDataService environmentalDataService) {
        this.environmentalDataService = environmentalDataService;
    }

    @GetMapping("/environmental-data")
    public Map<String, String> getEnvironmentalData(@RequestParam double latitude, @RequestParam double longitude) {
        return environmentalDataService.getEnvironmentalData(latitude, longitude);
    }
}
