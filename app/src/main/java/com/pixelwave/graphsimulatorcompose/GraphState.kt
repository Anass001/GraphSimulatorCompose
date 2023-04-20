package com.pixelwave.graphsimulatorcompose

data class GraphState(
    val canAddNodes: Boolean = true,
    val activeAlgorithm: Algorithm = Algorithm.None
)