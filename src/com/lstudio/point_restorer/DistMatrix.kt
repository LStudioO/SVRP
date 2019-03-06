package com.lstudio.point_restorer

import com.lstudio.point_restorer.primitives.Circle
import com.lstudio.point_restorer.primitives.Point
import java.util.Arrays
import java.util.LinkedList

class DistMatrix(private val pointsNumber: Int) {

    private val distances: DoubleArray = DoubleArray(pointsNumber * (pointsNumber - 1) / 2)

    constructor(matrix: Array<DoubleArray>) : this(matrix.size) {
        for (i in 0 until pointsNumber) {
            for (j in i + 1 until pointsNumber) {
                setDistance(i, j, matrix[i][j])
            }
        }
    }

    constructor(points: Array<Point>) : this(points.size) {
        for (i in 0 until pointsNumber) {
            for (j in i + 1 until pointsNumber) {
                setDistance(i, j, points[i].distance(points[j]))
            }
        }
    }

    fun getDistance(p1: Int, p2: Int): Double {
        checkPoints(p1, p2)
        return if (p1 == p2) {
            0.0
        } else distances[getIndex(p1, p2)]

    }

    fun setDistance(p1: Int, p2: Int, distance: Double): Double {
        checkPoints(p1, p2)
        if (p1 == p2) {
            return 0.0
        }

        val index = getIndex(p1, p2)
        val prevDistance = distances[index]
        distances[index] = distance

        return prevDistance
    }

    fun restorePoints(): Array<Point>? {
        var points: Array<Point>? = null

        if (pointsNumber == 0) {
            points = null
        } else {
            val pointsList = restorePointsList()
            if (pointsList != null) {
                points = pointsList.toTypedArray()
            }
        }

        return points
    }

    private fun restorePointsList(): List<Point>? {
        val points = LinkedList<Point>()
        val firstPoint = Point(0.0, 0.0, 0)
        val secondPoint = getSecondPoint(firstPoint)

        for (i in 0 until pointsNumber) {
            val firstCircle = Circle(firstPoint, getDistance(firstPoint.id, i))
            val secondCircle = Circle(secondPoint, getDistance(secondPoint.id, i))
            val intPoints = Circle.getIntersectionPoints(firstCircle, secondCircle, i) ?: return null
            val point = getNextPoint(points, intPoints)
            if (point != null) {
                points.add(point)
            } else {
                return null
            }
        }

        return points
    }

    private fun getSecondPoint(firstPoint: Point): Point {
        val secondPoint = Point(firstPoint.x, firstPoint.y, firstPoint.id)
        for (i in 0 until pointsNumber) {
            val dist = getDistance(firstPoint.id, i)
            if (!ComparatorContext.DOUBLE_COMPARATOR.equals(firstPoint.y, dist)) {
                secondPoint.y = dist
                secondPoint.id = i
                break
            }
        }

        return secondPoint
    }

    private fun getNextPoint(foundPoints: List<Point>, intPoints: Array<Point>): Point? {
        var point: Point? = null
        for (intPoint in intPoints) {
            if (checkPoint(foundPoints, intPoint)) {
                point = intPoint
                break
            }
        }

        return point
    }


    private fun checkPoint(foundPoints: List<Point>, point: Point): Boolean {
        for (p in foundPoints) {
            val realDist = getDistance(p.id, point.id)
            val calcDist = Point.distance(p, point)
            if (!ComparatorContext.DOUBLE_COMPARATOR.equals(realDist, calcDist)) {
                return false
            }
        }

        return true
    }

    private fun getIndex(p1: Int, p2: Int): Int {
        if (p1 > p2) {
            return getIndex(p2, p1)
        }

        val index = (pointsNumber - 1) * p1 + p2 - 1
        val offset = (p1 + 1) * p1 / 2

        return index - offset
    }

    private fun checkPoints(p1: Int, p2: Int) {
        if (p1 < 0 || p1 >= pointsNumber) {
            val message = String.format("point p1=%d does not exists", p1)
            throw IllegalArgumentException(message)
        }

        if (p2 < 0 || p2 >= pointsNumber) {
            val message = String.format("point p2=%d does not exists", p2)
            throw IllegalArgumentException(message)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val that = other as DistMatrix?

        if (pointsNumber != that!!.pointsNumber) return false
        return equalsDistances(that.distances)

    }

    override fun hashCode(): Int {
        var result = pointsNumber
        result = 31 * result + Arrays.hashCode(distances)
        return result
    }

    private fun equalsDistances(distances: DoubleArray): Boolean {
        if (this.distances.size != distances.size) {
            return false
        }

        for (i in this.distances.indices) {
            if (!ComparatorContext.DOUBLE_COMPARATOR.equals(this.distances[i], distances[i])) {
                return false
            }
        }

        return true
    }
}
