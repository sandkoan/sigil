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

fun prompt() {
    println("Welcom to the Sigil prompt.")
    println("The Prelude is imported by default.")
    System.out.flush()

    var line = readLine()
    // FIXME: I'm 90% sure this doesn't work
    while (true) {
        run {
            val tokens = lex(withPrelude(line!!))
            parseFuncs(tokens.listIterator()).map { funcs ->
                funcs["main"]?.let { eval(it.expr, funcs, mutableListOf()) } ?: Value.Null
            }.also {
                parseExpr(tokens.listIterator(), arrayListOf(), hashMapOf()).map { expr ->
                    { eval(expr, hashMapOf(), arrayListOf()) }
                }
            }
        }
            .mapCatching { println(it.toString()) }
            .getOrThrow()

        line = readLine()
    }
}

fun exec(fname: String) {
    val code  = File(fname).let {
        if (it.canRead())
            it.readText(Charsets.UTF_8)
        else
            throw FileNotFoundException("Could not open file '$fname'")
    }

    // TODO: Should be val x = parseFuncs ??
    run {
        parseFuncs(lex(withPrelude(code)).listIterator()).map { funcs ->
            funcs["main"]?.let { eval(it.expr, funcs, mutableListOf()) } ?: Value.Null
        }
    }.getOrThrow()
}
