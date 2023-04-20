package com.pixelwave.graphsimulatorcompose

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class GraphViewModel : ViewModel() {

    var state by mutableStateOf(GraphState())

    fun toggleAddNodes() {
        state = state.copy(canAddNodes = !state.canAddNodes, activeAlgorithm = Algorithm.None)
    }

    fun runAlgorithm(algorithm: Algorithm) {
        state = state.copy(activeAlgorithm = algorithm, canAddNodes = false)
    }
}