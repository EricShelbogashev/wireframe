package model.generator

import model.algebra.Line3
import model.algebra.Point3
import model.algebra.asMatrix
import model.algebra.base.DoubleMatrix
import model.algebra.toPoint3
import model.engine.Linearizable
import kotlin.math.pow

/**
 * Класс для генерации B-сплайнов.
 *
 * Позволяет создавать кривые на основе заданных контрольных точек. Кривые формируются
 * с использованием кубических B-сплайнов, которые обеспечивают гладкое и непрерывное соединение между точками.
 */
class BSplineCurve : Linearizable {
    private val controlPoints: MutableList<Point3> = mutableListOf()
    private val lineSegments: MutableList<Line3> = mutableListOf()

    companion object {
        /**
         * Матрица преобразования для кубического B-сплайна.
         */
        private val SPLINE_MATRIX = doubleArrayOf(
            -1.0, 3.0, -3.0, 1.0,
            3.0, -6.0, 3.0, 0.0,
            -3.0, 0.0, 3.0, 0.0,
            1.0, 4.0, 1.0, 0.0
        ).asMatrix(4, 4) / 6.0
    }

    /**
     * Добавляет контрольную точку к списку.
     *
     * @param point3 Точка, которая будет добавлена в качестве контрольной.
     */
    fun add(point3: Point3) {
        controlPoints.add(point3)
    }

    /**
     * Удаляет контрольную точку из списка.
     *
     * @param point3 Точка для удаления.
     */
    fun remove(point3: Point3) {
        controlPoints.remove(point3)
    }

    /**
     * Очищает список контрольных точек.
     */
    fun clear() {
        controlPoints.clear()
    }

    /**
     * Генерирует точки на B-сплайне с заданным шагом.
     *
     * Возвращает список точек, которые аппроксимируют B-сплайн.
     *
     * @param preferredStep Предпочтительный шаг между точками на кривой.
     * @return Список точек кривой.
     */
    override fun linearize(preferredStep: Double) {
        val segments = toLinearized(preferredStep)
        lineSegments.clear()
        lineSegments.addAll(segments)
    }

    override fun toLinearized(preferredStep: Double): List<Line3> {
        if (controlPoints.size == 1) return listOf(Line3(controlPoints.first(), controlPoints.first()))
        if (controlPoints.size == 2) return listOf(Line3(controlPoints.first(), controlPoints[1]))
        if (controlPoints.size == 3) return listOf(
            Line3(
                (controlPoints[1] - controlPoints[0]) / 2.0, (controlPoints[2] - controlPoints[1]) / 2.0
            )
        )

        val (n, step) = Linearizable.fit(1.0, preferredStep)

        val lines = mutableListOf<Line3>()
        val points = mutableListOf<Point3>()

        for (i in 1..(controlPoints.size - 3)) {
            val gSI = DoubleMatrix(
                4, 2, doubleArrayOf(
                    controlPoints[i - 1].x, controlPoints[i - 1].y,
                    controlPoints[i].x, controlPoints[i].y,
                    controlPoints[i + 1].x, controlPoints[i + 1].y,
                    controlPoints[i + 2].x, controlPoints[i + 2].y,
                )
            )
            for (j in 0..n) {
                val t = j * step
                val tI = DoubleMatrix(1, 4, doubleArrayOf(t.pow(3.0), t.pow(2.0), t, 1.0))
                val rI = tI * SPLINE_MATRIX * gSI
                val point = rI.transpose().toPoint3()
                points.add(point)
            }
        }

        for (i in 1 until points.size) {
            val line2 = Line3(points[i - 1], points[i])
            lines.add(line2)
        }
        return lines
    }
}
