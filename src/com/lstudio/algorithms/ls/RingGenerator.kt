package com.lstudio.algorithms.ls

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
        val newArrayIndexPermutation = initialArrayIndexOfPermutation.copyOf()

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

    fun findMinLengthRoute(routes: Array<IntArray>, matrixDistance: Array<FloatArray>): Float {
        var minLength = java.lang.Float.MAX_VALUE
        var currentLength: Float

        for (i in routes.indices) {
            currentLength = LocalSearch.computeLengthRoute(routes[i], matrixDistance)
            if (currentLength < minLength) {
                minLength = currentLength
            }
        }

        return minLength
    }

    fun copyArray(initialArray: IntArray): IntArray {
        val newArray = IntArray(initialArray.size)
        System.arraycopy(initialArray, 0, newArray, 0, initialArray.size)
        return newArray
    }

    fun outputArray(array: IntArray) {
        for (i in array.indices) {
            print(array[i].toString() + " ")
        }
        println()
    }

    fun findMinElementInArray(array: FloatArray): Float {
        var minElement = array[0]

        for (i in 1 until array.size) {
            if (minElement > array[i]) {
                minElement = array[i]
            }
        }

        return minElement
    }
}