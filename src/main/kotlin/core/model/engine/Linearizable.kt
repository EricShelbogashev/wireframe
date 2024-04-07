package model.engine

import model.algebra.Line3

interface Linearizable {
    // Метод должен возвращать упорядоченный список точек,
    // которые будут соединены линиями в дальнейшем
    fun linearize(preferredStep: Double)
    fun toLinearized(preferredStep: Double): List<Line3>

    companion object {
        fun fit(length: Double, step: Double): Pair<Int, Double> {
            val numberOfPoints = kotlin.math.ceil(length / step).toInt() + 1
            val adjustedStep = length / (numberOfPoints - 1)
            return numberOfPoints to adjustedStep
        }
    }
}