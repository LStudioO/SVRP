package com.lstudio.algorithms.rvm

class RingGeneratorParameters(
    private val paramsMin: ArrayList<Double>,
    private val paramsMax: ArrayList<Double>,
    private val steps: ArrayList<Double>
) {

    private var noImprovementIterations = 0
    private val noImprovementIterationsMax = 500
    private val params = ArrayList(paramsMin)
    private var currentParamIndex = 0
        set(value) {
            field = if (value >= params.size)
                0
            else
                value
        }

    fun getNewSet(): ArrayList<Double> {
        noImprovementIterations++
        generateNewParams()
        return ArrayList(params)
    }

    private fun generateNewParams() {
        params[currentParamIndex] += steps[currentParamIndex]

        if (params[currentParamIndex] > paramsMax[currentParamIndex]) {
            params[currentParamIndex] = paramsMin[currentParamIndex]
            currentParamIndex++
        }
    }

    fun hasAvailableCombination(): Boolean {
        return noImprovementIterations < noImprovementIterationsMax
    }

    fun markImprovement() {
        noImprovementIterations = 0
        currentParamIndex++
    }
}