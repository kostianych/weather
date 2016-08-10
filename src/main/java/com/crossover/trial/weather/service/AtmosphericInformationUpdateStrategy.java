package com.crossover.trial.weather.service;

import com.crossover.trial.weather.data.AtmosphericInformation;
import com.crossover.trial.weather.data.DataPoint;
import com.crossover.trial.weather.data.DataPointType;

/**
 * Created by const on 8/11/16.
 */
public class AtmosphericInformationUpdateStrategy {


    public void update(AtmosphericInformation ai, DataPoint dp) {
        ai.setLastUpdateTime(System.currentTimeMillis());
    }

    public static AtmosphericInformationUpdateStrategy createUpdateStrategy(DataPointType pointType) {
        switch (pointType) {
            case WIND:
                return new WindUpdateStrategy();
            case TEMPERATURE:
                return new TemperatureUpdateStrategy();
            case HUMIDTY:
                return new HumidityUpdateStrategy();
            case PRESSURE:
                return new PressureUpdateStrategy();
            case CLOUDCOVER:
                return new CloudCoverUpdateStrategy();
            case PRECIPITATION:
                return new PrecipitationUpdateStrategy();

        }
        return null;
    }

    static class WindUpdateStrategy extends AtmosphericInformationUpdateStrategy {
        @Override
        public void update(AtmosphericInformation ai, DataPoint dp) {
            super.update(ai, dp);
            if (dp.getMean() >= 0) {
                ai.setWind(dp);
                return;
            }

        }
    }

    static class TemperatureUpdateStrategy extends AtmosphericInformationUpdateStrategy {
        @Override
        public void update(AtmosphericInformation ai, DataPoint dp) {
            super.update(ai, dp);
            if (dp.getMean() >= -50 && dp.getMean() < 100) {
                ai.setTemperature(dp);
                return;
            }
        }
    }

    static class HumidityUpdateStrategy extends AtmosphericInformationUpdateStrategy {
        @Override
        public void update(AtmosphericInformation ai, DataPoint dp) {
            super.update(ai, dp);
            if (dp.getMean() >= 0 && dp.getMean() < 100) {
                ai.setHumidity(dp);
                return;
            }
        }
    }

    static class PressureUpdateStrategy extends AtmosphericInformationUpdateStrategy {
        @Override
        public void update(AtmosphericInformation ai, DataPoint dp) {
            super.update(ai, dp);
            if (dp.getMean() >= 650 && dp.getMean() < 800) {
                ai.setPressure(dp);
                return;
            }
        }
    }

    static class CloudCoverUpdateStrategy extends AtmosphericInformationUpdateStrategy {
        @Override
        public void update(AtmosphericInformation ai, DataPoint dp) {
            super.update(ai, dp);
            if (dp.getMean() >= 0 && dp.getMean() < 100) {
                ai.setCloudCover(dp);
                return;
            }
        }
    }

    static class PrecipitationUpdateStrategy extends AtmosphericInformationUpdateStrategy {
        @Override
        public void update(AtmosphericInformation ai, DataPoint dp) {
            super.update(ai, dp);
            if (dp.getMean() >=0 && dp.getMean() < 100) {
                ai.setPrecipitation(dp);
                return;
            }
        }
    }



}
