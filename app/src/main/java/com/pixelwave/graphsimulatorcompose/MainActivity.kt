package com.pixelwave.graphsimulatorcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.pixelwave.graphsimulatorcompose.ui.theme.GraphSimulatorComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val viewModel = GraphViewModel()
        super.onCreate(savedInstanceState)
        setContent {
            GraphSimulatorComposeTheme {
                GraphScreen(viewModel)
            }
        }
    }
}