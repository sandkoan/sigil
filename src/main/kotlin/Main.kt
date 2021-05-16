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

fun prompt(): Unit = TODO()

fun exec(fname: String) {
    val f = File(fname)
    val code = if (f.canRead())
        f.readText(Charsets.UTF_8)
    else
        throw FileNotFoundException("Could not open file '$fname'")

    val x = parseFuncs(lex(withCore(code)).iterator()).map { funcs ->
        funcs["main"]?.let { eval(it.expr, funcs, mutableListOf()) } ?: Value.Null
    }.getOrThrow()
}

