package com.lstudio.algorithms.rvm

import com.lstudio.algorithms.antcolony.TaskSettings
import com.lstudio.algorithms.antcolony.optimization.DefaultMMASOptimization

class RVM(private val provider: () -> DefaultMMASOptimization) {
    private fun computeLengthRoute(params: ArrayList<Double>): Double {
        with(TaskSettings) {
            alpha = params[0]
            beta = params[1]
            rho = params[2]
            stagnationIterationCount = params[3].toInt()
            randomFactor = params[4]
            routeIterations = params[5].toInt()
        }
        val defaultMMASOptimization = provider()
        defaultMMASOptimization.startAntOptimization()
        return defaultMMASOptimization.getCurrentLength()
    }

    fun calculate(): Pair<ArrayList<Double>, Double> {
        // params
        // alpha, beta, rho, stagnationConst, randomFactor, routeIterations
        val paramCount = 6
        val paramsMin: ArrayList<Double> = ArrayList(6)
        val paramsMax: ArrayList<Double> = ArrayList(6)
        val steps: ArrayList<Double> = ArrayList(6)

        paramsMin.addAll(arrayOf(0.1, 0.1, 0.05, 100.0, 0.01, 3.0))
        paramsMax.addAll(arrayOf(2.0, 2.0, 0.3, 1000.0, 0.1, 10.0))
        steps.addAll(arrayOf(0.01, 0.01, 0.01, 50.0, 0.001, 1.0))

        var bestSolution: Pair<ArrayList<Double>, Double> = Pair(arrayListOf(), Double.MAX_VALUE)

        val ringGenerator = RingGeneratorParameters(paramsMin, paramsMax, steps)
        var currentSolution: ArrayList<Double>

        var numberCompletedIterations = 0
        var differenceRoutesLength = 0.0

        while (ringGenerator.hasAvailableCombination()) {
            currentSolution = ringGenerator.getNewSet()
            println(currentSolution.joinToString { "$it | " })

            val length = computeLengthRoute(currentSolution)
            differenceRoutesLength = bestSolution.second - length

            if (differenceRoutesLength > 0) {
                numberCompletedIterations++
                bestSolution = Pair(currentSolution, length)
                ringGenerator.markImprovement()
            }
        }

        return bestSolution
    }
}