package com.lstudio.algorithms.ls.model

import java.util.*

internal class Vehicle(private val capacity: Int) {
    var routes = ArrayList<Node>()
    var load: Double = 0.0
    var currentLocation: Int = 0

    init {
        this.currentLocation = 0 //In depot Initially
        this.routes.clear()
    }

    //Add Customer to Vehicle routes
    fun addNode(Customer: Node) {
        routes.add(Customer)
        this.load += Customer.demand
        this.currentLocation = Customer.nodeId
    }

    //Check if we have Capacity Violation
    fun checkIfFits(dem: Double): Boolean {
        return load + dem <= capacity
    }
}