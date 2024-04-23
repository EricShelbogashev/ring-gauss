class Matrix {
    var cols: Int
    val rows: Int
    private var data: Array<Array<Int>>

    constructor(rows: Int, cols: Int) : this(
        Array(rows) { Array(cols) { 0 } }
    )

    constructor(data: Array<Array<Int>>) {
        this.rows = data.size
        this.cols = data[0].size
        require(rows > 0 && cols > 0) { "количество строк и столбцов должно быть положительным целым числом" }
        this.data = data
    }

    /**
     * Приведение матрицы к ступенчатому виду в кольце по модулю mod.
     */
    fun toRowEchelonForm(mod: Int): Pair<Matrix, List<Int>> {
        val matrix = copy()
        val pivotColumns = mutableListOf<Int>()
        var lead = 0
        for (r in 0 until rows) {
            if (lead >= cols) break
            var i = r
            while (matrix[i, lead] % mod == 0) {
                i++
                if (i == rows) {
                    i = r
                    lead++
                    if (lead == cols) {
                        return Pair(matrix, pivotColumns)
                    }
                }
            }

            // Перестановка строк
            for (k in 0 until cols) {
                val temp = matrix[r, k]
                matrix[r, k] = matrix[i, k]
                matrix[i, k] = temp
            }
            pivotColumns.add(lead)

            // Приведение элементов столбца к нулю с использованием аддитивной инверсии
            for (j in 0 until rows) {
                if (j != r) {
                    val mult = matrix[j, lead]
                    for (k in lead until cols) {
                        val factor = (mult * modInverse(matrix[r, lead], mod)) % mod
                        matrix[j, k] = (matrix[j, k] - factor * matrix[r, k] + mod * mod) % mod
                    }
                }
            }

            lead++
        }

        return Pair(matrix, pivotColumns)
    }

    private fun gcdExtended(a: Int, b: Int, x: IntArray, y: IntArray): Int {
        if (a == 0) {
            x[0] = 0
            y[0] = 1
            return b
        }
        val x1 = IntArray(1)
        val y1 = IntArray(1)
        val gcd = gcdExtended(b % a, a, x1, y1)

        x[0] = y1[0] - (b / a) * x1[0]
        y[0] = x1[0]

        return gcd
    }

    private fun modInverse(a: Int, mod: Int): Int {
        val x = IntArray(1)
        val y = IntArray(1)
        val g = gcdExtended(a, mod, x, y)
        if (g != 1) {
            throw IllegalArgumentException("Inverse does not exist.")
        } else {
            return (x[0] % mod + mod) % mod
        }
    }


    /**
     * Создает и возвращает глубокую копию этой матрицы.
     */
    private fun copy(): Matrix {
        val newMatrix = Matrix(rows, cols)
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                newMatrix[i, j] = this[i, j]
            }
        }
        return newMatrix
    }

    operator fun get(row: Int, col: Int): Int {
        if (row !in 0 until rows || col !in 0 until cols) {
            throw IndexOutOfBoundsException("обращение за пределы матрицы")
        }
        return data[row][col]
    }


    operator fun set(row: Int, col: Int, value: Int) {
        if (row !in 0 until rows || col !in 0 until cols) {
            throw IndexOutOfBoundsException("запись за пределы матрицы")
        }
        data[row][col] = value
    }

    override fun toString(): String {
        // Находим максимальную ширину числа в матрице для выравнивания
        val maxNumberWidth = data.flatten().maxOfOrNull { it.toString().length } ?: 0

        return data.joinToString(separator = "\n") { row ->
            row.joinToString(separator = " ") { elem -> "%${maxNumberWidth}d".format(elem) }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Matrix

        if (rows != other.rows || cols != other.cols) return false
        if (!data.contentDeepEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rows
        result = 31 * result + cols
        result = 31 * result + data.contentDeepHashCode()
        return result
    }
}