package com.lstudio.pointrestorer.primitives

import com.lstudio.pointrestorer.ComparatorContext

class Circle(var center: Point?, var radius: Double) {

    val isPoint: Boolean
        get() = ComparatorContext.DOUBLE_COMPARATOR.equalsZero(radius)

    fun getIntersectionPoints(circle: Circle, id: Int): Array<Point>? {
        return getIntersectionPoints(this, circle, id)
    }

    fun getIntersectionPoints(circle: Circle): Array<Point>? {
        return getIntersectionPoints(this, circle)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val circle = other as Circle?

        return if (!ComparatorContext.DOUBLE_COMPARATOR.equals(
                circle!!.radius,
                radius
            )
        ) false else center == circle.center
    }

    override fun hashCode(): Int {
        var result: Int = center!!.hashCode()
        val temp: Long = java.lang.Double.doubleToLongBits(radius)
        result = 31 * result + (temp xor temp.ushr(32)).toInt()
        return result
    }

    override fun toString(): String {
        return "{$radius: $center}"
    }

    companion object {
        fun getIntersectionPoints(c1: Circle, c2: Circle, id: Int): Array<Point>? {
            val points = getIntersectionPoints(c1, c2) ?: return null
            for (p in points) {
                p.id = id
            }

            return points
        }

        fun getIntersectionPoints(c1: Circle, c2: Circle): Array<Point>? {
            var points: Array<Point>? = null

            if (c1.isPoint) {
                points = getIntersectionPoints(c2, c1.center)
            } else if (c2.isPoint) {
                points = getIntersectionPoints(c1, c2.center)
            } else if (c1 != c2) {
                val cp1 = c1.center
                val cp2 = c2.center
                val x1 = cp1!!.x
                val y1 = cp1.y
                val x2 = cp2!!.x
                val y2 = cp2.y
                val r1 = c1.radius
                val r2 = c2.radius
                val d = Point.distance(cp1, cp2)

                if (ComparatorContext.DOUBLE_COMPARATOR.compare(
                        d,
                        r1 + r2
                    ) > 0 || ComparatorContext.DOUBLE_COMPARATOR.compare(d, Math.abs(r1 - r2)) < 0
                ) {
                    points = null
                } else {
                    val b = (r2 * r2 - r1 * r1 + d * d) / (2 * d)
                    val a = d - b
                    val x2SubX1 = x2 - x1
                    val y2SubY1 = y2 - y1
                    val aDivD = a / d
                    val x0 = x1 + aDivD * x2SubX1
                    val y0 = y1 + aDivD * y2SubY1
                    val sqrR1SubSqrA = r1 * r1 - a * a
                    val h: Double =
                        if (ComparatorContext.DOUBLE_COMPARATOR.equalsZero(sqrR1SubSqrA)) 0.0 else Math.sqrt(
                            sqrR1SubSqrA
                        )
                    val hDivD = h / d
                    val xOffset = hDivD * y2SubY1
                    val yOffset = hDivD * x2SubX1
                    val p1 = Point(x0 + xOffset, y0 - yOffset)
                    val p2 = Point(x0 - xOffset, y0 + yOffset)

                    points = if (p1 == p2) {
                        arrayOf(p1)
                    } else {
                        arrayOf(p1, p2)
                    }
                }
            }

            return points
        }

        private fun getIntersectionPoints(c: Circle, p: Point?): Array<Point>? {
            val points: Array<Point>?

            if (c.isPoint) {
                points = getIntersectionPoints(c.center!!, p)
            } else {
                if (ComparatorContext.DOUBLE_COMPARATOR.equals(c.radius, p!!.distance(c.center!!))) {
                    points = arrayOf(Point(p.x, p.y))
                } else {
                    points = null
                }
            }

            return points
        }

        private fun getIntersectionPoints(p1: Point, p2: Point?): Array<Point>? {
            val points: Array<Point>?

            if (p1 == p2) {
                points = arrayOf(Point(p1.x, p1.y))
            } else {
                points = null
            }

            return points
        }
    }
}

