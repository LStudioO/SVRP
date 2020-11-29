package com.lstudio.data

import com.lstudio.pointrestorer.primitives.Point
import java.io.File
import java.util.*

class TaskGenerator {
    fun generate(cityCount: Int, startDepots: Int, endDepots: Int) {
        val weights = Array(cityCount) { DoubleArray(cityCount) }

        val points = ArrayList<Point>()

        for (k in 0 until cityCount) {
            val x = Random().nextDouble() * 1000
            val y = Random().nextDouble() * 1000
            points.add(Point(x, y, k))
        }

        for (i in 0 until cityCount)
            for (j in 0 until cityCount) {
                weights[i][j] = points[i].distance(points[j])
            }

        val content = StringBuilder()

        content.append(
            "NAME: test_task_${cityCount}_${startDepots}_$endDepots\n" +
                    "VEHICLES: $startDepots\n" +
                    "CITY_COUNT: $cityCount\n" +
                    "START_DEPOT_COUNT: $startDepots\n" +
                    "END_DEPOT_COUNT: $endDepots\n" +
                    "EDGE_WEIGHT_SECTION\n"
        )

        for (i in 0 until cityCount) {
            var line = ""
            for (j in 0 until cityCount - 1) {
                line += "${weights[i][j]} "
            }
            line += "${weights[i][cityCount - 1]}"
            content.append("$line\n")
        }
        content.append("START_DEPOT_SECTION\n")
        for (k in 0 until startDepots)
            content.append("$k\n")

        content.append("END_DEPOT_SECTION\n")

        for (k in startDepots until startDepots + endDepots)
            content.append("$k 1\n")

        File("tasks\\test_task\\generated\\test_task_${cityCount}_${startDepots}_$endDepots.txt")
            .writeText(content.toString())

        System.out.println("Task(size $cityCount) was generated")
    }
}