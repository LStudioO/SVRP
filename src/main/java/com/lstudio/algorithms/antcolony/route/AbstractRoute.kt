package com.lstudio.algorithms.antcolony.route

import com.lstudio.algorithms.antcolony.Ant
import com.lstudio.algorithms.antcolony.City
import com.lstudio.algorithms.antcolony.CityType
import com.lstudio.algorithms.antcolony.TaskSettings.logging
import java.util.*
import java.util.stream.IntStream
import kotlin.collections.ArrayList

abstract class AbstractRoute(
    val size: Int, distanceMatrix: Array<DoubleArray>,
    private val numberOfAnts: Int, private val numberOfCities: Int, val trails: Array<DoubleArray>,
    private val antCapacity: Int, val cities: Array<City>, private val numberOfStartDepots: Int,
    val startDepots: IntArray
) {

    val random = Random()
    var candidateList = ArrayList<Int>()
    val graph: Array<DoubleArray> = distanceMatrix
    private val ants = ArrayList<Ant>()

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

    abstract fun selectNextCity(ant: Ant): List<Int>

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