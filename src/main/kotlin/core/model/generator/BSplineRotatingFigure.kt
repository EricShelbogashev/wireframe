package model.generator

import model.algebra.Line3
import model.algebra.Point3
import model.engine.Linearizable
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class BSplineRotatingFigure(
    private val curve: BSplineCurve,
    private val rotationSteps: Int,
) : Linearizable {
    private val theta: Double = 2 * PI / rotationSteps
    private val lineSegments: MutableList<Line3> = mutableListOf()

    override fun linearize(preferredStep: Double) {
        lineSegments.clear()
        linearizeToList(lineSegments, preferredStep)
    }

    override fun toLinearized(preferredStep: Double): List<Line3> {
        val listOf = mutableListOf<Line3>()
        linearizeToList(listOf, preferredStep)
        return listOf
    }

    private fun linearizeToList(list: MutableList<Line3>, preferredStep: Double) {
        val curveLines: List<Line3> = curve.toLinearized(preferredStep)
        for (i in 0 until rotationSteps) {
            for (line in curveLines) {
                val rotatedStart = rotatePoint(line.start, i * theta)
                val rotatedEnd = rotatePoint(line.end, i * theta)
                list.add(Line3(rotatedStart, rotatedEnd))
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
