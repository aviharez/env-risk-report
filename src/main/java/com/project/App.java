package com.project;

import com.project.api.WeatherClient;
import com.project.api.WeatherClientException;
import com.project.model.WeatherData;
import com.project.report.ReportGenerator;
import com.project.risk.RiskAssessor;
import com.project.risk.RiskFinding;
import com.project.risk.RiskLevel;

import java.util.List;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App {

    static final String API_KEY_ENV_VAR = "OWM_API_KEY";

    public static void main( String[] args ) {
        String apiKey = System.getenv(API_KEY_ENV_VAR);
        if (apiKey == null || apiKey.isBlank()) {
            printError(
                    "API key not found. Please set the '" + API_KEY_ENV_VAR + "' environment " +
                            "variable to your OpenWeatherMap API key and try again.\n" +
                            "See README.md for setup instructions."
            );
            System.exit(1);
        }

        String location = resolveLocation(args);
        if (location == null || location.isBlank()) {
            printError("No location provided. Exiting.");
            System.exit(1);
        }

        System.out.println("Fetching weather data for: " + location + " ...");
        System.out.println();

        WeatherClient client = new WeatherClient(apiKey);
        WeatherData weatherData;
        try {
            weatherData = client.fetchWeather(location);
        } catch (WeatherClientException e) {
            printError(formatClientError(e));
            System.exit(exitCodeFor(e.getErrorType()));
            return;
        }

        RiskAssessor assessor = new RiskAssessor();
        List<RiskFinding> findings = assessor.assess(weatherData);
        RiskLevel overall = assessor.overallLevel(findings);

        ReportGenerator generator = new ReportGenerator();
        String report = generator.generate(weatherData, findings, overall);
        System.out.println(report);
    }

    // Private helpers

    private static String resolveLocation(String[] args) {
        if (args.length > 0) {
            return String.join(" ", args).trim();
        }
        System.out.print("Enter city name or ZIP code (e.g. \"Jakarta,ID\" or \"10001,US\"): ");
        try (Scanner scanner = new Scanner(System.in)) {
            return scanner.nextLine().trim();
        }
    }

    private static String formatClientError(WeatherClientException e) {
        return switch (e.getErrorType()) {
            case INVALID_API_KEY ->
                "Authentication failed: " + e.getMessage() + "\n" +
                        "Verify that the '" + API_KEY_ENV_VAR + "' environment variable is set correctly.";
            case LOCATION_NOT_FOUND ->
                "Location not found: " + e.getMessage() + "\n" +
                        "Try a different format, e.g. \"London,GB\" or a ZIP code like \"10001,US\".";
            case RATE_LIMITED ->
                "Rate limit reached: " + e.getMessage() + "\n" +
                        "Free-tier accounts allow 60 calls/minute. Please wait a moment and retry.";
            case NETWORK_ERROR ->
                "Network error: " + e.getMessage() + "\n" +
                        "Check your internet connection and that api.openweathermap.org is reachable.";
            default -> "Unexpected error: " + e.getMessage();
        };
    }

    private static int exitCodeFor(WeatherClientException.ErrorType type) {
        return switch (type) {
            case INVALID_API_KEY -> 2;
            case LOCATION_NOT_FOUND -> 3;
            case RATE_LIMITED -> 4;
            case NETWORK_ERROR -> 5;
            default -> 1;
        };
    }

    private static void printError(String message) {
        System.err.println();
        System.err.println("ERROR: " + message);
        System.err.println();
    }
}
