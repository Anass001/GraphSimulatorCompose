package com.pixelwave.graphsimulatorcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pixelwave.graphsimulatorcompose.ui.Graph
import com.pixelwave.graphsimulatorcompose.ui.theme.GraphSimulatorComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GraphSimulatorComposeTheme {
                val isEdgeModeActive = remember { mutableStateOf(false) }
                Graph(modifier = Modifier.fillMaxSize(), isEdgeModeActive = isEdgeModeActive)
                Button(onClick = {
                    isEdgeModeActive.value = !isEdgeModeActive.value
                }) {
                    Text(text = if (isEdgeModeActive.value) "Disable" else "Enable")
                }
            }
        }
    }
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
    GraphSimulatorComposeTheme {
        Greeting("Android")
    }
}