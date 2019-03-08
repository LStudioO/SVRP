package com.lstudio

import com.lstudio.antcolony.AntColonyOptimization
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
        val decision = 1
        when (decision) {
            1 -> {
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

                val antColony = AntColonyOptimization(weight, startDepots, endDepots)
                antColony.startAntOptimization()
            }
            2 -> {
                println("Not implemented")
            }
            3 -> {
                println("Not implemented")
            }
            else -> println("Unknown option")
        }
        scanner.close()
    }
}