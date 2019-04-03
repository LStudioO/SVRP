package com.lstudio.algorithms.ls

internal class Node(
    var nodeId: Int, var demand: Int, var isStartDepot: Boolean,
    var isEndDepot: Boolean, var isRouted: Boolean = false
)