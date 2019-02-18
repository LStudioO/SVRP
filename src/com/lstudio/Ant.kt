package com.lstudio
class Ant(private var trailSize: Int) {
    var trail: IntArray = IntArray(trailSize)
    var visited: BooleanArray = BooleanArray(trailSize)

    fun visitCity(currentIndex: Int, city: Int) {
        trail[currentIndex + 1] = city
        visited[city] = true
    }

    fun visited(i: Int): Boolean {
        return visited[i]
    }

    fun trailLength(graph: Array<DoubleArray>): Double {
        var length = graph[trail[trailSize - 1]][trail[0]]
        for (i in 0 until trailSize - 1) {
            length += graph[trail[i]][trail[i + 1]]
        }
        return length
    }

    fun clear() {
        for (i in 0 until trailSize)
            visited[i] = false
    }
}