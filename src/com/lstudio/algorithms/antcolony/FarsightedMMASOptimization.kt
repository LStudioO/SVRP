package com.lstudio.algorithms.antcolony

import com.lstudio.algorithms.antcolony.TaskSettings.Q
import com.lstudio.algorithms.antcolony.TaskSettings.antCapacity
import com.lstudio.algorithms.antcolony.TaskSettings.iterations
import com.lstudio.algorithms.antcolony.TaskSettings.logging
import com.lstudio.algorithms.antcolony.TaskSettings.rho
import com.lstudio.algorithms.antcolony.TaskSettings.routeIterations
import com.lstudio.algorithms.antcolony.TaskSettings.stagnationConst
import com.lstudio.algorithms.antcolony.TaskSettings.tMax
import com.lstudio.algorithms.antcolony.TaskSettings.tMin
import com.lstudio.algorithms.antcolony.TaskSettings.visualize
import com.lstudio.algorithms.ls.GreedySolver
import com.lstudio.pointrestorer.DistMatrix
import com.lstudio.pointrestorer.primitives.Point
import com.lstudio.ui.Visualizer
import kotlin.math.max
import kotlin.math.min

class FarsightedMMASOptimization(
    distanceMatrix: Array<DoubleArray>,
    private val startDepots: IntArray,
    private val endDepots: HashMap<Int, Int>
) {

    private val numberOfCities: Int
    private val numberOfAnts: Int
    private val numberOfStartDepots: Int
    private val numberOfEndDepots: Int
    private val graph: Array<DoubleArray> = distanceMatrix
    private lateinit var cities: Array<City>
    private val trails: Array<DoubleArray>
    private var minLength = Double.MAX_VALUE

    private var bestSolution: Solution? = null
    private var stagnationBestSolution: Solution? = null
    private lateinit var iterationBestSolution: Solution

    // stagnation
    private var stagnationValue = 0
    private var stagnationCounter = 0

    init {
        numberOfCities = graph.size
        numberOfAnts = startDepots.size
        numberOfStartDepots = startDepots.size
        numberOfEndDepots = endDepots.keys.size

        // create pheromone matrix
        trails = Array(numberOfCities) { DoubleArray(numberOfCities) }

        visualizePoints()
    }

    private fun visualizePoints() {
        val dMatrix = DistMatrix(graph)
        val points = dMatrix.restorePoints()

        parseCities(points!!)

        if (visualize) {
            val visualizer = Visualizer(cities)
            visualizer.show()
        }
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
            run()
        }
    }

    // calculate t0, tMin, tMax
    private fun initMinMaxPheromone() {
        fun getCnn(): Double {
            val vehicleCapacity = 1000
            val greedySolver = GreedySolver(
                startDepots, endDepots,
                graph, vehicleCapacity
            )
            val result = greedySolver.solve()
            return result.cost
        }

        fun getT0(): Double {
            val cnn = getCnn()
            return 1.0 / (rho * cnn)
        }

        tMax = getT0()
        tMin = tMax / 10

        log("Parameters was initialized. Now tMin=$tMin and tMax=$tMax")
    }

    // initializing the pheromones
    private fun initializePheromones() {
        for (i in 0 until numberOfCities) {
            for (j in i until numberOfCities) {
                if (i != j) {
                    trails[i][j] = tMax
                    trails[j][i] = trails[i][j]
                }
            }
        }
        log("Pheromone trails was initialized")
    }

    private fun run() {
        initMinMaxPheromone()
        initializePheromones()
        runIterations()
        printResult()
    }

    private fun runIterations() {
        val routes = ArrayList<Route>()
        for (k in 0 until routeIterations) {
            routes.add(
                Route(
                    graph.size,
                    graph,
                    numberOfAnts,
                    numberOfCities,
                    trails,
                    antCapacity,
                    cities,
                    numberOfStartDepots,
                    startDepots
                )
            )
        }

        for (i in 0 until iterations) {
            try {
                findBestIterationSolution(routes)
                updatePheromones()
                daemonActions()
                printIterationSolution()
            } catch (ex: Exception) {
                log("ERROR: ${ex.message}")
            }
        }
    }

    private fun printIterationSolution() {
        iterationBestSolution.print(graph)
    }

    private fun findBestIterationSolution(routes: ArrayList<Route>) {
        val best = routes.map { it.constructSolution() }.minBy { routeLength(it) }!!
        iterationBestSolution = Solution(best.map { it.clone() })
    }

    // update pheromones
    private fun updatePheromones() {
        fun newEvaporationValue(i: Int, j: Int): Double {
            return (1.0 - rho) * trails[i][j]
        }

        fun depositDeltaTau(i: Int, j: Int): Double {
            var deltaTau = 0.0

            if (iterationBestSolution.containsRoute(i, j)) {
                deltaTau += Q / (iterationBestSolution.trailLength(graph) / numberOfCities)
            }

            return deltaTau
        }

        fun newDepositValue(i: Int, j: Int): Double {
            val depositRate = 1.0
            return trails[i][j] + depositRate * depositDeltaTau(i, j)
        }

        for (i in 0 until numberOfCities) {
            for (j in i until numberOfCities) {
                if (i != j) {
                    // do evaporation
                    trails[i][j] = newEvaporationValue(i, j)
                    trails[j][i] = trails[i][j]

                    // do deposit
                    trails[i][j] = newDepositValue(i, j)
                    trails[j][i] = trails[i][j]
                }
            }
        }
        log("Pheromones were updated")
    }

    // execute daemon actions: [UpdateTMinAndTMaxValues, UpdatePheromoneMatrix, RestartCheck after 1000 iterations
    private fun daemonActions() {
        fun updateMinAndMaxValues(evaporation: Double) {
            fun updateMinAndMax() {
                log("Yes. The tMin and tMax should be updated")

                tMax = (1.0 / (evaporation * bestSolution!!.trailLength(graph)))
                tMin = tMax / 10.0

                log("Now tMin=$tMin and tMax=$tMax")
            }

            log("Verifying if the pheromone limits should be updated")

            when {
                bestSolution == null -> {
                    bestSolution = iterationBestSolution.clone()
                    updateMinAndMax()
                }
                bestSolution!!.trailLength(graph) > iterationBestSolution.trailLength(graph) -> {
                    bestSolution = iterationBestSolution.clone()
                    updateMinAndMax()
                }
                else -> log("No. The tMin and tMax should be the same")
            }
        }

        fun updatePheromoneMatrix() {
            log("Updating the pheromone matrix values")
            // Updating the pheromone matrix values
            for (i in 0 until numberOfCities) {
                for (j in i until numberOfCities) {
                    if (i != j) {
                        trails[i][j] = min(trails[i][j], tMax)
                        trails[i][j] = max(trails[i][j], tMin)
                        trails[j][i] = trails[i][j]
                    }
                }
            }
        }

        fun restartCheck(s: Int) {
            stagnationValue = s
            log("Verifying if the pheromone matrix should be restarted")

            if (stagnationBestSolution == null) {
                stagnationBestSolution = bestSolution!!.clone()
            }

            if (stagnationBestSolution!!.trailLength(graph) == bestSolution!!.trailLength(graph)) {
                stagnationCounter++
            } else {
                stagnationBestSolution = bestSolution!!.clone()
                stagnationCounter = 0
            }

            if (stagnationCounter == stagnationValue) {
                log("The stagnation was reached. The pheromone matrix will be restarted")
                initMinMaxPheromone()
                initializePheromones()
                stagnationCounter = 0
            }
        }

        updateMinAndMaxValues(rho)
        updatePheromoneMatrix()
        restartCheck(stagnationConst)
    }

    private fun log(info: String) {
        if (logging)
            println(info)
    }

    private fun routeLength(ants: List<Ant>): Double {
        var length = 0.0
        for (a in ants) {
            length += a.trailLength(graph)
        }
        if (length < minLength)
            minLength = length
        return length
    }

    private fun getCurrentLength(): Double {
        return bestSolution?.trailLength(graph) ?: 0.0
    }

    /**
     * Print current solution
     */
    private fun printResult() {
        println("Best length: ${getCurrentLength()}")
    }
}