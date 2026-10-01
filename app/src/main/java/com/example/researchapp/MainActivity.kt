package com.example.researchapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.example.researchapp.ui.theme.ResearchAppTheme

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
                    AppDestinations.SETTINGS -> SettingsScreen()
                    AppDestinations.PROFILE ->  ProfileScreen()
                }
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: Int,
) {

    PROFILE("Profile", R.drawable.ic_account_box),
    MAP("Map", R.drawable.ic_home),
    REPORT("Report", R.drawable.ic_home),
    SETTINGS("Settings", R.drawable.ic_account_box)
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
    Text("Map Screen")
}

@Composable
fun ReportScreen() {
    Text("Report Screen")
}

@Composable
fun SettingsScreen() {
    Text("Settings Screen")
}

@Composable
fun ProfileScreen() {
    Text("Profile Screen")
}