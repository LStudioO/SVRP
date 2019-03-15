package com.lstudio.ui

import com.lstudio.algorithms.antcolony.City
import com.lstudio.algorithms.antcolony.CityType
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JFrame
import javax.swing.JPanel
import java.awt.GraphicsEnvironment

class Visualizer(private val cities: Array<City>) {
    private val frame: JFrame = JFrame("Points")
    private var width: Int
    private var height: Int

    private val maxX = cities.map { it.point?.x }.maxBy { it ?: 0.0 } ?: 0.0
    private val maxY = cities.map { it.point?.y }.maxBy { it ?: 0.0 } ?: 0.0

    private val multiplierX: Double
    private val multiplierY: Double

    private val pointDiameter: Int

    init {
        val gd = GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice
        width = gd.displayMode.width / 2
        height = gd.displayMode.height / 2

        multiplierX = width / maxX / 1.2
        multiplierY = height / maxY / 1.2

        pointDiameter = (width / 20)

        frame.setSize(width, height)

        frame.add(object : JPanel() {
            override fun paintComponent(g: Graphics?) {
                super.paintComponent(g)
                val g2d = g as Graphics2D
                drawPoints(g2d)
                drawConnections(g2d)
            }
        }, BorderLayout.CENTER)
    }

    private fun drawPoints(graphics: Graphics2D) {
        for (city in cities) {
            val cityPoint = city.point ?: continue
            val xCoordinate = (cityPoint.x * multiplierX).toInt()
            val yCoordinate = (cityPoint.y * multiplierY).toInt()
            graphics.color = when (city.type) {
                CityType.START_DEPOT -> Color.GREEN
                CityType.CUSTOMER -> Color.ORANGE
                CityType.END_DEPOT -> Color.BLUE
            }
            graphics.fillOval(xCoordinate, yCoordinate, pointDiameter, pointDiameter)
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

    private fun drawConnections(graphics: Graphics2D) {
        graphics.color = Color.RED

        val x1 = cities[0].point?.x!!.toInt() * multiplierX.toInt() + pointDiameter / 2
        val x2 = cities[2].point?.x!!.toInt() * multiplierX.toInt() + pointDiameter / 2

        val y1 = cities[0].point?.y!!.toInt() * multiplierY.toInt() + pointDiameter / 2
        val y2 = cities[2].point?.y!!.toInt() * multiplierY.toInt() + pointDiameter / 2

        drawArrowLine(graphics, x1, y1, x2, y2, 15, 15)
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
        val D = Math.sqrt((dx * dx + dy * dy).toDouble())
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

    fun show() {
        frame.isVisible = true
    }
}