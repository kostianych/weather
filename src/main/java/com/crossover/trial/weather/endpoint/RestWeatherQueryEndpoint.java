package com.crossover.trial.weather.endpoint;

import com.crossover.trial.weather.service.AirportDataService;
import com.google.gson.Gson;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.logging.Logger;

/**
 * The Weather App REST endpoint allows clients to query, update and check health stats. Currently, all data is
 * held in memory. The end point deploys to a single container
 *
 * @author code test administrator
 */
@Path("/query")
public class RestWeatherQueryEndpoint implements WeatherQueryEndpoint {

    public final static Logger LOGGER = Logger.getLogger("WeatherQuery");

    private AirportDataService airportDataService = AirportDataService.getInstance();

    /** shared gson json to object factory */
    public static final Gson gson = new Gson();

    /**
     * Retrieve service health including total size of valid data points and request frequency information.
     *
     * @return health stats for the service as a string
     */
    @Override
    public String ping() {
        Map<String, Object> retval = new HashMap<>();
        retval.put("datasize", airportDataService.getDataSize());
        retval.put("iata_freq", airportDataService.getFrequencyMap());
        retval.put("radius_freq", airportDataService.getRadiusFrequency());

        return gson.toJson(retval);
    }

    /**
     * Given a query in json format {'iata': CODE, 'radius': km} extracts the requested airport information and
     * return a list of matching atmosphere information.
     *
     * @param iata the iataCode
     * @param radiusString the radius in km
     *
     * @return a list of atmospheric information
     */
    @Override
    public Response weather(String iata, String radiusString) {
        return Response.status(Response.Status.OK).entity(airportDataService.getWeather(iata, radiusString)).build();
    }

}
