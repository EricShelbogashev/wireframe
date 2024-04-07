package core.model.algebra.base

import core.model.algebra.properties.Normalizable

interface Line<T : Number> : Normalizable<T, Line<T>> {
    val start: Point<T>
    val end: Point<T>
    val dimension: Int
    val direction: Point<T>

    operator fun div(scalar: T): Line<T>
    operator fun divAssign(scalar: T)
}