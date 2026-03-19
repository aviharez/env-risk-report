package com.project.risk;

/**
 * Severity levels used by the risk assessment engine.
 * Ordered from safest to most critical so that levels can be compared with
 * {@link Enum#compareTo(Enum)} if needed.
 */
public enum RiskLevel {

    /** No environmental concern detected. */
    OK,

    /** Conditions are within acceptable limits but worth monitoring. */
    ADVISORY,

    /** Conditions may strain cooling systems; action recommended. */
    WARNING,

    /** Conditions pose an immediate threat to hardware; urgent action required. */
    CRITICAL

}
