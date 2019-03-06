package com.lstudio.ant_colony

import java.lang.Exception

class Ant(private var trailSize: Int) {
    var trail: IntArray = IntArray(trailSize)
    var visited: BooleanArray = BooleanArray(trailSize)
    var currentIndex = 0

    fun visitCity(city: Int) {
        trail[currentIndex] = city
        visited[city] = true
        currentIndex++
        if (currentIndex >= trailSize)
            throw Exception("Inefficient capacity")
    }

    fun visited(i: Int): Boolean {
        return visited[i]
    }

    fun currentCity(): Int {
        return trail[currentIndex - 1]
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