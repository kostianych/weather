package com.crossover.trial.weather.service;

import com.crossover.trial.weather.data.AtmosphericInformation;
import com.crossover.trial.weather.data.DataPoint;
import com.crossover.trial.weather.data.DataPointType;
import com.crossover.trial.weather.exception.WeatherException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by const on 8/10/16.
 */
public class WeatherService {

    private static WeatherService instance;

    /** atmospheric information for each airport, idx corresponds with airportData */
    private static Map<String, AtmosphericInformation> atmosphericInformation = new HashMap();

    static {
        instance = new WeatherService();
    }

    private WeatherService() {

    }

    public static WeatherService getInstance() {
        return instance;
    }

    public void clear() {
        atmosphericInformation.clear();
    }

    public AtmosphericInformation getAtmosphericInformation(String iataCode) {
        return atmosphericInformation.get(iataCode);
    }

    public void addAtmosphericInformation(String iataCode) {
        AtmosphericInformation ai = new AtmosphericInformation();
        atmosphericInformation.put(iataCode, ai);
    }

    /**
     * Update the airports weather data with the collected data.
     *
     * @param iataCode the 3 letter IATA code
     * @param pointType the point type {@link DataPointType}
     * @param dp a datapoint object holding pointType data
     *
     * @throws WeatherException if the update can not be completed
     */
    public void addDataPoint(String iataCode, String pointType, DataPoint dp) throws WeatherException {
        AtmosphericInformation ai = getAtmosphericInformation(iataCode);
        updateAtmosphericInformation(ai, pointType, dp);
    }

    /**
     * update atmospheric information with the given data point for the given point type
     *
     * @param ai the atmospheric information object to update
     * @param pointType the data point type as a string
     * @param dp the actual data point
     */
    private void updateAtmosphericInformation(AtmosphericInformation ai, String pointType, DataPoint dp) throws WeatherException {
        final DataPointType dpType = DataPointType.valueOf(pointType.toUpperCase());
        AtmosphericInformationUpdateStrategy updateStrategy = AtmosphericInformationUpdateStrategy.createUpdateStrategy(dpType);
        updateStrategy.update(ai, dp);
    }

    // TODO: name correctly
    public int getDataSize() {
        int datasize = 0;
        for (AtmosphericInformation ai : atmosphericInformation.values()) {
            // we only count recent readings
            // updated in the last day
            if (ai.hasNotNullField() && ai.getLastUpdateTime() > System.currentTimeMillis() - 86400000) {
                datasize++;
            }
        }
        return datasize;
    }

}
