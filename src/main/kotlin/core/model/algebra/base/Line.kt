package model.algebra.base

import model.algebra.properties.Normalizable

interface Line<T> : Normalizable<T, Line<T>> {
    val start: Point<T>
    val end: Point<T>
    val dimension: Int
    val direction: Point<T>
}