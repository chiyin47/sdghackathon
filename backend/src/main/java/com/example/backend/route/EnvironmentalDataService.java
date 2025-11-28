package com.example.backend.route;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EnvironmentalDataService {

    public Map<String, String> getEnvironmentalData(double latitude, double longitude) {
        // In a real application, this would call an external API
        // For now, return mock data
        Map<String, String> data = new HashMap<>();
        data.put("airQuality", "Good");
        data.put("temperature", "25°C");
        data.put("humidity", "60%");
        data.put("latitude", String.valueOf(latitude));
        data.put("longitude", String.valueOf(longitude));
        return data;
    }
}
