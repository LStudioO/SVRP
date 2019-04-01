package com.lstudio.algorithms.ls;

import java.util.ArrayList;

class Vehicle {
    ArrayList<Node> routes = new ArrayList<>();
    private int capacity;
    int load;
    int currentLocation;
    Node endDepot;

    Vehicle(int cap) {
        this.capacity = cap;
        this.load = 0;
        this.currentLocation = 0; //In depot Initially
        this.routes.clear();
    }

    void AddNode(Node Customer)//Add Customer to Vehicle routes
    {
        routes.add(Customer);
        this.load += Customer.demand;
        this.currentLocation = Customer.NodeId;
    }

    boolean CheckIfFits(int dem) //Check if we have Capacity Violation
    {
        return load + dem <= capacity;
    }
}