package com.lstudio.algorithms.antcolony.island

import com.lstudio.algorithms.antcolony.optimization.FarsightedMMASOptimization
import kotlinx.coroutines.*

class IslandOptimization(
    private val distanceMatrix: Array<DoubleArray>,
    private val startDepots: IntArray,
    private val endDepots: HashMap<Int, Int>
) {

    @ObsoleteCoroutinesApi
    fun start() = runBlocking {
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

            acoList.minBy { it.bestSolution?.fitness ?: Double.MAX_VALUE }?.bestSolution?.let { bestSolution ->
                acoList.forEach { aco ->
                    jobs.add(CoroutineScope(context).async {
                        aco.applyMigrant(bestSolution)
                    })
                }

                jobs.awaitAll()
                jobs.clear()
            }
        }
        acoList.minBy { it.bestSolution?.fitness ?: Double.MAX_VALUE }?.printResult()
    }

    companion object {
        private const val threadCount = 8
        private const val islandsCount = 8
        private const val cycleIterations = 50
    }
}