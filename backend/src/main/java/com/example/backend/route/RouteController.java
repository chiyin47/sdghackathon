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
    public RouteResponse getRoute(@RequestParam String origin, @RequestParam String destination) {
        try {
            DirectionsResult directions = directionsService.getDirections(origin, destination);

            if (directions.routes.length == 0) {
                RouteResponse error = new RouteResponse();
                error.setContent("No routes found");
                return error;
            }

            var route = directions.routes[0]; // first route
            List<RouteResponse.LatLng> coords = new ArrayList<>();
            for (DirectionsLeg leg : route.legs) {
                for (DirectionsStep step : leg.steps) {
                    coords.add(new RouteResponse.LatLng(step.startLocation.lat, step.startLocation.lng));
                }
                coords.add(new RouteResponse.LatLng(leg.endLocation.lat, leg.endLocation.lng));
            }

            RouteResponse response = new RouteResponse();
            response.setContent("Route via " + route.summary);
            response.setDistance(route.legs[0].distance.humanReadable);
            response.setDuration(route.legs[0].duration.humanReadable);
            response.setFuelUsed("Estimated fuel: " + Math.round(route.legs[0].distance.inMeters / 1000 * 0.07) + " L");
            response.setCoordinates(coords);

            return response;

        } catch (Exception e) {
            RouteResponse error = new RouteResponse();
            error.setContent("Error fetching directions: " + e.getMessage());
            return error;
        }
    }
}
