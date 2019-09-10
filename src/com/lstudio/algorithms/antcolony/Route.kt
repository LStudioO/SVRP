package com.lstudio.algorithms.antcolony

import com.lstudio.algorithms.antcolony.TaskSettings.alpha
import com.lstudio.algorithms.antcolony.TaskSettings.beta
import com.lstudio.algorithms.antcolony.TaskSettings.logging
import com.lstudio.algorithms.antcolony.TaskSettings.randomFactor
import com.lstudio.utils.random
import java.util.*
import java.util.stream.IntStream
import kotlin.collections.ArrayList
import kotlin.math.pow
import kotlin.math.sqrt

class Route(
    val size: Int, distanceMatrix: Array<DoubleArray>,
    val numberOfAnts: Int, val numberOfCities: Int, val trails: Array<DoubleArray>,
    val antCapacity: Int, val cities: Array<City>, val numberOfStartDepots: Int,
    val startDepots: IntArray
) {

    private val random = Random()
    private var candidateList = ArrayList<Int>()
    private val graph: Array<DoubleArray> = distanceMatrix
    private val ants = ArrayList<Ant>()
    private lateinit var probabilities: MutableMap<List<Int>, Double>


    init {
        IntStream.range(0, numberOfAnts)
            .forEach { ants.add(Ant(numberOfCities, antCapacity)) }
    }

    // init ants
    private fun setupAnts() {
        candidateList.clear()
        candidateList.addAll((0 until size))
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
        log("Ants were set upped and located to start depots")
    }

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

    private fun calculateProbabilitiesNew(ant: Ant) {
        val i = ant.currentCity()

        var denominator = 0.0
        // calculate sum tau(ir) * n(ir)
        val customers = candidateList.filter { cities[it].type == CityType.CUSTOMER }

        for (j in candidateList) {
            denominator += trails[i][j].pow(alpha) * (1.0 / graph[i][j]).pow(beta)
        }

        val optimalNumber = sqrt(candidateList.size.toDouble()).toInt() // 20%

        val indexes1 = customers.shuffled().take(optimalNumber)
        val indexes2 = candidateList.shuffled().take(optimalNumber)

        // not all possible
        // generate all possible pairs (i,r), (r,j)
        // all possible r
        for (r in indexes1) {
            // all possible j
            for (j in indexes2) {
                if (r == j)
                    continue

                denominator += (trails[i][r] * trails[r][j]).pow(alpha) * (1.0 / graph[i][r] + 1.0 / graph[r][j]).pow(
                    beta
                )
            }
        }

        // make new probabilities
        probabilities = mutableMapOf()

        // generate all possible pairs (i,r), (r,j)
        // all possible r
        for (r in indexes1) {
            // all possible j
            for (j in indexes2) {
                if (r == j)
                    continue

                val list = arrayListOf(r, j)
                probabilities[list] =
                    (trails[i][r] * trails[r][j]).pow(alpha) * (1.0 / graph[i][r] + (1.0 / graph[r][j])).pow(
                        beta
                    ) / denominator
            }
        }

        for (j in candidateList) {
            val list = arrayListOf(j)
            probabilities[list] = trails[i][j].pow(alpha) * (1.0 / graph[i][j]).pow(beta) / denominator
        }

        var sumOne = 0.0
        var sumTwo = 0.0
        probabilities.filter { key -> key.key.size == 2 }.forEach { (_, u) -> sumOne += u }
        probabilities.filter { key -> key.key.size == 1 }.forEach { (_, u) -> sumTwo += u }
        if (sumOne + sumTwo > 2)
            throw java.lang.RuntimeException("Wtf")
    }

    private fun log(info: String) {
        if (logging)
            println(info)
    }

    fun constructSolution(): List<Ant> {
        setupAnts()
        moveAnts()
        return ants
    }
}