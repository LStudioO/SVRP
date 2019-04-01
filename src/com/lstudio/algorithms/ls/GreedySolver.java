package com.lstudio.algorithms.ls;

import java.util.HashMap;

public class GreedySolver {
    private final int noOfVehicles;
    private final Node[] nodes;
    private final double[][] distances;
    private final int noOfCustomers;
    private final Vehicle[] vehicles;
    int[] startDepots;
    HashMap<Integer, Integer> endDepots;

    private double cost;

    GreedySolver(int[] startDepots, HashMap<Integer, Integer> endDepots,
                 int numberOfCustomers, int numberOfVehicles,
                 double[][] distances, int vehicleCapacity) {
        this.startDepots = startDepots;
        this.endDepots = endDepots;
        this.noOfCustomers = numberOfCustomers;
        this.noOfVehicles = numberOfVehicles;
        this.distances = distances;
        this.cost = 0;

        nodes = new Node[noOfCustomers];

        for (int i = 0; i < noOfCustomers; i++) {
            boolean isStartDepot = false;
            boolean isEndDepot = false;

            for (int startDepot : startDepots) {
                if (startDepot == i) {
                    isStartDepot = true;
                    break;
                }
            }

            if (endDepots.containsKey(i))
                isEndDepot = true;

            nodes[i] = new Node(i, 150, isStartDepot, isEndDepot);
        }

        this.vehicles = new Vehicle[this.noOfVehicles];

        for (int i = 0; i < this.noOfVehicles; i++) {
            vehicles[i] = new Vehicle(vehicleCapacity);
        }
    }

    private boolean unassignedCustomerExists(Node[] Nodes) {
        for (int i = 1; i < Nodes.length; i++) {
            if (!Nodes[i].IsRouted)
                return true;
        }
        return false;
    }

    GreedySolver solve() {
        double CandCost, EndCost;
        int VehIndex = 0;

        // end start depot to each vehicle
        for (int i = 0; i < vehicles.length; i++) {
            Node node = nodes[startDepots[i]];
            node.IsRouted = true;
            vehicles[i].AddNode(node);
        }

        while (unassignedCustomerExists(nodes)) {
            int CustIndex = 0;
            Node Candidate = null;
            double minCost = (float) Double.MAX_VALUE;

            for (int i = 0; i < noOfCustomers; i++) {
                if (!nodes[i].IsRouted) {
                    if (vehicles[VehIndex].endDepot == null && nodes[i].isEndDepot) {
                        nodes[i].IsRouted = true;
                        vehicles[VehIndex].endDepot = nodes[i];
                        continue;
                    }
                    if (vehicles[VehIndex].CheckIfFits(nodes[i].demand)) {
                        CandCost = distances[vehicles[VehIndex].currentLocation][i];
                        if (minCost > CandCost) {
                            minCost = CandCost;
                            CustIndex = i;
                            Candidate = nodes[i];
                        }
                    }
                }
            }

            if (Candidate == null) {
                //Not a single Customer Fits
                if (VehIndex + 1 < vehicles.length) //We have more vehicles to assign
                {
                    if (vehicles[VehIndex].currentLocation != 0) {//End this route
                        EndCost = distances[vehicles[VehIndex].currentLocation][0];
                        vehicles[VehIndex].AddNode(nodes[0]);
                        this.cost += EndCost;
                    }
                    VehIndex = VehIndex + 1; //Go to next Vehicle
                } else //We DO NOT have any more vehicle to assign. The problem is unsolved under these parameters
                {
                    System.out.println("\nThe rest customers do not fit in any Vehicle\n" +
                            "The problem cannot be resolved under these constrains");
                    System.exit(0);
                }
            } else {
                vehicles[VehIndex].AddNode(Candidate);//If a fitting Customer is Found
                nodes[CustIndex].IsRouted = true;
                this.cost += minCost;
                VehIndex++;
                if (VehIndex > vehicles.length - 1) {
                    VehIndex = 0;
                }
            }
        }

        EndCost = distances[vehicles[VehIndex].currentLocation][0];
        vehicles[VehIndex].AddNode(nodes[0]);
        this.cost += EndCost;

        return this;
    }

    public void print() {
        System.out.println("=========================================================");

        for (int j = 0; j < noOfVehicles; j++) {
            if (!vehicles[j].routes.isEmpty()) {
                System.out.print("Vehicle " + j + ":");
                int RoutSize = vehicles[j].routes.size();
                for (int k = 0; k < RoutSize; k++) {
                    if (k == RoutSize - 1) {
                        System.out.print(vehicles[j].routes.get(k).NodeId);
                    } else {
                        System.out.print(vehicles[j].routes.get(k).NodeId + "->");
                    }
                }
                System.out.println();
            }
        }
        System.out.println("\nBest Value: " + this.cost + "\n");
    }

    Vehicle[] getVehicles() {
        return vehicles;
    }

    double getCost() {
        return cost;
    }
}