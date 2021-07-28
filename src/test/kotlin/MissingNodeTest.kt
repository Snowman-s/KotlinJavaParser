import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import snowesamosc.kotlinjsonparser.JsonException
import snowesamosc.kotlinjsonparser.node.MissingNode
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class MissingNodeTest {
    @Test
    internal fun valueCheckTest() {
        val missingNode = MissingNode

        assertFalse { missingNode.isArray() }
        assertFalse { missingNode.isNumber() }
        assertFalse { missingNode.isInt() }
        assertFalse { missingNode.isBoolean() }
        assertFalse { missingNode.isText() }
        assertFalse { missingNode.isArray() }
        assertFalse { missingNode.isNull() }
    }

    @Test
    internal fun valueTest() {
        val missingNode = MissingNode

        assertTrue(missingNode.asArray().isEmpty())
        assertEquals("", missingNode.asText())
        assertEquals(0, missingNode.asInt())
        assertEquals(0.toDouble(), missingNode.asDouble())
        assertEquals(false, missingNode.asBoolean())
    }

    @Test
    internal fun childTest() {
        val missingNode = MissingNode

        assertThrows<JsonException> { missingNode.get("hoge") }
        assertEquals(MissingNode, missingNode.find("fuga"))
    }

    @Test
    internal fun missingTest() {
        val missingNode = MissingNode

        assertTrue { missingNode.isMissing() }
    }
}