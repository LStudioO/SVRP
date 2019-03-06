package com.lstudio.point_restorer.primitives
import com.lstudio.point_restorer.ComparatorContext

class Point

@JvmOverloads constructor(var x: Double, var y: Double, var id: Int = 0) {

    fun distance(point: Point): Double {
        return distance(this, point)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val point = other as Point?

        if (!ComparatorContext.DOUBLE_COMPARATOR.equals(point!!.x, x)) return false
        return ComparatorContext.DOUBLE_COMPARATOR.equals(point.y, y)

    }

    override fun hashCode(): Int {
        var result: Int
        var temp: Long = java.lang.Double.doubleToLongBits(x)
        result = (temp xor temp.ushr(32)).toInt()
        temp = java.lang.Double.doubleToLongBits(y)
        result = 31 * result + (temp xor temp.ushr(32)).toInt()
        return result
    }

    override fun toString(): String {
        return "($x; $y)"
    }

    companion object {
        fun distance(p1: Point, p2: Point): Double {
            val a = p2.x - p1.x
            val b = p2.y - p1.y
            val sqrD = a * a + b * b

            return Math.sqrt(sqrD)
        }
    }
}