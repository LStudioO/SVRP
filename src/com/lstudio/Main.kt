package com.lstudio

import com.lstudio.algorithms.antcolony.AntColonyOptimization
import com.lstudio.algorithms.ls.LocalSearch
import com.lstudio.data.TaskReader
import java.util.*

object Main {

    @Throws(InstantiationException::class, IllegalAccessException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val scanner = Scanner(System.`in`)
        println("Run algorithm:")
        println("1 - MMAS")
        println("2 - LS")
        println("3 - MMAS ISLAND")

        val taskReader = TaskReader()
        taskReader.readTask("tasks\\test_task\\test_task_data.txt")
        System.out.println(
            "Task name: ${taskReader.name} \n" +
                    "City count: ${taskReader.cityCount}\n" +
                    "Vehicle сount: ${taskReader.vehicleCount}"
        )

        val weight = taskReader.weigths ?: return
        val endDepots = taskReader.endDepots ?: return
        val startDepots = taskReader.startDepots ?: return

        val decision = 1
        when (decision) {
            1 -> {
                val antColony = AntColonyOptimization(weight, startDepots, endDepots)
                antColony.startAntOptimization()
            }
            2 -> {
                val localSearch = LocalSearch(weight, startDepots, endDepots)
                localSearch.startOptimization()
            }
            3 -> {
                println("Not implemented")
            }
            else -> println("Unknown option")
        }
        scanner.close()
    }
}