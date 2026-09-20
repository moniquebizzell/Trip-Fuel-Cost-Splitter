package com.example.model

enum class UnitSystem(
    val distanceUnit: String,
    val distanceUnitPlural: String,
    val fuelUnit: String,
    val fuelUnitPlural: String,
    val economyUnit: String,
    val priceUnit: String
) {
    IMPERIAL(
        distanceUnit = "mi",
        distanceUnitPlural = "miles",
        fuelUnit = "gal",
        fuelUnitPlural = "gallons",
        economyUnit = "MPG",
        priceUnit = "$/gal"
    ),
    METRIC(
        distanceUnit = "km",
        distanceUnitPlural = "km",
        fuelUnit = "L",
        fuelUnitPlural = "liters",
        economyUnit = "km/L",
        priceUnit = "$/L"
    )
}

data class CalculationResult(
    val fuelNeeded: Double = 0.0,
    val totalCost: Double = 0.0,
    val costPerPerson: Double = 0.0,
    val costPerUnitDistance: Double = 0.0,
    val isValid: Boolean = true,
    val validationMessage: String? = null
)

data class TripPreset(
    val name: String,
    val distance: String,
    val economy: String,
    val price: String,
    val passengers: Int,
    val description: String
)
