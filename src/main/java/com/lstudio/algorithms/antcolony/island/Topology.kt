package com.lstudio.algorithms.antcolony.island

interface Topology {
    fun calculateNeighborhood(index: Int): List<Int>
}