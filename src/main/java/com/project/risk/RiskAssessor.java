package com.project.risk;

import com.project.model.WeatherData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stateless engine that evaluates weather data against server room safety thresholds
 * and returns a list of {@link RiskFinding} objects - one per assessed dimension.
 */
public class RiskAssessor {

    // Temperature thresholds (C)
    private static final double TEMP_ADVISORY = 30.0;
    private static final double TEMP_WARNING = 35.0;
    private static final double TEMP_CRITICAL = 40.0;

    // Humidity thresholds (%)
    private static final int HUMIDITY_ADVISORY = 60;
    private static final int HUMIDITY_WARNING = 70;
    private static final int HUMIDITY_CRITICAL = 85;

    /**
     * Evaluates all risk dimensions for the supplied weather snapshot.
     *
     * @param data Current weather data for the target location.
     * @return An unmodifiable list of findings, one per dimension assessed
     */
    public List<RiskFinding> assess(WeatherData data) {
        List<RiskFinding> findings = new ArrayList<>();
        findings.add(assessTemperature(data.getTemperatureCelsius()));
        findings.add(assessHumidity(data.getHumidityPercent()));
        return Collections.unmodifiableList(findings);
    }

    /**
     * Returns the single highest {@link RiskLevel} across all findings,
     * useful for producing an overall status line in the report.
     */
    public RiskLevel overallLevel(List<RiskFinding> findings) {
        return findings.stream()
                .map(RiskFinding::getLevel)
                .max(Enum::compareTo)
                .orElse(RiskLevel.OK);
    }

    // Private dimension accessors

    private RiskFinding assessTemperature(double tempC) {
        if (tempC >= TEMP_CRITICAL) {
            return new RiskFinding(
                    "Temperature",
                    RiskLevel.CRITICAL,
                    String.format("%.1f °C - Extreme outdoor heat", tempC),
                    "Outdoor air temperature is extremely high. Verify that all CRAC units are " +
                            "operating at full capacity, check for hot-aisle bypass, and consider " +
                            "temporarily reducing server workloads to lower heat output."
            );
        }

        if (tempC >= TEMP_WARNING) {
            return new RiskFinding(
                    "Temperature",
                    RiskLevel.WARNING,
                    String.format("%.1f °C - Heatwave conditions", tempC),
                    "Outdoor temperatures are elevated. Confirm cooling systems have sufficient " +
                            "headroom and that air intake vents are unobstructed. Schedule maintenance " +
                            "during cooler hours if possible."
            );
        }

        if (tempC >= TEMP_ADVISORY) {
            return new RiskFinding(
                    "Temperature",
                    RiskLevel.ADVISORY,
                    String.format("%.1f °C - Warm conditions", tempC),
                    "Temperatures are warmer than ideal. Monitor cooling unit performance " +
                            "and confirm CRAC/CRAH setpoints have not drifted."
            );
        }

        return new RiskFinding(
                "Temperature",
                RiskLevel.OK,
                String.format("%.1f °C - Within normal range", tempC),
                "No temperature-related action required at this time."
        );
    }

    private RiskFinding assessHumidity(int humidity) {
        if (humidity >= HUMIDITY_CRITICAL) {
            return new RiskFinding(
                    "Humidity",
                    RiskLevel.CRITICAL,
                    String.format("%d %% - High condensation / corrosion risk", humidity),
                    "Relative humidity is critically high. Inspect server room seals and cable " +
                            "entry points for moisture ingress. Ensure dehumidifiers are active and " +
                            "verify dew point is well below the lowest surface temperature in the room."
            );
        }

        if (humidity >= HUMIDITY_WARNING) {
            return new RiskFinding(
                    "Humidity",
                    RiskLevel.WARNING,
                    String.format("%d %% _ Condensation warning", humidity),
                    "Humidity may cause condensation on cold server surfaces. Inspect server " +
                            "room envelope integrity and activate supplemental dehumidification if available."
            );
        }

        if (humidity >= HUMIDITY_ADVISORY) {
            return new RiskFinding(
                    "Humidity",
                    RiskLevel.ADVISORY,
                    String.format("%d %% - Elevated humidity", humidity),
                    "Humidity is approaching the cautionary threshold. Monitor HVAC humidity " +
                            "controls and watch for any signs of condensation near cold water pipes."
            );
        }

        return new RiskFinding(
                "Humidity",
                RiskLevel.OK,
                String.format("%d %% - Within normal range", humidity),
                "No humidity-related action required at this time."
        );
    }

}
