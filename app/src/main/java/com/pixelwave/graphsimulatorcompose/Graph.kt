package com.pixelwave.graphsimulatorcompose

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

data class Line(val startNode: Int, val endNode: Int)

@Composable
fun Graph(
    modifier: Modifier = Modifier,
    state: GraphState
) {
    Log.i("Graph", "Graph Composable called")
    val nodePositions = remember { mutableStateListOf<Offset>() }
    val selectedNodes = remember { mutableStateListOf<Int>() }
    val lines = remember { mutableStateListOf<Line>() }
    val graphState = remember { mutableStateOf(false) }
    graphState.value = state.canAddNodes

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { tapOffset ->
                    if (!graphState.value) {
                        val node = nodePositions.find { offset ->
                            offset.x.toDp() - 25.dp <= tapOffset.x.toDp() && tapOffset.x.toDp() <= offset.x.toDp() + 25.dp &&
                                    offset.y.toDp() - 25.dp <= tapOffset.y.toDp() && tapOffset.y.toDp() <= offset.y.toDp() + 25.dp
                        }
                        if (node != null) {
                            if (selectedNodes.size < 2) {
                                val nodeId = nodePositions.indexOf(node) + 1
                                if (!selectedNodes.contains(nodeId)) {
                                    selectedNodes.add(nodeId)
                                }
                            }
                            if (selectedNodes.size == 2) {
                                val startNode = selectedNodes[0]
                                val endNode = selectedNodes[1]
                                lines.add(Line(startNode, endNode))
                                selectedNodes.clear()
                            }
                        } else {
                            selectedNodes.clear()
                        }
                    } else nodePositions.add(tapOffset)
                }
            }
    ) {
        nodePositions.forEachIndexed { index, offset ->
            Node(
                value = index + 1,
                nodeColor = if (selectedNodes.contains(index + 1)) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                position = offset
            )
        }
        Canvas(modifier = Modifier.fillMaxSize()) {
            lines.forEach { line ->
                val startNode =
                    nodePositions[line.startNode - 1]
                val endNode = nodePositions[line.endNode - 1]
                drawLineBetweenNodes(startNode, endNode, Color(0xFF36dfb4), 10f)
            }
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