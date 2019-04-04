package com.lstudio

import com.lstudio.algorithms.antcolony.AntColonyOptimization
import com.lstudio.algorithms.bruteforce.BruteforceSolver
import com.lstudio.algorithms.ls.TabuSearchSolver
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
        println("4 - BRUTEFORCE")

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

        val decision = 5
        when (decision) {
            1 -> {
                val antColony = AntColonyOptimization(weight, startDepots, endDepots)
                antColony.startAntOptimization()
            }
            2 -> {
                val tabuHorizon = 100
                val localSearch = TabuSearchSolver(tabuHorizon, weight, startDepots, endDepots, 1000)
                localSearch.solve()
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

                /**
                 *Distance between first and last and consecutive elements of a list.
                 **/
                fun distance(x: Int, y: Int): Double {
                    return weight[x][y]
                }

                /**
                 * Distance between first and last and consecutive elements of a list
                 */
                fun routeLength(route: List<Int>): Double {
                    var sum = 0.0
                    for (i in 1 until route.size) sum += distance(route[i], route[i - 1])
                    sum += distance(route[0], route[route.size - 1])
                    return sum
                }

                println("Enter solution")
                //val line = readLine()
                //val line1 = "[0, 2, 6, 10, 11, 12, 13, 9, 7, 5, 15]-[1, 3, 14]-[4, 8, 14]"
                val line1 = "[0, 2, 5, 7, 9, 13, 12, 15]-[1, 3, 11, 14]-[4, 6, 8, 10, 14]"
                val group = line1.split("-")
                val newGroup = group.map { it.drop(1).dropLast(1) }.map { it.split(", ") }
                var sum = 0.0
                newGroup.forEach{
                    val result = it.map { it.toInt() }
                    sum += routeLength(result)
                }
                println("Sum: $sum")
            }
            else -> println("Unknown option")
        }
        scanner.close()
    }


}