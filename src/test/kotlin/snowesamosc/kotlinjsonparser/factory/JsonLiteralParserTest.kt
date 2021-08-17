package snowesamosc.kotlinjsonparser.factory

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import snowesamosc.kotlinjsonparser.JsonException
import kotlin.test.assertEquals
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
                ]
            }
        """.trimIndent()

        val result1 = assertDoesNotThrow { parser.parse(str1) }

        assertEquals(42, result1.get("universe").asInt())
        assertEquals("42", result1.get("obj").get("universe_in_obj").asText())
        assertThrows<JsonException> { result1.get("obj").get("universe_in_cat") }
        assertTrue { result1.get("obj").find("universe_in_cat").isMissing() }
        val array = assertDoesNotThrow { result1.get("array").asArray() }
        assertEquals("universe_in_array", array[0].asText())
        assertEquals(42, array[1].asInt())
    }
}