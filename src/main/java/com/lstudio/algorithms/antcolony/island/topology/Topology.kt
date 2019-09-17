package com.lstudio.algorithms.antcolony.island.topology

interface Topology {
    fun getNeighborhood(index: Int): List<Int>
    fun buildConnectionGraph()
}