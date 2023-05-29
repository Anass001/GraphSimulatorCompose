package com.pixelwave.graphsimulatorcompose

import org.w3c.dom.Node

// function to extract adjacency list from the graph with distances (weights)
fun extractAdjacencyList(lines: List<Line>): Map<Int, List<Pair<Int, Int>>> {
    val adjacencyList = mutableMapOf<Int, MutableList<Pair<Int, Int>>>()
    lines.forEach { line ->
        adjacencyList[line.startNode] = adjacencyList.getOrDefault(line.startNode, mutableListOf())
            .also { it.add(Pair(line.endNode, line.weight)) }
        adjacencyList[line.endNode] = adjacencyList.getOrDefault(line.endNode, mutableListOf())
            .also { it.add(Pair(line.startNode, line.weight)) }
    }
    return adjacencyList
}