package com.lstudio.pointrestorer

class DoubleComparator(
    var epsilon: Double
) {

    fun equals(d1: Double, d2: Double): Boolean {
        return equals(d1, d2, epsilon)
    }

    fun equalsZero(d: Double): Boolean {
        return equalsZero(d, epsilon)
    }

    fun compare(d1: Double, d2: Double): Int {
        return compare(d1, d2, epsilon)
    }

    companion object {
        fun equals(d1: Double, d2: Double, epsilon: Double): Boolean {
            return if (d1 == d2) true else Math.abs(d1 - d2) < epsilon
        }

        fun equalsZero(d: Double, epsilon: Double): Boolean {
            return compare(d, 0.0, epsilon) == 0
        }

        fun compare(d1: Double, d2: Double, epsilon: Double): Int {
            return when {
                equals(d1, d2, epsilon) -> 0
                d1 < d2 -> -1
                else -> 1
            }
        }
    }
}
