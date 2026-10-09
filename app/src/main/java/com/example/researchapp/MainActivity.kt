package com.example.researchapp

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.example.researchapp.ui.theme.ResearchAppTheme
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.maps.android.compose.GoogleMap
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.content.ContextCompat
import com.example.researchapp.data.DatabaseProvider
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.example.researchapp.data.PotholeReport
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ResearchAppTheme {
                ResearchAppApp()
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@PreviewScreenSizes
@Composable
fun ResearchAppApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.MAP) }

    val context = LocalContext.current

    val database = remember {
        DatabaseProvider.getDatabase(context)
    }

    val potholeDao = database.potholeDao()
    val scope = rememberCoroutineScope()

    var selectedLocation by remember {
        mutableStateOf<LatLng?>(null)
    }

    val potholes by potholeDao
        .getAll()
        .collectAsState(initial = emptyList())

    scope.launch {
        // suspend functions can be called here
    }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        it.icon?.let { id ->
                            Icon(
                                painterResource(id),
                                contentDescription = it.label
                            )
                        }
                    },
                    label = { it.label?.let { text -> Text(text) } },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Box(
                modifier = Modifier.padding(innerPadding)
            ) {
                when (currentDestination) {
                    AppDestinations.MAP -> MapScreen(
                        potholes = potholes,
                        onLocationSelected = { location ->
                            selectedLocation = location
                            currentDestination = AppDestinations.REPORT
                        }
                    )
                    AppDestinations.REPORT -> {
                        ReportScreen(
                            initialLocation = selectedLocation,
                            onReportSubmitted = { report ->
                                scope.launch {
                                    try {
                                        potholeDao.insert(report)
                                        selectedLocation = null
                                        // Only navigate after successful insertion
                                        currentDestination = AppDestinations.MAP

                                    } catch (e: Exception) {
                                        Log.e("ResearchApp", "Failed to save report", e)
                                    }
                                }
                            }
                        )
                    }
                    AppDestinations.PROFILE ->  ProfileScreen()
                    AppDestinations.FEED -> FeedScreen()
                    AppDestinations.STATS -> StatsScreen()
                }
            }
        }
    }
}

enum class AppDestinations(
    val label: String?,
    val icon: Int?,
) {


    MAP("Map", R.drawable.outline_assistant_navigation_24),
    FEED("Feed", R.drawable.outline_feed_24),
    REPORT("Report", R.drawable.outline_report_24),
    STATS("Stats", R.drawable.outline_stats_24),
    PROFILE("Profile", R.drawable.outline_account_circle_24),
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ResearchAppTheme {
        Greeting("Android")
    }
}

@Composable
fun MapScreen(
    potholes: List<PotholeReport>,
    onLocationSelected: (LatLng) -> Unit
) {

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(34.0007, -81.0348),
            5f
        )
    }
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapClick = { location ->
            onLocationSelected(location)
        }
    ) {


        potholes.forEach {
            potholeReport -> Marker(
                state = rememberUpdatedMarkerState(
                    position = LatLng(
                        potholeReport.latitude,
                        potholeReport.longitude
                    )
                ),
                title = "Pothole",
                snippet = potholeReport.description
            )
        }
    }
}


