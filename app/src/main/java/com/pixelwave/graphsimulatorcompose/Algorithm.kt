package com.pixelwave.graphsimulatorcompose

sealed class Algorithm {
    object None : Algorithm()
    object Bfs : Algorithm()
    object Dfs : Algorithm()
    object Dijkstra : Algorithm()
    object BellmanFord : Algorithm()
    object Kruskal : Algorithm()
    object Prim : Algorithm()

    override fun toString(): String {
        return when (this) {
            is None -> "None"
            is Bfs -> "BFS"
            is Dfs -> "DFS"
            is Dijkstra -> "Dijkstra"
            is BellmanFord -> "Bellman Ford"
            is Kruskal -> "Kruskal"
            is Prim -> "Prim"
        }
    }
}
