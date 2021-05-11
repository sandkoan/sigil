sealed class Error {
    data class Expected(val tok: Token) : Error()
    object ExpectedToken : Error()
    data class Unexpected(val tok: Token) : Error()
    data class CannotFind(val s: String) : Error()
}

class interpreter {
}