import kotlin.Error

import Intrinsic as Intrins

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
    data class Intrinsic(val intrins: Intrins) : Lexeme()
    data class Ident(val str: String) : Lexeme()
    data class Value(val str: String) : Lexeme()
}

sealed class State {
    object Default : State()
    data class Number(var num: String) : State()
    data class Str(var s: String, val sline: Int, var escaped: Boolean) : State()
    data class Ident(var id: String) : State()
}

data class Token(val l: Lexeme)

fun lex(code: String): Result<ArrayList<Token>> {
    val tokens = ArrayList<Token>()
    val chars = code.toList()
    var state: State = State.Default

    var line = 1

    val iter = chars.listIterator()
    while (iter.hasNext()) {
        val c = iter.next()
        var incr = true

        when (state) {
            is State.Default -> when {
                c == '\u0000' -> break
                c.isWhitespace() -> {}
                c == '"' -> state = State.Str("", line, false)
                c.isDigit() -> state = State.Number("")
                else -> state = State.Ident("")
            }
            is State.Number -> when {
                c.isWhitespace() -> {
                    incr = false
//                    tokens.add(Token(Lexeme.Ident(state.num)))
                    state = State.Default
                }
//                c.isDigit() -> state.num += c
                else -> state.num += c
//                else -> state = State.Default
            }
            is State.Str -> when {
                c == '\\' && !state.escaped -> state.escaped = true
                c == '\u0000' -> return Result.failure(Error("Unexpected null character at ${state.sline}!"))
                c.isWhitespace() -> {
                    incr = false
//                    tokens.add(Token(Lexeme.Ident(state.s)))
                    state = State.Default
                }
                !state.escaped -> state.s += c
                c == 'n' -> state.s += '\n'
                c != 'n' -> return Result.failure(Error("Invalid escape sequence \\$c at $line"))
                else -> state = State.Default
            }
            is State.Ident ->
//                when
            {
//                c.isWhitespace() -> {
//                    incr = false
//                    tokens.add(Token(Lexeme.Ident(state.id)))
//                    state = State.Default
//                }
//                c.isLetter() -> state.id += c
//                else -> state = State.Default
            }
        }
        if (incr) {
            when (iter.next()) {
                '\n' -> line++
                else -> {}
            }
        }
    }
    return Result.success(tokens)
}