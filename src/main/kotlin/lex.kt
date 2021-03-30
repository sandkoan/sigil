enum class Intrinsic {
    IF,
    ADD, NEG, MUL, INV, REM,
    EQ, LESS,
    HEAD, TAIL, PAIR, FUSE,
    LITR, STR, WORDS,
    IN, OUT
}

sealed class Lexeme {
    object Fn : Lexeme()
    object Is : Lexeme()
    data class Intrinsic(val intrins: Intrinsic) : Lexeme()
    data class Ident(val str: String) : Lexeme()
    data class Value(val str: String) : Lexeme()
}

sealed class State {
    object Default : State()
    data class Number(val str: String) : State()
    data class String(val str: String, val line: Int, val bool: Boolean) : State()
    data class Ident(val str: String) : State()
}

data class Token(val l: Lexeme)

fun lex(code: String): Result<ArrayList<Token>> {
    var tokens  = ArrayList<Token>()
    var chars = code.toList()
    var state = State.Default
    var line = 1

    val iter = chars.listIterator()
    while (iter.hasNext()) {
        var c = iter.next()
        var incr = true

        when(sate) {
            State.Default -> when (c) {
                0 -> break,

            }
        }

    }


}