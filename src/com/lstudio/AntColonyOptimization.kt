package com.lstudio
import java.util.*
import java.util.stream.IntStream

class AntColonyOptimization(noOfCities: Int) {

    private val c = 1.0
    private val alpha = 1.0
    private val beta = 5.0
    private val evaporation = 0.5
    private val Q = 500.0
    private val antFactor = 0.8
    private val randomFactor = 0.01

    private val maxIterations = 1000

    private val numberOfCities: Int
    private val numberOfAnts: Int
    private val graph: Array<DoubleArray>
    private val trails: Array<DoubleArray>
    private val ants = ArrayList<Ant>()
    private val random = Random()
    private val probabilities: DoubleArray

    private var currentIndex: Int = 0

    private var bestTourOrder: IntArray? = null
    private var bestTourLength: Double = 0.toDouble()

    init {
        graph = generateRandomMatrix(noOfCities)
        numberOfCities = graph.size
        numberOfAnts = (numberOfCities * antFactor).toInt()

        trails = Array(numberOfCities) { DoubleArray(numberOfCities) }
        probabilities = DoubleArray(numberOfCities)
        IntStream.range(0, numberOfAnts)
            .forEach { ants.add(Ant(numberOfCities)) }
    }

    /**
     * Generate initial solution
     */
    fun generateRandomMatrix(n: Int): Array<DoubleArray> {
        val randomMatrix = Array(n) { DoubleArray(n) }
        IntStream.range(0, n)
            .forEach { i ->
                IntStream.range(0, n)
                    .forEach { j -> randomMatrix[i][j] = Math.abs(random.nextInt(100) + 1).toDouble() }
            }
        return randomMatrix
    }

    /**
     * Perform ant optimization
     */
    fun startAntOptimization() {
        IntStream.rangeClosed(1, 3)
            .forEach { i ->
                println("Attempt #$i")
                solve()
            }
    }

    /**
     * Use this method to run the main logic
     */
    fun solve(): IntArray {
        setupAnts()
        clearTrails()
        IntStream.range(0, maxIterations)
            .forEach { i ->
                moveAnts()
                updateTrails()
                updateBest()
            }
        println("Best tour length: " + (bestTourLength - numberOfCities))
        println("Best tour order: " + Arrays.toString(bestTourOrder))
        return bestTourOrder!!.clone()
    }

    /**
     * Prepare ants for the simulation
     */
    private fun setupAnts() {
        IntStream.range(0, numberOfAnts)
            .forEach { i ->
                ants.forEach { ant ->
                    ant.clear()
                    ant.visitCity(-1, random.nextInt(numberOfCities))
                }
            }
        currentIndex = 0
    }

    /**
     * At each iteration, move ants
     */
    private fun moveAnts() {
        IntStream.range(currentIndex, numberOfCities - 1)
            .forEach { i ->
                ants.forEach { ant -> ant.visitCity(currentIndex, selectNextCity(ant)) }
                currentIndex++
            }
    }

    /**
     * Select next city for each ant
     */
    private fun selectNextCity(ant: Ant): Int {
        val t = random.nextInt(numberOfCities - currentIndex)
        if (random.nextDouble() < randomFactor) {
            val cityIndex = IntStream.range(0, numberOfCities)
                .filter { i -> i == t && !ant.visited(i) }
                .findFirst()
            if (cityIndex.isPresent) {
                return cityIndex.asInt
            }
        }
        calculateProbabilities(ant)
        val r = random.nextDouble()
        var total = 0.0
        for (i in 0 until numberOfCities) {
            total += probabilities[i]
            if (total >= r) {
                return i
            }
        }

        throw RuntimeException("There are no other cities")
    }

    /**
     * Calculate the next city picks probabilites
     */
    fun calculateProbabilities(ant: Ant) {
        val i = ant.trail[currentIndex]
        var pheromone = 0.0
        for (l in 0 until numberOfCities) {
            if (!ant.visited(l)) {
                pheromone += Math.pow(trails[i][l], alpha) * Math.pow(1.0 / graph[i][l], beta)
            }
        }
        for (j in 0 until numberOfCities) {
            if (ant.visited(j)) {
                probabilities[j] = 0.0
            } else {
                val numerator = Math.pow(trails[i][j], alpha) * Math.pow(1.0 / graph[i][j], beta)
                probabilities[j] = numerator / pheromone
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
     * Update the best solution
     */
    private fun updateBest() {
        if (bestTourOrder == null) {
            bestTourOrder = ants[0].trail
            bestTourLength = ants[0]
                .trailLength(graph)
        }
        for (a in ants) {
            if (a.trailLength(graph) < bestTourLength) {
                bestTourLength = a.trailLength(graph)
                bestTourOrder = a.trail.clone()
            }
        }
    }

    /**
     * Clear trails after simulation
     */
    private fun clearTrails() {
        IntStream.range(0, numberOfCities)
            .forEach { i ->
                IntStream.range(0, numberOfCities)
                    .forEach { j -> trails[i][j] = c }
            }
    }

}