import java.io.File

data class Func(val args: List<String>, val expr: Expr)

// TODO: Fix path
fun withPrelude(code: String): String = File("sigil/core.sig").readText(Charsets.UTF_8) + code

fun input(msg: String): Value {
    print(msg)
    System.out.flush()

    val input = readLine()!!.trim()
    return Value.Str(input)
}

fun words(s: String): List<String> {
    val words = ArrayList<String>()
    val sb = StringBuilder()
    var inStr = false

    for (c in s) {
        when {
            c == '"' -> {
                inStr = !inStr
                sb.append(c)
            }
            c.isWhitespace() -> if (inStr) {
                sb.append(c)
            } else {
                if (sb.isNotEmpty())
                    words.add(sb.toString().trim())
                sb.setLength(0)
            }
            else -> sb.append(c)
        }
    }

    if (sb.isNotEmpty())
        words.add(sb.toString().trim())

    return words
}

fun lex(code: String): List<Token> {
    return words(code).map {
        when (it) {
            "let" -> Token.Fn
            "=" -> Token.Is
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
                if (v.isSuccess) Token.Value(v.getOrElse { Value.Null })
                else Token.Ident(it)
            }
        }
    }
}

fun eval(expr: Expr, funcs: Map<String, Func>, args: List<Value>): Value {
    return when (expr) {
        is Expr.If -> if (eval(expr.cond, funcs, args) == Value.Bool(true)) {
            eval(expr.t, funcs, args)
        } else {
            eval(expr.f, funcs, args)
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
        is Expr.Head -> when (val list = eval(expr.list, funcs, args)) {
            is Value.List -> list.items.elementAtOrElse(0) { Value.Null }
            is Value.Str -> if (list.s.isNotEmpty()) Value.Str(list.s[0].toString()) else Value.Null
            else -> Value.Null
        }
        is Expr.Tail -> when (val list = eval(expr.list, funcs, args)) {
            is Value.List -> if (list.items.drop(1).isNotEmpty()) Value.List(list.items.drop(1)
                .toMutableList()) else Value.Null
            is Value.Str -> if (list.s.substring(1).isNotEmpty()) Value.Str(list.s.substring(1)) else Value.Null
            else -> Value.Null
        }
        is Expr.Fuse -> {
            val x = eval(expr.x, funcs, args)
            val y = eval(expr.y, funcs, args)
            if (x is Value.List && y is Value.List) Value.List((x.items + y.items).toMutableList())
            else if (x is Value.List) Value.List((x.items + y).toMutableList())
            else if (y is Value.List) Value.List((mutableListOf(x) + y.items).toMutableList())
            else Value.List(mutableListOf(x, y))
        }
        is Expr.Pair -> Value.List(arrayListOf(eval(expr.f, funcs, args), eval(expr.p, funcs, args)))
        is Expr.Call -> {
            val f = funcs[expr.f]
            if (f != null) eval(f.expr, funcs, expr.params.map { eval(it, funcs, args) }) else Value.Null
        }
        is Expr.Words -> when (val s = eval(expr.x, funcs, args)) {
            is Value.Str -> Value.List(words(s.s).map { Value.Str(it) }.toMutableList())
            else -> Value.Null
        }
        is Expr.Litr -> when (val s = eval(expr.x, funcs, args)) {
            is Value.Str -> Value.of(s.s).getOrDefault(Value.Null)
            else -> Value.Null
        }
        is Expr.Input -> input(eval(expr.x, funcs, args).toString())
        is Expr.Print -> {
            val v = eval(expr.x, funcs, args)
            println(v.toString())
            v
        }
        is Expr.Str -> Value.Str(eval(expr.x, funcs, args).toString())
        is Expr.Value -> expr.v
        is Expr.Local -> args.getOrElse(expr.idx) { Value.Null }
    }
}

fun parseExpr(tokens: Iterator<Token>, args: List<String>, funcDefs: Map<String, Int>): Result<Expr> {
    return if (tokens.hasNext())
        Result.success(
            when (val v = tokens.next()) {
                is Token.If -> Expr.If(
                    parseExpr(tokens, args, funcDefs).getOrThrow(),
                    parseExpr(tokens, args, funcDefs).getOrThrow(),
                    parseExpr(tokens, args, funcDefs).getOrThrow()
                )
                is Token.Head -> Expr.Head(parseExpr(tokens, args, funcDefs).getOrThrow())
                is Token.Tail -> Expr.Tail(parseExpr(tokens, args, funcDefs).getOrThrow())
                is Token.Fuse -> Expr.Fuse(
                    parseExpr(tokens, args, funcDefs).getOrThrow(),
                    parseExpr(tokens, args, funcDefs).getOrThrow()
                )
                is Token.Pair -> Expr.Pair(
                    parseExpr(tokens, args, funcDefs).getOrThrow(),
                    parseExpr(tokens, args, funcDefs).getOrThrow()
                )
                is Token.Litr -> Expr.Litr(parseExpr(tokens, args, funcDefs).getOrThrow())
                is Token.Str -> Expr.Str(parseExpr(tokens, args, funcDefs).getOrThrow())
                is Token.Words -> Expr.Words(parseExpr(tokens, args, funcDefs).getOrThrow())
                is Token.Input -> Expr.Input(parseExpr(tokens, args, funcDefs).getOrThrow())
                is Token.Print -> Expr.Print(parseExpr(tokens, args, funcDefs).getOrThrow())

                is Token.Value -> Expr.Value(v.v)

                is Token.Eq -> Expr.Eq(
                    parseExpr(tokens, args, funcDefs).getOrThrow(),
                    parseExpr(tokens, args, funcDefs).getOrThrow()
                )
                is Token.Add -> Expr.Add(
                    parseExpr(tokens, args, funcDefs).getOrThrow(),
                    parseExpr(tokens, args, funcDefs).getOrThrow()
                )
                is Token.Neg -> Expr.Neg(parseExpr(tokens, args, funcDefs).getOrThrow())
                is Token.Mul -> Expr.Mul(
                    parseExpr(tokens, args, funcDefs).getOrThrow(),
                    parseExpr(tokens, args, funcDefs).getOrThrow()
                )
                is Token.Div -> Expr.Div(
                    parseExpr(tokens, args, funcDefs).getOrThrow(),
                    parseExpr(tokens, args, funcDefs).getOrThrow()
                )
                is Token.Rem -> Expr.Rem(
                    parseExpr(tokens, args, funcDefs).getOrThrow(),
                    parseExpr(tokens, args, funcDefs).getOrThrow()
                )
                is Token.Less -> Expr.Less(
                    parseExpr(tokens, args, funcDefs).getOrThrow(),
                    parseExpr(tokens, args, funcDefs).getOrThrow()
                )
                is Token.LessEq -> Expr.LessEq(
                    parseExpr(tokens, args, funcDefs).getOrThrow(),
                    parseExpr(tokens, args, funcDefs).getOrThrow()
                )

                is Token.Ident -> {
                    var idx = -1
                    for ((index, e) in args.withIndex()) {
                        if (v.i == e) {
                            idx = index
                            break
                        }
                    }

                    when {
                        idx >= 0 -> Expr.Local(idx)
                        funcDefs[v.i] != null -> {
                            val fArgs = funcDefs[v.i]!!
                            val params = mutableListOf<Expr>()
                            for (q in 0..fArgs) {
                                params.add(parseExpr(tokens, args, funcDefs).getOrThrow())
                            }
                            Expr.Call(v.i, params)
                        }
                        else -> return Result.failure(ParseError.CannotFind(v.i))
                    }
                }

                else -> return Result.failure(ParseError.Unexpected(v))
            }
        )
    else Result.failure(ParseError.ExpectedToken)
}

fun parseFuncs(tokens: Iterator<Token>): Result<Map<String, Func>> {
    val funcs = hashMapOf<String, Func>()
    val funcDefs = hashMapOf<String, Int>()

    /*
    By end, funcs is always empty, and funcDefs looks like:
    {
        "main": 0,
        "print": 1,
        "+": 2,
    }

    let print x = <body>
    let + x y = <body>
    let main = <body>
    */

    var n = -1
    var t = 0
    for (tok in tokens) {
        when (tok) {
            is Token.Fn -> { n = 0 }
            is Token.Ident -> {
                funcDefs[tok.i] = n
            }
            is Token.Is  -> { n = -1 }

            else -> if (n >= 0) {

            }
        }
    }


    while (true) {
        when (tokens.next()) {
            is Token.Fn -> {
            }
            else -> return Result.success(funcs)
        }

        val name = when (val s = tokens.next()) {
            is Token.Ident -> s.i
            else -> return Result.failure(ParseError.Expected(Token.Fn))
        }

        val args = mutableListOf<String>()
        while (true) {
            when (val s = tokens.next()) {
                is Token.Ident -> args.add(s.i)
                is Token.Is -> break
                else -> return Result.failure(ParseError.Expected(Token.Is))
            }
        }

        funcDefs[name] = args.size
        funcs[name] = Func(args, parseExpr(tokens, args, funcDefs).getOrThrow())
    }
}
