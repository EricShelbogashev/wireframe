package core.model.algebra.base

import core.model.algebra.properties.Normalizable

interface Point<T : Number> : Normalizable<T, Point<T>> {
    val dimension: Int
    fun get(index: Int): T
    fun component(index: Int): T
    fun componentSafe(index: Int): T
    operator fun plus(scalar: T): Point<T>
    operator fun plusAssign(scalar: T)
    operator fun plus(other: Point<T>): Point<T>
    operator fun plusAssign(other: Point<T>)
    operator fun minus(scalar: T): Point<T>
    operator fun minusAssign(scalar: T)
    operator fun minus(other: Point<T>): Point<T>
    operator fun minusAssign(other: Point<T>)
    operator fun times(scalar: Double): Point<T>
    operator fun timesAssign(scalar: Double)
    fun dot(other: Point<T>): T
    operator fun div(scalar: Double): Point<T>
    operator fun divAssign(scalar: Double)
    operator fun unaryMinus(): Point<T>
    operator fun unaryPlus(): Point<T>
    override fun toString(): String
    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
}