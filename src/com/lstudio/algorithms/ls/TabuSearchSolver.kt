package com.lstudio.algorithms.ls

import java.util.ArrayList
import java.util.HashMap
import java.util.Random

class TabuSearchSolver(
    private val tabuHorizon: Int, private val distances: Array<DoubleArray>, startDepots: IntArray,
    endDepots: HashMap<Int, Int>, private val iterations: Int
) {
    private val noOfVehicles: Int = startDepots.size
    private val bestSolutionVehicles: Array<Vehicle>

    private var vehicles: Array<Vehicle>? = null
    private var cost: Double = 0.toDouble()

    private var bestSolutionCost: Double = 0.toDouble()

    init {
        val vehicleCapacity = 1000

        var endDepotsCapacity = 0
        for (value in endDepots.values) {
            endDepotsCapacity += value
        }

        // val nodesCount = distances.size - endDepots.size + endDepotsCapacity

        // create solution using greedy algorithm
        val greedySolver = GreedySolver(
            startDepots, endDepots,
            distances, vehicleCapacity
        )
        val result = greedySolver.solve()
        result.print()

        this.vehicles = greedySolver.vehicles
        this.cost = greedySolver.cost

        this.bestSolutionVehicles = Array(this.noOfVehicles) {
            Vehicle(vehicleCapacity)
        }
    }

    fun solve(): TabuSearchSolver {
        //We use 1-0 exchange move
        var routesFrom: ArrayList<Node>
        var routesTo: ArrayList<Node>

        var MovingNodeDemand = 0

        var VehIndexFrom: Int
        var VehIndexTo: Int
        var BestNCost: Double
        var NeighborCost: Double

        var SwapIndexA = -1
        var SwapIndexB = -1
        var SwapRouteFrom = -1
        var SwapRouteTo = -1
        var iteration_number = 0

        val DimensionCustomer = this.distances[1].size
        val TABU_Matrix = Array(DimensionCustomer + 1) { IntArray(DimensionCustomer + 1) }

        this.bestSolutionCost = this.cost

        while (true) {
            BestNCost = java.lang.Double.MAX_VALUE

            VehIndexFrom = 0
            while (VehIndexFrom < this.vehicles!!.size) {
                routesFrom = this.vehicles!![VehIndexFrom].routes
                val RoutFromLength = routesFrom.size

                for (i in 1 until RoutFromLength - 1) { //Not possible to move depot!
                    VehIndexTo = 0
                    while (VehIndexTo < this.vehicles!!.size) {
                        routesTo = this.vehicles!![VehIndexTo].routes
                        val RouteToLength = routesTo.size
                        var j = 0
                        while (j < RouteToLength - 1) {//Not possible to move after last Depot!

                            MovingNodeDemand = routesFrom[i].demand

                            if (VehIndexFrom == VehIndexTo || this.vehicles!![VehIndexTo].checkIfFits(MovingNodeDemand)) {
                                //If we assign to a different route check capacity constrains
                                //if in the new route is the same no need to check for capacity

                                if (!(VehIndexFrom == VehIndexTo && (j == i || j == i - 1)))
                                // Not a move that Changes solution cost
                                {
                                    val MinusCost1 = this.distances[routesFrom[i - 1].nodeId][routesFrom[i].nodeId]
                                    val MinusCost2 = this.distances[routesFrom[i].nodeId][routesFrom[i + 1].nodeId]
                                    val MinusCost3 = this.distances[routesTo[j].nodeId][routesTo[j + 1].nodeId]

                                    val AddedCost1 = this.distances[routesFrom[i - 1].nodeId][routesFrom[i + 1].nodeId]
                                    val AddedCost2 = this.distances[routesTo[j].nodeId][routesFrom[i].nodeId]
                                    val AddedCost3 = this.distances[routesFrom[i].nodeId][routesTo[j + 1].nodeId]

                                    //Check if the move is a Tabu! - If it is Tabu break
                                    if (TABU_Matrix[routesFrom[i - 1].nodeId][routesFrom[i + 1].nodeId] != 0
                                        || TABU_Matrix[routesTo[j].nodeId][routesFrom[i].nodeId] != 0
                                        || TABU_Matrix[routesFrom[i].nodeId][routesTo[j + 1].nodeId] != 0
                                    ) {
                                        break
                                    }

                                    NeighborCost = (AddedCost1 + AddedCost2 + AddedCost3
                                            - MinusCost1 - MinusCost2 - MinusCost3)

                                    if (NeighborCost < BestNCost) {
                                        BestNCost = NeighborCost
                                        SwapIndexA = i
                                        SwapIndexB = j
                                        SwapRouteFrom = VehIndexFrom
                                        SwapRouteTo = VehIndexTo
                                    }
                                }
                            }
                            j++
                        }
                        VehIndexTo++
                    }
                }
                VehIndexFrom++
            }

            for (o in 0 until TABU_Matrix[0].size) {
                for (p in 0 until TABU_Matrix[0].size) {
                    if (TABU_Matrix[o][p] > 0) {
                        TABU_Matrix[o][p]--
                    }
                }
            }

            routesFrom = this.vehicles!![SwapRouteFrom].routes
            routesTo = this.vehicles!![SwapRouteTo].routes
            this.vehicles!![SwapRouteFrom].routes = null
            this.vehicles!![SwapRouteTo].routes = null

            val SwapNode = routesFrom[SwapIndexA]

            val NodeIDBefore = routesFrom[SwapIndexA - 1].nodeId
            val NodeIDAfter = routesFrom[SwapIndexA + 1].nodeId
            val NodeID_F = routesTo[SwapIndexB].nodeId
            val NodeID_G = routesTo[SwapIndexB + 1].nodeId

            val TabuRan = Random()
            val randomDelay1 = TabuRan.nextInt(5)
            val randomDelay2 = TabuRan.nextInt(5)
            val randomDelay3 = TabuRan.nextInt(5)

            TABU_Matrix[NodeIDBefore][SwapNode.nodeId] = this.tabuHorizon + randomDelay1
            TABU_Matrix[SwapNode.nodeId][NodeIDAfter] = this.tabuHorizon + randomDelay2
            TABU_Matrix[NodeID_F][NodeID_G] = this.tabuHorizon + randomDelay3

            routesFrom.removeAt(SwapIndexA)

            if (SwapRouteFrom == SwapRouteTo) {
                if (SwapIndexA < SwapIndexB) {
                    routesTo.add(SwapIndexB, SwapNode)
                } else {
                    routesTo.add(SwapIndexB + 1, SwapNode)
                }
            } else {
                routesTo.add(SwapIndexB + 1, SwapNode)
            }

            this.vehicles!![SwapRouteFrom].routes = routesFrom
            this.vehicles!![SwapRouteFrom].load -= MovingNodeDemand

            this.vehicles!![SwapRouteTo].routes = routesTo
            this.vehicles!![SwapRouteTo].load += MovingNodeDemand

            this.cost += BestNCost

            if (this.cost < this.bestSolutionCost) {
                iteration_number = 0
                this.SaveBestSolution()
            } else {
                iteration_number++
            }

            if (iterations == iteration_number) {
                break
            }
        }

        this.vehicles = this.bestSolutionVehicles
        this.cost = this.bestSolutionCost

        return this
    }

    private fun SaveBestSolution() {
        this.bestSolutionCost = this.cost
        for (j in 0 until this.noOfVehicles) {
            this.bestSolutionVehicles[j].routes.clear()
            if (!this.vehicles!![j].routes.isEmpty()) {
                val RoutSize = this.vehicles!![j].routes.size
                for (k in 0 until RoutSize) {
                    val n = this.vehicles!![j].routes[k]
                    this.bestSolutionVehicles[j].routes.add(n)
                }
            }
        }
    }

    fun print() {
        println("=========================================================")

        for (j in 0 until this.noOfVehicles) {
            if (!this.vehicles!![j].routes.isEmpty()) {
                print("Vehicle $j:")
                val RoutSize = this.vehicles!![j].routes.size
                for (k in 0 until RoutSize) {
                    if (k == RoutSize - 1) {
                        print(this.vehicles!![j].routes[k].nodeId)
                    } else {
                        print(this.vehicles!![j].routes[k].nodeId.toString() + "->")
                    }
                }
                println()
            }
        }
        println("\nBest Value: " + this.cost + "\n")
    }
}