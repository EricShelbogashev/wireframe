package model.algebra

import model.algebra.base.DoubleMatrix
import model.algebra.base.Matrix

fun DoubleArray.asMatrix(rows: Int, cols: Int): DoubleMatrix = DoubleMatrix(rows, cols, this)

fun <T : Matrix<Double, T>> Matrix<Double, T>.toPoint3(): Point3 {
    require((cols == 1 && rows in 2..3) || (cols in 2..3 && rows == 1)) { "размерность матрицы должна быть 1x3 или 3x1" }
    return if (cols == 1) {
        Point3(get(0, 0), get(1, 0), safeGet(2, 0))
    } else {
        Point3(get(0, 1), get(0, 1), safeGet(0, 2))
    }
}