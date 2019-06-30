package com.lstudio.algorithms.antcolony

import com.lstudio.algorithms.ls.GreedySolver
import com.lstudio.pointrestorer.DistMatrix
import com.lstudio.pointrestorer.primitives.Point
import com.lstudio.ui.Visualizer
import com.lstudio.utils.random
import java.lang.Exception
import java.util.*
import java.util.stream.IntStream
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FarsightedMMASOptimization(
    distanceMatrix: Array<DoubleArray>,
    private val startDepots: IntArray,
    private val endDepots: HashMap<Int, Int>
) {

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
    lateinit var probabilities: MutableMap<List<Int>, Double>
    private var candidateList = ArrayList<Int>()
    private var antCapacity = 200
    private var iterations = 5000
    private var minLength = Double.MAX_VALUE

    init {
        numberOfCities = graph.size
        candidateList.addAll((0 until graph.size))
        numberOfAnts = startDepots.size
        numberOfStartDepots = startDepots.size
        numberOfEndDepots = endDepots.keys.size

        trails = Array(numberOfCities) { DoubleArray(numberOfCities) }
        //probabilities = DoubleArray(numberOfCities)
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
            run()
        }
    }

    // set paramethers
    private val alpha = 1.0
    private val beta = 2.0
    private val rho = 0.1
    private var tMax = 0.0
    private var tMin = 0.0
    private val stagnation1 = 1000
    private val Q = 1.0

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

    // init ants
    private fun setupAnts() {
        candidateList.clear()
        candidateList.addAll((0 until graph.size))
        cities.forEach {
            it.clear()
        }

        for (i in 0 until numberOfStartDepots) {
            val ant = ants[i]
            ant.clear()
            val city = startDepots[i]
            ant.visitCity(city)
            candidateList.remove(city)
        }
        log("Candidates: ${candidateList.size}")
        log("Ants were set upped and located to start depots")
    }

    private fun printCurrentSolution() {
        println("Current solution:")
        for (i in 0 until ants.size) {
            println("Ant #${i + 1}:")
            ants[i].printTrail()
        }
    }

    fun run() {
        initMinMaxPheromone()
        initializePheromones()
        runIterations()

        println(minLength)
    }

    // iteration #1
    fun runIterations() {
        for (i in 0 until iterations) {
            //System.`in`.read()

            try {
                // println("Iteration ${i + 1}")
                setupAnts()
                moveAnts()
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

    // construct ant solution


    fun calculateProbabilitiesNew(ant: Ant) {
        val i = ant.currentCity()

        var denominator = 0.0
        // calculate sum tau(ir) * n(ir)
        val customers = candidateList.filter { cities[it].type == CityType.CUSTOMER }

        for (j in candidateList) {
            denominator += Math.pow(trails[i][j], alpha) * Math.pow(1.0 / graph[i][j], beta)

         //   log("1: T1: ${ trails[i][j] }")
         //   log("1: G1: ${ 1.0 / graph[i][j] }")
        }

        val optimalNumber = Math.sqrt(candidateList.size.toDouble()).toInt() // 20%

        val indexes1 = customers.shuffled().take(optimalNumber)
        val indexes2 = candidateList.shuffled().take(optimalNumber)

        // not all possible
//        // generate all possible pairs (i,r), (r,j)
//
        // all possible r
        for (r in indexes1) {
            // all possible j
            for (j in indexes2) {
                if (r == j)
                    continue

               // log("2: T1: ${ trails[i][r] } + T2: ${trails[r][j]}")
              //  log("2: G1: ${ 1.0 / graph[i][r] } + G2: ${1.0 / graph[r][j]}")

                denominator += Math.pow(
                    trails[i][r] * trails[r][j],
                    alpha
                ) * Math.pow(1.0 / graph[i][r] + 1.0 / graph[r][j], beta)
            }
        }

        // make new probabilities
        probabilities = mutableMapOf<List<Int>, Double>()

//        // generate all possible pairs (i,r), (r,j)
        // all possible r
        for (r in indexes1) {
            // all possible j
            for (j in indexes2) {
                if (r == j)
                    continue

                val list = arrayListOf(r, j)
                probabilities[list] = Math.pow(trails[i][r] * trails[r][j], alpha) * Math.pow(
                    1.0 / graph[i][r] + (1.0 / graph[r][j]),
                    beta
                ) / denominator
            }
        }

        for (j in candidateList) {
            val list = arrayListOf(j)
            probabilities[list] = Math.pow(trails[i][j], alpha) * Math.pow(1.0 / graph[i][j], beta) / denominator
        }

        var sumOne = 0.0
        var sumTwo = 0.0
        probabilities.filter { key -> key.key.size == 2 }.forEach { t, u -> sumOne += u }
        probabilities.filter { key -> key.key.size == 1 }.forEach { t, u -> sumTwo += u }
        if (sumOne + sumTwo > 2)
            throw java.lang.RuntimeException("Wtf")
    }




//    fun calculateProbabilitiesNew(ant: Ant) {
//        val i = ant.currentCity()
//
//        var denominator = 0.0
//        // calculate sum tau(ir) * n(ir)
//        val customers = candidateList.filter { cities[it].type == CityType.CUSTOMER }
//
//        for (j in candidateList) {
//            denominator += Math.pow(trails[i][j], alpha) * Math.pow(1.0 / graph[i][j], beta)
//        }
//
//        // generate all possible pairs (i,r), (r,j)
//
//        // all possible r
//        for (r in customers) {
//            // all possible j
//            for (j in candidateList) {
//                if (r == j)
//                    continue
//
//                denominator += Math.pow(
//                    trails[i][r] * trails[r][j],
//                    alpha
//                ) * Math.pow(1.0 / graph[i][r] + 1.0 / graph[r][j], beta)
//            }
//        }
//
//        // make new probabilities
//        probabilities = mutableMapOf<List<Int>, Double>()
//
//        // generate all possible pairs (i,r), (r,j)
//        // all possible r
//        for (r in customers) {
//            // all possible j
//            for (j in candidateList) {
//                if (r == j)
//                    continue
//
//                val list = arrayListOf(r, j)
//                probabilities[list] = Math.pow(trails[i][r] * trails[r][j], alpha) * Math.pow(
//                    1.0 / graph[i][r] + (1.0 / graph[r][j]),
//                    beta
//                ) / denominator
//            }
//        }
//
//        for (j in candidateList) {
//            val list = arrayListOf(j)
//            probabilities[list] = Math.pow(trails[i][j], alpha) * Math.pow(1.0 / graph[i][j], beta) / denominator
//        }
//
//        var sumOne = 0.0
//        var sumTwo = 0.0
//        probabilities.filter { key -> key.key.size == 2 }.forEach { t, u -> sumOne += u }
//        probabilities.filter { key -> key.key.size == 1 }.forEach { t, u -> sumTwo += u }
//        if (sumOne + sumTwo > 1.1)
//            throw java.lang.RuntimeException("Wtf")
//    }


//    fun calculateProbabilities(ant: Ant) {
//        val i = ant.currentCity()
//        var pheromone = 0.0
//        for (l in 0 until candidateList.size) {
//            val index = candidateList[l]
//            if (!ant.visited(index)) {
//                pheromone += Math.pow(trails[i][index], alpha) * Math.pow(1.0 / graph[i][index], beta)
//            }
//        }
//        for (k in 0 until candidateList.size) {
//            val index = candidateList[k]
//            if (ant.visited(index)) {
//                probabilities[index] = 0.0
//            } else {
//                val numerator = Math.pow(trails[i][index], alpha) * Math.pow(1.0 / graph[i][index], beta)
//                probabilities[index] = numerator / pheromone
//            }
//        }
//    }


    fun selectNextCity(ant: Ant): List<Int> {
        val t = candidateList.random()
        if (random.nextDouble() < randomFactor) {
            if (!ant.visited(t))
                return arrayListOf(t)
        }
        calculateProbabilitiesNew(ant)
        val r = random.nextDouble()
        var total = 0.0
        for (itm in probabilities) {
            val index = itm.key
            total += itm.value
            if (total >= r) {
                return index
            }
        }

        throw RuntimeException("There are no other cities")
    }


    private fun moveAnts() {
        log("Move started")
        loop@ while (ants.any { !it.isRouteCompleted }) {
            for (i in (0 until numberOfAnts).shuffled()) {
                val ant = ants[i]
                if (ant.isRouteCompleted)
                    continue
                if (candidateList.size == 0)
                    return

                var precity = selectNextCity(ant)

                if (precity.size == 2)
                    precity = precity.drop(1)

                for (city in precity) {
                    var currentCity = cities[city]

                    if (ants.count { !it.isRouteCompleted } == 1 && candidateList.size > 1) {
                        //if (currentCity.type == CityType.END_DEPOT) {
                        //candidateList.remove(city)

                        val depots = candidateList.filter { cities[it].type == CityType.END_DEPOT }
                        candidateList.removeAll(depots)


                        while (candidateList.size > 0) {
                            precity = selectNextCity(ant)

                            if (precity.size == 2)
                                precity = precity.drop(1)

                            for (city1 in precity) {
                                currentCity = cities[city1]
                                ant.visitCity(city1)
                                candidateList.remove(city1)
                            }
                        }

                        val lastCity = currentCity

                        // find best end depot

                        val bestDepot = depots.minBy {
                            graph[lastCity.index][it]
                        }!!

                        ant.visitCity(bestDepot)
                        //}
                        //break
                    }
                    ant.visitCity(city)
                    if (currentCity.type == CityType.END_DEPOT) {
                        ant.isRouteCompleted = true
                        currentCity.availableCapacity--
                        if (currentCity.availableCapacity == 0)
                            candidateList.remove(city)
                    } else
                        candidateList.remove(city)
                }

            }
        }

        if (candidateList.size > 0) {
            throw Exception("Something went wrong")
        }

        log("Routes were formed")
    }

    // determine current or global best

    fun determineBest(): Ant {
        return ants.minBy { it.trailLength(graph) / Math.pow(it.currentIndex.toDouble(), 2.0) }!! /// it.currentIndex }!!
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
                deltaTau += Q / (ant.trailLength(graph) / Math.pow(ant.currentIndex.toDouble(), 2.0)) /// ant.currentIndex)

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
        restartCheck(this.stagnation1)
    }

    // next iteration #2

    fun log(info: String) {
        System.out.println(info)
    }

    /**
     * Get current route length
     */
    private fun getCurrentLength(): Double {
        var length = 0.0
        for (a in ants) {
            length += a.trailLength(graph)
        }
        if (length < minLength)
            minLength = length
        return length
    }

    /**
     * Print current solution
     */
    private fun printResult() {
        println("Length: ${getCurrentLength()}")
    }
}