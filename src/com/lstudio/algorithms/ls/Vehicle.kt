package com.lstudio.algorithms.ls

import java.util.ArrayList

internal class Vehicle(private val capacity: Int) {
    var routes = ArrayList<Node>()
    var load: Int = 0
    var currentLocation: Int = 0

    init {
        this.load = 0
        this.currentLocation = 0 //In depot Initially
        this.routes.clear()
    }

    fun addNode(Customer: Node)//Add Customer to Vehicle routes
    {
        routes.add(Customer)
        this.load += Customer.demand
        this.currentLocation = Customer.nodeId
    }

    fun checkIfFits(dem: Int) //Check if we have Capacity Violation
            : Boolean {
        return load + dem <= capacity
    }
}