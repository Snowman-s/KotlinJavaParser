package snowesamosc.kotlinjsonparser.node

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import snowesamosc.kotlinjsonparser.JsonException
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class ArrayNodeTest {
    private lateinit var emptyArrayNode: ArrayNode

    @BeforeEach
    fun setUp() {
        emptyArrayNode = ArrayNode(emptyArray())
    }

    @Test
    fun isArray() = assertTrue { emptyArrayNode.isArray() }

    @Test
    fun asArray() = assertTrue { emptyArrayNode.asArray().isEmpty() }

    @Test
    fun isNumber() = assertFalse { emptyArrayNode.isNumber() }

    @Test
    fun isInt() = assertFalse { emptyArrayNode.isInt() }

    @Test
    fun asInt() {
        assertThrows<JsonException> { emptyArrayNode.asInt() }
    }

    @Test
    fun asDouble() {

    }

    @Test
    fun isBoolean() {
    }

    @Test
    fun asBoolean() {
    }

    @Test
    fun isText() {
    }

    @Test
    fun asText() {
    }

    @Test
    fun isNull() {
    }

    @Test
    fun get() {
    }

    @Test
    fun find() {
    }

    @Test
    fun isMissing() {
    }
}