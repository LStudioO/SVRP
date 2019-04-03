package com.lstudio.algorithms.bruteforce

import java.util.ArrayList

internal object ListPartitioner {
    /**
     * Returns the set of all partitions for a given set
     * e.g for [1,2], it returns [[1],[2]] and [[1,2]]
     */
    @Throws(Exception::class)
    fun getAllPartitions(inputList: List<Int>): List<List<List<Int>>> {
        val array = inputList.stream().mapToInt { i -> i }.toArray()
        val partitionArrays = getAllPartitions(array)
        val partitionsList = ArrayList<List<List<Int>>>()
        for (partition in partitionArrays) {
            val partitionSets = ArrayList<List<Int>>()
            for (set in partition) {
                val singleSet = set.toMutableList()
                partitionSets.add(singleSet)
            }
            partitionsList.add(partitionSets)
        }
        return partitionsList
    }

    /** Copied from following link:
     * https://stackoverflow.com/questions/36962150/partitions-of-a-set-storing-results-in-a-series-of-nested-lists
     */
    @Throws(Exception::class)
    private fun getAllPartitions(array: IntArray): Array<Array<IntArray>> {
        var res = arrayOf<Array<IntArray>>()
        var n = 1
        for (i in array.indices) {
            n *= 2
        }
        var i = 1
        while (i < n) {
            val contains = BooleanArray(array.size)
            var length = 0
            var k = i
            for (j in array.indices) {
                contains[j] = k % 2 == 1
                length += k % 2
                k /= 2
            }
            val firstPart : IntArray = IntArray(length)
            val secondPart : IntArray = IntArray(array.size - length)
            var p = 0
            var q = 0
            for (j in array.indices) {
                if (contains[j]) {
                    firstPart.set(p++, array[j])
                } else {
                    secondPart.set(q++, array[j])
                }
            }
            val partitions: Array<Array<IntArray>>
            if (length == array.size) {
                partitions = arrayOf(arrayOf(firstPart))
            } else {
                partitions = getAllPartitions(secondPart)
                for (j in partitions.indices) {
                    val partition = Array(partitions[j].size + 1, { IntArray(0) })
                    partition[0] = firstPart
                    System.arraycopy(partitions[j], 0, partition, 1, partitions[j].size)
                    partitions[j] = partition
                }
            }
            val newRes = Array(res.size + partitions.size, { arrayOf(IntArray(0)) })
            System.arraycopy(res, 0, newRes, 0, res.size)
            System.arraycopy(partitions, 0, newRes, res.size, partitions.size)
            res = newRes
            i += 2
        }
        return res
    }
}
