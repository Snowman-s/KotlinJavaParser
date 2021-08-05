package snowesamosc.kotlinjsonparser.factory

import org.junit.jupiter.api.Test
import kotlin.test.*

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

    @Test
    internal fun falseCreateTest() {
        val result = JsonLiteralImpl.False.greedyCreate("false")

        assertNotNull(result.literal)
        assertEquals("false", result.literal.asString())
        assertEquals("", result.remainString)

        val result2 = JsonLiteralImpl.False.greedyCreate(" false")

        assertNull(result2.literal)
        assertEquals(" false", result2.remainString)

        val result3 = JsonLiteralImpl.False.greedyCreate("falsee")

        assertNotNull(result3.literal)
        assertEquals("false", result3.literal.asString())
        assertEquals("e", result3.remainString)
    }

    @Test
    internal fun nullCreateTest() {
        val result = JsonLiteralImpl.Null.greedyCreate("null")

        assertNotNull(result.literal)
        assertEquals("null", result.literal.asString())
        assertEquals("", result.remainString)

        val result2 = JsonLiteralImpl.Null.greedyCreate(" null")

        assertNull(result2.literal)
        assertEquals(" null", result2.remainString)

        val result3 = JsonLiteralImpl.Null.greedyCreate("nulll")

        assertNotNull(result3.literal)
        assertEquals("null", result3.literal.asString())
        assertEquals("l", result3.remainString)
    }

    @Test
    internal fun trueCreateTest() {
        val result = JsonLiteralImpl.True.greedyCreate("true")

        assertNotNull(result.literal)
        assertEquals("true", result.literal.asString())
        assertEquals("", result.remainString)

        val result2 = JsonLiteralImpl.True.greedyCreate(" true")

        assertNull(result2.literal)
        assertEquals(" true", result2.remainString)

        val result3 = JsonLiteralImpl.True.greedyCreate("truee")

        assertNotNull(result3.literal)
        assertEquals("true", result3.literal.asString())
        assertEquals("e", result3.remainString)
    }

    @Test
    internal fun decimalPointCreateTest() {
        val result = JsonLiteralImpl.DecimalPoint.greedyCreate(".")

        assertNotNull(result.literal)
        assertEquals(".", result.literal.asString())
        assertEquals("", result.remainString)

        val result2 = JsonLiteralImpl.DecimalPoint.greedyCreate(" .")

        assertNull(result2.literal)
        assertEquals(" .", result2.remainString)

        val result3 = JsonLiteralImpl.DecimalPoint.greedyCreate("...")

        assertNotNull(result3.literal)
        assertEquals(".", result3.literal.asString())
        assertEquals("..", result3.remainString)
    }

    @Test
    internal fun minusCreateTest() {
        val result = JsonLiteralImpl.Minus.greedyCreate("-")

        assertNotNull(result.literal)
        assertEquals("-", result.literal.asString())
        assertEquals("", result.remainString)

        val result2 = JsonLiteralImpl.Minus.greedyCreate(" -")

        assertNull(result2.literal)
        assertEquals(" -", result2.remainString)

        val result3 = JsonLiteralImpl.Minus.greedyCreate("---")

        assertNotNull(result3.literal)
        assertEquals("-", result3.literal.asString())
        assertEquals("--", result3.remainString)
    }

    @Test
    internal fun plusCreateTest() {
        val result = JsonLiteralImpl.Plus.greedyCreate("+")

        assertNotNull(result.literal)
        assertEquals("+", result.literal.asString())
        assertEquals("", result.remainString)

        val result2 = JsonLiteralImpl.Plus.greedyCreate(" +")

        assertNull(result2.literal)
        assertEquals(" +", result2.remainString)

        val result3 = JsonLiteralImpl.Plus.greedyCreate("+++")

        assertNotNull(result3.literal)
        assertEquals("+", result3.literal.asString())
        assertEquals("++", result3.remainString)
    }

    @Test
    internal fun zeroCreateTest() {
        val result = JsonLiteralImpl.Zero.greedyCreate("0")

        assertNotNull(result.literal)
        assertEquals("0", result.literal.asString())
        assertEquals("", result.remainString)

        val result2 = JsonLiteralImpl.Plus.greedyCreate(" 0")

        assertNull(result2.literal)
        assertEquals(" 0", result2.remainString)

        val result3 = JsonLiteralImpl.Zero.greedyCreate("000")

        assertNotNull(result3.literal)
        assertEquals("0", result3.literal.asString())
        assertEquals("00", result3.remainString)
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