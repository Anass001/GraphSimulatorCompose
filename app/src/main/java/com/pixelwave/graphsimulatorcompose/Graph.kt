package com.pixelwave.graphsimulatorcompose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Graph(
    modifier: Modifier = Modifier,
    state: GraphState,
    onNodeAdded: (Offset) -> Unit,
    onLineAdded: (Line) -> Unit,
    onAlgorithmFinished: () -> Unit,
    onNodeVisited: (Int) -> Unit
) {
    val selectedNodes = remember { mutableStateListOf<Int>() }

    val graphState = remember { mutableStateOf(GraphState()) }
    graphState.value = state

    val title = remember {
        mutableStateOf("")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { tapOffset ->
                    if (!graphState.value.canAddNodes) {
                        val node = graphState.value.nodeInfo.find { node ->
                            node.position.x.toDp() - 25.dp <= tapOffset.x.toDp() && tapOffset.x.toDp() <= node.position.x.toDp() + 25.dp &&
                                    node.position.y.toDp() - 25.dp <= tapOffset.y.toDp() && tapOffset.y.toDp() <= node.position.y.toDp() + 25.dp
                        }
                        if (node != null) {
                            if (selectedNodes.size < 2) {
                                val nodeId = graphState.value.nodeInfo.indexOf(node) + 1
                                if (!selectedNodes.contains(nodeId)) {
                                    selectedNodes.add(nodeId)
                                }
                            }
                            if (selectedNodes.size == 2) {
                                val startNode = selectedNodes[0]
                                val endNode = selectedNodes[1]
                                if (startNode != endNode) {
                                    val line = Line(startNode, endNode)
                                    if (!graphState.value.lines.contains(line)) {
                                        onLineAdded(line)
                                    }
                                }
                                selectedNodes.clear()
                            }
                        } else {
                            selectedNodes.clear()
                        }
                    } else {
                        onNodeAdded(tapOffset)
                    }
                    // to force recomposition :) don't judge
                    title.value = title.value.plus(" ")
                }
            }
    ) {
        Text(
            text = "Visited Nodes: ${graphState.value.visitedNodes.size}"
        )
        graphState.value.nodeInfo.forEachIndexed { index, node ->
            Node(
                value = index + 1,
                nodeColor = if (selectedNodes.contains(index + 1)) MaterialTheme.colorScheme.secondary else node.color,
                position = node.position,
            )
        }
        Text(
            text = title.value,
            color = Color.White,
            fontSize = 5.sp,
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            graphState.value.lines.forEach { line ->
                val startNode =
                    graphState.value.nodeInfo[line.startNode - 1].position
                val endNode = graphState.value.nodeInfo[line.endNode - 1].position
                drawLineBetweenNodes(startNode, endNode, Color(0xFF36dfb4), 10f)
            }
        }

        when (graphState.value.activeAlgorithm) {
            Algorithm.Bfs -> {
                val coroutineScope = rememberCoroutineScope()
                selectedNodes.clear()
                LaunchedEffect(Unit) {

                    val adjacencyList = extractAdjacencyList(graphState.value.lines)
                    val visitedNodes = graphState.value.visitedNodes
                    val queue = mutableListOf<Int>()
                    val startNode = 1
                    queue.add(startNode)
                    while (queue.isNotEmpty()) {

                        val currentNode = queue.removeFirst()

                        coroutineScope.launch {
                            graphState.value.nodeInfo[currentNode - 1] =
                                graphState.value.nodeInfo[currentNode - 1].copy(
                                    color = Color(0xFF36dfb4)
                                )
                        }

                        if (!visitedNodes.contains(currentNode)) {
                            visitedNodes.add(currentNode)
                            val neighbours = adjacencyList[currentNode]
                            if (neighbours != null) {
                                for (neighbour in neighbours) {
                                    queue.add(neighbour)
                                }
                            }
                            graphState.value = graphState.value.copy(
                                visitedNodes = visitedNodes,
                                canAddNodes = false
                            )
                        }
                        delay(500)
                    }

                    title.value = title.value.plus(" ")
                }
            }

            Algorithm.Dfs -> {
                val coroutineScope = rememberCoroutineScope()
                selectedNodes.clear()
                LaunchedEffect(Unit) {
                    val adjacencyList = extractAdjacencyList(graphState.value.lines)
                    val stack = mutableListOf<Int>()
                    val startNode = 1
                    stack.add(startNode)
                    while (stack.isNotEmpty()) {

                        val currentNode = stack.removeLast()

                        coroutineScope.launch {
                            graphState.value.nodeInfo[currentNode - 1] =
                                graphState.value.nodeInfo[currentNode - 1].copy(
                                    color = Color(0xFF36dfb4)
                                )
                        }

                        if (!graphState.value.visitedNodes.contains(currentNode)) {
                            title.value = title.value.plus(" ")
                            onNodeVisited(currentNode)
                            val neighbours = adjacencyList[currentNode]
                            if (neighbours != null) {
                                for (neighbour in neighbours) {
                                    stack.add(neighbour)
                                }
                            }
                            graphState.value = graphState.value.copy(
                                visitedNodes = graphState.value.visitedNodes,
                                canAddNodes = false
                            )
                        }
                        delay(500)
                    }
                }
            }

            else -> {
            }
        }

        if (graphState.value.visitedNodes.size == graphState.value.nodeInfo.size) {
            onAlgorithmFinished()
        }
    }
}

fun DrawScope.drawLineBetweenNodes(start: Offset, end: Offset, color: Color, strokeWidth: Float) {
    drawLine(
        color = color,
        strokeWidth = strokeWidth,
        start = start,
        end = end
    )
}