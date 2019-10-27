package com.lstudio.ui

import com.lstudio.algorithms.antcolony.City
import com.lstudio.algorithms.antcolony.CityType
import com.lstudio.algorithms.antcolony.Solution
import java.awt.*
import javax.swing.JPanel
import kotlin.math.absoluteValue
import kotlin.math.sqrt

class Visualizer(private val frame: JPanel, private val cities: Array<City>) {
    private var width: Int
    private var height: Int

    private val maxX: Double
    private val maxY: Double

    private val multiplierX: Double
    private val multiplierY: Double

    private val pointDiameter: Int

    private var solution: Solution? = null

    private val marginX = 50
    private val marginY = 50

    private val colors = arrayOf(
        Color.RED, Color.BLUE, Color.GREEN, Color.PINK, Color.ORANGE,
        Color.MAGENTA, Color.GRAY
    )

    init {
        // normalize point (remove negative coordinates)
        val minX = cities.mapNotNull { it.point?.x }.filter { it < 0.0 }.minBy { it }?.absoluteValue ?: 0.0
        val minY = cities.mapNotNull { it.point?.y }.filter { it < 0.0 }.minBy { it }?.absoluteValue ?: 0.0

        cities.forEach {
            it.point?.let { point ->
                point.x += minX
                point.y += minY
            }
        }

        // add x-y margins

        cities.forEach {
            it.point?.let { point ->
                point.x += marginX
                point.y += marginY
            }
        }

        maxX = cities.map { it.point?.x }.maxBy { it ?: 0.0 } ?: 0.0
        maxY = cities.map { it.point?.y }.maxBy { it ?: 0.0 } ?: 0.0

        width = frame.width
        height = frame.height

        multiplierX = width / maxX / 1.3
        multiplierY = height / maxY / 1.3

        pointDiameter = (width / 30)
    }

    private fun drawPoints(graphics: Graphics2D) {
        for (city in cities) {
            val cityPoint = city.point ?: continue
            val xCoordinate = (cityPoint.x * multiplierX).toInt()
            val yCoordinate = (cityPoint.y * multiplierY).toInt()
            graphics.color = when (city.type) {
                CityType.START_DEPOT -> Color.GREEN
                CityType.CUSTOMER -> Color.ORANGE
                CityType.END_DEPOT -> Color.RED
            }
            graphics.fillRect(xCoordinate, yCoordinate, pointDiameter, pointDiameter)
            graphics.color = Color.BLACK
            val fm = graphics.fontMetrics
            val text = (city.index + 1).toString()
            val textWidth = fm.getStringBounds(text, graphics).width
            graphics.drawString(
                text,
                (xCoordinate + pointDiameter / 2 - textWidth / 2).toFloat(),
                (yCoordinate + pointDiameter / 2 + fm.maxAscent / 2).toFloat()
            )
        }
    }

    private fun drawConnections(graphics: Graphics2D, solution: Solution) {
        val colorList = colors.toList().shuffled().take(solution.ants.size)

        solution.ants.forEachIndexed { index, ant ->
            graphics.color = colorList[index]

            val routeList = ant.getRoute()
            for (i in 0 until routeList.size - 1) {
                val city1 = cities[routeList[i]]
                val city2 = cities[routeList[i + 1]]

                val x1 = city1.point?.x!! * multiplierX + pointDiameter / 2
                val x2 = city2.point?.x!! * multiplierX + pointDiameter / 2

                val y1 = city1.point?.y!! * multiplierY + pointDiameter / 2
                val y2 = city2.point?.y!! * multiplierY + pointDiameter / 2

                drawArrowLine(graphics, x1.toInt(), y1.toInt(), x2.toInt(), y2.toInt(), 5, 5)
            }
        }
    }

    /**
     * Draw an arrow line between two points.
     * @param g the graphics component.
     * @param x1 x-position of first point.
     * @param y1 y-position of first point.
     * @param x2 x-position of second point.
     * @param y2 y-position of second point.
     * @param d  the width of the arrow.
     * @param h  the height of the arrow.
     */
    private fun drawArrowLine(g: Graphics, x1: Int, y1: Int, x2: Int, y2: Int, d: Int, h: Int) {
        val dx = x2 - x1
        val dy = y2 - y1
        val D = sqrt((dx * dx + dy * dy).toDouble())
        var xm = D - d
        var xn = xm
        var ym = h.toDouble()
        var yn = (-h).toDouble()
        var x: Double
        val sin = dy / D
        val cos = dx / D

        x = xm * cos - ym * sin + x1
        ym = xm * sin + ym * cos + y1.toDouble()
        xm = x

        x = xn * cos - yn * sin + x1
        yn = xn * sin + yn * cos + y1.toDouble()
        xn = x

        val xPoints = intArrayOf(x2, xm.toInt(), xn.toInt())
        val yPoints = intArrayOf(y2, ym.toInt(), yn.toInt())

        g.drawLine(x1, y1, x2, y2)
        g.fillPolygon(xPoints, yPoints, 3)
    }

    fun showSolution(solution: Solution) {
        this.solution = solution
       // frame.revalidate()
        frame.repaint()
    }

    fun show() {
        frame.isVisible = true
    }

    fun draw(g: Graphics) {
        val g2d = g as Graphics2D
        drawPoints(g2d)
        solution?.let {
            drawConnections(g2d, it)
        }
    }
}