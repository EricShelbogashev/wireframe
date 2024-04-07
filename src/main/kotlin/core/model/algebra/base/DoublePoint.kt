package core.model.algebra.base

import kotlin.math.pow
import kotlin.math.sqrt

abstract class DoublePoint<Heir : DoublePoint<Heir>>(
    protected val components: DoubleArray,
    private val generator: (DoubleArray) -> Heir,
) : Point<Double> {
    override val dimension: Int = components.size
    override fun get(index: Int): Double {
        return component(index)
    }

    init {
        require(components.isNotEmpty()) { "точка должна содержать хотя бы одну компоненту" }
    }

    override fun component(index: Int): Double {
        require(index in 0 until dimension) { "точка не содержит компоненту с индексом $index" }
        return components[index]
    }

    override fun componentSafe(index: Int): Double {
        if (index !in 0 until dimension) return .0
        return components[index]
    }

    override fun minusAssign(other: Point<Double>) {
        require(dimension == other.dimension) { "размерность точки $other не совпадает с $this" }
        transformSelf { i, c -> c - other.component(i) }
    }

    override fun minusAssign(scalar: Double) {
        plusAssign(-scalar)
    }

    override fun plusAssign(other: Point<Double>) {
        require(dimension == other.dimension) { "размерность точки $other не совпадает с $this" }
        transformSelf { i, c -> c + other.component(i) }
    }

    override fun plusAssign(scalar: Double) {
        transformSelf { _, c -> c + scalar }
    }

    override fun times(scalar: Double): Heir {
        return transform { _, c -> c * scalar }
    }

    override fun timesAssign(scalar: Double) {
        transformSelf { _, c -> c * scalar }
    }

    override fun dot(other: Point<Double>): Double {
        require(dimension == other.dimension) { "размерность точки $other не совпадает с $this" }
        return components.mapIndexed { i, c -> c * other.component(i) }.sum()
    }

    override fun plus(scalar: Double): Heir {
        return transform { _, c -> c + scalar }
    }

    override fun plus(other: Point<Double>): Heir {
        require(dimension == other.dimension) { "размерность точки $other не совпадает с $this" }
        return transform { i, c -> c + other.component(i) }
    }

    override fun minus(scalar: Double): Heir {
        return plus(-scalar)
    }

    override fun minus(other: Point<Double>): Heir {
        require(dimension == other.dimension) { "размерность точки $other не совпадает с $this" }
        return transform { i, c -> c - other.component(i) }
    }

    override fun div(scalar: Double): Heir {
        return transform { _, c -> c / scalar }
    }

    override fun divAssign(scalar: Double) {
        transformSelf { _, c -> c / scalar }
    }

    override fun unaryMinus(): Heir {
        return transform { _, c -> -c }
    }

    override fun unaryPlus(): Heir {
        return generator(components.copyOf())
    }

    override fun norm(): Double {
        return sqrt(components.sumOf { it.pow(2) })
    }

    override fun normalize() {
        val norm = norm()
        transformSelf { _, c -> c / norm }
    }

    override fun toNormalized(): Heir {
        val norm = norm()
        return transform { _, c -> c / norm }
    }

    private fun transform(operation: (Int, Double) -> Double): Heir {
        return generator(components.mapIndexed(operation).toDoubleArray())
    }

    private fun transformSelf(operation: (Int, Double) -> Double) {
        components.forEachIndexed { i, c ->
            components[i] = operation(i, c)
        }
    }

    override fun toString(): String {
        return components.joinToString(prefix = "[", postfix = "]", separator = ", ")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DoublePoint<*>

        return components.contentEquals(other.components)
    }

    override fun hashCode(): Int {
        return components.contentHashCode()
    }
}