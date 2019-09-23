package com.lstudio

fun IntArray.copyArray(): IntArray {
    val newArray = IntArray(this.size)
    System.arraycopy(this, 0, newArray, 0, this.size)
    return newArray
}