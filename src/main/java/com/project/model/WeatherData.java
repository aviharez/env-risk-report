package com.project.model;

/**
 * Normalized weather snapshot for a given location
 * All temperature values are in Celsius
 */
public class WeatherData {

    private final String cityName;
    private final String countryCode;
    private final double temperatureCelsius;
    private final double feelsLikeCelsius;
    private final int humidityPercent;
    private final String weatherDescription;

    public WeatherData(String cityName,
                       String countryCode,
                       double temperatureCelsius,
                       double feelsLikeCelsius,
                       int humidityPercent,
                       String weatherDescription) {
        this.cityName = cityName;
        this.countryCode = countryCode;
        this.temperatureCelsius = temperatureCelsius;
        this.feelsLikeCelsius = feelsLikeCelsius;
        this.humidityPercent = humidityPercent;
        this.weatherDescription = weatherDescription;
    }

    public String getCityName() {
        return this.cityName;
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public double getTemperatureCelsius() {
        return this.temperatureCelsius;
    }

    public double getFeelsLikeCelsius() {
        return this.feelsLikeCelsius;
    }

    public int getHumidityPercent() {
        return this.humidityPercent;
    }

    public String getWeatherDescription() {
        return this.weatherDescription;
    }

}
