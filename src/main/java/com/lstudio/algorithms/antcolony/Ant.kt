package com.lstudio.algorithms.antcolony

class Ant(private val trailSize: Int, private val capacity: Int) {
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

    fun containsRoute(i: Int, j: Int): Boolean {
        if (!visited(i) || !visited(j))
            return false

        val firstCityIndex = trail.indexOf(i)
        if (trail[firstCityIndex + 1] == j)
            return true

        return false
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
        for (i in 0 until currentIndex - 1) {
            print("${trail[i]} -> ")
        }
        println("${trail[currentIndex - 1]}")
    }

    fun trailString(): String {
        val stringBuilder = StringBuilder()
        for (i in 0 until currentIndex - 1) {
            stringBuilder.append("${trail[i]} -> ")
        }
        stringBuilder.append("${trail[currentIndex - 1]}\n")
        return stringBuilder.toString()
    }

    fun getRoute(): ArrayList<Int> {
        val route = arrayListOf<Int>()
        for (i in 0 until currentIndex) {
            route.add(trail[i])
        }
        return route
    }

    fun clear() {
        for (i in 0 until trailSize)
            visited[i] = false

        currentIndex = 0
        isRouteCompleted = false
    }

    fun clone(): Ant {
        val newAnt = Ant(trailSize, capacity)
        newAnt.trail = trail.clone()
        newAnt.currentIndex = currentIndex
        newAnt.isRouteCompleted = isRouteCompleted
        newAnt.visited = visited.clone()
        return newAnt
    }
}