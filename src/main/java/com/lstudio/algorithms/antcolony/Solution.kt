package com.lstudio.algorithms.antcolony

class Solution(val ants: List<Ant>) {

    var fitness = 0.0

    fun trailLength(graph: Array<DoubleArray>): Double {
        fitness = ants.sumByDouble { it.trailLength(graph) }
        return fitness
    }

    fun containsRoute(i: Int, j: Int): Boolean {
        return ants.any { it.containsRoute(i, j) }
    }

    fun clone(): Solution {
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