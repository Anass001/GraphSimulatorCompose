package com.pixelwave.graphsimulatorcompose.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import com.pixelwave.graphsimulatorcompose.Node

@Composable
fun Graph(modifier: Modifier = Modifier, isEdgeModeActive: MutableState<Boolean>) {
    val nodePositions = remember { mutableStateListOf<Offset>() }
    val selectedNodes = remember { mutableStateListOf<Int>() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { tapOffset ->
                    if (isEdgeModeActive.value) {
                        val node = nodePositions.find { offset ->
                            offset.x - 25 <= tapOffset.x && tapOffset.x <= offset.x + 25 &&
                                    offset.y - 25 <= tapOffset.y && tapOffset.y <= offset.y + 25
                        }
                        if (node != null && !selectedNodes.contains(nodePositions.indexOf(node) + 1)) {
                            selectedNodes.add(nodePositions.indexOf(node) + 1)
                        } else if (node != null && selectedNodes.contains(nodePositions.indexOf(node) + 1)) {
                            selectedNodes.remove(nodePositions.indexOf(node) + 1)
                        }

                    } else nodePositions.add(tapOffset)
                }
            }
    ) {
        nodePositions.forEachIndexed { index, offset ->
            if (selectedNodes.contains(index + 1)) {
                Node(
                    value = index + 1,
                    nodeColor = Color.Red,
                    position = offset
                )
            } else {
                Node(
                    value = index + 1,
                    nodeColor = Color.Gray,
                    position = offset
                )
            }
        }
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (selectedNodes.size >= 2) {
                val startNode = selectedNodes[0]
                val endNode = selectedNodes[selectedNodes.size - 1]
                drawLineBetweenNodes(
                    nodePositions[startNode - 1],
                    nodePositions[endNode - 1],
                    Color.Gray,
                    5f
                )
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



