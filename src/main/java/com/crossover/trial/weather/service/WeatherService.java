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
        final DataPointType dptype = DataPointType.valueOf(pointType.toUpperCase());

        if (pointType.equalsIgnoreCase(DataPointType.WIND.name())) {
            if (dp.getMean() >= 0) {
                ai.setWind(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        }

        if (pointType.equalsIgnoreCase(DataPointType.TEMPERATURE.name())) {
            if (dp.getMean() >= -50 && dp.getMean() < 100) {
                ai.setTemperature(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        }

        if (pointType.equalsIgnoreCase(DataPointType.HUMIDTY.name())) {
            if (dp.getMean() >= 0 && dp.getMean() < 100) {
                ai.setHumidity(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        }

        if (pointType.equalsIgnoreCase(DataPointType.PRESSURE.name())) {
            if (dp.getMean() >= 650 && dp.getMean() < 800) {
                ai.setPressure(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        }

        if (pointType.equalsIgnoreCase(DataPointType.CLOUDCOVER.name())) {
            if (dp.getMean() >= 0 && dp.getMean() < 100) {
                ai.setCloudCover(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        }

        if (pointType.equalsIgnoreCase(DataPointType.PRECIPITATION.name())) {
            if (dp.getMean() >=0 && dp.getMean() < 100) {
                ai.setPrecipitation(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        }

        throw new IllegalStateException("couldn't update atmospheric data");
    }

    // TODO: name correctly
    public int getDataSize() {
        int datasize = 0;
        for (AtmosphericInformation ai : atmosphericInformation.values()) {
            // we only count recent readings
            if (ai.getCloudCover() != null
                    || ai.getHumidity() != null
                    || ai.getPressure() != null
                    || ai.getPrecipitation() != null
                    || ai.getTemperature() != null
                    || ai.getWind() != null) {
                // updated in the last day
                if (ai.getLastUpdateTime() > System.currentTimeMillis() - 86400000) {
                    datasize++;
                }
            }
        }
        return datasize;
    }






}
