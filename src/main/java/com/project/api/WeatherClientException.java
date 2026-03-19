package com.project.api;

/**
 * Checked exception for all failures originating from the weather API layer.
 * Callers can inspect {@link #getErrorType()} to distinguish between user-fixable
 * problems (bad API key, unknown location) and transient issues (network, rate limit).
 */
public class WeatherClientException extends Exception {

    public enum ErrorType {
        /** The API key is missing, invalid, or unauthorized */
        INVALID_API_KEY,
        /** The requested city / ZIP code was not found by the API. */
        LOCATION_NOT_FOUND,
        /** The API rate limit has exceeded (HTTP 429) */
        RATE_LIMITED,
        /** A network-level error prevented the request from completing. */
        NETWORK_ERROR,
        /** Any other unexpected API or parsing error. */
        UNEXPECTED_ERROR
    }

    private final ErrorType errorType;

    public WeatherClientException(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }

    public WeatherClientException(String message, ErrorType errorType, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

}
