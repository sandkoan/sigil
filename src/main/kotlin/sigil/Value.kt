package sigil

/*
* Value makes up the fundamental data structure of sigil, the atoms that it can be boiled down to:
* Num, Str, Bool, List, and Null. Even other structures like Pair are just Lists with two elements.
* */
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

    data class List(var items: MutableList<Value>) : Value() {
        override fun toString(): String = items.toString()
    }

    object Null : Value() {
        override fun toString(): String = "null"
    }
    /*
    * Converts from a string value to a Sigil atom ("Value") without using too many custom defined values.
    * Sigil atoms are really just wrappers around Kotlin datatypes, which makes it easier to convert and manipulate them.
    * */
    companion object {
        fun of(s: String): Result<Value> {
            val s = s.trim()
            when (s) {
                "null" -> return Result.success(Null)
                "true" -> return Result.success(Bool(true))
                "false" -> return Result.success(Bool(false))
                else -> {
                    try {
                        return Result.success(Num(s.toDouble()))
                    } catch (e: NumberFormatException) {
                    }

                    if (s.indexOf('"') == 0 && s.lastIndexOf('"') == s.length - 1 && s.count { it == '"' } == 2) {
//                        return Result.success(Str(s.substring(1)))
                        return Result.success(Str(s.substring(1, s.length - 1)))
                    }

                    /*
                    else if (s.indexOf('[') == 0 && s.lastIndexOf(']') == s.length - 1) {
                        var x = s.trim('[', ']')
                        for (i in x) {

                        }
                        return Result.success()
                    }
                    */

                    return Result.failure(Error("Not a valid Value"))
                }
            }
        }
    }
}
