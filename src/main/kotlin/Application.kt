fun main(args: Array<String>) {
    if (args.size != 2) {
        println("количество аргументов должно равняться 2")
    }

    val parser = MatrixParser()
    val matrix = try {
        parser.createMatrixFromString(args[0])
    } catch (e: Exception) {
        System.err.println("в процессе чтения возникла ошибка")
        e.printStackTrace()
        return
    }
    val mod = try {
        val int = args[1].toInt()
        require(int > 0)
        int
    } catch (e: Exception) {
        System.err.println("модуль не является положительным целочисленным или содержит лишние символы")
        e.printStackTrace()
        return
    }
    val (m, _   ) = matrix.toRowEchelonForm(mod)
    println(parser.createStringFromMatrix(m))
}