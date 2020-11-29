package com.lstudio.algorithms.ls.model

internal class Node(
    val nodeId: Int,
    val demand: Int,
    val isStartDepot: Boolean,
    val isEndDepot: Boolean,
    var isRouted: Boolean = false
) {
    override fun toString(): String {
        return "$nodeId"
    }
}