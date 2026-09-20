package com.example.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TripPreset
import com.example.model.UnitSystem
import com.example.ui.theme.HighlightBadgeBgDark
import com.example.ui.theme.HighlightBadgeBgLight
import com.example.ui.theme.HighlightGlowAmber
import com.example.ui.theme.HighlightGlowTeal
import com.example.ui.theme.PrimaryLight
import com.example.ui.theme.SecondaryLight
import com.example.ui.theme.SplitCardBgDark
import com.example.ui.theme.SplitCardBgLight
import com.example.ui.theme.SplitCardBorderDark
import com.example.ui.theme.SplitCardBorderLight
import com.example.ui.theme.TripCostCardBgDark
import com.example.ui.theme.TripCostCardBgLight
import com.example.ui.theme.TripCostCardBorderDark
import com.example.ui.theme.TripCostCardBorderLight
import com.example.viewmodel.FuelEstimatorUiState
import com.example.viewmodel.FuelEstimatorViewModel
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FuelEstimatorScreen(
    viewModel: FuelEstimatorViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    fun copySummaryToClipboard() {
        val summaryText = formatTripSummary(uiState)
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Trip Fuel Estimate", summaryText)
        clipboard.setPrimaryClip(clip)
        scope.launch {
            snackbarHostState.showSnackbar("Trip summary copied to clipboard!")
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsCar,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Fuel Splitter",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Road Trip Estimator",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.resetDefaults()
                            focusManager.clearFocus()
                            scope.launch {
                                snackbarHostState.showSnackbar("Values reset to default")
                            }
                        },
                        modifier = Modifier.testTag("reset_trip_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Reset values"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            // Persistent High-Contrast Breakdown Bar: ALWAYS visible on screen
            StickyTripCostBottomBar(
                uiState = uiState,
                onShare = { copySummaryToClipboard() },
                onScrollToBreakdown = {
                    scope.launch {
                        scrollState.animateScrollTo(scrollState.maxValue)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 640.dp)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Unit System Segmented Button Row (Miles / Kilometers)
                UnitSystemSelector(
                    selectedUnit = uiState.unitSystem,
                    onUnitSelected = { viewModel.setUnitSystem(it) }
                )

                // Input Section Card
                InputsCard(
                    distance = uiState.distance,
                    fuelEconomy = uiState.fuelEconomy,
                    gasPrice = uiState.gasPrice,
                    passengers = uiState.passengers,
                    unitSystem = uiState.unitSystem,
                    onDistanceChange = viewModel::onDistanceChange,
                    onFuelEconomyChange = viewModel::onFuelEconomyChange,
                    onGasPriceChange = viewModel::onGasPriceChange,
                    onIncrementPassengers = viewModel::incrementPassengers,
                    onDecrementPassengers = viewModel::decrementPassengers,
                    onSetPassengers = viewModel::setPassengers
                )

                // Output Dashboard Section: Placed immediately after parameters so it is directly visible!
                OutputDashboardSection(
                    uiState = uiState,
                    onCopySummary = { copySummaryToClipboard() }
                )

                // Quick Trip Presets
                TripPresetsSection(
                    unitSystem = uiState.unitSystem,
                    onSelectPreset = { preset ->
                        viewModel.applyPreset(preset)
                        focusManager.clearFocus()
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

/**
 * Persistent high-contrast sticky bottom dock that highlights the calculation breakdown live at all times.
 */
@Composable
fun StickyTripCostBottomBar(
    uiState: FuelEstimatorUiState,
    onShare: () -> Unit,
    onScrollToBreakdown: () -> Unit,
    modifier: Modifier = Modifier
) {
    val result = uiState.calculationResult
    val isDark = isSystemInDarkTheme()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("sticky_trip_cost_bottom_bar")
            .clickable { onScrollToBreakdown() },
        color = if (isDark) Color(0xFF102327) else Color(0xFFE3F6F8),
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
        border = BorderStroke(
            1.5.dp,
            if (result.isValid) HighlightGlowTeal else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left side: Total Trip Cost
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (result.isValid) HighlightGlowTeal else MaterialTheme.colorScheme.outline)
                        )
                        Text(
                            text = "TOTAL COST",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            ),
                            color = if (isDark) HighlightGlowTeal else PrimaryLight
                        )
                    }

                    Text(
                        text = if (result.isValid) formatCurrency(result.totalCost) else "$--.--",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.testTag("sticky_total_cost_text")
                    )

                    if (result.isValid && result.fuelNeeded > 0) {
                        Text(
                            text = "${String.format(Locale.US, "%.1f", result.fuelNeeded)} ${uiState.unitSystem.fuelUnitPlural}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Vertical Divider
                Box(
                    modifier = Modifier
                        .height(38.dp)
                        .width(1.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Right side: Each Person Owes
                Column(
                    modifier = Modifier.weight(1.3f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (result.isValid) HighlightGlowAmber else MaterialTheme.colorScheme.outline)
                        )
                        Text(
                            text = if (uiState.passengers == 1) "SOLO TRIP" else "${uiState.passengers}-WAY SPLIT",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            ),
                            color = if (isDark) HighlightGlowAmber else SecondaryLight
                        )
                    }

                    Text(
                        text = if (result.isValid) "${formatCurrency(result.costPerPerson)} each" else "$--.-- each",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black
                        ),
                        color = if (isDark) HighlightGlowAmber else SecondaryLight,
                        modifier = Modifier.testTag("sticky_split_cost_text")
                    )

                    Text(
                        text = "Tap to view full breakdown ▾",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Share Button
                if (result.isValid && result.totalCost > 0.0) {
                    IconButton(
                        onClick = onShare,
                        modifier = Modifier
                            .testTag("sticky_share_button")
                            .size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Share breakdown",
                            tint = if (isDark) HighlightGlowAmber else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UnitSystemSelector(
    selectedUnit: UnitSystem,
    onUnitSelected: (UnitSystem) -> Unit,
    modifier: Modifier = Modifier
) {
    SingleChoiceSegmentedButtonRow(
        modifier = modifier.fillMaxWidth()
    ) {
        SegmentedButton(
            selected = selectedUnit == UnitSystem.IMPERIAL,
            onClick = { onUnitSelected(UnitSystem.IMPERIAL) },
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
            modifier = Modifier.testTag("unit_toggle_imperial")
        ) {
            Text(
                text = "Miles & Gallons (US)",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
            )
        }
        SegmentedButton(
            selected = selectedUnit == UnitSystem.METRIC,
            onClick = { onUnitSelected(UnitSystem.METRIC) },
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
            modifier = Modifier.testTag("unit_toggle_metric")
        ) {
            Text(
                text = "Kilometers & Liters",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InputsCard(
    distance: String,
    fuelEconomy: String,
    gasPrice: String,
    passengers: Int,
    unitSystem: UnitSystem,
    onDistanceChange: (String) -> Unit,
    onFuelEconomyChange: (String) -> Unit,
    onGasPriceChange: (String) -> Unit,
    onIncrementPassengers: () -> Unit,
    onDecrementPassengers: () -> Unit,
    onSetPassengers: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Trip Parameters",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )

            // 1. Distance Input
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                OutlinedTextField(
                    value = distance,
                    onValueChange = onDistanceChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("distance_input"),
                    label = { Text("Trip Distance") },
                    placeholder = { Text("e.g. 350") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Route,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = unitSystem.distanceUnitPlural,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (distance.isNotEmpty()) {
                                IconButton(
                                    onClick = { onDistanceChange("") },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear distance",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Quick Distance Presets
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val presets = if (unitSystem == UnitSystem.IMPERIAL) {
                        listOf("50", "150", "300", "500", "800")
                    } else {
                        listOf("80", "250", "500", "800", "1200")
                    }
                    presets.forEach { presetVal ->
                        FilterChip(
                            selected = distance == presetVal,
                            onClick = { onDistanceChange(presetVal) },
                            label = {
                                Text("$presetVal ${unitSystem.distanceUnit}")
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }

            // 2. Car Fuel Economy Input
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                OutlinedTextField(
                    value = fuelEconomy,
                    onValueChange = onFuelEconomyChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("fuel_economy_input"),
                    label = { Text("Car Fuel Economy") },
                    placeholder = { Text("e.g. 28") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = unitSystem.economyUnit,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (fuelEconomy.isNotEmpty()) {
                                IconButton(
                                    onClick = { onFuelEconomyChange("") },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear fuel economy",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Quick Economy Presets
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val ecoPresets = if (unitSystem == UnitSystem.IMPERIAL) {
                        listOf("20" to "Truck", "28" to "Sedan", "36" to "Compact", "48" to "Hybrid")
                    } else {
                        listOf("8.5" to "SUV", "12" to "Sedan", "15" to "Compact", "20" to "Hybrid")
                    }
                    ecoPresets.forEach { (ecoVal, label) ->
                        FilterChip(
                            selected = fuelEconomy == ecoVal,
                            onClick = { onFuelEconomyChange(ecoVal) },
                            label = { Text("$ecoVal ($label)") },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }

            // 3. Gas Price Input
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                OutlinedTextField(
                    value = gasPrice,
                    onValueChange = onGasPriceChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("gas_price_input"),
                    label = { Text("Gas / Fuel Price") },
                    placeholder = { Text("e.g. 3.75") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.AttachMoney,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = unitSystem.priceUnit,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (gasPrice.isNotEmpty()) {
                                IconButton(
                                    onClick = { onGasPriceChange("") },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear gas price",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            // 4. Passengers Stepper Control
            PassengersStepperSection(
                passengers = passengers,
                onIncrement = onIncrementPassengers,
                onDecrement = onDecrementPassengers,
                onSetPassengers = onSetPassengers
            )
        }
    }
}

@Composable
fun PassengersStepperSection(
    passengers: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onSetPassengers: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Passengers",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (passengers == 1) "Solo traveler (no split)" else "Splitting trip with $passengers people",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Stepper Control [-] Count [+]
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    FilledIconButton(
                        onClick = onDecrement,
                        enabled = passengers > 1,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("passenger_stepper_decrement"),
                        shape = CircleShape,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            disabledContentColor = MaterialTheme.colorScheme.outline
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease passengers",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .widthIn(min = 40.dp)
                            .testTag("passenger_count_text"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$passengers",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            textAlign = TextAlign.Center
                        )
                    }

                    FilledIconButton(
                        onClick = onIncrement,
                        enabled = passengers < 20,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("passenger_stepper_increment"),
                        shape = CircleShape,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase passengers",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Quick passenger selection chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(1 to "Solo", 2 to "2", 3 to "3", 4 to "4", 5 to "5").forEach { (num, label) ->
                FilterChip(
                    selected = passengers == num,
                    onClick = { onSetPassengers(num) },
                    label = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = if (num == 1) Icons.Default.Person else Icons.Default.People,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(label)
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
    }
}

/**
 * Output Dashboard: Prominently displays the visual breakdown of trip costs:
 * 1. Live Status & Header with Share action
 * 2. Visual Trip Expense Journey Flow
 * 3. Visual Passenger Split Visualizer (Avatar & Share grid)
 * 4. High-Contrast 'Total Trip Cost' Card
 * 5. High-Contrast 'Each Passenger Owes' Card
 */
@Composable
fun OutputDashboardSection(
    uiState: FuelEstimatorUiState,
    onCopySummary: () -> Unit,
    modifier: Modifier = Modifier
) {
    val result = uiState.calculationResult
    val isDark = isSystemInDarkTheme()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Prominent Header with Live Status Tag
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isDark) HighlightBadgeBgDark else HighlightBadgeBgLight,
                    border = BorderStroke(1.dp, if (isDark) HighlightGlowTeal else PrimaryLight)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = if (isDark) HighlightGlowTeal else PrimaryLight,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "LIVE BREAKDOWN",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            ),
                            color = if (isDark) HighlightGlowTeal else PrimaryLight
                        )
                    }
                }

                Text(
                    text = "Trip Cost Breakdown",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (result.isValid && result.totalCost > 0.0) {
                IconButton(
                    onClick = onCopySummary,
                    modifier = Modifier.testTag("copy_trip_summary_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy trip summary",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        if (!result.isValid && result.validationMessage != null) {
            Surface(
                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalGasStation,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = result.validationMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        // 1. Visual Expense Flow: Visual Pipeline showing input-to-split progression
        if (result.isValid && result.totalCost > 0.0) {
            VisualExpenseFlowCard(
                distance = uiState.distance,
                distanceUnit = uiState.unitSystem.distanceUnit,
                fuelNeeded = result.fuelNeeded,
                fuelUnit = uiState.unitSystem.fuelUnitPlural,
                totalCost = result.totalCost,
                costPerPerson = result.costPerPerson,
                passengers = uiState.passengers,
                isDark = isDark
            )

            // 2. Visual Passenger Split Visualizer (Avatars & Equal Shares)
            if (uiState.passengers > 1) {
                VisualPassengerSplitCard(
                    passengers = uiState.passengers,
                    costPerPerson = result.costPerPerson,
                    totalCost = result.totalCost,
                    isDark = isDark
                )
            }
        }

        // 3. Result Card 1: TOTAL TRIP COST (High-Contrast Petroleum/Teal Card)
        TotalTripCostCard(
            totalCost = if (result.isValid) result.totalCost else 0.0,
            fuelNeeded = if (result.isValid) result.fuelNeeded else 0.0,
            costPerUnit = if (result.isValid) result.costPerUnitDistance else 0.0,
            unitSystem = uiState.unitSystem,
            isDark = isDark
        )

        // 4. Result Card 2: EACH PASSENGER OWES (High-Contrast Golden/Amber Hero Card)
        EachPassengerOwesCard(
            costPerPerson = if (result.isValid) result.costPerPerson else 0.0,
            passengers = uiState.passengers,
            totalCost = if (result.isValid) result.totalCost else 0.0,
            isDark = isDark,
            onCopySummary = onCopySummary
        )
    }
}

/**
 * Visual step-by-step pipeline illustrating the road trip calculation journey.
 */
@Composable
fun VisualExpenseFlowCard(
    distance: String,
    distanceUnit: String,
    fuelNeeded: Double,
    fuelUnit: String,
    totalCost: Double,
    costPerPerson: Double,
    passengers: Int,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (isDark) Color(0xFF142427) else Color(0xFFEFFBFC),
        border = BorderStroke(1.dp, if (isDark) HighlightGlowTeal.copy(alpha = 0.4f) else PrimaryLight.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "CALCULATION FLOW",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                ),
                color = if (isDark) HighlightGlowTeal else PrimaryLight
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Step 1: Distance
                FlowStepItem(
                    label = "Trip",
                    value = "$distance $distanceUnit",
                    icon = Icons.Default.Route,
                    color = MaterialTheme.colorScheme.primary
                )

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.outline
                )

                // Step 2: Fuel Needed
                FlowStepItem(
                    label = "Fuel Needed",
                    value = "${String.format(Locale.US, "%.1f", fuelNeeded)} $fuelUnit",
                    icon = Icons.Default.LocalGasStation,
                    color = MaterialTheme.colorScheme.primary
                )

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.outline
                )

                // Step 3: Total Cost
                FlowStepItem(
                    label = "Total Cost",
                    value = formatCurrency(totalCost),
                    icon = Icons.Default.AttachMoney,
                    color = if (isDark) HighlightGlowTeal else PrimaryLight
                )

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.outline
                )

                // Step 4: Per Person
                FlowStepItem(
                    label = if (passengers == 1) "Solo" else "Each ($passengers)",
                    value = formatCurrency(costPerPerson),
                    icon = Icons.Default.People,
                    color = if (isDark) HighlightGlowAmber else SecondaryLight
                )
            }
        }
    }
}

@Composable
private fun FlowStepItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = color
                )
            }
        }
    }
}

/**
 * Visual Passenger Split Card showing visual avatars and equal slices of the road trip bill.
 */
@Composable
fun VisualPassengerSplitCard(
    passengers: Int,
    costPerPerson: Double,
    totalCost: Double,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("visual_passenger_split_card"),
        shape = RoundedCornerShape(16.dp),
        color = if (isDark) Color(0xFF281E0C) else Color(0xFFFFF9ED),
        border = BorderStroke(1.2.dp, if (isDark) HighlightGlowAmber.copy(alpha = 0.5f) else SplitCardBorderLight)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = null,
                        tint = if (isDark) HighlightGlowAmber else SecondaryLight,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "EQUAL PASSENGER SPLIT",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = if (isDark) HighlightGlowAmber else SecondaryLight
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isDark) Color(0xFF4A3414) else Color(0xFFFFE0B2)
                ) {
                    Text(
                        text = "$passengers Travelers",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isDark) HighlightGlowAmber else SecondaryLight,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Proportional Multi-Segment Progress Bar (each passenger gets equal slice)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                val segmentColors = listOf(
                    HighlightGlowTeal,
                    HighlightGlowAmber,
                    Color(0xFF81C784),
                    Color(0xFFBA68C8),
                    Color(0xFFFF8A65),
                    Color(0xFF4DD0E1)
                )
                for (i in 0 until passengers) {
                    val color = segmentColors[i % segmentColors.size]
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .background(color)
                    )
                }
            }

            // Passenger Chips / Share Visuals
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (i in 1..passengers) {
                    val isDriver = (i == 1)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(
                            1.dp,
                            if (isDriver) (if (isDark) HighlightGlowAmber else SecondaryLight)
                            else MaterialTheme.colorScheme.outlineVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (isDriver) (if (isDark) Color(0xFF4A3414) else Color(0xFFFFE0B2))
                                else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = if (isDriver) (if (isDark) HighlightGlowAmber else SecondaryLight)
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = if (isDriver) "Driver ($i)" else "Person $i",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = formatCurrency(costPerPerson),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = if (isDark) HighlightGlowAmber else SecondaryLight
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TotalTripCostCard(
    totalCost: Double,
    fuelNeeded: Double,
    costPerUnit: Double,
    unitSystem: UnitSystem,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isDark) TripCostCardBgDark else TripCostCardBgLight
    val borderColor = if (isDark) TripCostCardBorderDark else TripCostCardBorderLight

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("total_trip_cost_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(2.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.LocalGasStation,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Text(
                        text = "Total Trip Cost",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = String.format(Locale.US, "%.1f %s", fuelNeeded, unitSystem.fuelUnitPlural),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Big Prominent Cost
            Text(
                text = formatCurrency(totalCost),
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-1).sp
                ),
                color = if (isDark) HighlightGlowTeal else MaterialTheme.colorScheme.primary,
                modifier = Modifier.testTag("total_trip_cost_value")
            )

            HorizontalDivider(
                color = borderColor.copy(alpha = 0.5f),
                modifier = Modifier.padding(vertical = 2.dp)
            )

            // Secondary Metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Fuel Needed: ${String.format(Locale.US, "%.2f", fuelNeeded)} ${unitSystem.fuelUnitPlural}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${formatCurrency(costPerUnit)} / ${unitSystem.distanceUnit}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun EachPassengerOwesCard(
    costPerPerson: Double,
    passengers: Int,
    totalCost: Double,
    isDark: Boolean,
    onCopySummary: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isDark) SplitCardBgDark else SplitCardBgLight
    val borderColor = if (isDark) SplitCardBorderDark else SplitCardBorderLight

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("each_passenger_owes_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(2.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Text(
                        text = "Each Passenger Owes",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = if (passengers == 1) "1 Person" else "$passengers-Way Split",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // High-Contrast Per-Person Amount
            Text(
                text = formatCurrency(costPerPerson),
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1).sp
                ),
                color = if (isDark) HighlightGlowAmber else MaterialTheme.colorScheme.secondary,
                modifier = Modifier.testTag("each_passenger_owes_value")
            )

            HorizontalDivider(
                color = borderColor.copy(alpha = 0.5f),
                modifier = Modifier.padding(vertical = 2.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (passengers == 1) {
                        "Driver pays full amount: ${formatCurrency(totalCost)}"
                    } else {
                        "Divided evenly among $passengers people"
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    onClick = onCopySummary,
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                    border = BorderStroke(1.dp, borderColor.copy(alpha = 0.7f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Share",
                            tint = if (isDark) HighlightGlowAmber else MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Share",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (isDark) HighlightGlowAmber else MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TripPresetsSection(
    unitSystem: UnitSystem,
    onSelectPreset: (TripPreset) -> Unit,
    modifier: Modifier = Modifier
) {
    val presets = if (unitSystem == UnitSystem.IMPERIAL) {
        listOf(
            TripPreset("Day Trip", "120", "30", "3.65", 2, "120 mi • 2 people"),
            TripPreset("Weekend Getaway", "340", "28", "3.75", 4, "340 mi • 4 people"),
            TripPreset("Cross-State Road Trip", "850", "26", "3.85", 4, "850 mi • 4 people")
        )
    } else {
        listOf(
            TripPreset("Day Trip", "200", "13", "1.65", 2, "200 km • 2 people"),
            TripPreset("Weekend Getaway", "550", "12", "1.70", 4, "550 km • 4 people"),
            TripPreset("Cross-Country", "1400", "11", "1.75", 4, "1400 km • 4 people")
        )
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Quick Itinerary Ideas",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            presets.forEach { preset ->
                OutlinedCard(
                    onClick = { onSelectPreset(preset) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = preset.name,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = preset.description,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

private fun formatCurrency(amount: Double): String {
    return String.format(Locale.US, "$%.2f", amount)
}

private fun formatTripSummary(state: FuelEstimatorUiState): String {
    val res = state.calculationResult
    val unit = state.unitSystem
    return buildString {
        appendLine("🚗 Road Trip Fuel Split")
        appendLine("• Distance: ${state.distance} ${unit.distanceUnitPlural}")
        appendLine("• Fuel Economy: ${state.fuelEconomy} ${unit.economyUnit}")
        appendLine("• Fuel Needed: ${String.format(Locale.US, "%.1f", res.fuelNeeded)} ${unit.fuelUnitPlural}")
        appendLine("• Gas Price: $${state.gasPrice}/${unit.fuelUnit}")
        appendLine("• Total Trip Cost: ${formatCurrency(res.totalCost)}")
        appendLine("• Split (${state.passengers} travelers): ${formatCurrency(res.costPerPerson)} each")
    }
}

