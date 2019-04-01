package com.lstudio.algorithms.ls;

class Node {
    int NodeId;
    int demand; //Node Demand if Customer
    boolean IsRouted;
    boolean isStartDepot;
    boolean isEndDepot;

    Node(int id, int demand, boolean isStartDepot, boolean isEndDepot) //Cunstructor for Customers
    {
        this.NodeId = id;
        this.demand = demand;
        this.IsRouted = false;
        this.isEndDepot = isEndDepot;
        this.isStartDepot = isStartDepot;
    }
}