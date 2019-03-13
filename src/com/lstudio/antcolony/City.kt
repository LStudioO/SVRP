package com.lstudio.antcolony

import com.lstudio.pointrestorer.primitives.Point

enum class CityType {
    START_DEPOT, CUSTOMER, END_DEPOT
}

class City(val index: Int, private val isStartDepot: Boolean, val capacity: Int) {
    val type: CityType
        get() {
            if (capacity > 0)
                return CityType.END_DEPOT
            if (isStartDepot)
                return CityType.START_DEPOT
            return CityType.CUSTOMER
        }

    var availableCapacity = capacity
    var point: Point? = null

    fun clear() {
        availableCapacity = capacity
    }
}