package com.lstudio

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
                taskReader.readTask("D:\\Study\\Diploma\\Project\\tasks\\test_task\\test_task_data.txt")
                System.out.println(
                    "Task name: ${taskReader.name} \n" +
                            "City count: ${taskReader.cityCount}\n" +
                            "Vehicle сount: ${taskReader.vehicleCount}"
                )
                val antColony = AntColonyOptimization(21)
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