package com.lstudio.algorithms.antcolony.island

import com.lstudio.algorithms.antcolony.island.topology.HypercubeTopology
import com.lstudio.algorithms.antcolony.island.topology.Topology
import com.lstudio.algorithms.antcolony.optimization.FarsightedMMASOptimization
import kotlinx.coroutines.*

class IslandOptimization(
    private val distanceMatrix: Array<DoubleArray>,
    private val startDepots: IntArray,
    private val endDepots: HashMap<Int, Int>,
    private var topology: Topology? = HypercubeTopology(islandsCount)
) {

    var bestValue: Double = 0.0
    private var islandDaemon: IslandDaemon? = null

    @ObsoleteCoroutinesApi
    fun start() = runBlocking {

        // init island daemon if needed
        topology?.let {
            islandDaemon = IslandDaemon(it)
        }

        // create instances of ACO
        val acoList = arrayListOf<FarsightedMMASOptimization>()

        repeat(islandsCount) {
            acoList.add(FarsightedMMASOptimization(distanceMatrix, startDepots, endDepots))
        }

        acoList.forEach { it.setup() }

        val context = newFixedThreadPoolContext(threadCount, "ACO")

        // iterations, replace with while (termination condition)
        for (i in 0..100) {
            val jobs = ArrayList<Deferred<Unit>>()
            // default ACO phase
            // forEach of ACO instances
            acoList.forEach { aco ->
                jobs.add(CoroutineScope(context).async {
                    aco.runIterations(cycleIterations)
                })
            }

            jobs.awaitAll()
            jobs.clear()

            // migration phase
            val migrants =
                if (islandDaemon == null) {
                    arrayListOf(acoList.minBy { it.bestSolution?.fitness ?: Double.MAX_VALUE }?.bestSolution)
                } else {
                    islandDaemon?.getMigrants(acoList)
                }

            acoList.forEachIndexed { index, aco ->
                migrants?.let { migrants ->
                    val k = if (migrants.size == 1) 0 else index
                    val bestSolution = migrants[k]
                    if (bestSolution != null)
                        jobs.add(CoroutineScope(context).async {
                            aco.applyMigrant(bestSolution)
                        })
                }
            }

            jobs.awaitAll()
            jobs.clear()

        }
        bestValue =
            acoList.minBy { it.bestSolution?.fitness ?: Double.MAX_VALUE }?.getCurrentLength() ?: -1.0 //?.printResult()
    }

    companion object {
        private const val threadCount = 8
        private const val islandsCount = 8
        private const val cycleIterations = 50
    }
}