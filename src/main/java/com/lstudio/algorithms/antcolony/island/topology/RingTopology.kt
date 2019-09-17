package com.lstudio.algorithms.antcolony.island.topology

class RingTopology(size: Int) : AbstractTopology(size) {
    override fun calculateNeighborhood(index: Int): List<Int> {
        val list = arrayListOf<Int>()
        val next = (index + 1) % size
        val previous = if (index == 0) size - 1 else (index - 1) % size
        list.apply {
            add(previous)
            add(next)
        }
        return list
    }
}