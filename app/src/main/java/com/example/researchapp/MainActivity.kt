package com.example.researchapp

import android.R.attr.description
import android.icu.text.CaseMap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.example.researchapp.ui.theme.ResearchAppTheme
import com.google.maps.android.compose.GoogleMap

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

@PreviewScreenSizes
@Composable
fun ResearchAppApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.MAP) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            painterResource(it.icon),
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
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
                    AppDestinations.MAP -> MapScreen()
                    AppDestinations.REPORT -> ReportScreen()
                    AppDestinations.PROFILE ->  ProfileScreen()
                    AppDestinations.FEED -> FeedScreen()
                    AppDestinations.STATS -> StatsScreen()
                }
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: Int,
) {


    MAP("Map", R.drawable.outline_assistant_navigation_24),
    FEED("Feed", R.drawable.outline_feed_24),
    REPORT("Report", R.drawable.outline_report_24),
    STATS("Stats", R.drawable.outline_stats_24),
    PROFILE("Profile", R.drawable.outline_account_circle_24)
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
fun MapScreen() {
    GoogleMap()
}

@Composable
fun ReportScreen() {

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        ReportHeader()

        Spacer(modifier = Modifier.height(24.dp))

        LocationSection()

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
                // WIP
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
fun LocationSection() {
    Text(
        text = "Location",
        style = MaterialTheme.typography.titleMedium
    )

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedButton(
        onClick = {
            // We'll get the location here later
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Use Current Location")
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
