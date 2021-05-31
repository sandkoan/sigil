package sigil

/*
* Custom error class that makes it easier to handle / comprehend Sigil's compiler errors.
* */
sealed class ParseError {
    data class Expected(val tok: Token) : Error()
    object ExpectedToken : Error()
    data class Unexpected(val tok: Token) : Error()
    data class CannotFind(val s: String) : Error()
}

/*
* Simple utility method to pretty print the data structures used in the interpreter.
* */
fun Any.prettyPrint(): String {
    var indentLevel = 0
    val indentWidth = 4

    fun padding() = "".padStart(indentLevel * indentWidth)

    val toString = toString()

    val sb = StringBuilder(toString.length)

    var i = 0
    while (i < toString.length) {
        when (val char = toString[i]) {
            '(', '[', '{' -> {
                indentLevel++
                sb.appendLine(char).append(padding())
            }
            ')', ']', '}' -> {
                indentLevel--
                sb.appendLine().append(padding()).append(char)
            }
            ',' -> {
                sb.appendLine(char).append(padding())
                // Ignore space after comma as we have added a newline
                val nextChar = toString.getOrElse(i + 1) { char }
                if (nextChar == ' ') i++
            }
            else -> {
                sb.append(char)
            }
        }
        i++
    }

    return sb.toString()
}
