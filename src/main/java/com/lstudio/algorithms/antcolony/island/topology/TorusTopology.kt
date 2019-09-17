package com.lstudio.algorithms.antcolony.island.topology

import java.lang.Exception
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class TorusTopology(size: Int) : AbstractTopology(size) {
    // torus dimensions
    private var x: Int
    private var y: Int

    init {
        if (size % 2 != 0)
            throw Exception("Unsupported size: $size for hypercube")

        val pair = calculateTorusSize(size)
        x = max(pair.first, pair.second)
        y = min(pair.first, pair.second)
    }

    private fun calculateTorusSize(n: Int): Pair<Int, Int> {
        val divisorsList = ArrayList<Pair<Int, Int>>()
        var i = 1
        while (i <= sqrt(n.toDouble())) {
            if (n % i == 0) {
                divisorsList.add(Pair(i, n / i))
            }
            i++
        }
        return divisorsList.minBy { abs(it.first - it.second) } ?: Pair(0, 0)
    }

    override fun calculateNeighborhood(index: Int): List<Int> {
        val xNext =
            if ((index + 1) % x == 0)
                index - x + 1
            else
                index + 1
        val xPrev =
            if (index % x == 0)
                index + x - 1
            else
                index - 1
        val yNext =
            if (index / x == y - 1)
                index - (y - 1) * x
            else
                index + x
        val yPrev =
            if (index / x == 0)
                index + (y - 1) * x
            else
                index - x

        return arrayListOf(xNext, xPrev, yNext, yPrev)
    }
}