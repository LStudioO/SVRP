package com.lstudio

import com.lstudio.algorithms.antcolony.optimization.AntColonyOptimization
import com.lstudio.algorithms.antcolony.optimization.DefaultMMASOptimization
import com.lstudio.algorithms.antcolony.optimization.FarsightedMMASOptimization
import com.lstudio.algorithms.bruteforce.BruteforceSolver
import com.lstudio.algorithms.ls.TabuSearchSolver
import com.lstudio.data.TaskGenerator
import com.lstudio.data.TaskReader
import java.util.*

object Main {

    @Throws(InstantiationException::class, IllegalAccessException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val scanner = Scanner(System.`in`)
        println("Run algorithm:")
        println("1 - AS")
        println("2 - LS")
        println("3 - MMAS")
        println("4 - MMAS FARSIGHTED")
        println("5 - BRUTEFORCE")
        println("6 - TASK GENERATOR")


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

        val input = Scanner(System.`in`)
        val decision = input.nextInt()
        when (decision) {
            1 -> {
                val antColony =
                    AntColonyOptimization(weight, startDepots, endDepots)
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
                val antColony = DefaultMMASOptimization(weight, startDepots, endDepots)
                antColony.startAntOptimization()
            }
            4 -> {
                val antColony = FarsightedMMASOptimization(
                    weight,
                    startDepots,
                    endDepots
                )
                antColony.startAntOptimization()
            }
            5 -> {
                val bruteforceSolver = BruteforceSolver(startDepots, endDepots, weight)
                bruteforceSolver.solve()
            }
            6 -> {
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