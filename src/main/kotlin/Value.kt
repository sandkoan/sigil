sealed class Value {
    data class Num(var n: Double) : Value() {
        override fun toString(): String = n.toString()
    }

    data class Str(var s: String) : Value() {
        override fun toString(): String = s
    }

    data class Bool(var b: Boolean) : Value() {
        override fun toString(): String = b.toString()
    }

    data class List(var l: ArrayList<Value>) : Value() {
        override fun toString(): String = l.toString()
    }

    object Null : Value() {
        override fun toString(): String = "null"
    }

    companion object {
        fun of(s: String): Result<Value> {
            val s = s.trim()
            when (s) {
                "null" -> return Result.success(Null)
                "true" -> return Result.success(Bool(true))
                "false" -> return Result.success(Bool(false))
                // Add toValue for a list
                // "[" ->
                else -> {
                    try {
                        return Result.success(Num(s.toDouble()))
                    } catch (e: NumberFormatException) {
                    }

                    if (s.iterator().nextChar() == '"') {
//                        return Result.success(Str(s.substring(1)))
                        return Result.success(Str(s.substring(1, s.length - 1)))
                    }
                    return Result.failure(Error("Not a valid Value"))
                }
            }
        }
    }
}
