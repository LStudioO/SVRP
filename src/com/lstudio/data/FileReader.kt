package com.lstudio.data

import java.io.File

class TaskReader {

    var name: String? = null
    var vehicleCount: Int = 0
    var cityCount: Int = 0
    var startDepotCount: Int = 0
    var endDepotCount: Int = 0
    var startDepots: IntArray? = null
    var endDepots: HashMap<Int, Int>? = null
    var weigths: Array<DoubleArray>? = null

    fun readTask(filePath: String) {
        val file = File(filePath)
        if (!file.exists()) {
            throw Exception("File doesn't exist")
        }
        val text = file.readLines()
        name = text[0].substringAfter("NAME: ")
        vehicleCount = text[1].substringAfter("VEHICLES: ").toInt()
        cityCount = text[2].substringAfter("CITY_COUNT: ").toInt()
        startDepotCount = text[3].substringAfter("START_DEPOT_COUNT: ").toInt()
        endDepotCount = text[4].substringAfter("END_DEPOT_COUNT: ").toInt()
        weigths = Array(cityCount) { DoubleArray(cityCount) }
        var offset = 6
        for (i in offset until cityCount + offset) {
            val raw = text[i]
            val splittedRaw = raw.split(' ')
            splittedRaw.forEachIndexed { index, value ->
                weigths!![i - offset][index] = value.toDouble()
            }
        }
        startDepots = IntArray(startDepotCount)
        offset += cityCount + 1
        for (i in offset until startDepotCount + offset) {
            val value = text[i]
            startDepots!![i - offset] = value.toInt()
        }
        endDepots = HashMap(endDepotCount)
        offset += startDepotCount + 1
        for (i in offset until endDepotCount + offset) {
            val index = text[i].split(' ')[0].toInt()
            val capacity = text[i].split(' ')[1].toInt()
            endDepots!![index] = capacity
        }
    }
}