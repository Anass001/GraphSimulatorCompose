package com.pixelwave.graphsimulatorcompose

fun extractAdjacencyList(lines: List<Line>): Map<Int, List<Int>> {
    val adjacencyList = mutableMapOf<Int, MutableList<Int>>()
    for (line in lines) {
        val startNode = line.startNode
        val endNode = line.endNode
        adjacencyList.getOrPut(startNode) { mutableListOf() }.add(endNode)
        adjacencyList.getOrPut(endNode) { mutableListOf() }.add(startNode)
    }
    return adjacencyList
}