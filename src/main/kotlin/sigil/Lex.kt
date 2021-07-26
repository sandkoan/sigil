package sigil

import exec
import java.io.File
import java.io.FileNotFoundException

/*
* Splits input text by whitespace, while preserving whitespace within "strings."
* */
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

/*
* Function that maps the split words from `words` to tokens (either native identifiers, or nonnative).
* */
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
        .also { println(it) }
}

fun main() {
    val fname = "C:\\Users\\govin\\IdeaProjects\\sigil\\src\\main\\resources\\sigil\\hello.sig"
    val code = File(fname).let {
        if (it.canRead())
            it.readText(Charsets.UTF_8)
        else
            throw FileNotFoundException("Could not open file `$fname`")
    }
    lex(code)
}
