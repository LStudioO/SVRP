package com.lstudio

import com.lstudio.algorithms.antcolony.AntColonyOptimization
import com.lstudio.algorithms.bruteforce.BruteforceSolver
import com.lstudio.algorithms.ls.TabuSearchSolver
import com.lstudio.data.TaskGenerator
import com.lstudio.data.TaskReader
import java.util.*
import java.util.concurrent.TimeUnit

object Main {

    @Throws(InstantiationException::class, IllegalAccessException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val scanner = Scanner(System.`in`)
        println("Run algorithm:")
        println("1 - MMAS")
        println("2 - LS")
        println("3 - MMAS ISLAND")
        println("4 - BRUTEFORCE")
        println("5 - TASK GENERATOR")


        val taskReader = TaskReader()
        taskReader.readTask("tasks\\test_task\\generated\\test_task_30_4_7.txt")
        System.out.println(
            "Task name: ${taskReader.name} \n" +
                    "City count: ${taskReader.cityCount}\n" +
                    "Vehicle сount: ${taskReader.vehicleCount}"
        )

        val weight = taskReader.weigths ?: return
        val endDepots = taskReader.endDepots ?: return
        val startDepots = taskReader.startDepots ?: return

        val input = Scanner(System.`in`)
        val decision = input.nextInt()
        when (decision) {
            1 -> {
                val antColony = AntColonyOptimization(weight, startDepots, endDepots)
                antColony.startAntOptimization()
            }
            2 -> {
                val tabuHorizon = 100
                val startTimeMillis = System.currentTimeMillis()
                val localSearch = TabuSearchSolver(tabuHorizon, weight, startDepots, endDepots, 1000)
                localSearch.solve()
                println("Solution time: ${(System.currentTimeMillis() - startTimeMillis)} milliseconds")
                localSearch.print()
            }
            3 -> {
                println("Not implemented")
            }
            4 -> {
                val bruteforceSolver = BruteforceSolver(startDepots, endDepots, weight)
                bruteforceSolver.solve()
            }
            5 -> {
                val taskGenerator = TaskGenerator()
                val cin = Scanner(System.`in`)
                while (true) {
                    System.out.println("Enter customers count")
                    val answer = cin.nextInt()
                    if (answer == -1)
                        break
                    taskGenerator.generate(answer, 10, 12)
                }
            }
            else -> println("Unknown option")
        }
        scanner.close()
    }
}