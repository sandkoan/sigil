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
    var line = readLine()
    while (line != null) {
        val x = {
            val tokens = lex(withPrelude(line!!))
            parseFuncs(tokens.iterator()).map { funcs ->
                funcs["main"]?.let { eval(it.expr, funcs, mutableListOf()) } ?: Value.Null
            }.also {
                parseExpr(tokens.iterator(), arrayListOf(), hashMapOf()).map { expr ->
                    eval(expr, hashMapOf(), arrayListOf())
                }
            }
        }
        line = readLine()
    }
    TODO()
}

fun exec(fname: String) {
    val f = File(fname)
    val code = if (f.canRead())
        f.readText(Charsets.UTF_8)
    else
        throw FileNotFoundException("Could not open file '$fname'")

    val x = parseFuncs(lex(withPrelude(code)).iterator()).map { funcs ->
        funcs["main"]?.let { eval(it.expr, funcs, mutableListOf()) } ?: Value.Null
    }.getOrThrow()
}
