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
        oneCharLiteralTest('[') { JsonLiteralImpl.BeginArray.greedyCreate(it) }
    }

    @Test
    internal fun beginObjectCreateTest() {
        oneCharLiteralTest('{') { JsonLiteralImpl.BeginObject.greedyCreate(it) }
    }

    @Test
    internal fun endArrayCreateTest() {
        oneCharLiteralTest(']') { JsonLiteralImpl.EndArray.greedyCreate(it) }
    }

    @Test
    internal fun endObjectCreateTest() {
        oneCharLiteralTest('}') { JsonLiteralImpl.EndObject.greedyCreate(it) }
    }

    @Test
    internal fun nameSeparatorCreateTest() {
        oneCharLiteralTest(':') { JsonLiteralImpl.NameSeparator.greedyCreate(it) }
    }

    @Test
    internal fun valueSeparatorCreateTest() {
        oneCharLiteralTest(',') { JsonLiteralImpl.ValueSeparator.greedyCreate(it) }
    }

    private fun literalTest(
        literalCreator: (String) -> GreedyCreateResult,
        originalString: String,
        childrenStrList: List<String>,
        remain: String
    ) {
        val result = literalCreator(originalString)
        assertNotNull(result.literal)
        assertTrue { result.literal.hasChildren() }
        assertContentEquals(childrenStrList,
            result.literal.getChildren().map { jsonLiteral -> jsonLiteral.asString() })
        assertEquals(remain, result.remainString)
    }

    private fun oneCharLiteralTest(
        theChar: Char,
        literalCreator: (String) -> GreedyCreateResult
    ) {
        literalTest(literalCreator, theChar.toString(), listOf("", theChar.toString(), ""), "")
        literalTest(literalCreator, " \t\n$theChar \n", listOf(" \t\n", theChar.toString(), " \n"), "")
        literalTest(literalCreator, " \t" + theChar + "Hoge [\n ", listOf(" \t", theChar.toString(), ""), "Hoge [\n ")
    }
}