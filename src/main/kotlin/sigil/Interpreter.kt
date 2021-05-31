package sigil

import java.io.File

/*
* Function class with list of arguments, and the expression that the function evaluates to.
* */
data class Func(val args: List<String>, val expr: Expr)

/*
* Prepends code from "core" library to user's source code - adds some useful utlity / wrapper functions.
* */
fun withPrelude(code: String): String = File("sigil/core.sig").readText(Charsets.UTF_8) + code

/*
* Takes input from the user, optionally, prompting them with a message.
* */
fun input(msg: String): Value {
    println(msg)
    System.out.flush()
    return Value.Str(readLine()!!.trim())
}

/*
* Recursively expands expression into subatomic expressions in a tree like format.
* */
fun parseExpr(tokens: Iterator<Token>, args: List<String>, funcDefs: Map<String, Int>): Result<Expr> {
    return if (tokens.hasNext())
        Result.success(
            when (val v = tokens.next()) {
                is Token.If -> {
                    val x = parseExpr(tokens, args, funcDefs).getOrThrow()
//                    println(tokens.hasNext())
                    val y = parseExpr(tokens, args, funcDefs).getOrThrow()
//                    println(tokens.hasNext())
                    val z = parseExpr(tokens, args, funcDefs).getOrThrow()
//                    println(tokens.hasNext())

                    Expr.If(
                        x,
                        y,
                        z
                    )
                }
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
                    // Check if identifier is an argument to function
                    var idx = -1
                    for ((index, arg) in args.withIndex()) {
                        if (v.i == arg) {
                            idx = index
                            break
                        }
                    }

                    when {
                        idx >= 0 -> Expr.Local(idx)
                        funcDefs[v.i] != null -> {
                            val fArgs = funcDefs[v.i]!!
                            val params = mutableListOf<Expr>()
                            // Add evaluated arguments to params list
                            for (q in 0..fArgs) {
                                if (tokens.hasNext())
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

/*
* Sigil uses Polish notation, so the compiler needs to know the arity of every function in a Map.
* Then converts function body into an evaluable Expr using `parseExpr`, and adds thee expression to a list,
* to be evaluated at a later time.
* */
fun parseFuncs(tokens: Iterator<Token>): Result<Map<String, Func>> {
    val funcs = hashMapOf<String, Func>()
    val funcDefs = hashMapOf<String, Int>()

    // Populate funcDefs with the arity of every function defined
    val ids = mutableListOf<String>()
    val l = mutableListOf<Token>()

    var inDec = false
    for (tok in tokens) {
        l.add(tok)
        when (tok) {
            is Token.Fn -> inDec = true
            is Token.Ident -> if (inDec) ids.add(tok.i)
            is Token.Is -> {
                funcDefs[ids[0]] = (ids.size - 1).coerceAtLeast(0)
                inDec = false
                ids.clear()
            }
            else -> ids.clear()
        }
    }

    val tokens = l.iterator()

    // Use funcDefs to expand function body to Expr.
    while (true) {
        if (tokens.hasNext()) {
            when (tokens.next()) {
                is Token.Fn -> {
                }
                else -> return Result.success(funcs)
            }
        } else return Result.success(funcs)

        val name = if (tokens.hasNext()) {
            when (val s = tokens.next()) {
                is Token.Ident -> s.i
                else -> return Result.failure(ParseError.Expected(Token.Fn))
            }
        } else {
            return Result.failure(ParseError.Expected(Token.Fn))
        }

        val args = mutableListOf<String>()
        while (true) {
            if (tokens.hasNext()) {
                when (val s = tokens.next()) {
                    is Token.Ident -> args.add(s.i)
                    is Token.Is -> break
                    else -> return Result.failure(ParseError.Expected(Token.Is))
                }
            } else {
                return Result.failure(ParseError.Expected(Token.Is))
            }
        }

        funcDefs[name] = args.size
        funcs[name] = Func(args, parseExpr(tokens, args, funcDefs).getOrThrow())
        println(funcs.prettyPrint())
    }
}

fun main() {
    val s = """
        let print x =
            __print x

        let main =
            print "Hello World"
    """.trimIndent()

    println(parseFuncs(lex(s).iterator()).getOrThrow().prettyPrint())
}