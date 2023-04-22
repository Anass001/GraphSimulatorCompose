package com.pixelwave.graphsimulatorcompose

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel

class GraphViewModel : ViewModel() {

    var state by mutableStateOf(GraphState())

    fun toggleAddNodes() {
        state = state.copy(canAddNodes = !(state.canAddNodes))
    }

    fun runAlgorithm() {
        state = state.copy(algorithmRunning = true, canAddNodes = false)
    }

    fun selectAlgorithm(algorithm: Algorithm) {
        state = state.copy(selectedAlgorithm = algorithm)
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
            visitedNodes = mutableListOf(),
            nodeInfo = nodes.toMutableList(),
            algorithmRunning = false,
        )
    }

    fun clearGraph() {
        state = state.copy(
            nodeInfo = mutableListOf(),
            lines = mutableListOf(),
            adjacencyList = mapOf(),
            visitedNodes = mutableListOf(),
            selectedAlgorithm = Algorithm.None,
            canAddNodes = true
        )
    }

    fun addVisitedNode(node: Int) {
        state = state.copy(visitedNodes = state.visitedNodes.also { it.add(node) })
    }
}