package core.model.engine

import core.model.algebra.Line3

interface Linearizable {
    // Метод должен возвращать упорядоченный список точек,
    // которые будут соединены линиями в дальнейшем
    fun linearize(preferredStep: Double) {
        val (n, _) = fit(1.0, preferredStep)
        return linearize(n)
    }

    fun toLinearized(preferredStep: Double): List<Line3> {
        val (n, _) = fit(1.0, preferredStep)
        return toLinearized(n)
    }

    fun linearize(n: Int)
    fun toLinearized(n: Int): List<Line3>

    companion object {
        fun fit(length: Double, step: Double): Pair<Int, Double> {
            val numberOfPoints = kotlin.math.ceil(length / step).toInt() + 1
            val adjustedStep = length / (numberOfPoints - 1)
            return numberOfPoints to adjustedStep
        }
    }
}