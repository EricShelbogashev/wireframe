package core.model.generator

import core.Context
import core.model.algebra.Line3
import core.model.algebra.Point3
import core.model.engine.Linearizable
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class BSplineRotatingFigure(
    private val curve: BSplineCurve,
    context: Context,
) : Linearizable {
    private val rotationSteps: Int = context.m
    private val circleSteps: Int = context.m1 * context.m
    private val theta: Double = 2 * PI / rotationSteps
    private val lineSegments: MutableList<Line3> = mutableListOf()
    private val eth: Double = 2 * PI / circleSteps

    override fun linearize(n: Int) {
        lineSegments.clear()
        linearizeToList(lineSegments, n)
    }

    override fun toLinearized(n: Int): List<Line3> {
        val listOf = mutableListOf<Line3>()
        linearizeToList(listOf, n)
        return listOf
    }

    private fun linearizeToList(list: MutableList<Line3>, n: Int) {
        val curveLines: List<Line3> = curve.toLinearized(n)
        for (i in 0 until rotationSteps) {
            for (line in curveLines) {
                val rotatedStart = rotatePoint(line.start, i * theta)
                val rotatedEnd = rotatePoint(line.end, i * theta)
                list.add(Line3(rotatedStart, rotatedEnd))
            }
        }
        for (point in curve.controlPointsBSpline) {
            var last = point
            for (i in 1..circleSteps) {
                val cur = rotatePoint(point, i * eth)
                list.add(Line3(last, cur))
                last = cur
            }
        }
    }

    private fun rotatePoint(point: Point3, theta: Double): Point3 {
        val x = point.x
        val y = cos(theta) * point.y
        val z = sin(theta) * point.y
        return Point3(x, y, z)
    }
}
