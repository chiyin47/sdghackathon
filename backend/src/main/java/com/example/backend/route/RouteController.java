package com.example.backend.route;

import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/route")
public class RouteController {

    @Autowired
    private DirectionsService directionsService;

    @GetMapping
    public RouteResponse getRoute(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam(required = false) String waypoints
    ) {
        try {
            String[] waypointArray = (waypoints != null && !waypoints.isEmpty()) ? waypoints.split("\\|") : new String[0];
            DirectionsResult directions = directionsService.getDirections(origin, destination, waypointArray);

            if (directions.routes.length == 0) {
                RouteResponse error = new RouteResponse();
                error.setContent("No routes found");
                return error;
            }

            var route = directions.routes[0]; // first route
            List<RouteResponse.LatLng> coords = new ArrayList<>();
            for (DirectionsLeg leg : route.legs) {
                for (DirectionsStep step : leg.steps) {
                    List<com.google.maps.model.LatLng> path = step.polyline.decodePath();
                    for (com.google.maps.model.LatLng point : path) {
                        coords.add(new RouteResponse.LatLng(point.lat, point.lng));
                    }
                }
            }

            long totalDistance = 0;
            long totalDuration = 0;
            for (DirectionsLeg leg : route.legs) {
                totalDistance += leg.distance.inMeters;
                totalDuration += leg.duration.inSeconds;
            }

            long hours = totalDuration / 3600;
            long minutes = (totalDuration % 3600) / 60;
            String readableDuration = String.format("%d hours %d mins", hours, minutes);

            RouteResponse response = new RouteResponse();
            response.setContent("Route via " + route.summary);
            response.setDistance(String.format("%.2f km", totalDistance / 1000.0));
            response.setDuration(readableDuration);
            response.setFuelUsed("Estimated fuel: " + Math.round(totalDistance / 1000 * 0.07) + " L");
            response.setCoordinates(coords);

            return response;

        } catch (Exception e) {
            RouteResponse error = new RouteResponse();
            error.setContent("Error fetching directions: " + e.getMessage());
            return error;
        }
    }
}
