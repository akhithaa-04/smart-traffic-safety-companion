package com.stsc.backend.service;

/**
 * Simple haversine-distance helper. This is the "proximity radius" approach
 * mentioned in the project scope - we deliberately use straight-line
 * distance between coordinates rather than matching against a full route
 * polyline, which keeps the relevance-checking logic easy to build,
 * explain, and test.
 */
public final class GeoUtil {

    private static final double EARTH_RADIUS_METERS = 6371000;

    private GeoUtil() {}

    public static double distanceMeters(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_METERS * c;
    }
}
