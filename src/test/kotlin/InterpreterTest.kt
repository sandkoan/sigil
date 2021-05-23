import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class InterpreterKtTest {
    internal class WordsTest {
        @Test
        fun `test words newline and tab`() {
            val s = "let  \tmain = govind; = ! f  \n"
            assertEquals(
                arrayListOf(
                    "let",
                    "main",
                    "=",
                    "govind;",
                    "=",
                    "!",
                    "f"
                ),
                words(s)
            )
        }

        @Test
        fun `test words multiple string args`() {
            val s = "let main \"  waster\" + \"govind\" "
            assertEquals(
                arrayListOf(
                    "let",
                    "main",
                    "\"  waster\"",
                    "+",
                    "\"govind\"",
                ),
                words(s)
            )
        }
    }

    internal class LexTest {
        @Test
        fun `test const function`() {
            val s = """
                let main =
                    -5.3
            """.trimIndent()
            assertEquals(
                arrayListOf(
                    Token.Fn,
                    Token.Ident("main"),
                    Token.Is,
                    Token.Value(Value.Num(-5.3))
                ),
                lex(s)
            )
        }

        @Test
        fun `test zero arg function`() {
            val s = """
                let main =
                    null
            """.trimIndent()
            assertEquals(
                arrayListOf(
                    Token.Fn,
                    Token.Ident("main"),
                    Token.Is,
                    Token.Value(Value.Null)
                ),
                lex(s)
            )
        }

        @Test
        fun `test one arg function`() {
            val s = """
                let greet x =
                    print x
            """.trimIndent()
            assertEquals(
                arrayListOf(
                    Token.Fn,
                    Token.Ident("greet"),
                    Token.Ident("x"),
                    Token.Is,
                    Token.Ident("print"),
                    Token.Ident("x"),
                ),
                lex(s)
            )
        }

        @Test
        fun `test two arg function`() {
            val s = """
                let add x y =
                    __add x y
            """.trimIndent()
            assertEquals(
                arrayListOf(
                    Token.Fn,
                    Token.Ident("add"),
                    Token.Ident("x"),
                    Token.Ident("y"),
                    Token.Is,
                    Token.Add,
                    Token.Ident("x"),
                    Token.Ident("y")
                ),
                lex(s)
            )
        }

        @Test
        fun `test multiple functions`() {
            val s = """
                let greet x =
                    print x
                    
                let printMod x y =
                    greet __rem x y

                let main =
                    printMod 10 3
            """.trimIndent()
            assertEquals(
                arrayListOf(
                    Token.Fn,
                    Token.Ident("greet"),
                    Token.Ident("x"),
                    Token.Is,
                    Token.Ident("print"),
                    Token.Ident("x"),

                    Token.Fn,
                    Token.Ident("printMod"),
                    Token.Ident("x"),
                    Token.Ident("y"),
                    Token.Is,
                    Token.Ident("greet"),
                    Token.Rem,
                    Token.Ident("x"),
                    Token.Ident("y"),

                    Token.Fn,
                    Token.Ident("main"),
                    Token.Is,
                    Token.Ident("printMod"),
                    Token.Value(Value.Num(10.0)),
                    Token.Value(Value.Num(3.0))
                ),
                lex(s)
            )
        }
    }

    internal class ParseFuncsTest {
        @Test
        fun `test const function`() {
            val s = """
                let main =
                    -5.3
            """.trimIndent()
            assertEquals(
                hashMapOf("main" to 0),
                parseFuncs(lex(s).iterator()).getOrThrow().also { println(it) }
            )
        }

        @Test
        fun `test zero arg function`() {
            val s = """
                let main =
                    null
            """.trimIndent()
            assertEquals(
                hashMapOf("main" to 0),
                parseFuncs(lex(s).iterator()).getOrThrow().also { println(it) }
            )
        }

        @Test
        fun `test one arg function`() {
            val s = """
                let greet x =
                    __print x
            """.trimIndent()
            assertEquals(
                hashMapOf("greet" to 1),
                parseFuncs(lex(s).iterator()).getOrThrow().also { println(it) }
            )
        }

        @Test
        fun `test two arg function`() {
            val s = """
                let add x y =
                    __add x y
            """.trimIndent()
            assertEquals(
                hashMapOf("add" to 2),
                parseFuncs(lex(s).iterator()).getOrThrow().also { println(it) }
            )
        }

        @Test
        fun `test multiple functions`() {
            val s = """
                let greet x =
                    __print x
                    
                let printMod x y =
                    greet __rem x y

                let main =
                    printMod 10 3
            """.trimIndent()
            assertEquals(
                hashMapOf("greet" to 1, "printMod" to 2, "main" to 0),
                parseFuncs(lex(s).iterator()).getOrThrow().also { println(it) }
            )
        }
    }
}