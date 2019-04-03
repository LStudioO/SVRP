package com.lstudio.algorithms.ls

import java.util.HashMap

class GreedySolver internal constructor(
    startDepots: IntArray, private val endDepots: HashMap<Int, Int>,
    private val distances: Array<DoubleArray>, vehicleCapacity: Int
) {
    private val noOfVehicles: Int = startDepots.size
    private val nodes: ArrayList<Node>
    private val noOfCustomers: Int
    internal val vehicles: Array<Vehicle>

    internal var cost: Double = 0.0
        private set

    init {
        // form list of start depots, customers, end depots
        val startDepotsIndices = startDepots.toList()
        val endDepotsIndices = endDepots.keys.toList()
        val customerIndices = (1 until distances.size).toMutableList().apply {
            removeAll(startDepotsIndices + endDepotsIndices)
        }
        noOfCustomers = distances.size - 1 - endDepots.size + endDepots.values.sum()
        nodes = ArrayList(noOfCustomers)
        // create start depot nodes
        startDepotsIndices.forEach {
            nodes.add(Node(it, -1, isStartDepot = true, isEndDepot = false))
        }
        // create customer nodes
        customerIndices.forEach {
            nodes.add(Node(it, -1, isStartDepot = false, isEndDepot = false))
        }
        // create end depot nodes
        endDepotsIndices.forEach {
            val capacity = endDepots[it]!!
            for (i in 0 until capacity)
                nodes.add(Node(it, -1, isStartDepot = false, isEndDepot = true))
        }
        // create vehicles
        this.vehicles = Array(this.noOfVehicles) {
            Vehicle(vehicleCapacity)
        }
    }

    private fun unassignedCustomerExists(nodes: ArrayList<Node>): Boolean {
        for (i in 0 until nodes.size) {
            val node = nodes[i]
            if (!node.isRouted && !node.isEndDepot)
                return true
        }
        return false
    }

    internal fun solve(): GreedySolver {
        // candidate cost for current route
        var candCost: Double
        // add start depots to vehicles
        for (vehicle in vehicles) {
            val startNode =
                nodes.find { !it.isRouted && it.isStartDepot } ?: throw Exception("Invalid count of start depots")
            startNode.isRouted = true
            vehicle.addNode(startNode)
        }

        // add customers to vehicles
        loop@ while (unassignedCustomerExists(nodes)) {
            for (vehicle in vehicles) {
                var minCost = Double.MAX_VALUE
                var candidate: Node? = null
                // find best node for current vehicle
                for (i in 0 until noOfCustomers) {
                    if (!nodes[i].isRouted && !nodes[i].isEndDepot) {
                        if (vehicle.currentLocation == i)
                            continue
                        candCost = distances[vehicle.currentLocation][i]
                        System.out.println("$candCost ${vehicle.currentLocation} -> $i ")
                        if (minCost > candCost) {
                            minCost = candCost
                            candidate = nodes[i]
                        }
                    }
                }

                // if candidate doesn't exist - break loop
                if (candidate == null)
                    break@loop

                // add best vehicle to route
                candidate.isRouted = true
                vehicle.addNode(candidate)
                cost += minCost
            }
        }

        // add end depots
        for (vehicle in vehicles) {
            var minCost = Double.MAX_VALUE
            var currentNode: Node? = null
            nodes.filter { it.isEndDepot && !it.isRouted }.forEach {
                candCost = distances[vehicle.currentLocation][it.nodeId]
                if (minCost > candCost) {
                    minCost = candCost
                    currentNode = it
                }
            }

            if (currentNode == null)
                throw Exception("Invalid count of end depots")

            currentNode?.isRouted = true
            vehicle.addNode(currentNode!!)
            cost += minCost
        }

        return this
    }

    fun print() {
        println("Greedy algorithm")
        println("=========================================================")
        for (j in 0 until noOfVehicles) {
            if (!vehicles[j].routes.isEmpty()) {
                print("Vehicle $j:")
                val routeSize = vehicles[j].routes.size
                for (k in 0 until routeSize) {
                    if (k == routeSize - 1) {
                        print(vehicles[j].routes[k].nodeId)
                    } else {
                        print(vehicles[j].routes[k].nodeId.toString() + "->")
                    }
                }
                println()
            }
        }
        println("\nBest Value: " + this.cost + "\n")
    }
}