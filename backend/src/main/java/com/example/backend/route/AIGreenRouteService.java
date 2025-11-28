package com.example.backend.route;

import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.internal.PolylineEncoding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AIGreenRouteService {

    @Autowired
    private AIModelService aiModelService;

    public DirectionsResult findBestRoute(DirectionsResult directionsResult) {
        // For now, simply return the original directionsResult.
        // The logic to determine the "best" route based on AI prediction will be implemented here.
        processRoutes(directionsResult); // Process the routes to get AI predictions
        return directionsResult;
    }

    public List<RouteResponse> processRoutes(DirectionsResult result) {
        List<RouteResponse> routeResponses = new ArrayList<>();

        if (result == null || result.routes == null || result.routes.length == 0) {
            return routeResponses;
        }

        int routeNumber = 1;
        for (DirectionsRoute route : result.routes) {
            String distance = "N/A";
            String duration = "N/A";
            long distanceMeters = 0;
            long durationSeconds = 0;

            if (route.legs != null && route.legs.length > 0) {
                distance = route.legs[0].distance.humanReadable;
                duration = route.legs[0].duration.humanReadable;
                distanceMeters = route.legs[0].distance.inMeters;
                durationSeconds = route.legs[0].duration.inSeconds;
            }

            // Create a prompt for the AI model with more detailed requests
            String prompt = String.format(
                    "Critically analyze this specific route for fuel efficiency, providing a unique and comprehensive AI-driven deep thought suggestion. " +
                    "This route covers %d meters and has an estimated duration of %d seconds. " +
                    "Provide a precise and differentiated estimate of fuel consumption in liters for an average car, *meticulously factoring in dynamic traffic patterns, potential congestion during typical peak hours, and road characteristics specific to this exact distance and duration*. " +
                    "Classify its overall efficiency distinctly (e.g., 'exceptionally efficient', 'highly efficient', 'moderately efficient', 'less efficient', 'inefficient'). " +
                    "Offer highly specific, actionable, and unique driving recommendations tailored precisely to this route's segments to maximize fuel savings and minimize environmental impact. " +
                    "Furthermore, detail advanced strategies to navigate *this specific route* during typical peak hours, suggesting optimal departure times, alternative segment approaches, and congestion avoidance tactics to significantly save fuel. " +
                    "Your response MUST be structured clearly as follows:\n\n" +
                    "Fuel: X.XX liters\n" +
                    "Efficiency: [Distinct Classification]\n" +
                    "Recommendation: [Highly specific, actionable driving recommendations for this route]\n" +
                    "Peak Hours Strategy: [Advanced, route-specific strategies for avoiding congestion and saving fuel]\n\n" +
                    "Ensure all information is concise, uniquely tailored to this route, and focuses on practical, differentiating advice.",
                    distanceMeters, durationSeconds);

            String aiResponse = aiModelService.getAIResponse(prompt);
            System.out.println("Raw AI Response for route " + routeNumber + ":\n" + aiResponse);

            String fuelPrediction = "Fuel prediction unavailable";
            double parsedFuel = Double.MAX_VALUE;

            String efficiencyClassification = "N/A";
            String drivingRecommendation = "No specific recommendation.";
            String peakHoursAdvice = "No specific peak hour advice.";
            String fullPredictionSummary = "No AI prediction available.";

            // Attempt to parse AI response for fuel prediction
            boolean aiFuelParsedSuccessfully = false;
            if (aiResponse != null && !aiResponse.isEmpty()) {
                Pattern fuelPattern = Pattern.compile("Fuel: ([\\d.]+) liters");
                Matcher fuelMatcher = fuelPattern.matcher(aiResponse);
                if (fuelMatcher.find()) {
                    String fuelValueStr = fuelMatcher.group(1);
                    try {
                        parsedFuel = Double.parseDouble(fuelValueStr);
                        fuelPrediction = String.format("%.2f liters", parsedFuel);
                        aiFuelParsedSuccessfully = true;
                    } catch (NumberFormatException e) {
                        System.err.println("Failed to parse AI fuel prediction: '" + fuelValueStr + "' for route " + routeNumber + ". Resorting to rough estimation. " + e.getMessage());
                    }
                } else {
                    System.err.println("AI response did not contain expected 'Fuel: X liters' pattern for route " + routeNumber + ". Resorting to rough estimation. Response snippet: " + aiResponse.substring(0, Math.min(aiResponse.length(), 200)) + "...");
                }

                // If AI fuel prediction failed or was not found, use a rough estimation (this block is now correctly inside the main if)
                if (!aiFuelParsedSuccessfully) {
                    parsedFuel = (distanceMeters / 1000.0) * 0.08 + (durationSeconds * 0.0005);
                    fuelPrediction = String.format("Estimated: %.2f liters", parsedFuel);
                    System.out.println("Using rough fuel estimation for route " + routeNumber + ": " + fuelPrediction);
                }

                // Extract Efficiency
                Pattern efficiencyPattern = Pattern.compile("Efficiency: (.+)");
                Matcher efficiencyMatcher = efficiencyPattern.matcher(aiResponse);
                if (efficiencyMatcher.find()) {
                    efficiencyClassification = efficiencyMatcher.group(1).trim();
                }

                // Extract Recommendation
                Pattern recommendationPattern = Pattern.compile("Recommendation: (.+)", Pattern.DOTALL);
                Matcher recommendationMatcher = recommendationPattern.matcher(aiResponse);
                if (recommendationMatcher.find()) {
                    drivingRecommendation = recommendationMatcher.group(1).trim();
                    // Remove subsequent sections if they were accidentally captured
                    int peakHoursIndex = drivingRecommendation.indexOf("Peak Hours Strategy:");
                    if (peakHoursIndex != -1) {
                        drivingRecommendation = drivingRecommendation.substring(0, peakHoursIndex).trim();
                    }
                }

                // Extract Peak Hours Strategy
                Pattern peakHoursPattern = Pattern.compile("Peak Hours Strategy: (.+)", Pattern.DOTALL);
                Matcher peakHoursMatcher = peakHoursPattern.matcher(aiResponse);
                if (peakHoursMatcher.find()) {
                    peakHoursAdvice = peakHoursMatcher.group(1).trim();
                }

                // Fallback for AI-provided efficiency, recommendation, and peak hour advice if not parsed
                if (efficiencyClassification.equals("N/A")) {
                    efficiencyClassification = "Not explicitly classified by AI, based on fuel: " + (parsedFuel < 5.0 ? "Efficient" : (parsedFuel < 10.0 ? "Average" : "Less Efficient"));
                }
                if (drivingRecommendation.equals("No specific recommendation.")) {
                    drivingRecommendation = "General advice: Drive smoothly, avoid rapid acceleration/braking, maintain consistent speed. Check tire pressure regularly.";
                }
                if (peakHoursAdvice.equals("No specific peak hour advice.")) {
                    peakHoursAdvice = "General peak hour strategy: Consider leaving earlier/later, use real-time traffic apps, and explore less congested alternative routes.";
                }

                // Construct full prediction summary for display
                fullPredictionSummary = String.format(
                    "Fuel: %s\nEfficiency: %s\nRecommendation: %s\nPeak Hours Strategy: %s",
                    fuelPrediction,
                    efficiencyClassification,
                    drivingRecommendation,
                    peakHoursAdvice
                );
            } // This is the correct closing brace for if (aiResponse != null && !aiResponse.isEmpty())


            RouteResponse routeResponse = new RouteResponse();
            routeResponse.setRouteNumber(routeNumber++);
            routeResponse.setDistance(distance);
            routeResponse.setDuration(duration);
            routeResponse.setFuelUsed(fuelPrediction); // Using AI prediction for fuel used
            routeResponse.setFuelSavingPrediction(fullPredictionSummary); // Store comprehensive AI response
            routeResponse.setParsedFuelConsumption(parsedFuel); // Store parsed fuel for comparison

            List<RouteResponse.LatLng> pathCoordinates = new ArrayList<>();
            if (route.overviewPolyline != null && route.overviewPolyline.getEncodedPath() != null) {
                List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());
                for (com.google.maps.model.LatLng latLng : decodedPath) {
                    pathCoordinates.add(new RouteResponse.LatLng(latLng.lat, latLng.lng));
                }
            }
            routeResponse.setCoordinates(pathCoordinates);
            routeResponses.add(routeResponse);
        }

        // Find the most fuel-efficient route and mark it green
        RouteResponse mostEfficientRoute = null;
        double minFuel = Double.MAX_VALUE;

        for (RouteResponse routeResponse : routeResponses) {
            if (routeResponse.getParsedFuelConsumption() < minFuel) {
                minFuel = routeResponse.getParsedFuelConsumption();
                mostEfficientRoute = routeResponse;
            }
            routeResponse.setColor("red"); // Default all to red first
        }

        if (mostEfficientRoute != null) {
            mostEfficientRoute.setColor("green"); // Mark the most efficient as green
        }

        return routeResponses;
    }
}