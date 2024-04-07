package core.model.algebra

import core.model.algebra.base.DoubleLine
import core.model.algebra.properties.Transformable

class Line3(start: Point3, end: Point3) : DoubleLine<Point3, Line3>(
    start,
    end,
    { s, e -> Line3(s, e) }
), Transformable<Line3> {
    operator fun component1(): Point3 = start
    operator fun component2(): Point3 = end

    override fun scale(factor: Double) {
        start.scale(factor)
        end.scale(factor)
    }

    override fun scale(factorX: Double, factorY: Double, factorZ: Double) {
        start.scale(factorX, factorY, factorZ)
        end.scale(factorX, factorY, factorZ)
    }

    override fun toScaled(factor: Double): Line3 {
        return Line3(start.toScaled(factor), end.toScaled(factor))
    }

    override fun toScaled(factorX: Double, factorY: Double, factorZ: Double): Line3 {
        return Line3(start.toScaled(factorX, factorY, factorZ), end.toScaled(factorX, factorY, factorZ))
    }

    override fun rotate(radX: Double, radY: Double, radZ: Double) {
        start.toRotated(radX, radY, radZ)
        end.toRotated(radX, radY, radZ)
    }

    override fun toRotated(radX: Double, radY: Double, radZ: Double): Line3 {
        return Line3(start.toRotated(radX, radY, radZ), end.toRotated(radX, radY, radZ))
    }

    override fun applyPerspective(fov: Double, aspect: Double, n: Double, f: Double) {
        start.applyPerspective(fov, aspect, n, f)
        end.applyPerspective(fov, aspect, n, f)
    }

    override fun toAppliedPerspective(fov: Double, aspect: Double, n: Double, f: Double): Line3 {
        return Line3(
            start.toAppliedPerspective(fov, aspect, n, f),
            end.toAppliedPerspective(fov, aspect, n, f)
        )
    }

    override fun toString(): String {
        return "Line3($start, $end)"
    }

//    fun t(point3: Point3): Line3 {
//        return Line3(
//            start + point3,
//            end + point3,
//        )
//    }
}