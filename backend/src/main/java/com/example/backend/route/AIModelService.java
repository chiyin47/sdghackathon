package com.example.backend.route;

import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;

@Service
public class AIModelService {

    /**
     * Finds the best route by considering traffic and fuel efficiency.
     * It calculates a score for each route and picks the one with the lowest score.
     *
     * @param result The DirectionsResult containing multiple routes.
     * @return A new DirectionsResult containing only the best route.
     */
    public DirectionsResult findBestRoute(DirectionsResult result) {
        if (result == null || result.routes == null || result.routes.length == 0) {
            return result;
        }

        // Find the route with the best score (lower is better).
        // The score is a combination of duration in traffic and estimated fuel
        // consumption.
        DirectionsRoute bestRoute = Arrays.stream(result.routes)
                .min(Comparator.comparingDouble(this::calculateRouteScore))
                .orElse(result.routes[0]); // Default to the first route

        DirectionsResult newResult = new DirectionsResult();
        newResult.routes = new DirectionsRoute[] { bestRoute };
        newResult.geocodedWaypoints = result.geocodedWaypoints;

        return newResult;
    }

    private double calculateRouteScore(DirectionsRoute route) {
        // Constants for weighting time vs. fuel. These can be tuned.
        final double timeWeight = 0.5;
        final double fuelWeight = 0.5;
        final double averageFuelConsumption = 0.07; // Liters per kilometer

        long totalDurationInTraffic = 0;
        long totalDistance = 0;

        for (DirectionsLeg leg : route.legs) {
            // The duration in traffic is not always available.
            // When it's not, we fall back to the normal duration.
            if (leg.durationInTraffic != null) {
                totalDurationInTraffic += leg.durationInTraffic.inSeconds;
            } else {
                totalDurationInTraffic += leg.duration.inSeconds;
            }
            totalDistance += leg.distance.inMeters;
        }

        // Calculate estimated fuel consumption in liters.
        double fuelUsed = (totalDistance / 1000.0) * averageFuelConsumption;

        // Simple scoring model: a weighted sum of time and fuel.
        // Since the units are different (seconds and liters), this is a simplified
        // approach.
        // A more advanced model could normalize these values before combining them.
        double score = (timeWeight * totalDurationInTraffic) + (fuelWeight * fuelUsed);

        return score;
    }
}