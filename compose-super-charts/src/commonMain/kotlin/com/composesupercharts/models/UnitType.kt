package com.composesupercharts.models

enum class UnitType(val divisor: Double) {
    PERCENTAGE(1.0),
    CURRENCY(1.0),
    CRORE(10_000_000.0),
    LAKH(100_000.0),
    THOUSAND(1_000.0),
    HUNDRED(100.0);

    companion object {
        fun fromValue(maxValue: Float): UnitType =
                when {
                    maxValue >= 10_000_000f -> CRORE
                    maxValue >= 100_000f -> LAKH
                    maxValue >= 1_000f -> THOUSAND
                    else -> HUNDRED
                }
    }
}
