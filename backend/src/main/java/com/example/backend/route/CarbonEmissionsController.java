package com.example.backend.route;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class CarbonEmissionsController {

    private final CarbonEmissionsService carbonEmissionsService;

    @Autowired
    public CarbonEmissionsController(CarbonEmissionsService carbonEmissionsService) {
        this.carbonEmissionsService = carbonEmissionsService;
    }

    @GetMapping("/carbon-emissions")
    public double getCarbonEmissions(
            @RequestParam double distance,
            @RequestParam String transportationMode) {
        return carbonEmissionsService.calculateCarbonEmissions(distance, transportationMode);
    }
}
