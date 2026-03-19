package com.project.risk;

/**
 * A single assessment finding produced by the {@link RiskAssessor}.
 * Each finding covers one dimension (e.g. temperature, humidity) and carries
 * a severity level, a brief label, and a plain recommendation.
 */
public class RiskFinding {
    private final String category;
    private final RiskLevel level;
    private final String detail;
    private final String recommendation;

    public RiskFinding(String category,
                       RiskLevel level,
                       String detail,
                       String recommendation) {
        this.category = category;
        this.level = level;
        this.detail = detail;
        this.recommendation = recommendation;
    }

    public String getCategory() {
        return category;
    }

    public RiskLevel getLevel() {
        return level;
    }

    public String getDetail() {
        return detail;
    }

    public String getRecommendation() {
        return recommendation;
    }
}
