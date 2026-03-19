package com.project.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Gson-mapped POJO for the OopenWeatherMap "Current Weather Data" JSON response.
 * Only the fields relevant to environmental risk assessment are mapped.
 */
public class OwmResponse {

    /** Top-level main block */
    @SerializedName("main")
    Main main;

    /** Top-level weather array */
    @SerializedName("weather")
    List<Weather> weather;

    /** Human-readable city name returned from API */
    @SerializedName("name")
    String name;

    /** Nested sys block containing country code */
    @SerializedName("sys")
    Sys sys;

    /** HTTP-style status code embedded in the response body */
    @SerializedName("cod")
    int cod;

    /** Error message returned when cod != 200 */
    @SerializedName("message")
    String message;

    // Inner classes mirroring the JSON structure

    static class Main {
        @SerializedName("temp")
        double temp;

        @SerializedName("feels_like")
        double feelsLike;

        @SerializedName("humidity")
        int humidity;
    }

    static class Weather {
        @SerializedName("description")
        String description;
    }

    static class Sys {
        @SerializedName("country")
        String country;
    }

}
