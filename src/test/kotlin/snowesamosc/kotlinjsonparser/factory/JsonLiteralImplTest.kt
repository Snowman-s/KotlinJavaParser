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

        val result2 = JsonLiteralImpl.Zero.greedyCreate(" 0")

        assertNull(result2.literal)
        assertEquals(" 0", result2.remainString)

        val result3 = JsonLiteralImpl.Zero.greedyCreate("000")

        assertNotNull(result3.literal)
        assertEquals("0", result3.literal.asString())
        assertEquals("00", result3.remainString)
    }

    @Test
    internal fun abnfDigitCreateTest() {
        val result = JsonLiteralImpl.ABNFDigit.greedyCreate("5")

        assertNotNull(result.literal)
        assertEquals("5", result.literal.asString())
        assertEquals("", result.remainString)

        val result2 = JsonLiteralImpl.ABNFDigit.greedyCreate(" 9")

        assertNull(result2.literal)
        assertEquals(" 9", result2.remainString)

        val result3 = JsonLiteralImpl.ABNFDigit.greedyCreate("842")

        assertNotNull(result3.literal)
        assertEquals("8", result3.literal.asString())
        assertEquals("42", result3.remainString)

        val result4 = JsonLiteralImpl.ABNFDigit.greedyCreate("0")

        assertNotNull(result4.literal)
        assertEquals("0", result4.literal.asString())
    }

    @Test
    internal fun digit19CreateTest() {
        val result = JsonLiteralImpl.Digit19.greedyCreate("5")

        assertNotNull(result.literal)
        assertEquals("5", result.literal.asString())
        assertEquals("", result.remainString)

        val result2 = JsonLiteralImpl.Digit19.greedyCreate(" 9")

        assertNull(result2.literal)
        assertEquals(" 9", result2.remainString)

        val result3 = JsonLiteralImpl.Digit19.greedyCreate("842")

        assertNotNull(result3.literal)
        assertEquals("8", result3.literal.asString())
        assertEquals("42", result3.remainString)

        val result4 = JsonLiteralImpl.Digit19.greedyCreate("0")

        assertNull(result4.literal)
        assertEquals("0", result4.remainString)
    }

    @Test
    internal fun eCreateTest() {
        val result = JsonLiteralImpl.E.greedyCreate("e")

        assertNotNull(result.literal)
        assertEquals("e", result.literal.asString())
        assertEquals("", result.remainString)

        val result2 = JsonLiteralImpl.E.greedyCreate(" e")

        assertNull(result2.literal)
        assertEquals(" e", result2.remainString)

        val result3 = JsonLiteralImpl.E.greedyCreate("eee")

        assertNotNull(result3.literal)
        assertEquals("e", result3.literal.asString())
        assertEquals("ee", result3.remainString)

        val result4 = JsonLiteralImpl.E.greedyCreate("f")

        assertNull(result4.literal)
        assertEquals("f", result4.remainString)
    }

    @Test
    internal fun expCreateTest() {
        val result = JsonLiteralImpl.Exp.greedyCreate("e80")

        assertNotNull(result.literal)
        assertContentEquals(
            listOf("e", "8", "0"),
            result.literal.getChildren().map { jsonLiteral -> jsonLiteral.asString() })
        assertEquals("", result.remainString)

        val result2 = JsonLiteralImpl.Exp.greedyCreate("E09e")

        assertNotNull(result2.literal)
        assertContentEquals(
            listOf("E", "0", "9"),
            result2.literal.getChildren().map { jsonLiteral -> jsonLiteral.asString() })
        assertEquals("e", result2.remainString)

        val result3 = JsonLiteralImpl.Exp.greedyCreate("E-23-")

        assertNotNull(result3.literal)
        assertContentEquals(
            listOf("E", "-", "2", "3"),
            result3.literal.getChildren().map { jsonLiteral -> jsonLiteral.asString() })
        assertEquals("-", result3.remainString)

        val result4 = JsonLiteralImpl.Exp.greedyCreate("E+24 ")

        assertNotNull(result4.literal)
        assertContentEquals(
            listOf("E", "+", "2", "4"),
            result4.literal.getChildren().map { jsonLiteral -> jsonLiteral.asString() })
        assertEquals(" ", result4.remainString)

        val result5 = JsonLiteralImpl.Exp.greedyCreate("E-+24")

        assertNull(result5.literal)
        assertEquals("E-+24", result5.remainString)
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