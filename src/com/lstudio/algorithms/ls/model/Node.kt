package com.lstudio.algorithms.ls.model

internal class Node(
    var nodeId: Int, var demand: Int, var isStartDepot: Boolean,
    var isEndDepot: Boolean, var isRouted: Boolean = false

) {
    override fun toString(): String {
        return "$nodeId"
    }
}