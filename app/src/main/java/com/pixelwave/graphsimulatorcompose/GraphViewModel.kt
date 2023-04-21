package com.pixelwave.graphsimulatorcompose

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay

class GraphViewModel : ViewModel() {

    var state by mutableStateOf(GraphState())

    fun toggleAddNodes() {
        state = state.copy(canAddNodes = !(state.canAddNodes), activeAlgorithm = Algorithm.None)
    }

    fun runAlgorithm(algorithm: Algorithm) {
        state = state.copy(activeAlgorithm = algorithm, canAddNodes = false)
    }

    fun addNode(position: Offset) {
        state =
            state.copy(nodeInfo = state.nodeInfo.also { it.add(NodeInfo(position = position)) })
    }

    fun addLine(line: Line) {
        state = state.copy(lines = state.lines.also { it.add(line) })
    }

    fun clearAlgorithm() {
        val nodes = state.nodeInfo.map { it.copy(color = Color(0xFF6650a4)) }
        state = state.copy(
            activeAlgorithm = Algorithm.None,
            visitedNodes = mutableListOf(),
            nodeInfo = nodes.toMutableList()
        )
    }

    fun clearGraph() {
        state = state.copy(
            nodeInfo = mutableListOf(),
            lines = mutableListOf(),
            adjacencyList = mapOf(),
            visitedNodes = mutableListOf(),
            activeAlgorithm = Algorithm.None,
            canAddNodes = true
        )
    }

    fun addVisitedNode(node: Int) {
        state = state.copy(visitedNodes = state.visitedNodes.also { it.add(node) })
    }
}