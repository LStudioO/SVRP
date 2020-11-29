package com.lstudio.algorithms.antcolony.route

import com.lstudio.algorithms.antcolony.Ant
import com.lstudio.algorithms.antcolony.City
import com.lstudio.algorithms.antcolony.CityType
import com.lstudio.algorithms.antcolony.TaskSettings
import com.lstudio.algorithms.antcolony.TaskSettings.randomFactor
import com.lstudio.utils.random
import kotlin.math.pow

class FarsightedRoute(
    size: Int, distanceMatrix: Array<DoubleArray>,
    numberOfAnts: Int, numberOfCities: Int, trails: Array<DoubleArray>,
    antCapacity: Int, cities: Array<City>, numberOfStartDepots: Int,
    startDepots: IntArray
) : AbstractRoute(
    size,
    distanceMatrix,
    numberOfAnts,
    numberOfCities,
    trails,
    antCapacity,
    cities,
    numberOfStartDepots,
    startDepots
) {
    private lateinit var probabilities: MutableMap<List<Int>, Double>

    override fun selectNextCity(ant: Ant): List<Int> {
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

    private fun calculateProbabilitiesNew(ant: Ant) {
        val i = ant.currentCity()

        var denominator = 0.0
        // calculate sum tau(ir) * n(ir)
        val customers = candidateList.filter { cities[it].type == CityType.CUSTOMER }

        for (j in candidateList) {
            denominator += trails[i][j].pow(TaskSettings.alpha) * (1.0 / graph[i][j]).pow(
                TaskSettings.beta
            )
        }

        val indexes1 = customers
        val indexes2 = candidateList

        // not all possible
        // generate all possible pairs (i,r), (r,j)
        // all possible r
        for (r in indexes1) {
            // all possible j
            for (j in indexes2) {
                if (r == j)
                    continue

                denominator += (trails[i][r] * trails[r][j]).pow(TaskSettings.alpha) * (1.0 / graph[i][r] + 1.0 / graph[r][j]).pow(
                    TaskSettings.beta
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
                    (trails[i][r] * trails[r][j]).pow(TaskSettings.alpha) * (1.0 / graph[i][r] + (1.0 / graph[r][j])).pow(
                        TaskSettings.beta
                    ) / denominator
            }
        }

        for (j in candidateList) {
            val list = arrayListOf(j)
            probabilities[list] = trails[i][j].pow(TaskSettings.alpha) * (1.0 / graph[i][j]).pow(
                TaskSettings.beta
            ) / denominator
        }

        var sumOne = 0.0
        var sumTwo = 0.0
        probabilities.filter { key -> key.key.size == 2 }.forEach { (_, u) -> sumOne += u }
        probabilities.filter { key -> key.key.size == 1 }.forEach { (_, u) -> sumTwo += u }
        if (sumOne + sumTwo > 2)
            throw java.lang.RuntimeException("Sum can not be bigger than 2")
    }
}