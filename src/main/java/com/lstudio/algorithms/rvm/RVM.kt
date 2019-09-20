package com.lstudio.algorithms.rvm

import com.lstudio.copyArray

class RVM {


    private fun computeLengthRoute(route: IntArray, matrixDistance: Array<FloatArray>): Float {
        var routeLength = 0f

        for (i in 0 until route.size - 1) {
            routeLength += matrixDistance[route[i]][route[i + 1]]
        }
        routeLength += matrixDistance[route[route.size - 1]][route[0]]

        return routeLength
    }

    fun calculate(initialSolution: IntArray, matrixDistance: Array<FloatArray>): IntArray {
        val ringGenerator = RingGenerator(matrixDistance.size)
        var bestSolution = initialSolution.copyArray()
        var currentSolution: IntArray

        var numberCompletedIterations = 0
        var differenceRoutesLength = 0f

        while (ringGenerator.numberGeneration < ringGenerator.maxNumberGeneration) {
            currentSolution = ringGenerator.generateNewRoute(bestSolution)
            differenceRoutesLength =
                computeLengthRoute(bestSolution, matrixDistance) - computeLengthRoute(currentSolution, matrixDistance)

            if (differenceRoutesLength > 0) {
                numberCompletedIterations++
                bestSolution = currentSolution.copyArray()
                ringGenerator.resetNumberGeneration()
            }
        }

        return bestSolution
    }
}