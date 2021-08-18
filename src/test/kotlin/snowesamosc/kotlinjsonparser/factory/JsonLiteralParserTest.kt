package snowesamosc.kotlinjsonparser.factory

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import snowesamosc.kotlinjsonparser.JsonException
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class JsonLiteralParserTest {
    lateinit var parser: JsonLiteralParser

    @BeforeEach
    internal fun setUp() {
        parser = JsonLiteralParser()
    }

    @Test
    fun parse() {
        val str1 = """
            {
                "universe": 42,
                "obj": {
                    "universe_in_obj": "42"
                },
                "array":[
                    "universe_in_array",
                    42
                ],
                "overlap":[
                    "universe_dispose"
                ],
                "overlap":"universe_active"
            }
        """.trimIndent()

        val result1 = assertDoesNotThrow { parser.parse(str1) }

        assertEquals(42, result1.get("universe").asInt())
        assertEquals("42", result1.get("obj").get("universe_in_obj").asText())
        assertThrows<JsonException> { result1.get("obj").get("universe_in_cat") }
        assertTrue { result1.get("obj").find("universe_in_cat").isMissing() }
        val result1Array = assertDoesNotThrow { result1.get("array").asArray() }
        assertEquals("universe_in_array", result1Array[0].asText())
        assertEquals(42, result1Array[1].asInt())
        assertFalse(result1.get("overlap").isArray())
        assertEquals("universe_active", result1.get("overlap").asText())

        val str2 = """
            42
        """.trimIndent()

        val result2 = assertDoesNotThrow { parser.parse(str2) }
        assertEquals(42, result2.asInt())

        val str3 = """
            042
        """.trimIndent()

        assertThrows<JsonException> { parser.parse(str3) }

        val str4 = """
            [
                42e0, 42.0, 42.0e0
            ]
        """.trimIndent()

        val result4 = assertDoesNotThrow { parser.parse(str4) }
        result4.asArray().forEach {
            assertFalse { it.isInt() }
            assertThrows<JsonException> { it.asInt() }
        }
        assertTrue { (result4.asArray().map { it.asNumber() }).distinct().count() == 1 }
    }
}