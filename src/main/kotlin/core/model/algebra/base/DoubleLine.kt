package core.model.algebra.base

import core.model.algebra.Point3

open class DoubleLine<PointHeir : DoublePoint<PointHeir>, LineHeir : DoubleLine<PointHeir, LineHeir>>(
    final override val start: PointHeir,
    final override val end: PointHeir,
    private val generator: (PointHeir, PointHeir) -> LineHeir,
) : Line<Double> {
    override val dimension: Int = start.dimension
    override val direction: PointHeir = end - start

    override fun divAssign(scalar: Double) {
        start /= scalar
        end /= scalar
    }

    override fun div(scalar: Double): LineHeir {
        return generator(start / scalar, end / scalar)
    }

    init {
        require(start.dimension == end.dimension) { "размерности начальной и конечной точек должны совпадать" }
    }

    override fun norm(): Double {
        return direction.norm()
    }

    override fun normalize() {
        val norm = norm()
        start /= norm
        end /= norm
    }

    override fun toNormalized(): LineHeir {
        val norm = norm()
        return generator(start / norm, end / norm)
    }

    fun translate(shift: Point3) {
        start += shift
        end += shift
    }
}