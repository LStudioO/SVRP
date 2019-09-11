package com.lstudio.algorithms.antcolony.route

import com.lstudio.algorithms.antcolony.Ant
import com.lstudio.algorithms.antcolony.City
import com.lstudio.algorithms.antcolony.TaskSettings.alpha
import com.lstudio.algorithms.antcolony.TaskSettings.beta
import com.lstudio.algorithms.antcolony.TaskSettings.randomFactor
import com.lstudio.utils.random
import kotlin.math.pow

class BasicRoute(
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

    private val probabilities: DoubleArray = DoubleArray(numberOfCities)

    private fun calculateProbabilities(ant: Ant) {
        val i = ant.currentCity()
        var pheromone = 0.0
        for (l in 0 until candidateList.size) {
            val index = candidateList[l]
            if (!ant.visited(index)) {
                pheromone += trails[i][index].pow(alpha) * (1.0 / graph[i][index]).pow(beta)
            }
        }
        for (k in 0 until candidateList.size) {
            val index = candidateList[k]
            if (ant.visited(index)) {
                probabilities[index] = 0.0
            } else {
                val numerator = trails[i][index].pow(alpha) * (1.0 / graph[i][index]).pow(beta)
                probabilities[index] = numerator / pheromone
            }
        }
    }

    override fun selectNextCity(ant: Ant): List<Int> {
        val t = candidateList.random()
        if (random.nextDouble() < randomFactor) {
            if (!ant.visited(t))
                return listOf(t)
        }
        calculateProbabilities(ant)
        val r = random.nextDouble()
        var total = 0.0
        for (i in 0 until candidateList.size) {
            val index = candidateList[i]
            total += probabilities[index]
            if (total >= r) {
                return listOf(index)
            }
        }

        throw RuntimeException("There are no other cities")
    }
}