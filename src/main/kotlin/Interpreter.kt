import java.io.File
import kotlin.Error

sealed class Error {
    data class Expected(val tok: Token) : Error()
    object ExpectedToken : Error()
    data class Unexpected(val tok: Token) : Error()
    data class CannotFind(val s: String) : Error()
}

data class Func(val args: ArrayList<String>, val expr: Expr)

fun input(msg: String): Value {
    print(msg)
    System.out.flush()

    val input = readLine()!!.replace("\n", "")
    return Value.Str(input)
}

fun eval(expr: Expr, funcs: Map<String, Func>, args: List<Value>): Value {
    when (expr) {
        is Expr.If -> {
            if (eval(expr.cond, funcs, args) == Value.Bool(true)) {
                eval(expr.t, funcs, args)
            } else {
                eval(expr.f, funcs, args)
            }
        }
        is Expr.Eq -> Value.Bool(eval(expr.x, funcs, args) == eval(expr.y, funcs, args))
        is Expr.Add -> {
            val a = eval(expr.x, funcs, args)
            val b = eval(expr.y, funcs, args)
            if (a is Value.Num && b is Value.Num) Value.Num(a.n + b.n)
            else if (a is Value.Str && b is Value.Str) Value.Str(a.s + b.s)
            else Value.Null
        }
        is Expr.Neg -> when (val y = eval(expr.n, funcs, args)) {
            is Value.Num -> Value.Num(-y.n)
            else -> Value.Null
        }
        is Expr.Mul -> {
            val a = eval(expr.x, funcs, args)
            val b = eval(expr.y, funcs, args)
            if (a is Value.Num && b is Value.Num) Value.Num(a.n * b.n)
            else Value.Null
        }
        is Expr.Div -> {
            val a = eval(expr.x, funcs, args)
            val b = eval(expr.y, funcs, args)
            if (a is Value.Num && b is Value.Num) Value.Num(a.n / b.n)
            else Value.Null
        }
        is Expr.Rem -> {
            val a = eval(expr.x, funcs, args)
            val b = eval(expr.y, funcs, args)
            if (a is Value.Num && b is Value.Num) Value.Num(a.n % b.n)
            else Value.Null
        }
        is Expr.Less -> {
            val a = eval(expr.x, funcs, args)
            val b = eval(expr.y, funcs, args)
            if (a is Value.Num && b is Value.Num) Value.Bool(a.n < b.n)
            else if (a is Value.Str && b is Value.Str) Value.Bool(a.s < b.s)
            else Value.Null
        }
        is Expr.LessEq -> {
            val a = eval(expr.x, funcs, args)
            val b = eval(expr.y, funcs, args)
            if (a is Value.Num && b is Value.Num) Value.Bool(a.n <= b.n)
            else if (a is Value.Str && b is Value.Str) Value.Bool(a.s <= b.s)
            else Value.Null
        }
        is Expr.Head -> {
            when (val items = eval(expr.h, funcs, args)) {
                is Value.List -> items.l.elementAtOrElse(0) { Value.Null }
                is Value.Str -> if (items.s.isNotEmpty()) Value.Str(items.s[0].toString())
                else -> Value.Null
            }
        }
//        is Expr.Tail -> {
//            when (val items = eval(expr.t, funcs, args)) {
//                is Value.List -> items.l.drop(1).forEach()
//                is Value.Str -> if (items.s.isNotEmpty()) Value.Str(items.s[0].toString())
//                else -> Value.Null
//            }
//        }
//        is Expr.Fuse -> { }
        is Expr.Pair -> Value.List(arrayListOf(eval(expr.f, funcs, args), eval(expr.p, funcs, args)))
        is Expr.Call -> {
            val f = funcs[expr.f]
            if (f != null) {
                eval(f.expr, funcs, expr.params.map { eval(it, funcs, args) } as ArrayList<Value>)
            } else {
                Value.Null
            }
        }
        is Expr.Words -> {
            when (val s = eval(expr.x, funcs, args)) {
                Value.List(words(s).)
            }
        }

    }
}

fun parseFuncs(tokens: Iterator<Token>) : Result<Map<String, Func>> = TODO()

fun words(s: String): List<String> {
    val s = "$s "
    var inStr = false
    for (c in s) {
        when (c) {
            '"' -> inStr = !inStr
        }

    }
}

fun lex(code: String): List<Token> {
    return words(code).map {
        when (it) {
            "fn" -> Token.Fn
            "is" -> Token.Is
            "if" -> Token.If
            "__head" -> Token.Head
            "__tail" -> Token.Tail
            "__fuse" -> Token.Fuse
            "__pair" -> Token.Pair
            "__litr" -> Token.Litr
            "__str" -> Token.Str
            "__words" -> Token.Words
            "__input" -> Token.Input
            "__print" -> Token.Print
            "__eq" -> Token.Eq
            "__add" -> Token.Add
            "__neg" -> Token.Neg
            "__mul" -> Token.Mul
            "__div" -> Token.Div
            "__rem" -> Token.Rem
            "__less" -> Token.Less
            "__lesseq" -> Token.LessEq
            else -> {
                val v = Value.of(it)
                if (v.isSuccess) Token.Value(v.getOrNull()!!)
                else Token.Ident(it)
            }
        }
    }
}

fun withCore(code: String) : String = File("sigil/core.sig").readText(Charsets.UTF_8) + code

fun prompt(): Unit = TODO()

fun exec(fname: String) : String = TODO()

fun usage() = println("Usage: sigil [file]")

fun main(args: Array<String>): Unit = TODO()
















