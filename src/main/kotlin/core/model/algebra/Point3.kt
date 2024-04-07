package model.algebra

import model.algebra.base.DoubleMatrix
import model.algebra.base.DoublePoint
import model.algebra.properties.Transformable
import kotlin.math.cos
import kotlin.math.sin

class Point3(x: Double, y: Double, z: Double) : DoublePoint<Point3>(
    doubleArrayOf(x, y, z),
    { Point3(it[0], it[1], it[2]) }
), Transformable<Point3> {
    val x get() = component1()
    val y get() = component2()
    val z get() = component3()

    operator fun component1(): Double = components[0]
    operator fun component2(): Double = components[1]
    operator fun component3(): Double = components[2]

    override fun scale(factor: Double) {
        components[0] *= factor
        components[1] *= factor
        components[2] *= factor
    }

    override fun scale(factorX: Double, factorY: Double, factorZ: Double) {
        components[0] *= factorX
        components[1] *= factorY
        components[2] *= factorZ
    }

    override fun toScaled(factor: Double): Point3 {
        return Point3(
            components[0] * factor,
            components[1] * factor,
            components[2] * factor
        )
    }

    override fun toScaled(factorX: Double, factorY: Double, factorZ: Double): Point3 {
        return Point3(
            components[0] * factorX,
            components[1] * factorY,
            components[2] * factorZ
        )
    }

    override fun rotate(radX: Double, radY: Double, radZ: Double) {
        val (d0, d1, d2) = toRotated(radX, radY, radZ)
        components[0] = d0
        components[1] = d1
        components[2] = d2
    }

    override fun toRotated(radX: Double, radY: Double, radZ: Double): Point3 {
        val rx = Point3(x, y * cos(radX) - z * sin(radX), y * sin(radX) + z * cos(radX))
        val ry = Point3(rx.x * cos(radY) + rx.z * sin(radY), rx.y, -rx.x * sin(radY) + rx.z * cos(radY))
        val rz = Point3(ry.x * cos(radZ) - ry.y * sin(radZ), ry.x * sin(radZ) + ry.y * cos(radZ), ry.z)
        return rz
    }

    override fun applyPerspective(fov: Double, aspect: Double, n: Double, f: Double) {
        // TODO: переделать, 71 страница https://portal.tpu.ru/SHARED/j/JBOLOTOVA/academic/ComputerGraphics/6.Perspective.pdf
        val perspective: DoubleMatrix = DoubleMatrix.perspective(fov, aspect, n, f)
        val pointMatrix = DoubleMatrix.row(doubleArrayOf(x, y, z, .0))
        applyMatrix { pointMatrix * perspective }
    }

    override fun toAppliedPerspective(fov: Double, aspect: Double, n: Double, f: Double): Point3 {
//        val perspective: DoubleMatrix = DoubleMatrix.perspective(fov, aspect, n, f)
        // TODO: переделать
        return Point3(
            x / (f * z + 1.0),
            y / (f * z + 1.0),
            z / (f * z + 1.0),
        )
    }

    private fun applyMatrix(transform: (DoubleMatrix) -> DoubleMatrix) {
        val pointMatrix = DoubleMatrix.row(doubleArrayOf(x, y, z))
        val transformedMatrix = transform(pointMatrix)
        components[0] = transformedMatrix[0, 0]
        components[1] = transformedMatrix[0, 1]
        components[2] = transformedMatrix[0, 2]
    }

    private fun toAppliedMatrix(transform: (DoubleMatrix) -> DoubleMatrix): Point3 {
        val pointMatrix = DoubleMatrix.row(doubleArrayOf(x, y, z, 1.0))
        val transformedMatrix = transform(pointMatrix)
        return Point3(transformedMatrix[0, 0], transformedMatrix[0, 1], transformedMatrix[0, 2])
    }
}