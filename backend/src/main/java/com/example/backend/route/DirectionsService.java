package com.example.backend.route;

import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
public class DirectionsService {

    @Value("${google.maps.api.key}")
    private String apiKey;

    public DirectionsResult getDirections(String origin, String destination, String... waypoints) throws Exception {
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(apiKey)
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();

        DirectionsApiRequest request = DirectionsApi.newRequest(context)
                .origin(origin)
                .destination(destination)
                .mode(TravelMode.DRIVING)
                .alternatives(true)
                .departureTime(Instant.now());

        if (waypoints != null && waypoints.length > 0) {
            request.waypoints(waypoints);
        }

        DirectionsResult result = request.await();

        context.shutdown();
        return result;
    }

    public DirectionsResult getDirections(String origin, String destination) throws Exception {
        return getDirections(origin, destination, new String[0]);
    }
}
