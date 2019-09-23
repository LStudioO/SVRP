package com.lstudio.algorithms.antcolony.island.topology

class HypercubeTopology(size: Int) : AbstractTopology(size) {
    init {
        if (size % 2 != 0)
            throw Exception("Unsupported size: $size for hypercube")
    }

    override fun calculateNeighborhood(index: Int): List<Int> {
        return (0 until size).filter { it != index }.filter {
            val n = index xor it
            ((n and (n - 1)) == 0)
        }
    }
}