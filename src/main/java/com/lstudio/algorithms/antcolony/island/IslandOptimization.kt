package com.lstudio.algorithms.antcolony.island

import com.lstudio.algorithms.antcolony.island.topology.HypercubeTopology
import com.lstudio.algorithms.antcolony.island.topology.Topology
import com.lstudio.algorithms.antcolony.optimization.FarsightedMMASOptimization
import kotlinx.coroutines.*

class IslandOptimization(
    private val distanceMatrix: Array<DoubleArray>,
    private val startDepots: IntArray,
    private val endDepots: HashMap<Int, Int>,
    private val topology: Topology? = HypercubeTopology(islandsCount)
) {

    var bestValue: Double = 0.0
        private set
    private var islandDaemon: IslandDaemon? = null
    private val acoList = arrayListOf<FarsightedMMASOptimization>()

    private fun setupAco() {
        bestValue = 0.0
        acoList.clear()
        // init island daemon if needed
        topology?.let {
            islandDaemon = IslandDaemon(it)
        }
        // create instances of ACO
        repeat(islandsCount) {
            acoList.add(FarsightedMMASOptimization(distanceMatrix, startDepots, endDepots))
        }
        acoList.forEach { it.setup() }
    }

    @ObsoleteCoroutinesApi
    suspend fun runExperiment(output: ((String) -> Unit)? = null) {
        GlobalScope.launch {
            setupAco()
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
            val acoBest = acoList.minBy { it.bestSolution?.fitness ?: Double.MAX_VALUE }
            bestValue = acoBest?.getCurrentLength() ?: -1.0
            output?.invoke(acoBest?.bestSolution?.toString().orEmpty() + "Length: $bestValue")
        }
    }

    companion object {
        private const val threadCount = 8
        const val islandsCount = 8
        private const val cycleIterations = 50
    }
}