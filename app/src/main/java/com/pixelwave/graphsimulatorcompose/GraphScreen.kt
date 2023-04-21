package com.pixelwave.graphsimulatorcompose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GraphScreen(viewModel: GraphViewModel = GraphViewModel()) {
    Column(modifier = Modifier.fillMaxWidth()) {
        val graphState = viewModel.state
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Button(
                onClick = {
                    viewModel.toggleAddNodes()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (graphState.canAddNodes) Color(0xFF36dfb4) else Color(
                        0xFF6650a4
                    )
                )
            ) {
                Text(text = "Add Nodes")
            }
            Button(
                onClick = { viewModel.runAlgorithm(Algorithm.Bfs) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (graphState.activeAlgorithm is Algorithm.Bfs) Color(
                        0xFF36dfb4
                    ) else Color(
                        0xFF6650a4
                    )
                )
            ) {
                Text(text = "BFS")
            }
            Button(
                onClick = { viewModel.runAlgorithm(Algorithm.Dfs) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (graphState.activeAlgorithm is Algorithm.Dfs) Color(
                        0xFF36dfb4
                    ) else Color(
                        0xFF6650a4
                    )
                )
            ) {
                Text(text = "DFS")
            }
        }
        Graph(
            modifier = Modifier.fillMaxSize(),
            state = graphState,
            onNodeAdded = { offset ->
                viewModel.addNode(offset)
            },
            onLineAdded = { line ->
                viewModel.addLine(line)
            },
            onAlgorithmFinished = {
                viewModel.clearAlgorithm()
            }
        )
    }
}