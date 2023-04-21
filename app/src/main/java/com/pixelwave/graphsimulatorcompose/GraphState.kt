package com.pixelwave.graphsimulatorcompose

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class GraphState(
    val canAddNodes: Boolean = true,
    val activeAlgorithm: Algorithm = Algorithm.None,
    val nodeInfos: MutableList<NodeInfo> = mutableListOf(),
    val lines: MutableList<Line> = mutableListOf(),
    val adjacencyList: Map<Int, List<Int>> = mapOf(),
    val visitedNodes: MutableList<Int> = mutableListOf(),
    val delayEnded: Boolean = false,
)

data class NodeInfo(val position: Offset, val color: Color = Color.Green)

data class Line(val startNode: Int, val endNode: Int)