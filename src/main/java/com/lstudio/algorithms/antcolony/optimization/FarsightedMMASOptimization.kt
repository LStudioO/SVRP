package com.lstudio.algorithms.antcolony.optimization

import com.lstudio.algorithms.antcolony.TaskSettings
import com.lstudio.algorithms.antcolony.route.AbstractRoute
import com.lstudio.algorithms.antcolony.route.FarsightedRoute

class FarsightedMMASOptimization(
    distanceMatrix: Array<DoubleArray>,
    startDepots: IntArray,
    endDepots: HashMap<Int, Int>
) : DefaultMMASOptimization(distanceMatrix, startDepots, endDepots) {
    override fun createRoute(): AbstractRoute {
        return FarsightedRoute(
            graph.size,
            graph,
            numberOfAnts,
            numberOfCities,
            trails,
            TaskSettings.antCapacity,
            cities,
            numberOfStartDepots,
            startDepots
        )
    }
}