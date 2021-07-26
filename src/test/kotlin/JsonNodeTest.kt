import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import snowesamosc.kotlinjsonparser.node.JsonNode
import snowesamosc.kotlinjsonparser.JsonValueNotFoundException
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class JsonNodeTest {
    @Test
    internal fun valueCheckTest() {
        val missingNode = JsonNode.MissingNode

        assertFalse { missingNode.isArray() }
        assertFalse { missingNode.isNumber() }
        assertFalse { missingNode.isInt() }
        assertFalse { missingNode.isFraction() }
        assertFalse { missingNode.isBoolean() }
        assertFalse { missingNode.isText() }
        assertFalse { missingNode.isArray() }
        assertFalse { missingNode.isNull() }
    }

    @Test
    internal fun valueTest() {
        val missingNode = JsonNode.MissingNode

        assertTrue(missingNode.asArray().isEmpty())
        assertEquals("", missingNode.asText())
        assertEquals(0, missingNode.asInt())
        assertEquals(0.toDouble(), missingNode.asDouble())
        assertEquals(false, missingNode.asBoolean())
    }

    @Test
    internal fun childTest() {
        val missingNode = JsonNode.MissingNode

        assertThrows<JsonValueNotFoundException> { missingNode.get("hoge") }
        assertEquals(JsonNode.MissingNode, missingNode.find("fuga"))
    }

    @Test
    internal fun missingTest() {
        val missingNode = JsonNode.MissingNode

        assertTrue { missingNode.isMissing() }
    }
}