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
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@OptIn(ExperimentalTextApi::class)
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
                                    val weight = floor(
                                        sqrt(
                                            (graphState.value.nodeInfo[startNode - 1].position.x - graphState.value.nodeInfo[endNode - 1].position.x).pow(
                                                2
                                            ) +
                                                    (graphState.value.nodeInfo[startNode - 1].position.y - graphState.value.nodeInfo[endNode - 1].position.y).pow(
                                                        2
                                                    )
                                        )
                                    )
                                    val line = Line(startNode, endNode, weight.toInt())
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
        val textMeasurer = rememberTextMeasurer()
        Canvas(modifier = Modifier.fillMaxSize()) {
            graphState.value.lines.forEach { line ->
                val startNode =
                    graphState.value.nodeInfo[line.startNode - 1].position
                val endNode = graphState.value.nodeInfo[line.endNode - 1].position
                drawLineBetweenNodes(startNode, endNode, Color(0xFF36dfb4), 10f)

                val center = Offset(
                    (startNode.x + endNode.x) / 2,
                    (startNode.y + endNode.y) / 2
                )
                val angle = atan(
                    (endNode.y - startNode.y) /
                            (endNode.x - startNode.x)
                )

                val distance = 25
                val x = center.x + distance * cos(angle)
                val y = center.y + distance * sin(angle)
                val offset = Offset(x.toFloat(), y.toFloat())
                drawText(textMeasurer, "${line.weight}", topLeft = offset)
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
                        val queue = mutableListOf<Pair<Int, Int>>()
                        val startNode = 1
                        queue.add(Pair(startNode, 0))
                        while (queue.isNotEmpty()) {

                            val currentNode = queue.removeFirst()

                            coroutineScope.launch {
                                graphState.value.nodeInfo[currentNode.first - 1] =
                                    graphState.value.nodeInfo[currentNode.first - 1].copy(
                                        color = Color(0xFF36dfb4)
                                    )
                            }

                            if (!visitedNodes.contains(currentNode.first)) {
                                title.value = title.value.plus(" ")
                                onNodeVisited(currentNode.first)
                                adjacencyList[currentNode.first]?.forEach {
                                    queue.add(Pair(it.first, currentNode.second + 1))
                                }
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
                        val visitedNodes = graphState.value.visitedNodes
                        val stack = mutableListOf<Pair<Int, Int>>()
                        val startNode = 1
                        stack.add(Pair(startNode, 0))
                        while (stack.isNotEmpty()) {

                            val currentNode = stack.removeLast()

                            coroutineScope.launch {
                                graphState.value.nodeInfo[currentNode.first - 1] =
                                    graphState.value.nodeInfo[currentNode.first - 1].copy(
                                        color = Color(0xFF36dfb4)
                                    )
                            }

                            if (!visitedNodes.contains(currentNode.first)) {
                                title.value = title.value.plus(" ")
                                onNodeVisited(currentNode.first)
                                adjacencyList[currentNode.first]?.forEach {
                                    stack.add(Pair(it.first, currentNode.second + 1))
                                }
                            }
                            delay(500)
                        }

                        title.value = title.value.plus(" ")
                    }
                }

                Algorithm.Dijkstra -> {
                    val coroutineScope = rememberCoroutineScope()
                    selectedNodes.clear()
                    LaunchedEffect(Unit) {

                        val adjacencyList = extractAdjacencyList(graphState.value.lines)
                        val visitedNodes = graphState.value.visitedNodes
                        val queue = mutableListOf<Pair<Int, Int>>()
                        val startNode = 1
                        queue.add(Pair(startNode, 0))
                        while (queue.isNotEmpty()) {

                            val currentNode = queue.removeFirst()

                            coroutineScope.launch {
                                graphState.value.nodeInfo[currentNode.first - 1] =
                                    graphState.value.nodeInfo[currentNode.first - 1].copy(
                                        color = Color(0xFF36dfb4)
                                    )
                            }

                            if (!visitedNodes.contains(currentNode.first)) {
                                title.value = title.value.plus(" ")
                                onNodeVisited(currentNode.first)
                                adjacencyList[currentNode.first]?.forEach {
                                    queue.add(Pair(it.first, currentNode.second + 1))
                                }
                            }
                            delay(500)
                        }

                        title.value = title.value.plus(" ")
                    }
                }

                Algorithm.BellmanFord -> {
                    val coroutineScope = rememberCoroutineScope()
                    selectedNodes.clear()
                    LaunchedEffect(Unit) {

                        val adjacencyList = extractAdjacencyList(graphState.value.lines)
                        val visitedNodes = graphState.value.visitedNodes
                        val queue = mutableListOf<Pair<Int, Int>>()
                        val startNode = 1
                        queue.add(Pair(startNode, 0))
                        while (queue.isNotEmpty()) {

                            val currentNode = queue.removeFirst()

                            coroutineScope.launch {
                                graphState.value.nodeInfo[currentNode.first - 1] =
                                    graphState.value.nodeInfo[currentNode.first - 1].copy(
                                        color = Color(0xFF36dfb4)
                                    )
                            }

                            if (!visitedNodes.contains(currentNode.first)) {
                                title.value = title.value.plus(" ")
                                onNodeVisited(currentNode.first)
                                adjacencyList[currentNode.first]?.forEach {
                                    queue.add(Pair(it.first, currentNode.second + 1))
                                }
                            }
                            delay(500)
                        }

                        title.value = title.value.plus(" ")
                    }
                }

                Algorithm.Kruskal -> {
                    val coroutineScope = rememberCoroutineScope()
                    selectedNodes.clear()
                    LaunchedEffect(Unit) {

                        val adjacencyList = extractAdjacencyList(graphState.value.lines)
                        val visitedNodes = graphState.value.visitedNodes
                        val queue = mutableListOf<Pair<Int, Int>>()
                        val startNode = 1
                        queue.add(Pair(startNode, 0))
                        while (queue.isNotEmpty()) {

                            val currentNode = queue.removeFirst()

                            coroutineScope.launch {
                                graphState.value.nodeInfo[currentNode.first - 1] =
                                    graphState.value.nodeInfo[currentNode.first - 1].copy(
                                        color = Color(0xFF36dfb4)
                                    )
                            }

                            if (!visitedNodes.contains(currentNode.first)) {
                                title.value = title.value.plus(" ")
                                onNodeVisited(currentNode.first)
                                adjacencyList[currentNode.first]?.forEach {
                                    queue.add(Pair(it.first, currentNode.second + 1))
                                }
                            }
                            delay(500)
                        }

                        title.value = title.value.plus(" ")
                    }
                }

                Algorithm.Prim -> {
                    val coroutineScope = rememberCoroutineScope()
                    selectedNodes.clear()
                    LaunchedEffect(Unit) {

                        val adjacencyList = extractAdjacencyList(graphState.value.lines)
                        val visitedNodes = graphState.value.visitedNodes
                        val queue = mutableListOf<Pair<Int, Int>>()
                        val startNode = 1
                        queue.add(Pair(startNode, 0))
                        while (queue.isNotEmpty()) {

                            val currentNode = queue.removeFirst()

                            coroutineScope.launch {
                                graphState.value.nodeInfo[currentNode.first - 1] =
                                    graphState.value.nodeInfo[currentNode.first - 1].copy(
                                        color = Color(0xFF36dfb4)
                                    )
                            }

                            if (!visitedNodes.contains(currentNode.first)) {
                                title.value = title.value.plus(" ")
                                onNodeVisited(currentNode.first)
                                adjacencyList[currentNode.first]?.forEach {
                                    queue.add(Pair(it.first, currentNode.second + 1))
                                }
                            }
                            delay(500)
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
//    Log.i(
//        "DrawLine",
//        "start: $start, end: $end, theta: $theta, angleSign: ${angleSign(start, end)}"
//    )
}