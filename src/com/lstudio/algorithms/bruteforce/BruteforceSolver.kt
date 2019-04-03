package com.lstudio.algorithms.bruteforce

import com.lstudio.algorithms.bruteforce.ListPartitioner.getAllPartitions
import com.lstudio.algorithms.ls.Vehicle
import java.util.HashMap
import java.util.concurrent.TimeUnit

class BruteforceSolver internal constructor(
    startDepots: IntArray, private val endDepots: HashMap<Int, Int>,
    private val distances: Array<DoubleArray>
) {
    private val noOfVehicles: Int = startDepots.size
    private val noOfCustomers: Int = distances.size - 1 - endDepots.size + endDepots.values.sum()
    private val vehicles: Array<Vehicle>
    private var cost: Double = 0.0

    val startDepotsIndices = startDepots.toList()
    val endDepotsIndices = endDepots.keys.toList()
    val customerIndices = (1 until distances.size).toMutableList().apply {
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
        val startTimeMillis = System.currentTimeMillis()
        val shortestRouteSet = shortestRouteWithPartitions(customerIds, vehicles)
        println("Solution time: ${TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startTimeMillis)} seconds")
        System.out.printf("Shortest distance: %.1f\n", maxLengthForRoutes(shortestRouteSet))
        println("Shortest route: $shortestRouteSet")

        val startDepotList = startDepotsIndices.toMutableList()
        val startDepotPermList = permutations(startDepotList)
        val startDepotDistPerList = startDepotPermList.distinct()
        println("There are ${startDepotDistPerList.size} permutations of start depot\n")
        for (perm in startDepotDistPerList)
            println(perm.joinToString(""))


        val endDepotsList = ArrayList<Int>()
        for (item in endDepots) {
            val key = item.key
            val value = item.value
            for (i in 0 until value)
                endDepotsList.add(key)
        }

        val endDepotList = endDepotsList.toMutableList()
        val endDepotPermList = permutations(endDepotList)
        val endDepotDistPerList = endDepotPermList.distinct()
        println("There are ${endDepotDistPerList.size} permutations of end depot\n")
        for (perm in endDepotDistPerList)
            println(perm.joinToString(""))

        val totalList = ArrayList<ArrayList<List<Int>>>()

        for (start in startDepotDistPerList) {
            for (end in endDepotDistPerList) {
                val list = ArrayList<List<Int>>()
                for (i in 0 until vehicles) {
                    val vehicleRoute = shortestRouteSet[i].toMutableList()
                    vehicleRoute.add(0, start[i])
                    vehicleRoute.add(end[i])
                    list.add(vehicleRoute)
                }
                totalList.add(list)
            }
        }


        val result = totalList.minBy {
            var sum = 0.0
            it.forEach {
                sum += routeLength(it)
            }
            sum
        }

        println("Minimum route: $result")
        var sum = 0.0
        result?.forEach {
            sum += routeLength(it)
        }
        println("Route length: $sum")

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
        sum += distance(route[0], route[route.size - 1])
        return sum
    }

    /**
     * Returns minimum from a list based on route length
     */
    fun shortestRoute(routes: java.util.ArrayList<java.util.ArrayList<Int>>): java.util.ArrayList<Int> {
        return routes.minBy {
            routeLength(it)
        }!!
    }

    /**
     * Return all permutations of a list, each starting with the first item
     */
    fun allRoutes(original: java.util.ArrayList<Int>): java.util.ArrayList<java.util.ArrayList<Int>> {
        if (original.size < 2) {
            return arrayListOf(original)
        } else {
            val firstElement = original.removeAt(0)
            return permutations(original).map {
                it.add(0, firstElement)
                return@map it
            } as java.util.ArrayList<java.util.ArrayList<Int>>
        }
    }

    /**
     * Return maximum from a given route list
     */
    fun maxLengthForRoutes(routeList: List<List<Int>>): Double {
        val routeLengths = java.util.ArrayList<Double>()
        return routeList.mapTo(routeLengths) {
            routeLength(it)
        }.sum()
    }

    /**
     * This function receives all k-subsets of a route and returns the subset
     * with minimum distance cost. Note the total time is always equal to
     * the max time taken by any single vehicle
     */
    fun shortestRouteWithPartitions(locationIds: List<Int>, partitions: Int): List<List<Int>> {
        return allShortRoutesWithPartitions(locationIds, partitions)
            .distinct()
            .minBy {
                maxLengthForRoutes(it)
            }!!
    }

    /**
     * Our partitions represent number of vehicles. This function yields
     * an optimal path for each vehicle given the destinations assigned to it
     */
    fun allShortRoutesWithPartitions(seq: List<Int>, vehicles: Int): java.util.ArrayList<List<List<Int>>> {
        val shortRoutesList = java.util.ArrayList<List<List<Int>>>()
        return getAllPartitions(seq).filter {
            it.size == vehicles
        }.mapTo(shortRoutesList) {
            val shortestRouteWithCurrentPartitions = java.util.ArrayList<List<Int>>()
            it.mapTo(shortestRouteWithCurrentPartitions) {
                val r = ArrayList<Int>()
                r.addAll(it)
                val allRot = allRoutes(r)
                shortestRoute(allRot)
            }
        }
    }
}