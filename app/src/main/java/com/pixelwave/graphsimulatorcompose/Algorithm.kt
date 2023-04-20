package com.pixelwave.graphsimulatorcompose

sealed class Algorithm {
    object None : Algorithm()
    object Bfs : Algorithm()
    object Dfs : Algorithm()
    object Dijkstra : Algorithm()
    object Kruskal : Algorithm()
    object Prim : Algorithm()
}
