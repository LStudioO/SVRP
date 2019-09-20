package com.lstudio.algorithms.rvm

import com.lstudio.copyArray

class RingGenerator(permutationLength: Int) {

    private val permutation: IntArray = IntArray(permutationLength)
    private var firstElementSwap: Int = 0
    private var secondElementSwap: Int = 0
    var numberGeneration: Int = 0
        private set

    val maxNumberGeneration: Int
        get() = this.permutation.size * (this.permutation.size - 1) / 2 - this.permutation.size

    init {
        this.firstElementSwap = 0
        this.secondElementSwap = 2
        this.numberGeneration = 0
        this.permutation[firstElementSwap] = 1
        this.permutation[secondElementSwap] = 1
    }

    fun generateNewRoute(route: IntArray): IntArray {
        val newRoute = IntArray(route.size)
        if (this.numberGeneration > 0) {
            changePermutation()
        }

        val arrayIndexOfPermutation = fillArrayIndexOfPermutation()

        val newArrayIndexOfPermutation = computeNewPermutationIndex2opt(arrayIndexOfPermutation)

        for (i in route.indices) {
            if (arrayIndexOfPermutation[i] == newArrayIndexOfPermutation[i]) {
                newRoute[i] = route[i]
            } else {
                newRoute[i] = route[newArrayIndexOfPermutation[i]]
            }
        }

        this.numberGeneration++

        return newRoute
    }

    private fun changePermutation() {
        this.permutation[this.secondElementSwap++] = 0
        if (this.secondElementSwap == this.permutation.size) {
            if (this.firstElementSwap == this.permutation.size - 3) {
                this.permutation[this.permutation.size - 3] = 0
                this.firstElementSwap = 0
                this.secondElementSwap = this.firstElementSwap + 2
                this.permutation[this.firstElementSwap] = 1
                this.permutation[this.secondElementSwap] = 1
            } else {
                this.permutation[this.firstElementSwap++] = 0
                this.permutation[this.firstElementSwap] = 1
                this.secondElementSwap = this.firstElementSwap + 2
                this.permutation[this.secondElementSwap] = 1
            }
        } else {
            if (this.firstElementSwap == 0 && this.secondElementSwap == this.permutation.size - 1) {
                this.permutation[this.firstElementSwap++] = 0
                this.permutation[this.firstElementSwap] = 1
                this.secondElementSwap = this.firstElementSwap + 2
            }
            this.permutation[this.secondElementSwap] = 1
        }
    }

    private fun fillArrayIndexOfPermutation(): IntArray {
        val arrayIndexOfPermutation = IntArray(this.permutation.size)

        for (i in this.permutation.indices) {
            arrayIndexOfPermutation[i] = i
        }

        return arrayIndexOfPermutation
    }

    private fun computeNewPermutationIndex2opt(initialArrayIndexOfPermutation: IntArray): IntArray {
        val newArrayIndexPermutation = initialArrayIndexOfPermutation.copyArray()

        for (i in initialArrayIndexOfPermutation.indices) {
            if (i > this.firstElementSwap && i <= this.secondElementSwap) {
                newArrayIndexPermutation[i] =
                    initialArrayIndexOfPermutation[this.firstElementSwap + this.secondElementSwap + 1 - i]
            }
        }

        return newArrayIndexPermutation
    }

    fun resetNumberGeneration() {
        this.numberGeneration = 0
    }
}
