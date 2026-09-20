package com.example

import com.example.viewmodel.FuelEstimatorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FuelEstimatorViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: FuelEstimatorViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = FuelEstimatorViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun reactiveCalculation_computesCorrectValues() = runTest(testDispatcher) {
        backgroundScope.launch { viewModel.uiState.collect {} }
        viewModel.onDistanceChange("300")
        viewModel.onFuelEconomyChange("30")
        viewModel.onGasPriceChange("3.50")
        viewModel.setPassengers(4)
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        val result = state.calculationResult

        assertTrue(result.isValid)
        assertEquals(10.0, result.fuelNeeded, 0.001)
        assertEquals(35.0, result.totalCost, 0.001)
        assertEquals(8.75, result.costPerPerson, 0.001)
    }

    @Test
    fun passengers_cannotBeLessThanOne() = runTest(testDispatcher) {
        backgroundScope.launch { viewModel.uiState.collect {} }
        viewModel.setPassengers(1)
        viewModel.decrementPassengers()
        testScheduler.advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.passengers)
    }

    @Test
    fun zeroOrEmptyInputs_handledGracefullyWithoutCrashing() = runTest(testDispatcher) {
        backgroundScope.launch { viewModel.uiState.collect {} }
        viewModel.onDistanceChange("")
        viewModel.onFuelEconomyChange("0")
        viewModel.onGasPriceChange("abc")
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.calculationResult.isValid)
        assertEquals(0.0, state.calculationResult.totalCost, 0.001)
        assertEquals(0.0, state.calculationResult.costPerPerson, 0.001)
    }

    @Test
    fun passengerIncrement_updatesPerPersonCostReactively() = runTest(testDispatcher) {
        backgroundScope.launch { viewModel.uiState.collect {} }
        viewModel.onDistanceChange("200")
        viewModel.onFuelEconomyChange("25")
        viewModel.onGasPriceChange("4.00") // 8 gallons * $4 = $32 total
        viewModel.setPassengers(2) // $16 per person
        testScheduler.advanceUntilIdle()

        assertEquals(16.0, viewModel.uiState.value.calculationResult.costPerPerson, 0.001)

        viewModel.incrementPassengers() // 3 passengers -> 32 / 3 = 10.666...
        testScheduler.advanceUntilIdle()

        assertEquals(3, viewModel.uiState.value.passengers)
        assertEquals(32.0 / 3.0, viewModel.uiState.value.calculationResult.costPerPerson, 0.001)
    }
}
