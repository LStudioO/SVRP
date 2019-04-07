package com.lstudio.algorithms.antcolony

import java.lang.Exception

class Ant(private var trailSize: Int, var capacity: Int) {
    var trail: IntArray = IntArray(trailSize)
    var visited: BooleanArray = BooleanArray(trailSize)
    var currentIndex = 0
    var isRouteCompleted = false

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
        var length = 0.0
        for (i in 1 until currentIndex) {
            length += graph[trail[i]][trail[i - 1]]
        }
        return length
    }

    fun printTrail() {
        for (i in 0 until currentIndex - 1)
        {
            print("${trail[i]} -> ")
        }
        println("${trail[currentIndex - 1]}")
    }

    fun clear() {
        for (i in 0 until trailSize)
            visited[i] = false

        currentIndex = 0
        isRouteCompleted = false
    }
}