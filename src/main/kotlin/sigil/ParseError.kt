package sigil

sealed class ParseError {
    data class Expected(val tok: Token) : Error()
    object ExpectedToken : Error()
    data class Unexpected(val tok: Token) : Error()
    data class CannotFind(val s: String) : Error()
}