package com.lstudio

import com.lstudio.algorithms.antcolony.TaskSettings
import com.lstudio.algorithms.antcolony.island.IslandOptimization
import com.lstudio.algorithms.antcolony.island.topology.HypercubeTopology
import com.lstudio.algorithms.antcolony.island.topology.RingTopology
import com.lstudio.algorithms.antcolony.island.topology.TorusTopology
import com.lstudio.algorithms.antcolony.optimization.DefaultMMASOptimization
import com.lstudio.algorithms.antcolony.optimization.FarsightedMMASOptimization
import com.lstudio.algorithms.bruteforce.BruteforceSolver
import com.lstudio.algorithms.ls.TabuSearchSolver
import com.lstudio.algorithms.rvm.RVM
import com.lstudio.data.TaskGenerator
import com.lstudio.data.TaskReader
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.*

object Main {

    @Throws(InstantiationException::class, IllegalAccessException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val scanner = Scanner(System.`in`)
        println("Run algorithm:")
        println("1 - FIND PARAMETERS")
        println("2 - LS")
        println("3 - MMAS")
        println("4 - MMAS FARSIGHTED")
        println("5 - BRUTEFORCE")
        println("6 - ISLAND MMAS")
        println("7 - TASK GENERATOR")
        println("8 - RUN EXPERIMENTS")

        val taskReader = TaskReader()
        taskReader.readTask("tasks\\test_task\\generated\\test_task_10_2_2.txt")
        println(
            "Task name: ${taskReader.name} \n" +
                    "City count: ${taskReader.cityCount}\n" +
                    "Vehicle сount: ${taskReader.vehicleCount}"
        )

        val weight = taskReader.weigths ?: return
        val endDepots = taskReader.endDepots ?: return
        val startDepots = taskReader.startDepots ?: return

        val input = Scanner(System.`in`)
        when (input.nextInt()) {
            1 -> {
                val rvm = RVM { DefaultMMASOptimization(weight, startDepots, endDepots) }
                val result = rvm.calculate()
                println(result.first.joinToString { "$it | " })
                println(result.second)
            }
            2 -> {
                val tabuHorizon = 100
                val localSearch = TabuSearchSolver(tabuHorizon, weight, startDepots, endDepots, 1000)
                localSearch.solve()
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
                runBlocking {
                    val islandOptimization = IslandOptimization(
                        weight,
                        startDepots,
                        endDepots
                    )
                    islandOptimization.runExperiment {
                        println(it)
                    }
                }
            }
            7 -> {
                val taskGenerator = TaskGenerator()
                val cin = Scanner(System.`in`)
                while (true) {
                    println("Enter customers count")
                    val answer = cin.nextInt()
                    if (answer == -1)
                        break
                    taskGenerator.generate(answer, 5, 5)
                }
            }
            8 -> {
                runExperiments()
            }
            else -> println("Unknown option")
        }
        scanner.close()
    }

