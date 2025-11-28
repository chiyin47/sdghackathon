package com.example.backend.route;

import org.springframework.stereotype.Service;

@Service
public class CarbonEmissionsService {

    public double calculateCarbonEmissions(double distance, String transportationMode) {
        // Placeholder for actual carbon emission calculation
        // For now, a simple mock calculation
        switch (transportationMode.toLowerCase()) {
            case "car":
                return distance * 0.120; // kg CO2 per km (example value)
            case "bike":
                return distance * 0.005; // kg CO2 per km (example value)
            case "public_transport":
                return distance * 0.050; // kg CO2 per km (example value)
            default:
                return 0.0;
        }
    }
}
