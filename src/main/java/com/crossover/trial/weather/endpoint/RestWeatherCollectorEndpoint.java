package com.crossover.trial.weather.endpoint;

import com.crossover.trial.weather.service.AirportService;
import com.crossover.trial.weather.exception.WeatherException;
import com.crossover.trial.weather.data.AirportData;
import com.crossover.trial.weather.data.DataPoint;
import com.crossover.trial.weather.service.WeatherService;
import com.google.gson.Gson;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

/**
 * A REST implementation of the WeatherCollector API. Accessible only to airport weather collection
 * sites via secure VPN.
 *
 * @author code test administrator
 */

@Path("/collect")
public class RestWeatherCollectorEndpoint implements WeatherCollectorEndpoint {
    public final static Logger LOGGER = Logger.getLogger(RestWeatherCollectorEndpoint.class.getName());

    private AirportService airportDataService = AirportService.getInstance();

    private WeatherService weatherService = WeatherService.getInstance();


    /** shared gson json to object factory */
    public final static Gson gson = new Gson();

    @Override
    public Response ping() {
        return Response.status(Response.Status.OK).entity("ready").build();
    }

    @Override
    public Response updateWeather(@PathParam("iata") String iataCode,
                                  @PathParam("pointType") String pointType,
                                  String datapointJson) {
        try {
            weatherService.addDataPoint(iataCode, pointType, gson.fromJson(datapointJson, DataPoint.class));
        } catch (WeatherException e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.OK).build();
    }


    @Override
    public Response getAirports() {
        return Response.status(Response.Status.OK).entity(airportDataService.getAirportsIata()).build();
    }


    @Override
    public Response getAirport(@PathParam("iata") String iata) {
        AirportData ad = airportDataService.getAirportData(iata);
        return Response.status(Response.Status.OK).entity(ad).build();
    }


    @Override
    @Path("/airport/{iata}/{lat}/{long}")
    public Response addAirport(@PathParam("iata") String iata,
                               @PathParam("lat") String latString,
                               @PathParam("long") String longString) {
        airportDataService.addAirport(iata.replaceAll("\"", ""), Double.valueOf(latString), Double.valueOf(longString));
        return Response.status(Response.Status.OK).build();
    }


    @Override
    public Response deleteAirport(@PathParam("iata") String iata) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @Override
    public Response exit() {
        System.exit(0);
        return Response.noContent().build();
    }

}