    private fun runExperiments() {
        // best params
        with(TaskSettings) {
            alpha =  1.57
            beta =0.55
            rho = 0.05
            stagnationIterationCount = 1000
            randomFactor = 0.012
            routeIterations = 7
        }
        
        val tasks = arrayListOf(
            "tasks\\test_task\\generated\\test_task_16_3_4.txt",
            "tasks\\test_task\\generated\\test_task_30_4_7.txt",
            "tasks\\test_task\\generated\\test_task_36_5_8.txt",
            "tasks\\test_task\\generated\\test_task_43_5_8.txt",
            "tasks\\test_task\\generated\\test_task_57_10_12.txt",
            "tasks\\test_task\\generated\\test_task_65_3_5.txt",
            "tasks\\test_task\\generated\\test_task_70_6_8.txt"
        )

        tasks.forEach { taskPath ->
            val taskReader = TaskReader()
            taskReader.readTask(taskPath)
            println(
                "Task name: ${taskReader.name} \n" +
                        "City count: ${taskReader.cityCount}\n" +
                        "Vehicle сount: ${taskReader.vehicleCount}"
            )

            val weight = taskReader.weigths ?: return
            val endDepots = taskReader.endDepots ?: return
            val startDepots = taskReader.startDepots ?: return

            val fileWriter =
                File(taskPath.replace(".txt", "_experiments.txt"))
            fileWriter.delete()

            fileWriter.appendText("Local Search \n")

            for (i in 0 until 10) {
                val tabuHorizon = 100
                val startTimeMillis = System.nanoTime()
                val localSearch = TabuSearchSolver(tabuHorizon, weight, startDepots, endDepots, 1000)
                localSearch.solve()
                val time = "Solution time: ${(System.nanoTime() - startTimeMillis)} nanoseconds\n"
                println(time)
                fileWriter.appendText(time)
                localSearch.print()
                fileWriter.appendText("Best Value:  ${localSearch.cost}\n\n")
            }

            fileWriter.appendText("MMAS \n")

            for (i in 0 until 10) {
                val startTimeMillis = System.nanoTime()
                val antColony = DefaultMMASOptimization(
                    weight,
                    startDepots,
                    endDepots
                )
                antColony.startAntOptimization()
                val time = "Solution time: ${(System.nanoTime() - startTimeMillis)} nanoseconds\n"
                println(time)
                fileWriter.appendText(time)
                fileWriter.appendText("Best Value:  ${antColony.getCurrentLength()}\n\n")
            }

            fileWriter.appendText("FARSIGHTED MMAS \n")

            for (i in 0 until 10) {
                val startTimeMillis = System.nanoTime()
                val antColony = FarsightedMMASOptimization(
                    weight,
                    startDepots,
                    endDepots
                )
                antColony.startAntOptimization()
                val time = "Solution time: ${(System.nanoTime() - startTimeMillis)} nanoseconds\n"
                println(time)
                fileWriter.appendText(time)
                fileWriter.appendText("Best Value:  ${antColony.getCurrentLength()}\n\n")
            }

            fileWriter.appendText("ISLAND FARSIGHTED MMAS COMPLETE \n")

            for (i in 0 until 10) {
                val startTimeMillis = System.nanoTime()
                val islandOptimization = IslandOptimization(
                    weight,
                    startDepots,
                    endDepots,
                    null
                )
                runBlocking {
                    islandOptimization.runExperiment()
                }
                val time = "Solution time: ${(System.nanoTime() - startTimeMillis)} nanoseconds\n"
                println(time)
                fileWriter.appendText(time)
                fileWriter.appendText("Best Value:  ${islandOptimization.bestValue}\n\n")
            }


            fileWriter.appendText("ISLAND FARSIGHTED MMAS HYPERCUBE\n")

            for (i in 0 until 10) {
                val startTimeMillis = System.nanoTime()
                val islandOptimization = IslandOptimization(
                    weight,
                    startDepots,
                    endDepots,
                    HypercubeTopology(IslandOptimization.islandsCount)
                )
                runBlocking {
                    islandOptimization.runExperiment()
                }
                val time = "Solution time: ${(System.nanoTime() - startTimeMillis)} nanoseconds\n"
                println(time)
                fileWriter.appendText(time)
                fileWriter.appendText("Best Value:  ${islandOptimization.bestValue}\n\n")
            }

            fileWriter.appendText("ISLAND FARSIGHTED MMAS RING\n")

            for (i in 0 until 10) {
                val startTimeMillis = System.nanoTime()
                val islandOptimization = IslandOptimization(
                    weight,
                    startDepots,
                    endDepots,
                    RingTopology(IslandOptimization.islandsCount)
                )
                runBlocking {
                    islandOptimization.runExperiment()
                }
                val time = "Solution time: ${(System.nanoTime() - startTimeMillis)} nanoseconds\n"
                println(time)
                fileWriter.appendText(time)
                fileWriter.appendText("Best Value:  ${islandOptimization.bestValue}\n\n")
            }

            fileWriter.appendText("ISLAND FARSIGHTED MMAS TORUS\n")

            for (i in 0 until 10) {
                val startTimeMillis = System.nanoTime()
                val islandOptimization = IslandOptimization(
                    weight,
                    startDepots,
                    endDepots,
                    TorusTopology(IslandOptimization.islandsCount)
                )
                runBlocking {
                    islandOptimization.runExperiment()
                }
                val time = "Solution time: ${(System.nanoTime() - startTimeMillis)} nanoseconds\n"
                println(time)
                fileWriter.appendText(time)
                fileWriter.appendText("Best Value:  ${islandOptimization.bestValue}\n\n")
            }
        }
    }
}