package com.lstudio.algorithms.ls

class LocalSearch(
    distanceMatrix: Array<DoubleArray>,
    private val startDepots: IntArray,
    private val endDepots: HashMap<Int, Int>
) {

    fun startOptimization() {
        println("Not implemented")
    }

    /**
     * Method recession vector for the finding best route
     * @param initialSolution   initial solution
     * @param matrixDistance    matrix distance
     * @return best route
     */
    private fun recessionVectorMethod(initialSolution: IntArray, matrixDistance: Array<FloatArray>): IntArray {
        val ringGenerator = RingGenerator(matrixDistance.size)
        var bestSolution = initialSolution.copyOf()
        var currentSolution: IntArray

        var numberCompletedIterations = 0
        var differenceRoutesLength: Float

        while (ringGenerator.numberGeneration < ringGenerator.maxNumberGeneration) {
            currentSolution = ringGenerator.generateNewRoute(bestSolution)
            differenceRoutesLength = computeLengthRoute(bestSolution, matrixDistance) -
                    computeLengthRoute(currentSolution, matrixDistance)

            if (differenceRoutesLength > 0) {
                numberCompletedIterations++
                bestSolution = currentSolution.copyOf()
                ringGenerator.resetNumberGeneration()
            }
        }

        return bestSolution
    }

    companion object {
        /**
         * Compute length of the route
         */
        fun computeLengthRoute(route: IntArray, matrixDistance: Array<FloatArray>): Float {
            var routeLength = 0f

            for (i in 0 until route.size - 1) {
                routeLength += matrixDistance[route[i]][route[i + 1]]
            }
            routeLength += matrixDistance[route[route.size - 1]][route[0]]

            return routeLength
        }
    }

}