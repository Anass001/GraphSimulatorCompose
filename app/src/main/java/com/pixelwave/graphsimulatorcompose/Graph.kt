package com.pixelwave.graphsimulatorcompose

import android.util.Log
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
import java.util.PriorityQueue
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin

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

        if (graphState.value.algorithmRunning) {
            when (graphState.value.selectedAlgorithm) {
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

                Algorithm.Dijkstra -> {
                    val coroutineScope = rememberCoroutineScope()
                    selectedNodes.clear()
                    LaunchedEffect(Unit) {
                        val adjacencyList = extractAdjacencyList(graphState.value.lines)
                        val visitedNodes = graphState.value.visitedNodes
                        val distances = mutableMapOf<Int, Int>()
                        val previousNodes = mutableMapOf<Int, Int>()
                        val startNode = 1
                        val endNode = graphState.value.nodeInfo.size
                        for (node in graphState.value.nodeInfo) {
//                            distances[node.value] = Int.MAX_VALUE
                        }
                        distances[startNode] = 0
                        val queue = PriorityQueue<Int>(compareBy { distances[it] })
                        queue.add(startNode)
                        while (queue.isNotEmpty()) {
                            val currentNode = queue.remove()
                            if (!visitedNodes.contains(currentNode)) {
                                visitedNodes.add(currentNode)
                                val neighbours = adjacencyList[currentNode]
                                if (neighbours != null) {
                                    for (neighbour in neighbours) {
                                        val newDistance = distances[currentNode]!! + 1
                                        if (newDistance < distances[neighbour]!!) {
                                            distances[neighbour] = newDistance
                                            previousNodes[neighbour] = currentNode
                                            queue.add(neighbour)
                                        }
                                    }
                                }
                                graphState.value = graphState.value.copy(
                                    visitedNodes = visitedNodes,
                                    canAddNodes = false
                                )
                            }
                            coroutineScope.launch {
                                graphState.value.nodeInfo[currentNode - 1] =
                                    graphState.value.nodeInfo[currentNode - 1].copy(
                                        color = Color(0xFF36dfb4)
                                    )
                            }
                            delay(500)
                        }
                        var currentNode = endNode
                        while (currentNode != startNode) {
                            coroutineScope.launch {
                                graphState.value.nodeInfo[currentNode - 1] =
                                    graphState.value.nodeInfo[currentNode - 1].copy(
                                        color = Color(0xFF36dfb4)
                                    )
                            }
                            currentNode = previousNodes[currentNode]!!
                            delay(500)
                        }
                        coroutineScope.launch {
                            graphState.value.nodeInfo[currentNode - 1] =
                                graphState.value.nodeInfo[currentNode - 1].copy(
                                    color = Color(0xFF36dfb4)
                                )
                        }
                        title.value = title.value.plus(" ")
                    }
                }

                else -> {
                }
            }
        }

        if (graphState.value.visitedNodes.size == graphState.value.nodeInfo.size) {
            onAlgorithmFinished()
        }
    }
}

fun angleSign(start: Offset, end: Offset): Float {
    return if (end.x < start.x) -1f else 1f
}

fun DrawScope.drawLineBetweenNodes(start: Offset, end: Offset, color: Color, strokeWidth: Float) {
    val theta = atan((end.y - start.y) / (end.x - start.x))
    val linePointStart = start + Offset(
        angleSign(start, end)*(25.dp.toPx() * cos(theta)),
        angleSign(start, end)*(25.dp.toPx() * sin(theta))
    )
    val linePointEnd = end - Offset(
        angleSign(start, end)*(25.dp.toPx() * cos(theta)),
        angleSign(start, end)*(25.dp.toPx() * sin(theta))
    )
    drawLine(
        color = color,
        strokeWidth = strokeWidth,
        start = linePointStart,
        end = linePointEnd,
    )
    // draw a little arrow at the end of the line
    drawLine(
        color = color,
        strokeWidth = strokeWidth,
        start = linePointEnd,
        end = linePointEnd + Offset(
            angleSign(start, end)*(-10.dp.toPx() * cos(theta + 0.5f)),
            angleSign(start, end)*(-10.dp.toPx() * sin(theta + 0.5f))
        )
    )
    drawLine(
        color = color,
        strokeWidth = strokeWidth,
        start = linePointEnd,
        end = linePointEnd + Offset(
            angleSign(start, end)*(-10.dp.toPx() * cos(theta - 0.5f)),
            angleSign(start, end)*(-10.dp.toPx() * sin(theta - 0.5f))
        )
    )
    Log.i(
        "DrawLine",
        "start: $start, end: $end, theta: $theta, angleSign: ${angleSign(start, end)}"
    )
}