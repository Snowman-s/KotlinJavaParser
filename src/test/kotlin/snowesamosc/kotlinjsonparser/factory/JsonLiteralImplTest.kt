package snowesamosc.kotlinjsonparser.factory

import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class JsonLiteralImplTest {
    @Test
    internal fun wsCreateTest() {
        val result = JsonLiteralImpl.WS.greedyCreate("")

        assertNotNull(result.literal)
        assertEquals("", result.literal.asString())
        assertEquals("", result.remainString)

        val result2 = JsonLiteralImpl.WS.greedyCreate(" \t\n ")

        assertNotNull(result2.literal)
        assertEquals(" \t\n ", result2.literal.asString())
        assertEquals("", result2.remainString)

        val result3 = JsonLiteralImpl.WS.greedyCreate(" \tHoge ")

        assertNotNull(result3.literal)
        assertEquals(" \t", result3.literal.asString())
        assertEquals("Hoge ", result3.remainString)
    }

    @Test
    internal fun beginArrayCreateTest() {
        val result = JsonLiteralImpl.BeginArray.greedyCreate("[")

        assertNotNull(result.literal)
        assertTrue { result.literal.hasChildren() }
        assertContentEquals(listOf("", "[", ""), result.literal.getChildren().map { jsonLiteral -> jsonLiteral.asString() })
        assertEquals("", result.remainString)

        val result2 = JsonLiteralImpl.BeginArray.greedyCreate(" \t\n[ \n")

        assertNotNull(result2.literal)
        assertContentEquals(listOf(" \t\n", "[", " \n"),
            result2.literal.getChildren().map { jsonLiteral -> jsonLiteral.asString() })
        assertEquals("", result2.remainString)

        val result3 = JsonLiteralImpl.BeginArray.greedyCreate(" \t[Hoge [\n ")
        assertNotNull(result3.literal)
        assertContentEquals(listOf(" \t", "[", ""),
            result3.literal.getChildren().map { jsonLiteral -> jsonLiteral.asString() })
        assertEquals("Hoge [\n ", result3.remainString)
    }
}