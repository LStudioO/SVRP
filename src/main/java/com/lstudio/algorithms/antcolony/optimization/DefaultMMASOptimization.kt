package com.lstudio.algorithms.antcolony.optimization

import com.lstudio.algorithms.antcolony.Ant
import com.lstudio.algorithms.antcolony.City
import com.lstudio.algorithms.antcolony.Solution
import com.lstudio.algorithms.antcolony.TaskSettings
import com.lstudio.algorithms.antcolony.route.AbstractRoute
import com.lstudio.algorithms.antcolony.route.BasicRoute
import com.lstudio.algorithms.ls.GreedySolver
import com.lstudio.pointrestorer.DistMatrix
import com.lstudio.pointrestorer.primitives.Point
import com.lstudio.ui.Visualizer
import kotlin.math.max
import kotlin.math.min

open class DefaultMMASOptimization(
    distanceMatrix: Array<DoubleArray>,
    val startDepots: IntArray,
    val endDepots: HashMap<Int, Int>
) {
    val numberOfCities: Int
    val numberOfAnts: Int
    val numberOfStartDepots: Int
    private val numberOfEndDepots: Int
    val graph: Array<DoubleArray> = distanceMatrix
    lateinit var cities: Array<City>
    val trails: Array<DoubleArray>
    private var minLength = Double.MAX_VALUE

    var bestSolution: Solution? = null
        set(value) {
            field = value
            iterationsWithoutImprovement = 0
        }
    private var stagnationBestSolution: Solution? = null
    private lateinit var iterationBestSolution: Solution

    private var iterationsWithoutImprovement = 0

    // stagnation
    private var stagnationValue = 0
    private var stagnationCounter = 0

    var visualizer: Visualizer? = null

    init {
        numberOfCities = graph.size
        numberOfAnts = startDepots.size
        numberOfStartDepots = startDepots.size
        numberOfEndDepots = endDepots.keys.size

        // create pheromone matrix
        trails = Array(numberOfCities) { DoubleArray(numberOfCities) }

        prepareCitiesForVisualization()
    }

    private fun prepareCitiesForVisualization() {
        val dMatrix = DistMatrix(graph)
        val points = dMatrix.restorePoints()
        parseCities(points!!)
    }

    private fun visualizeSolution(solution: Solution) {
        visualizer?.showSolution(solution)
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
            return 1.0 / (TaskSettings.rho * cnn)
        }

        TaskSettings.tMax = getT0()
        TaskSettings.tMin = TaskSettings.tMax / 10

        log("Parameters was initialized. Now tMin=${TaskSettings.tMin} and tMax=${TaskSettings.tMax}")
    }

    // initializing the pheromones
    private fun initializePheromones() {
        for (i in 0 until numberOfCities) {
            for (j in i until numberOfCities) {
                if (i != j) {
                    trails[i][j] = TaskSettings.tMax
                    trails[j][i] = trails[i][j]
                }
            }
        }
        log("Pheromone trails was initialized")
    }

    private fun run() {
        setup()
        runIterations(TaskSettings.iterationsWithoutImprovement)
        bestSolution?.let {
            visualizeSolution(it)
        }
        printResult()
    }


    fun setup() {
        initMinMaxPheromone()
        initializePheromones()
    }


    open fun createRoute(): AbstractRoute {
        return BasicRoute(
            graph.size,
            graph,
            numberOfAnts,
            numberOfCities,
            trails,
            TaskSettings.antCapacity,
            cities,
            numberOfStartDepots,
            startDepots
        )
    }

    fun runIterations(iterationsCount: Int) {
        val routes = ArrayList<AbstractRoute>()
        for (k in 0 until TaskSettings.routeIterations) {
            routes.add(
                createRoute()
            )
        }

        while (iterationsWithoutImprovement < iterationsCount) {
            try {
                findBestIterationSolution(routes)
                printIterationSolution()
                updatePheromones()
                daemonActions()
                iterationsWithoutImprovement++
            } catch (ex: Exception) {
                log("ERROR: ${ex.message}")
            }
        }
    }

    private fun printIterationSolution() {
        iterationBestSolution.print(graph)
    }

    private fun findBestIterationSolution(routes: ArrayList<AbstractRoute>) {
        val best = routes.map { it.constructSolution() }.minBy { routeLength(it) }!!
        iterationBestSolution = Solution(best.map { it.clone() })
        visualizeSolution(iterationBestSolution)
    }

    // update pheromones
    private fun updatePheromones() {
        fun newEvaporationValue(i: Int, j: Int): Double {
            return (1.0 - TaskSettings.rho) * trails[i][j]
        }

        fun depositDeltaTau(i: Int, j: Int): Double {
            var deltaTau = 0.0

            if (iterationBestSolution.containsRoute(i, j)) {
                deltaTau += TaskSettings.Q / (iterationBestSolution.trailLength(graph) / numberOfCities)
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

                TaskSettings.tMax = (1.0 / (evaporation * bestSolution!!.trailLength(graph)))
                TaskSettings.tMin = TaskSettings.tMax / 10.0

                log("Now tMin=${TaskSettings.tMin} and tMax=${TaskSettings.tMax}")
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
                        trails[i][j] = min(trails[i][j], TaskSettings.tMax)
                        trails[i][j] = max(trails[i][j], TaskSettings.tMin)
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

        updateMinAndMaxValues(TaskSettings.rho)
        updatePheromoneMatrix()
        restartCheck(TaskSettings.stagnationIterationCount)
    }

    private fun log(info: String) {
        if (TaskSettings.logging)
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

    fun getCurrentLength(): Double {
        return bestSolution?.trailLength(graph) ?: 0.0
    }

    /**
     * Print current solution
     */
    fun printResult() {
        println("Best length: ${getCurrentLength()}")
    }

    fun applyMigrant(bestSolution: Solution) {
        if (bestSolution.fitness <= iterationBestSolution.fitness) {
            iterationBestSolution = bestSolution.clone()
            updatePheromones()
            daemonActions()
        }
    }
}