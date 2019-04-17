package com.lstudio.algorithms.bruteforce

import com.lstudio.algorithms.bruteforce.ListPartitioner.getAllPartitions
import com.lstudio.algorithms.bruteforce.ListPartitioner.getAllPlacements
import com.lstudio.algorithms.ls.model.Vehicle
import java.util.HashMap
import java.util.concurrent.TimeUnit

class BruteforceSolver internal constructor(
    startDepots: IntArray, private val endDepots: HashMap<Int, Int>,
    private val distances: Array<DoubleArray>
) {
    private val noOfVehicles: Int = startDepots.size
    private val vehicles: Array<Vehicle>
    private val startDepotsIndices = startDepots.toList()
    private val endDepotsIndices = endDepots.keys.toList()
    private val customerIndices = (1 until distances.size).toMutableList().apply {
        removeAll(startDepotsIndices + endDepotsIndices)
    }

    init {
        // create vehicles
        this.vehicles = Array(this.noOfVehicles) {
            Vehicle(0)
        }
    }

    internal fun solve(): BruteforceSolver {
        val vehicles = 3
        val customerIds: List<Int> = customerIndices.toList()
        val endDepotsList = ArrayList<Int>()
        for (item in endDepots) {
            val key = item.key
            val value = item.value
            for (i in 0 until value)
                endDepotsList.add(key)
        }
        val startTimeMillis = System.currentTimeMillis()
        val shortestRouteSet = shortestRouteWithPartitions(customerIds, vehicles, startDepotsIndices, endDepotsList)
        println("Solution time: ${(System.currentTimeMillis() - startTimeMillis)} milliseconds")
        System.out.printf("Shortest distance: %.1f\n", maxLengthForRoutes(shortestRouteSet))
        println("Shortest route: $shortestRouteSet")
        return this
    }

    /**
     *Distance between first and last and consecutive elements of a list.
     **/
    fun distance(x: Int, y: Int): Double {
        return distances[x][y]
    }

    /**
     * Distance between first and last and consecutive elements of a list
     */
    fun routeLength(route: List<Int>): Double {
        var sum = 0.0
        for (i in 1 until route.size) sum += distance(route[i], route[i - 1])
        return sum
    }

    /**
     * Returns minimum from a list based on route length
     */
    fun shortestRoute(routes: ArrayList<ArrayList<Int>>): ArrayList<Int> {
        return routes.minBy {
            routeLength(it)
        }!!
    }

    /**
     * Return all permutations of a list, each starting with the first item
     */
    fun allRoutes(original: ArrayList<Int>): ArrayList<ArrayList<Int>> {
        if (original.size < 2) {
            return arrayListOf(original)
        } else {
            val firstElement = original.removeAt(0)
            val lastElement = original.removeAt(original.lastIndex)
            return permutations(original).map {
                it.add(0, firstElement)
                it.add(lastElement)
                return@map it
            } as ArrayList<ArrayList<Int>>
        }
    }

    /**
     * Return maximum from a given route list
     */
    fun maxLengthForRoutes(routeList: List<List<Int>>): Double {
        val routeLengths = ArrayList<Double>()
        return routeList.mapTo(routeLengths) {
            routeLength(it)
        }.sum()
    }

    /**
     * This function receives all k-subsets of a route and returns the subset
     * with minimum distance cost. Note the total time is always equal to
     * the max time taken by any single vehicle
     */
    fun shortestRouteWithPartitions(
        locationIds: List<Int>, partitions: Int,
        startDepots: List<Int>, endDepots: List<Int>
    ): List<List<Int>> {
        val short = allShortRoutesWithPartitions(locationIds, partitions, startDepots, endDepots)
        return short.distinct()
            .minBy {
                maxLengthForRoutes(it)
            }!!
    }

    /**
     * Our partitions represent number of vehicles. This function yields
     * an optimal path for each vehicle given the destinations assigned to it
     */
    fun allShortRoutesWithPartitions(
        seq: List<Int>,
        vehicles: Int,
        startDepots: List<Int>,
        endDepots: List<Int>
    ): ArrayList<List<List<Int>>> {
        val shortRoutesList = ArrayList<List<List<Int>>>()
        val partitions = getAllPartitions(seq).filter {
            it.size == vehicles
        }

        val partitionsWithStartDepots = ArrayList<List<List<Int>>>()

        // add start depots
        val startDepotPermutations = permutations(startDepots.toMutableList())
        partitions.forEach {
            startDepotPermutations.forEach { permutation ->
                val entireRoute = ArrayList<ArrayList<Int>>()
                permutation.forEachIndexed { index, startDepot ->
                    val item = ArrayList<Int>()
                    item.add(startDepot)
                    item.addAll(it[index])
                    entireRoute.add(item)
                }
                partitionsWithStartDepots.add(entireRoute)
            }
        }

        val partitionsWithStartEndDepots = ArrayList<List<List<Int>>>()

        // add end depots
        val endDepotPlacements = getAllPlacements(endDepots.toMutableList(), noOfVehicles)
        partitionsWithStartDepots.forEach {
            endDepotPlacements.forEach { permutation ->
                val entireRoute = ArrayList<ArrayList<Int>>()
                permutation.forEachIndexed { index, endDepot ->
                    val item = ArrayList<Int>()
                    item.addAll(it[index])
                    item.add(endDepot)
                    entireRoute.add(item)
                }
                partitionsWithStartEndDepots.add(entireRoute)
            }
        }

        partitionsWithStartEndDepots.mapTo(shortRoutesList) {
            val shortestRouteWithCurrentPartitions = ArrayList<List<Int>>()
            it.mapTo(shortestRouteWithCurrentPartitions) {
                val r = ArrayList<Int>()
                r.addAll(it)
                val allRot = allRoutes(r)
                shortestRoute(allRot)
            }
        }
        return shortRoutesList
    }
}