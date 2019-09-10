package com.lstudio.utils

import java.util.*

fun <E> ArrayList<E>.random(): E {
    val random = Random()
    val listSize = this.size
    val randomIndex = random.nextInt(listSize)
    return this[randomIndex]
}