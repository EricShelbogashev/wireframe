package model.objects

import model.algebra.Line3
import model.algebra.properties.Transformable
import model.engine.Linearizable
import kotlin.math.max
import kotlin.math.min

class WireframeObject(
    lines: List<Line3>,
) : Linearizable, Transformable<WireframeObject> {
    private var modifiedLines: MutableList<Line3> = mutableListOf()
    var maxHeight = 0.0
    var minHeight = 255.0

    private fun updateHeight() {
        if (modifiedLines.isEmpty()) {
            maxHeight = 0.0
            return
        }
        minHeight = modifiedLines.minBy { max(it.start.x, it.end.x) }.let { min(it.start.x, it.end.x) }
        maxHeight = modifiedLines.maxBy { max(it.start.x, it.end.x) }.let { max(it.start.x, it.end.x) }
        if (minHeight > maxHeight) {
            val tmpMin = minHeight
            minHeight = maxHeight
            maxHeight = tmpMin
        }
    }

    init {
        modifiedLines.addAll(lines)
        updateHeight()
    }

    override fun linearize(preferredStep: Double) {
        TODO("Not yet implemented")
    }

    override fun toLinearized(preferredStep: Double): List<Line3> {
        return modifiedLines
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

}