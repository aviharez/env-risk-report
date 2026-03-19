package com.project.report;

import com.project.model.WeatherData;
import com.project.risk.RiskFinding;
import com.project.risk.RiskLevel;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Formats a completed environmental assessment into a plain-text
 * "Site Environmental Audit" report suitable for on-screen display
 * or saving to a file
 */
public class ReportGenerator {

    private static final int LINE_WIDTH = 70;
    private static final String BORDER = "=".repeat(LINE_WIDTH);
    private static final String DIVIDER = "-".repeat(LINE_WIDTH);

    /**
     * Builds and returns the full report as a single formatted string.
     *
     * @param data Weather data used for the assessment.
     * @param findings Individual risk findings produced by the assessor.
     * @param overall The highest severity level across all findings.
     * @return Multi-line report string ready for printing.
     */
    public String generate(WeatherData data, List<RiskFinding> findings, RiskLevel overall) {
        StringBuilder sb = new StringBuilder();
        String timestamp = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z"));

        appendLine(sb, BORDER);
        appendCentered(sb, "HARDWAREHERO");
        appendCentered(sb, "Site Environmental Audit Report");
        appendLine(sb, BORDER);

        // Location & timestamp
        appendLine(sb, "  Location  : " + data.getCityName() + ", " + data.getCountryCode());
        appendLine(sb, "  Conditions: " + capitalize(data.getWeatherDescription()));
        appendLine(sb, "  Generated : " + timestamp);
        appendLine(sb, DIVIDER);

        // Live readings
        appendCentered(sb, "CURRENT READINGS");
        appendLine(sb, "");
        appendLine(sb, String.format("  %-22s %.1f °C (feels like %.1f °C)", "Temperature:", data.getTemperatureCelsius(), data.getFeelsLikeCelsius()));
        appendLine(sb, String.format("  %-22s %d %%", "Relative Humidity:", data.getHumidityPercent()));
        appendLine(sb, DIVIDER);

        // Overall status
        appendCentered(sb, "OVERALL STATUS: " + formatOverall(overall));
        appendLine(sb, DIVIDER);

        // Individual findings
        appendCentered(sb, "RISK FINDINGS");
        appendLine(sb, "");

        for (RiskFinding finding : findings) {
            appendLine(sb, "  [" + levelBadge(finding.getLevel()) + "] " + finding.getCategory());
            appendLine(sb, "     Reading : " + finding.getDetail());
            appendLine(sb, "     Action  : " + wrapText(finding.getRecommendation(), 55, "             "));
            appendLine(sb, "");
        }

        appendLine(sb, DIVIDER);

        // Footer
        appendLine(sb, "  This report was generated automatically by HardwareHero.");
        appendLine(sb, "  Weather data provided by OpenWeatherMap (openweathermap.org).");
        appendLine(sb, "  For urgent concerns, contact your HardwareHero technician.");
        appendLine(sb, BORDER);

        return sb.toString();

    }

    // Private formatting helpers

    private String formatOverall(RiskLevel level) {
        return switch (level) {
            case OK -> "ALL CLEAR - No immediate environmental concerns.";
            case ADVISORY -> "ADVISORY - Minor concerns detected; monitor closely.";
            case WARNING -> "WARNING - Conditions require prompt attention.";
            case CRITICAL -> "CRITICAL - Immediate action required to protect hardware.";
        };
    }

    private String levelBadge(RiskLevel level) {
        return switch (level) {
            case OK -> " OK  ";
            case ADVISORY -> " ADV ";
            case WARNING -> "WARN ";
            case CRITICAL -> "CRIT!";
        };
    }

    private void appendLine(StringBuilder sb, String line) {
        sb.append(line).append(System.lineSeparator());
    }

    private void appendCentered(StringBuilder sb, String text) {
        int padding = Math.max(0, (LINE_WIDTH - text.length()) / 2);
        sb.append(" ".repeat(padding)).append(text).append(System.lineSeparator());
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private String wrapText(String text, int maxWidth, String indent) {
        if (text.length() <= maxWidth) return text;
        StringBuilder result = new StringBuilder();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        for (String word : words) {
            if (line.length() + word.length() + 1 > maxWidth && line.length() > 0) {
                result.append(line).append(System.lineSeparator()).append(indent);
                line = new StringBuilder();
            }
            if (line.length() > 0) line.append(" ");
            line.append(word);
        }
        result.append(line);
        return result.toString();
    }

}
