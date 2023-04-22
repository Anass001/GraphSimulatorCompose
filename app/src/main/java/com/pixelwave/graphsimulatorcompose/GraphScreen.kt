package com.pixelwave.graphsimulatorcompose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphScreen(viewModel: GraphViewModel = GraphViewModel()) {
    Column(modifier = Modifier.fillMaxWidth()) {
        val graphState = viewModel.state
        var expanded by remember {
            mutableStateOf(false)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
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
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 5.dp)
            ) {
                Text(text = "Add Nodes")
            }
            Button(
                onClick = { viewModel.clearGraph() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF44336)
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 5.dp)
            ) {
                Text(text = "Clear")
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 10.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = viewModel.state.selectedAlgorithm.toString(),
                        onValueChange = { },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        DropdownMenuItem(
                            text = { Text(text = "BFS") },
                            onClick = {
                                viewModel.selectAlgorithm(Algorithm.Bfs)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(text = "DFS") },
                            onClick = {
                                viewModel.selectAlgorithm(Algorithm.Dfs)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(text = "Dijkstra") },
                            onClick = {
                                viewModel.selectAlgorithm(Algorithm.Dijkstra)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(text = "Bellman Ford") },
                            onClick = {
                                viewModel.selectAlgorithm(Algorithm.BellmanFord)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(text = "Kruskal") },
                            onClick = {
                                viewModel.selectAlgorithm(Algorithm.Kruskal)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(text = "Prim") },
                            onClick = {
                                viewModel.selectAlgorithm(Algorithm.Prim)
                                expanded = false
                            }
                        )
                    }
                }
            }
            Button(
                onClick = { viewModel.runAlgorithm() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (graphState.algorithmRunning) Color(
                        0xFF36dfb4
                    ) else Color(
                        0xFF6650a4
                    )
                )
            ) {
                if (graphState.algorithmRunning)
                    Text(text = "Running...")
                else
                    Text(text = "Run")
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
            },
            onNodeVisited = { nodeId ->
                viewModel.addVisitedNode(nodeId)
            }
        )
    }
}