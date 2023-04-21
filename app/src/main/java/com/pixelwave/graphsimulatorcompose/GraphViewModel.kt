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
        state = state.copy(canAddNodes = !(state.canAddNodes), activeAlgorithm = Algorithm.None)
    }

    fun runAlgorithm(algorithm: Algorithm) {
        state = state.copy(activeAlgorithm = algorithm, canAddNodes = false)
    }

    fun addNode(position: Offset) {
        state =
            state.copy(nodeInfos = state.nodeInfos.also { it.add(NodeInfo(position, Color.Black)) })
    }

    fun addLine(line: Line) {
        state = state.copy(lines = state.lines.also { it.add(line) })
    }

    fun clearAlgorithm() {
        state = state.copy(activeAlgorithm = Algorithm.None)
    }
}