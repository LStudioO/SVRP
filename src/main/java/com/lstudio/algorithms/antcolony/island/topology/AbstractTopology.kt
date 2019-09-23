package com.lstudio.algorithms.antcolony.island.topology

abstract class AbstractTopology(val size: Int) : Topology {
    private val connectionGraph: HashMap<Int, List<Int>> = HashMap(size)

    abstract fun calculateNeighborhood(index: Int): List<Int>

    override fun getNeighborhood(index: Int): List<Int> {
        if (!connectionGraph.containsKey(index))
            throw Exception("Invalid index! Did you call buildConnectionGraph() before?")
        else
            return connectionGraph[index]!!
    }

    override fun buildConnectionGraph() {
        for (i in 0 until size) {
            connectionGraph[i] = calculateNeighborhood(i)
        }
    }

    fun printConnections() {
        connectionGraph.forEach { (key, value) -> println("$key = $value") }
    }
}