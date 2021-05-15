import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class InterpreterKtTest {
    @Test
    fun `test words newline and tab`() {
        val s = "fn  \tmain is govind; = ! f  \n"
        assertEquals(
            arrayListOf(
                "fn",
                "main",
                "is",
                "govind;",
                "=",
                "!",
                "f"
            ), words(s)
        )
    }

    @Test
    fun `test words multiple string args`() {
        val s = "fn main \"  waster\" + \"govind\" "
        assertEquals(
            arrayListOf(
                "fn",
                "main",
                "\"  waster\"",
                "+",
                "\"govind\"",
            ), words(s)
        )
    }
}