package com.lstudio.algorithms.antcolony.island

import com.lstudio.algorithms.antcolony.Solution
import com.lstudio.algorithms.antcolony.island.topology.Topology
import com.lstudio.algorithms.antcolony.optimization.DefaultMMASOptimization

class IslandDaemon(private val topology: Topology) {

    init {
        topology.buildConnectionGraph()
    }

    fun getMigrants(acoList: List<DefaultMMASOptimization>): List<Solution> {
        return acoList
            .mapIndexed { index, _ ->
                val migrants = topology.getNeighborhood(index).mapNotNull { acoList[it].bestSolution?.clone() }
                migrants.minBy { it.fitness }
            }
            .filterNotNull()
    }
}