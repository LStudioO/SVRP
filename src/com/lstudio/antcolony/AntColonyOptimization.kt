package com.lstudio.antcolony

import com.lstudio.pointrestorer.DistMatrix
import com.lstudio.pointrestorer.primitives.Point
import com.lstudio.ui.Visualizer
import com.lstudio.utils.random
import java.util.*
import java.util.stream.IntStream
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AntColonyOptimization(
    distanceMatrix: Array<DoubleArray>,
    private val startDepots: IntArray,
    private val endDepots: HashMap<Int, Int>
) {
    private val c = 1.0
    private val alpha = 1.0
    private val beta = 5.0
    private val evaporation = 0.5
    private val Q = 500.0
    private val randomFactor = 0.01
    private val numberOfCities: Int
    private val numberOfAnts: Int
    private val numberOfStartDepots: Int
    private val numberOfEndDepots: Int
    private val graph: Array<DoubleArray> = distanceMatrix
    private lateinit var cities: Array<City>
    private val trails: Array<DoubleArray>
    private val ants = ArrayList<Ant>()
    private val random = Random()
    private val probabilities: DoubleArray
    private var candidateList = ArrayList<Int>()
    private var antCapacity = 200
    private var iterations = 10

    init {
        numberOfCities = graph.size
        candidateList.addAll((0 until graph.size))
        numberOfAnts = startDepots.size
        numberOfStartDepots = startDepots.size
        numberOfEndDepots = endDepots.keys.size

        trails = Array(numberOfCities) { DoubleArray(numberOfCities) }
        probabilities = DoubleArray(numberOfCities)
        IntStream.range(0, numberOfAnts)
            .forEach { ants.add(Ant(numberOfCities, antCapacity)) }

        val dMatrix = DistMatrix(graph)
        val points = dMatrix.restorePoints()

        parseCities(points!!)

        val visualizer = Visualizer(cities)
        visualizer.show()
    }

    private fun parseCities(points: Array<Point>) {
        cities = Array(numberOfCities) {
            val isStartDepot = startDepots.contains(it)
            var capacity = 0
            if (endDepots.containsKey(it))
                capacity = endDepots[it]!!
            City(it, isStartDepot, capacity).apply {
                this.point = points[it]
            }
        }
    }

    /**
     * Perform ant optimization
     */
    fun startAntOptimization() {
        for (i in 1..1) {
            println("Attempt #$i")
            solve()
        }
    }

    /**
     * Method to run the main logic
     */
    fun solve() {
        setupAnts()
        clearTrails()
        // for (i in 0 until iterations) {
        moveAnts()
        updateTrails()
        //}
        printCurrentSolution()
        printResult()
    }

    private fun printCurrentSolution() {
        println("Current solution:")
        for (i in 0 until ants.size) {
            println("Ant #${i + 1}:")
            ants[i].printTrail()
        }
    }

    /**
     * Prepare ants for the simulation
     */
    private fun setupAnts() {
        for (i in 0 until numberOfStartDepots) {
            val ant = ants[i]
            ant.clear()
            val city = startDepots[i]
            ant.visitCity(city)
            candidateList.remove(city)
        }
    }

    /**
     * At each iteration, move ants in random order
     */
    private fun moveAnts() {
        while (ants.any { !it.isRouteCompleted }) {
            for (i in (0 until numberOfAnts).shuffled()) {
                val ant = ants[i]
                if (ant.isRouteCompleted)
                    continue
                if (candidateList.size == 0)
                    return
                val city = selectNextCity(ant)
                ant.visitCity(city)
                val currentCity = cities[city]
                if (currentCity.type == CityType.END_DEPOT) {
                    currentCity.availableCapacity--
                    if (currentCity.availableCapacity == 0)
                        candidateList.remove(city)
                } else
                    candidateList.remove(city)
            }
        }
    }

    /**
     * Select next city for each ant
     */
    private fun selectNextCity(ant: Ant): Int {
        val t = candidateList.random()
        if (random.nextDouble() < randomFactor) {
            if (!ant.visited(t))
                return t
        }
        calculateProbabilities(ant)
        val r = random.nextDouble()
        var total = 0.0
        for (i in 0 until candidateList.size) {
            val index = candidateList[i]
            total += probabilities[index]
            if (total >= r) {
                return index
            }
        }

        throw RuntimeException("There are no other cities")
    }

    /**
     * Calculate the next city picks probabilites
     */
    fun calculateProbabilities(ant: Ant) {
        val i = ant.currentCity()
        var pheromone = 0.0
        for (l in 0 until candidateList.size) {
            val index = candidateList[l]
            if (!ant.visited(index)) {
                pheromone += Math.pow(trails[i][index], alpha) * Math.pow(1.0 / graph[i][index], beta)
            }
        }
        for (k in 0 until candidateList.size) {
            val index = candidateList[k]
            if (ant.visited(index)) {
                probabilities[index] = 0.0
            } else {
                val numerator = Math.pow(trails[i][index], alpha) * Math.pow(1.0 / graph[i][index], beta)
                probabilities[index] = numerator / pheromone
            }
        }
    }

    /**
     * Update trails that ants used
     */
    private fun updateTrails() {
        for (i in 0 until numberOfCities) {
            for (j in 0 until numberOfCities) {
                trails[i][j] *= evaporation
            }
        }
        for (a in ants) {
            val contribution = Q / a.trailLength(graph)
            for (i in 0 until numberOfCities - 1) {
                trails[a.trail[i]][a.trail[i + 1]] += contribution
            }
            trails[a.trail[numberOfCities - 1]][a.trail[0]] += contribution
        }
    }

    /**
     * Get current route length
     */
    private fun getCurrentLength() : Double {
        var length = 0.0
        for (a in ants) {
            length += a.trailLength(graph)
        }
        return length
    }

    /**
     * Print current solution
     */
    private fun printResult() {
        println("Length: ${getCurrentLength()}")
    }

    /**
     * Clear trails after simulation
     */
    private fun clearTrails() {
        for (i in 0 until numberOfCities) {
            for (j in 0 until numberOfCities) {
                trails[i][j] = c
            }
        }
    }

    /**
     * Get distance between 2 cities
     */
    private fun calculateDistance(city1: City, city2: City): Double {
        return graph[city1.index][city2.index]
    }

    /**
     * Get distance between 2 cities by its indices
     */
    private fun calculateDistance(cityIndex1: Int, cityIndex2: Int): Double {
        return graph[cityIndex1][cityIndex2]
    }

    /**
     * Calculate distance from specified city to closest available end depot
     */
    private fun calculateDistanceToClosestEndDepot(cityIndex: Int): Double {
        val possibleDepots = ArrayList<Int>()
        for (i in candidateList) {
            if (cities[i].type == CityType.END_DEPOT)
                possibleDepots.add(i)
        }
        val depotIndex = possibleDepots.minBy { graph[cityIndex][it] } ?: return -1.0
        return graph[cityIndex][depotIndex]
    }
}