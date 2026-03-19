package com.project.api;

import com.google.gson.Gson;
import com.project.model.WeatherData;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Thin HTTP client that fetches current weather data from the
 * OpenWeatherMap "Current Weather Data" v2.5 endpoint.
 *
 * <p>The API key is read at construction time and is never stored in source code.
 * Callers are expected to supply it via the {@code OWM_API_KEY} environment variable
 * or any other mechanism that avoids hard-coding secrets.</p>
 *
 * <p>All HTTP and parsing errors are translated into typed
 * {@link WeatherClientException} instances so that callers do not need to
 * handle raw {@link IOException} or HTTP status codes.</p>
 */
public class WeatherClient {

    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    private final String apiKey;
    private final OkHttpClient httpClient;
    private final Gson gson;

    /**
     * @param apiKey A valid OpenWeatherMap API key. Must not be null or blank.
     */
    public WeatherClient(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("API key must not be null or blank.");
        }
        this.apiKey = apiKey;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }

    /**
     * Fetches current weather for the given location query.
     *
     * @param location A city name (e.g. {@code "London"}, {@code "Jakarta,ID"}) or a
     *                 ZIP/postal code with country code (e.g. {@code "10001, US"}).
     *                 The method auto-detects whether a ZIP lookup is needed.
     * @return A {@link WeatherData} snapshot for the requested location.
     * @throws WeatherClientException on any API, network, or parsing failure.
     */
    public WeatherData fetchWeather(String location) throws WeatherClientException {
        if (location == null || location.isBlank()) {
            throw new WeatherClientException(
                    "Location must not be empty.",
                    WeatherClientException.ErrorType.LOCATION_NOT_FOUND
            );
        }

        String url = buildUrl(location.trim());
        Request request = new Request.Builder().url(url).build();

        try (Response response = httpClient.newCall(request).execute()) {
            return handleResponse(response);
        } catch (WeatherClientException e) {
            throw e;
        } catch (IOException e) {
            throw new WeatherClientException(
                    "Network error: " + e.getMessage(),
                    WeatherClientException.ErrorType.NETWORK_ERROR,
                    e
            );
        }
    }

    // Private helpers

    /**
     * Builds the correct API URL depending on whether the input looks like a ZIP code.
     */
    private String buildUrl(String location) {
        // A ZIP/postal code query looks like "10001,US" or pure digits like "10001".
        // City queries may contain letters, so we distinguish by checking for a leading digit.
        boolean looksLikeZip = location.matches("^[0-9].*");
        String queryParam = looksLikeZip ? "zip" : "q";
        return String.format("%s?%s=%s&appid=%s&units=metric", BASE_URL, queryParam, location, apiKey);
    }

    /**
     * Parses the HTTP response body and maps API-level errors to typed exception.
     */
    private WeatherData handleResponse(Response response) throws WeatherClientException, IOException {
        ResponseBody body = response.body();
        if (body == null) {
            throw new WeatherClientException(
                    "Empty response received from API.",
                    WeatherClientException.ErrorType.UNEXPECTED_ERROR
            );
        }

        String json = body.string();

        if (response.code() == 401) {
            throw new WeatherClientException(
                    "Invalid or missing API key. Verify your OWM_API_KEY environment variable.",
                    WeatherClientException.ErrorType.INVALID_API_KEY
            );
        }

        if (response.code() == 429) {
            throw new WeatherClientException(
                    "OpenWeatherMap rate limit exceeded. Please wait before retrying.",
                    WeatherClientException.ErrorType.RATE_LIMITED
            );
        }

        OwmResponse owmResponse = gson.fromJson(json, OwmResponse.class);

        if (owmResponse.cod != 200) {
            String apiMessage = (owmResponse.message != null) ? owmResponse.message : "Unknown location";
            throw new WeatherClientException(
                    "Location not found: " + apiMessage,
                    WeatherClientException.ErrorType.LOCATION_NOT_FOUND
            );
        }

        if (owmResponse.main == null) {
            throw new WeatherClientException(
                    "Unexpected API response structure - 'main' block is missing.",
                    WeatherClientException.ErrorType.UNEXPECTED_ERROR
            );
        }

        String description = (owmResponse.weather != null && !owmResponse.weather.isEmpty())
                ? owmResponse.weather.get(0).description
                : "N/A";

        String country = (owmResponse.sys != null) ? owmResponse.sys.country : "N/A";

        return new WeatherData(
                owmResponse.name,
                country,
                owmResponse.main.temp,
                owmResponse.main.feelsLike,
                owmResponse.main.humidity,
                description
        );
    }

}
