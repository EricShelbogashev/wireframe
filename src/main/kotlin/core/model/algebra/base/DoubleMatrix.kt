package core.model.algebra.base

import kotlin.math.tan

class DoubleMatrix(override val rows: Int, override val cols: Int, generator: (Int, Int) -> Double) :
    Matrix<Double, DoubleMatrix> {
    private val data: DoubleArray = DoubleArray(rows * cols) { index ->
        val col = index % cols
        val row = index / cols
        generator(row, col)
    }

    init {
        require(rows > 0) { "Rows must be positive" }
        require(cols > 0) { "Columns must be positive" }
        require(data.size == rows * cols) { "Data size does not match matrix dimensions" }
    }

    init {
        require(rows > 0 && cols > 0) { "Rows and columns must be positive" }
    }

    constructor(rows: Int, cols: Int, data: DoubleArray) : this(rows, cols, { row, col -> data[row * cols + col] })
    constructor(rows: Int, cols: Int) : this(rows, cols, { _, _ -> 0.0 })

    override fun times(other: DoubleMatrix): DoubleMatrix {
        require(cols == other.rows) { "Matrix dimensions are not compatible for multiplication." }

        return DoubleMatrix(rows, other.cols) { row, col ->
            (0 until cols).sumOf { k -> this[row, k] * other[k, col] }
        }
    }

    override fun times(scalar: Double): DoubleMatrix =
        DoubleMatrix(rows, cols) { row, col -> this[row, col] * scalar }

    override fun div(scalar: Double): DoubleMatrix {
        require(scalar != 0.0) { "Cannot divide by zero." }
        return this * (1.0 / scalar)
    }

    override fun get(row: Int, col: Int): Double {
        checkIndexBounds(row, col)
        return data[row * cols + col]
    }

    override fun safeGet(row: Int, col: Int): Double {
        if (row !in 0 until rows || col !in 0 until cols) {
            return 0.0
        }
        return data[row * cols + col]
    }

    override fun set(row: Int, col: Int, value: Double) {
        checkIndexBounds(row, col)
        data[row * cols + col] = value
    }

    override fun replace(row: Int, col: Int, value: Double): Double {
        checkIndexBounds(row, col)
        return data[row * cols + col].also {
            data[row * cols + col] = value
        }
    }

    override fun plus(other: DoubleMatrix): DoubleMatrix {
        require(rows == other.rows && cols == other.cols) { "Matrices must have the same dimensions." }

        return DoubleMatrix(rows, cols) { row, col -> this[row, col] + other[row, col] }
    }

    override fun plus(scalar: Double): DoubleMatrix =
        DoubleMatrix(rows, cols) { row, col -> this[row, col] + scalar }

    override fun transpose(): DoubleMatrix = DoubleMatrix(cols, rows) { row, col -> this[col, row] }

    override fun isZero(): Boolean = data.all { it == 0.0 }

    override fun dot(other: DoubleMatrix): Double {
        require(rows == other.rows && cols == other.cols) { "Matrices must have the same dimensions for dot product." }
        return (0 until rows).sumOf { row ->
            (0 until cols).sumOf { col -> this[row, col] * other[row, col] }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DoubleMatrix

        if (rows != other.rows || cols != other.cols) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int = data.contentHashCode()

    override fun toString(): String {
        return (0 until rows).joinToString(separator = "\n") { row ->
            (0 until cols).joinToString(separator = " ", prefix = "[", postfix = "]") { col ->
                "%.2f".format(data[row * cols + col])
            }
        }
    }

    private fun checkIndexBounds(row: Int, column: Int) {
        if (row !in 0 until rows || column !in 0 until cols) {
            throw IndexOutOfBoundsException("Matrix indices ($row, $column) out of bounds.")
        }
    }

    companion object {
        fun column(data: DoubleArray): DoubleMatrix {
            require(data.isNotEmpty()) { "Array must contains at least 1 element" }
            return DoubleMatrix(data.size, 1, data)
        }

        fun row(data: DoubleArray): DoubleMatrix {
            require(data.isNotEmpty()) { "Array must contains at least 1 element" }
            return DoubleMatrix(1, data.size, data)
        }
    }
}