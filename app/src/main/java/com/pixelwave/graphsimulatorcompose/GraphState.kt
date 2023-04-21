package com.pixelwave.graphsimulatorcompose

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class GraphState(
    val canAddNodes: Boolean = true,
    val activeAlgorithm: Algorithm = Algorithm.None,
    val nodeInfo: MutableList<NodeInfo> = mutableListOf(),
    val lines: MutableList<Line> = mutableListOf(),

    val adjacencyList: Map<Int, List<Int>> = mapOf(),
    val visitedNodes: MutableList<Int> = mutableListOf(),
)

data class NodeInfo(val position: Offset, val color: Color = Color(0xFF6650a4))

data class Line(val startNode: Int, val endNode: Int)