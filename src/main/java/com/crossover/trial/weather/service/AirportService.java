package com.crossover.trial.weather.service;

import com.crossover.trial.weather.data.AirportData;
import com.crossover.trial.weather.data.AtmosphericInformation;
import java.util.*;

/**
 * Created by const on 8/8/16.
 */
public class AirportService {

    private static AirportService instance;

    private WeatherService weatherService = WeatherService.getInstance();

    /** all known airports */
    private static Map<String, AirportData> airportDataMap = new HashMap<>();

    /**
     * Internal performance counter to better understand most requested information, this map can be improved but
     * for now provides the basis for future performance optimizations. Due to the stateless deployment architecture
     * we don't want to write this to disk, but will pull it off using a REST request and aggregate with other
     * performance metrics {@link #()}
     */
    private static Map<AirportData, Integer> requestFrequency = new HashMap<>();

    private static Map<Double, Integer> radiusFreq = new HashMap<>();

    /** earth radius in KM */
    private static final double R = 6372.8;


    static {
        instance = new AirportService();
    }

    private AirportService() {
        //init();
    }

    public static AirportService getInstance() {
        return instance;
    }

    /**
     * A dummy init method that loads hard coded data
     */
    public void init() {
        airportDataMap.clear();
        requestFrequency.clear();

        addAirport("BOS", 42.364347, -71.005181);
        addAirport("EWR", 40.6925, -74.168667);
        addAirport("JFK", 40.639751, -73.778925);
        addAirport("LGA", 40.777245, -73.872608);
        addAirport("MMU", 40.79935, -74.4148747);
    }

    public Set<String> getAirportsIata() {
        Set<String> retval = new HashSet<>();
        for (String iata : airportDataMap.keySet()) {
            retval.add(iata);
        }
        return retval;
    }

    /**
     * Given an iataCode get the airport data
     *
     * @param iataCode as a string
     * @return airport data or null if not found
     */
    public static AirportData getAirportData(String iataCode) {
        return airportDataMap.get(iataCode);
    }

    /**
     * Add a new known airport to our list.
     *
     * @param iataCode 3 letter code
     * @param latitude in degrees
     * @param longitude in degrees
     *
     * @return the added airport
     */
    public AirportData addAirport(String iataCode, double latitude, double longitude) {
        AirportData ad = new AirportData();
        ad.setIata(iataCode);
        ad.setLatitude(latitude);
        ad.setLatitude(longitude);
        airportDataMap.put(iataCode, ad);

        weatherService.addAtmosphericInformation(iataCode);

        return ad;
    }


    public Map<String, Double> getFrequencyMap() {
        Map<String, Double> freq = new HashMap<>();
        // fraction of queries
        for (AirportData data : airportDataMap.values()) {
            double frac = (double)requestFrequency.getOrDefault(data, 0) / requestFrequency.size();
            freq.put(data.getIata(), frac);
        }
        return freq;

    }

    public int[] getRadiusFrequency() {
        int m = radiusFreq.keySet().stream()
                .max(Double::compare)
                .orElse(1000.0).intValue() + 1;

        int[] hist = new int[m];
        for (Map.Entry<Double, Integer> e : radiusFreq.entrySet()) {
            int i = e.getKey().intValue() % 10;
            hist[i] += e.getValue();
        }
        return hist;
    }

    /**
     * Haversine distance between two airports.
     *
     * @param ad1 airport 1
     * @param ad2 airport 2
     * @return the distance in KM
     */
    public double calculateDistance(AirportData ad1, AirportData ad2) {
        double deltaLat = Math.toRadians(ad2.getLatitude() - ad1.getLatitude());
        double deltaLon = Math.toRadians(ad2.getLongitude() - ad1.getLongitude());
        double a =  Math.pow(Math.sin(deltaLat / 2), 2) + Math.pow(Math.sin(deltaLon / 2), 2)
                * Math.cos(ad1.getLatitude()) * Math.cos(ad2.getLatitude());
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }

    public List<AtmosphericInformation> getWeather(String iata, String radiusString) {
        double radius = radiusString == null || radiusString.trim().isEmpty() ? 0 : Double.valueOf(radiusString);
        updateRequestFrequency(iata, radius);

        List<AtmosphericInformation> result = new ArrayList<>();
        if (radius == 0) {
            result.add(weatherService.getAtmosphericInformation(iata));
        } else {
            AirportData ad = airportDataMap.get(iata);
            airportDataMap.values().stream().filter(airportData -> calculateDistance(ad, airportData) <= radius).forEach(airportData -> {
                AtmosphericInformation ai = weatherService.getAtmosphericInformation(airportData.getIata());
                if (ai.getCloudCover() != null || ai.getHumidity() != null || ai.getPrecipitation() != null
                        || ai.getPressure() != null || ai.getTemperature() != null || ai.getWind() != null) {
                    result.add(ai);
                }
            });
        }
        return result;
    }

    /**
     * Records information about how often requests are made
     *
     * @param iata an iata code
     * @param radius query radius
     */
    public void updateRequestFrequency(String iata, Double radius) {
        AirportData airportData = airportDataMap.get(iata);
        requestFrequency.put(airportData, requestFrequency.getOrDefault(airportData, 0) + 1);
        radiusFreq.put(radius, radiusFreq.getOrDefault(radius, 0));
    }

}
