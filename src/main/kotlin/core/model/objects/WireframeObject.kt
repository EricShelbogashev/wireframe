package core.model.objects

import core.model.algebra.Line3
import core.model.algebra.Point3
import core.model.engine.SceneObject
import kotlin.math.max
import kotlin.math.min

class WireframeObject(
    lines: List<Line3> = listOf(),
) : SceneObject<WireframeObject> {
    var modifiedLines: MutableList<Line3> = lines.toMutableList()
    val lines get(): List<Line3> = modifiedLines
    var maxHeight = 0.0
    var minHeight = 255.0
    var x: Double = 0.0
    var y: Double = 0.0
    var z: Double = 0.0

    init {
        updateHeight()
    }

    private fun updateHeight() {
        if (modifiedLines.isEmpty()) {
            maxHeight = 0.0
            return
        }
        minHeight = modifiedLines.minBy { min(it.start.z, it.end.z) }.let { min(it.start.z, it.end.z) }
        maxHeight = modifiedLines.maxBy { max(it.start.z, it.end.z) }.let { max(it.start.z, it.end.z) }
        if (minHeight > maxHeight) {
            val tmpMin = minHeight
            minHeight = maxHeight
            maxHeight = tmpMin
        }
    }

    override fun translate(x: Double, y: Double, z: Double) {
        this.x += x
        this.y += y
        this.z += z
        val point = Point3(x, y, z)
        modifiedLines.forEach { it.translate(point) }
    }

    override fun scale(factor: Double) {
        modifiedLines.forEach { it.scale(factor) }
        updateHeight()
    }

    override fun scale(factorX: Double, factorY: Double, factorZ: Double) {
        modifiedLines.forEach { it.scale(factorX, factorY, factorZ) }
        updateHeight()
    }

    override fun toScaled(factor: Double): WireframeObject {
        return WireframeObject(modifiedLines.map { it.toScaled(factor) })
    }

    override fun toScaled(factorX: Double, factorY: Double, factorZ: Double): WireframeObject {
        return WireframeObject(modifiedLines.map { it.toScaled(factorX, factorY, factorZ) })
    }

    override fun rotate(radX: Double, radY: Double, radZ: Double) {
        modifiedLines.forEach { it.rotate(radX, radY, radZ) }
        updateHeight()
    }

    override fun toRotated(radX: Double, radY: Double, radZ: Double): WireframeObject {
        return WireframeObject(modifiedLines.map { it.toRotated(radX, radY, radZ) })
    }

    override fun applyPerspective(fov: Double, aspect: Double, n: Double, f: Double) {
        modifiedLines.forEach { it.toAppliedPerspective(fov, aspect, n, f) }
        updateHeight()
    }

    override fun toAppliedPerspective(fov: Double, aspect: Double, n: Double, f: Double): WireframeObject {
        return WireframeObject(modifiedLines.map { it.toAppliedPerspective(fov, aspect, n, f) })
    }

    fun toNormalized(): WireframeObject {
        val absoluteMax = lines.maxOf { max(it.start.norm(), it.end.norm()) }
        val avgX = lines.sumOf { it.start.x + it.end.x } / lines.size / absoluteMax / 2
        val avgY = lines.sumOf { it.start.y + it.end.y } / lines.size / absoluteMax / 2
        val avgZ = lines.sumOf { it.start.z + it.end.z } / lines.size / absoluteMax / 2
        val result = lines.map {
            val normIt = it / absoluteMax
            Line3(
                Point3(normIt.start.z - avgZ, normIt.start.y - avgY, normIt.start.x - avgX),
                Point3(normIt.end.z - avgZ, normIt.end.y - avgY, normIt.end.x - avgX)
            )
        }.toMutableList()
        return WireframeObject(result)
    }

    fun toTranslated(point3: Point3) : WireframeObject {
        val result = lines.map { it.toTranslated(point3) }
        return WireframeObject(result)
    }

}