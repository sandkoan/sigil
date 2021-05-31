import sigil.*
import java.io.File
import java.io.FileNotFoundException

fun main(args: Array<String>) {
    when {
        args.isEmpty() -> prompt()
        args.size == 1 -> exec(args[0])
        else -> usage()
    }
}

fun usage() = println("Usage: sigil [file]")

/*
* For on the fly evaluation of code, a REPL. Unfortunately, it is quite tempermental,
* and thus needs some work to make it more ergonomic and viable.
* */
fun prompt() {
    println("Welcome to the Sigil prompt.")
    println("The Prelude is imported by default.")
    System.out.flush()

    var line = readLine()

    /*
    * Lex input from the user, convert to iterator, and pass to parseFuncs(),
    * which generates a Map with the name of each function, and the Expr
    * the function evaluates to. If a main function exists, evaluate it, else do nothing.
    * Additionally, evaluate the main function assuming that no previous function was defined
    * (a clean state).
    */
    while (true) {
        run {
            val tokens = lex(
//                withPrelude(
                    line!!
//                )
            )
            parseFuncs(tokens.iterator()).map { funcs ->
                funcs["main"]?.let { eval(it.expr, funcs, mutableListOf()) } ?: Value.Null
            }.also {
                parseExpr(tokens.iterator(), mutableListOf(), hashMapOf()).map { expr ->
                    { eval(expr, hashMapOf(), mutableListOf()) }
                }
            }
        }
            .mapCatching { println(it.toString()) }
            .getOrThrow()

        line = readLine()
    }
}

/*
* Execute code from a file - other than that, mostly like the prompt.
* */
fun exec(fname: String) {
    val code = File(fname).let {
        if (it.canRead())
            it.readText(Charsets.UTF_8)
        else
            throw FileNotFoundException("Could not open file '$fname'")
    }

    /*
    * Lex input from the user, convert to iterator, and pass to parseFuncs(),
    * which generates a Map with the name of each function, and the Expr
    * the function evaluates to. If a main function exists, evaluate it, else do nothing.
    * */
    run {
        parseFuncs(lex(
//            withPrelude(
            code
//            )
        ).iterator()).map { funcs ->
            funcs["main"]?.let { eval(it.expr, funcs, mutableListOf()) } ?: Value.Null
        }
    }.getOrThrow()
}
