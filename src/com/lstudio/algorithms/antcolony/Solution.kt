package com.lstudio.algorithms.antcolony

class Solution(private val ants: List<Ant>) {
    fun trailLength(graph: Array<DoubleArray>): Double {
        return ants.sumByDouble { it.trailLength(graph) }
    }

    fun containsRoute(i: Int, j: Int): Boolean {
        return ants.any { it.containsRoute(i, j) }
    }

    fun clone(): Solution? {
        return Solution(ants.map { it.clone() })
    }

    fun print(graph: Array<DoubleArray>) {
        println("Current solution:")
        for (i in 0 until ants.size) {
            println("Ant #${i + 1}:")
            ants[i].printTrail()
        }
        println("Length: ${trailLength(graph)}")
    }
}