package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.CalculationResult
import com.example.model.TripPreset
import com.example.model.UnitSystem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class FuelEstimatorUiState(
    val distance: String = "320",
    val fuelEconomy: String = "28",
    val gasPrice: String = "3.75",
    val passengers: Int = 4,
    val unitSystem: UnitSystem = UnitSystem.IMPERIAL,
    val calculationResult: CalculationResult = CalculationResult()
)

class FuelEstimatorViewModel : ViewModel() {

    private val _distance = MutableStateFlow("320")
    private val _fuelEconomy = MutableStateFlow("28")
    private val _gasPrice = MutableStateFlow("3.75")
    private val _passengers = MutableStateFlow(4)
    private val _unitSystem = MutableStateFlow(UnitSystem.IMPERIAL)

    val uiState: StateFlow<FuelEstimatorUiState> = combine(
        _distance,
        _fuelEconomy,
        _gasPrice,
        _passengers,
        _unitSystem
    ) { distance, economy, price, passengers, unitSystem ->
        val safePassengers = passengers.coerceAtLeast(1)
        val result = calculate(
            distanceStr = distance,
            economyStr = economy,
            priceStr = price,
            passengers = safePassengers
        )
        FuelEstimatorUiState(
            distance = distance,
            fuelEconomy = economy,
            gasPrice = price,
            passengers = safePassengers,
            unitSystem = unitSystem,
            calculationResult = result
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = calculateInitialState()
    )

    fun onDistanceChange(newDistance: String) {
        val sanitized = sanitizeDecimalInput(newDistance)
        _distance.value = sanitized
    }

    fun onFuelEconomyChange(newEconomy: String) {
        val sanitized = sanitizeDecimalInput(newEconomy)
        _fuelEconomy.value = sanitized
    }

    fun onGasPriceChange(newPrice: String) {
        val sanitized = sanitizeDecimalInput(newPrice)
        _gasPrice.value = sanitized
    }

    fun incrementPassengers() {
        if (_passengers.value < 20) {
            _passengers.value += 1
        }
    }

    fun decrementPassengers() {
        if (_passengers.value > 1) {
            _passengers.value -= 1
        }
    }

    fun setPassengers(count: Int) {
        _passengers.value = count.coerceIn(1, 20)
    }

    fun setUnitSystem(system: UnitSystem) {
        if (_unitSystem.value != system) {
            _unitSystem.value = system
            // Adjust sample values smoothly when switching units
            if (system == UnitSystem.METRIC) {
                val d = _distance.value.toDoubleOrNull()
                if (d != null && d > 0) {
                    _distance.value = (d * 1.60934).toInt().toString()
                }
                val mpg = _fuelEconomy.value.toDoubleOrNull()
                if (mpg != null && mpg > 0) {
                    // Convert MPG to km/L: 1 MPG ≈ 0.425144 km/L
                    val kmPerL = mpg * 0.425144
                    _fuelEconomy.value = String.format(java.util.Locale.US, "%.1f", kmPerL)
                }
                val priceGal = _gasPrice.value.toDoubleOrNull()
                if (priceGal != null && priceGal > 0) {
                    // Convert $/gal to $/L: 1 gal ≈ 3.78541 L
                    val pricePerL = priceGal / 3.78541
                    _gasPrice.value = String.format(java.util.Locale.US, "%.2f", pricePerL)
                }
            } else {
                val d = _distance.value.toDoubleOrNull()
                if (d != null && d > 0) {
                    _distance.value = (d / 1.60934).toInt().toString()
                }
                val kmPerL = _fuelEconomy.value.toDoubleOrNull()
                if (kmPerL != null && kmPerL > 0) {
                    val mpg = kmPerL / 0.425144
                    _fuelEconomy.value = String.format(java.util.Locale.US, "%.1f", mpg)
                }
                val pricePerL = _gasPrice.value.toDoubleOrNull()
                if (pricePerL != null && pricePerL > 0) {
                    val priceGal = pricePerL * 3.78541
                    _gasPrice.value = String.format(java.util.Locale.US, "%.2f", priceGal)
                }
            }
        }
    }

    fun applyPreset(preset: TripPreset) {
        _distance.value = preset.distance
        _fuelEconomy.value = preset.economy
        _gasPrice.value = preset.price
        _passengers.value = preset.passengers.coerceAtLeast(1)
    }

    fun resetDefaults() {
        if (_unitSystem.value == UnitSystem.IMPERIAL) {
            _distance.value = "320"
            _fuelEconomy.value = "28"
            _gasPrice.value = "3.75"
        } else {
            _distance.value = "500"
            _fuelEconomy.value = "12"
            _gasPrice.value = "1.65"
        }
        _passengers.value = 4
    }

    private fun sanitizeDecimalInput(input: String): String {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return ""
        // Keep only digits and at most one decimal point
        val sb = StringBuilder()
        var hasDecimal = false
        for (ch in trimmed) {
            if (ch.isDigit()) {
                sb.append(ch)
            } else if ((ch == '.' || ch == ',') && !hasDecimal) {
                sb.append('.')
                hasDecimal = true
            }
        }
        return sb.toString()
    }

    companion object {
        fun calculate(
            distanceStr: String,
            economyStr: String,
            priceStr: String,
            passengers: Int
        ): CalculationResult {
            val distance = distanceStr.toDoubleOrNull()
            val economy = economyStr.toDoubleOrNull()
            val price = priceStr.toDoubleOrNull()

            if (distance == null || distance <= 0.0) {
                return CalculationResult(
                    isValid = false,
                    validationMessage = "Enter trip distance"
                )
            }
            if (economy == null || economy <= 0.0) {
                return CalculationResult(
                    isValid = false,
                    validationMessage = "Enter fuel economy greater than 0"
                )
            }
            if (price == null || price <= 0.0) {
                return CalculationResult(
                    isValid = false,
                    validationMessage = "Enter fuel price greater than 0"
                )
            }

            val fuelNeeded = distance / economy
            val totalCost = fuelNeeded * price
            val safePassengers = passengers.coerceAtLeast(1)
            val costPerPerson = totalCost / safePassengers
            val costPerDistance = totalCost / distance

            return CalculationResult(
                fuelNeeded = fuelNeeded,
                totalCost = totalCost,
                costPerPerson = costPerPerson,
                costPerUnitDistance = costPerDistance,
                isValid = true,
                validationMessage = null
            )
        }

        fun calculateInitialState(): FuelEstimatorUiState {
            val dist = "320"
            val eco = "28"
            val price = "3.75"
            val pass = 4
            return FuelEstimatorUiState(
                distance = dist,
                fuelEconomy = eco,
                gasPrice = price,
                passengers = pass,
                unitSystem = UnitSystem.IMPERIAL,
                calculationResult = calculate(dist, eco, price, pass)
            )
        }
    }
}
