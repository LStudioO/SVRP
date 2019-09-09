package com.lstudio.algorithms.antcolony

import com.lstudio.algorithms.antcolony.TaskSettings.Q
import com.lstudio.algorithms.antcolony.TaskSettings.rho
import com.lstudio.algorithms.antcolony.TaskSettings.stagnation1
import com.lstudio.algorithms.antcolony.TaskSettings.tMax
import com.lstudio.algorithms.antcolony.TaskSettings.tMin
import com.lstudio.algorithms.ls.GreedySolver
import com.lstudio.pointrestorer.DistMatrix
import com.lstudio.pointrestorer.primitives.Point
import com.lstudio.ui.Visualizer

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
    private var candidateList = ArrayList<Int>()
    private var antCapacity = 200
    private var iterations = 5000
    private var minLength = Double.MAX_VALUE
    private var routeIterations = 10
    lateinit var bestSolution: List<Ant>

    init {
        numberOfCities = graph.size
        candidateList.addAll((0 until graph.size))
        numberOfAnts = startDepots.size
        numberOfStartDepots = startDepots.size
        numberOfEndDepots = endDepots.keys.size

        trails = Array(numberOfCities) { DoubleArray(numberOfCities) }
        //probabilities = DoubleArray(numberOfCities)


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
            run()
        }
    }

    // set paramethers


    var bestSoFar: Ant? = null

    // stagnation
    var stagnation = 0

    var stagnationCounter = 0

    var bestAnt: Ant? = null

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
    fun initializePheromones() {
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


    private fun printCurrentSolution() {
        TODO("unimplemented")
//        println("Current solution:")
//        for (i in 0 until ants.size) {
//            println("Ant #${i + 1}:")
//            ants[i].printTrail()
//        }
    }

    fun run() {
        initMinMaxPheromone()
        initializePheromones()
        runIterations()

        println(minLength)
    }

    // iteration #1
    fun runIterations() {
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
                val best = routes.map { it.constructSolution() }.minBy { routeLength(it) }!!
                // todo clone best
                bestSolution = best

                updatePheromones()
                daemonActions()
                printCurrentSolution()
                printResult()
            } catch (ex: Exception) {
                log("ERROR: ${ex.message}")
            }
        }
        log("Best length: $minLength")
    }

    // determine current or global best
    fun determineBest(): Ant {

        TODO("unimplemented")

//        return ants.minBy {
//            it.trailLength(graph) / Math.pow(
//                it.currentIndex.toDouble(),
//                2.0
//            )
//        }!! /// it.currentIndex }!!
    }

    // update pheromones
    fun updatePheromones() {
        fun newEvaporationValue(i: Int, j: Int): Double {
            return (1.0 - rho) * trails[i][j]
        }

        fun depositDeltaTau(i: Int, j: Int): Double {
            var deltaTau = 0.0

            val ant = determineBest()
            if (ant.containsRoute(i, j))
                deltaTau += Q / (ant.trailLength(graph) / Math.pow(
                    ant.currentIndex.toDouble(),
                    2.0
                )) /// ant.currentIndex)

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
    fun daemonActions() {
        fun updateMinAndMaxValues(evaporation: Double) {


            fun updateMinAndMax() {

                log("Yes. The tMin and tMax should be updated")

                tMax = (1.0 / (evaporation * bestSoFar!!.trailLength(graph)))
                tMin = tMax / 10.0

                log("Now tMin=$tMin and tMax=$tMax")
            }

            log("Verifying if the pheromone limits should be updated")

            when {
                bestSoFar == null -> {
                    bestSoFar = determineBest().clone()
                    updateMinAndMax()
                }
                bestSoFar!!.trailLength(graph) != determineBest().trailLength(graph) -> {
                    bestSoFar = determineBest().clone()
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
                        trails[i][j] = Math.min(trails[i][j], tMax)
                        trails[i][j] = Math.max(trails[i][j], tMin)
                        trails[j][i] = trails[i][j]
                    }
                }
            }
        }

        fun restartCheck(stagn: Int) {
            stagnation = stagn
            log("Verifying if the pheromone matrix should be restarted")

            if (bestAnt == null) {
                bestAnt = bestSoFar!!.clone()
            }

            if (bestAnt!!.trailLength(graph) == bestSoFar!!.trailLength(graph)) {
                stagnationCounter++
            } else {
                bestAnt = bestSoFar!!.clone()
                stagnationCounter = 0
            }

            if (stagnationCounter == stagnation) {
                log("The stagnation was reached. The pheromone matrix will be restarted")
                initMinMaxPheromone()
                initializePheromones()
                stagnationCounter = 0
            }
        }

        updateMinAndMaxValues(rho)
        updatePheromoneMatrix()
        restartCheck(stagnation1)
    }

    // next iteration #2

    fun log(info: String) {
        System.out.println(info)
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
        return routeLength(bestSolution)
    }

    /**
     * Print current solution
     */
    private fun printResult() {
        println("Length: ${getCurrentLength()}")
    }
}