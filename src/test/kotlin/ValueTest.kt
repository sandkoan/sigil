import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ValueTest {
    @Test
    fun `test float toValue and toString`() {
        val v = Value.of("-466.00349")
        assertEquals(v.getOrThrow(), Value.Num(-466.00349))
        assertEquals(v.getOrThrow().toString(), "-466.00349")
    }

    @Test
    fun `test int toValue and toString`() {
        val v = Value.of("823475")
        assertEquals(v.getOrThrow(), Value.Num(823475.0))
        assertEquals(v.getOrThrow().toString(), "823475.0")
    }

    @Test
    fun `test string toValue and toString`() {
        val v = Value.of("\"Hello World\"")
//        assertEquals(v.getOrThrow(), Value.Str("Hello World\""))
        assertEquals(v.getOrThrow(), Value.Str("Hello World"))
        assertEquals(v.getOrThrow().toString(), "Hello World")
    }

    @Test
    fun `test bool toValue and toString`() {
        val t = Value.of("true")
        assertEquals(t.getOrThrow(), Value.Bool(true))
        assertEquals(t.getOrThrow().toString(), "true")

        val f = Value.of("false")
        assertEquals(f.getOrThrow(), Value.Bool(false))
        assertEquals(f.getOrThrow().toString(), "false")
    }

    @Test
    fun `test list toString`() {
        val v = Value.List(arrayListOf(
            Value.Num(334.0),
            Value.Num(-4545.0034),
            Value.Str("34545"),
            Value.Bool(false)
        ))
        assertEquals(v.items, arrayListOf(
            Value.Num(334.0),
            Value.Num(-4545.0034),
            Value.Str("34545"),
            Value.Bool(false)
        ))
    }

    @Test
    fun `test null toValue toString`() {
        val v = Value.of("null")
        assertEquals(v.getOrThrow(), Value.Null)
        assertEquals(v.getOrThrow().toString(), "null")
    }
}