@Composable
fun ReportScreen(
    initialLocation: LatLng?,
    onReportSubmitted: (PotholeReport) -> Unit
) {

    var description by rememberSaveable {
        mutableStateOf("")
    }

    var severity by rememberSaveable {
        mutableFloatStateOf(0.5f)
    }

    var obscuredSelected by rememberSaveable {
        mutableStateOf(false)
    }

    var bikeLaneSelected by rememberSaveable {
        mutableStateOf(false)
    }

    var latitude by rememberSaveable {
        mutableStateOf(initialLocation?.latitude)
    }

    var longitude by rememberSaveable {
        mutableStateOf(initialLocation?.longitude)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        ReportHeader()

        Spacer(modifier = Modifier.height(24.dp))

        LocationSection(
            latitude = latitude,
            longitude = longitude,
            onLocationFound = { lat, lng ->
                latitude = lat
                longitude = lng
            })

        Spacer(modifier = Modifier.height(24.dp))

        DescriptionSection(
            description = description,
            onDescriptionChange = { description = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        SeveritySection(
            severity = severity,
            onSeverityChange = { severity = it }

        )

        Spacer(modifier = Modifier.height(24.dp))

        TagsSection(
            obscuredSelected = obscuredSelected,
            onObscuredChange = { obscuredSelected = it },
            bikeLaneSelected = bikeLaneSelected,
            onBikeLaneChange = { bikeLaneSelected = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        SubmitButton(
            onSubmit = {
                val lat = latitude
                val lng = longitude

                if (lat != null && lng != null) {
                    val report = PotholeReport (
                        latitude = lat,
                        longitude = lng,
                        description = description,
                        severity = severity,
                        bikeLane = bikeLaneSelected,
                        obscured = obscuredSelected
                    )
                    onReportSubmitted(report)
                }
            }
        )
    }
}

@Composable
fun SettingsScreen() {
    Text("Settings Screen")
}

@Composable
fun ProfileScreen() {
    Text("Profile Screen")
}

@Composable
fun FeedScreen() {
    Text("Feed Screen")
}

@Composable
fun StatsScreen() {
    Text("Stats Screen")
}

@Composable
fun ReportHeader() {
    Text(
        text = "Report Pothole",
        style = MaterialTheme.typography.headlineMedium
    )
}

@Composable
fun LocationSection(
    latitude: Double?,
    longitude: Double?,
    onLocationFound: (Double, Double) -> Unit
) {

    val context = LocalContext.current

    val fusedLocationClient =
        remember {
            LocationServices.getFusedLocationProviderClient(context)
        }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            null
        ).addOnSuccessListener { location ->

            if (location != null) {
                onLocationFound(
                    location.latitude,
                    location.longitude
                )
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->

        val fineLocationGranted =
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true

        val coarseLocationGranted =
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineLocationGranted || coarseLocationGranted) {
            getCurrentLocation()
        }
    }

    Text(
        text = "Location",
        style = MaterialTheme.typography.titleMedium
    )

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedButton(
        onClick = {
            val fineLocationGranted =
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

            val coarseLocationGranted =
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

            if (fineLocationGranted || coarseLocationGranted) {
                getCurrentLocation()
            } else {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Use Current Location")
    }

    if (latitude != null && longitude != null) {
        Text(
            text = "Latitude: $latitude\nLongitude: $longitude"
        )
    }
}

@Composable
fun DescriptionSection(
    description: String,
    onDescriptionChange: (String) -> Unit
) {

    Text(
        text = "Description",
        style = MaterialTheme.typography.titleMedium
    )

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = description,
        onValueChange = onDescriptionChange,
        placeholder = {
            Text("Describe the pothole or hazard...")
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun SeveritySection(severity: Float,
                    onSeverityChange: (Float) -> Unit
) {
    Text(
        text = "Severity",
        style = MaterialTheme.typography.titleMedium
    )

    Slider(
        value = severity,
        onValueChange = onSeverityChange,
        valueRange = 0f..1f
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Minor")
        Text("Critical")
    }
}

@Composable
fun TagsSection(obscuredSelected: Boolean,
                onObscuredChange: (Boolean) -> Unit,
                bikeLaneSelected: Boolean,
                onBikeLaneChange: (Boolean) -> Unit
) {
    Text(
        text = "Tags",
        style = MaterialTheme.typography.titleMedium
    )

    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        FilterChip(
            selected = obscuredSelected,
            onClick = { onObscuredChange(!obscuredSelected) },
            label = { Text("Obscured") }
        )

        FilterChip(
            selected = bikeLaneSelected,
            onClick = { onBikeLaneChange(!bikeLaneSelected) },
            label = { Text("Bike Lane") }
        )
    }
}

@Composable
fun SubmitButton(
    onSubmit: () -> Unit
) {

    Button(
        onClick = onSubmit,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Submit Report")
    }
}
