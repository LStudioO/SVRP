package com.lstudio.algorithms.ls

import com.lstudio.algorithms.ls.model.Node
import com.lstudio.algorithms.ls.model.Vehicle
import java.util.*

class TabuSearchSolver(
    private val tabuHorizon: Int,
    private val distances: Array<DoubleArray>,
    startDepots: IntArray,
    endDepots: HashMap<Int, Int>,
    private val iterations: Int
) {
    private val noOfVehicles: Int = startDepots.size
    private val bestSolutionVehicles: Array<Vehicle>

    private var vehicles: Array<Vehicle>? = null
    var cost: Double = 0.0

    private var bestSolutionCost: Double = 0.0

    init {
        val vehicleCapacity = 1000
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

        var movingNodeDemand = 0

        var vIndexFrom: Int
        var vIndexTo: Int
        var bestNCost: Double
        var neighborCost: Double

        var swapIndexA = -1
        var swapIndexB = -1
        var swapRouteFrom = -1
        var swapRouteTo = -1
        var iterationNumber = 0

        val customerDimension = this.distances[1].size
        val tabuMatrix = Array(customerDimension + 1) { IntArray(customerDimension + 1) }

        this.bestSolutionCost = this.cost

        var wasNewVariantCreated = false

        while (true) {
            bestNCost = java.lang.Double.MAX_VALUE
            vIndexFrom = 0
            while (vIndexFrom < this.vehicles!!.size) {
                routesFrom = this.vehicles!![vIndexFrom].routes
                val routeFromLength = routesFrom.size

                for (i in 1 until routeFromLength - 1) { //Not possible to move depot!
                    vIndexTo = 0
                    while (vIndexTo < this.vehicles!!.size) {
                        routesTo = this.vehicles!![vIndexTo].routes
                        val routeToLength = routesTo.size
                        var j = 0
                        while (j < routeToLength - 1) {//Not possible to move after last Depot!

                            movingNodeDemand = routesFrom[i].demand

                            if (vIndexFrom == vIndexTo || this.vehicles!![vIndexTo].checkIfFits(movingNodeDemand.toDouble())) {
                                //If we assign to a different route check capacity constrains
                                //if in the new route is the same no need to check for capacity

                                if (!(vIndexFrom == vIndexTo && (j == i || j == i - 1)))
                                // Not a move that Changes solution cost
                                {
                                    val minusCost1 = this.distances[routesFrom[i - 1].nodeId][routesFrom[i].nodeId]
                                    val minusCost2 = this.distances[routesFrom[i].nodeId][routesFrom[i + 1].nodeId]
                                    val minusCost3 = this.distances[routesTo[j].nodeId][routesTo[j + 1].nodeId]

                                    val addedCost1 = this.distances[routesFrom[i - 1].nodeId][routesFrom[i + 1].nodeId]
                                    val addedCost2 = this.distances[routesTo[j].nodeId][routesFrom[i].nodeId]
                                    val addedCost3 = this.distances[routesFrom[i].nodeId][routesTo[j + 1].nodeId]

                                    //Check if the move is a Tabu! - If it is Tabu break
                                    if (tabuMatrix[routesFrom[i - 1].nodeId][routesFrom[i + 1].nodeId] != 0
                                        || tabuMatrix[routesTo[j].nodeId][routesFrom[i].nodeId] != 0
                                        || tabuMatrix[routesFrom[i].nodeId][routesTo[j + 1].nodeId] != 0
                                    ) {
                                        break
                                    }

                                    neighborCost = (addedCost1 + addedCost2 + addedCost3
                                            - minusCost1 - minusCost2 - minusCost3)

                                    if (neighborCost < bestNCost) {
                                        wasNewVariantCreated = true
                                        bestNCost = neighborCost
                                        swapIndexA = i
                                        swapIndexB = j
                                        swapRouteFrom = vIndexFrom
                                        swapRouteTo = vIndexTo
                                    }
                                }
                            }
                            j++
                        }
                        vIndexTo++
                    }
                }
                vIndexFrom++
            }

            if (!wasNewVariantCreated) {
                this.vehicles = this.bestSolutionVehicles
                this.cost = this.bestSolutionCost
                return this
            } else {
                wasNewVariantCreated = false
            }

            for (o in tabuMatrix[0].indices) {
                for (p in tabuMatrix[0].indices) {
                    if (tabuMatrix[o][p] > 0) {
                        tabuMatrix[o][p]--
                    }
                }
            }

            routesFrom = this.vehicles!![swapRouteFrom].routes
            routesTo = this.vehicles!![swapRouteTo].routes
            this.vehicles!![swapRouteFrom].routes = ArrayList()
            this.vehicles!![swapRouteTo].routes = ArrayList()

            val swapNode = routesFrom[swapIndexA]

            val nodeIDBefore = routesFrom[swapIndexA - 1].nodeId
            val nodeIDAfter = routesFrom[swapIndexA + 1].nodeId
            val nodeID_F = routesTo[swapIndexB].nodeId
            val nodeID_G = routesTo[swapIndexB + 1].nodeId

            val tabuRan = Random()
            val randomDelay1 = tabuRan.nextInt(5)
            val randomDelay2 = tabuRan.nextInt(5)
            val randomDelay3 = tabuRan.nextInt(5)

            tabuMatrix[nodeIDBefore][swapNode.nodeId] = this.tabuHorizon + randomDelay1
            tabuMatrix[swapNode.nodeId][nodeIDAfter] = this.tabuHorizon + randomDelay2
            tabuMatrix[nodeID_F][nodeID_G] = this.tabuHorizon + randomDelay3

            routesFrom.removeAt(swapIndexA)

            if (swapRouteFrom == swapRouteTo) {
                if (swapIndexA < swapIndexB) {
                    routesTo.add(swapIndexB, swapNode)
                } else {
                    routesTo.add(swapIndexB + 1, swapNode)
                }
            } else {
                routesTo.add(swapIndexB + 1, swapNode)
            }

            this.vehicles!![swapRouteFrom].routes = routesFrom
            this.vehicles!![swapRouteFrom].load -= movingNodeDemand

            this.vehicles!![swapRouteTo].routes = routesTo
            this.vehicles!![swapRouteTo].load += movingNodeDemand

            this.cost += bestNCost

            if (this.cost < this.bestSolutionCost) {
                iterationNumber = 0
                this.saveBestSolution()
            } else {
                iterationNumber++
            }

            if (iterations == iterationNumber) {
                break
            }
        }

        this.vehicles = this.bestSolutionVehicles
        this.cost = this.bestSolutionCost

        return this
    }

    private fun saveBestSolution() {
        this.bestSolutionCost = this.cost
        for (j in 0 until this.noOfVehicles) {
            this.bestSolutionVehicles[j].routes.clear()
            if (this.vehicles!![j].routes.isNotEmpty()) {
                val routeSize = this.vehicles!![j].routes.size
                for (k in 0 until routeSize) {
                    val n = this.vehicles!![j].routes[k]
                    this.bestSolutionVehicles[j].routes.add(n)
                }
            }
        }
    }

    fun print() {
        println("=========================================================")
        println("Tabu search")
        vehicles?.forEach {
            println(it.routes)
        }
        println("Best Value: " + this.cost + "\n")
    }
}