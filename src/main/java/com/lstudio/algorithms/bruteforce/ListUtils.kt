package com.lstudio.algorithms.bruteforce

import java.util.ArrayList

fun permutations(original: MutableList<Int>): ArrayList<ArrayList<Int>> {
    if (original.size == 0) {
        val result = ArrayList<ArrayList<Int>>()
        result.add(ArrayList())
        return result
    }
    val firstElement = original.removeAt(0)
    val returnValue = ArrayList<ArrayList<Int>>()
    val permutations = permutations(original)
    for (smallerPermuted in permutations) {
        for (index in 0..smallerPermuted.size) {
            val temp = ArrayList(smallerPermuted)
            temp.add(index, firstElement)
            returnValue.add(temp)
        }
    }
    return returnValue
}