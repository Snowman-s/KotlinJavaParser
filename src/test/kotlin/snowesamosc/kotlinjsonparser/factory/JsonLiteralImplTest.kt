package snowesamosc.kotlinjsonparser.factory

import org.junit.jupiter.api.Test
import kotlin.test.*

internal class JsonLiteralImplTest {
    @Test
    internal fun jsonTextCreateTest() {
        val result = JsonLiteralImpl.JsonText.greedyCreate("    \n{\"Uni\":42}")

        assertNotNull(result.literal)
        assertContentEquals(
            listOf("    \n", "{\"Uni\":42}", ""),
            result.literal.getChildren().map { jsonLiteral -> jsonLiteral.asString() })
        assertEquals("", result.remainString)

        val result2 = JsonLiteralImpl.Value.greedyCreate(" 60s")

        assertNull(result2.literal)
        assertEquals(" 60s", result2.remainString)

        val result3 = JsonLiteralImpl.JsonText.greedyCreate("    \n\"Uni\"")

        assertNotNull(result3.literal)
        assertContentEquals(
            listOf("    \n", "\"Uni\"", ""),
            result3.literal.getChildren().map { jsonLiteral -> jsonLiteral.asString() })
        assertEquals("", result3.remainString)

    }

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
    internal fun valueCreateTest() {
        val result = JsonLiteralImpl.Value.greedyCreate("false")

        assertNotNull(result.literal)
        assertContentEquals(
            listOf("false"),
            result.literal.getChildren().map { jsonLiteral -> jsonLiteral.asString() })

        val node = result.literal.asJsonNode()
        assertEquals(false, node.asBoolean())
        assertEquals("", result.remainString)

        val result2 = JsonLiteralImpl.Value.greedyCreate("\"drfedas\"")

        assertNotNull(result2.literal)
        assertContentEquals(
            listOf("\"drfedas\""),
            result2.literal.getChildren().map { jsonLiteral -> jsonLiteral.asString() })
        val node2 = result2.literal.asJsonNode()
        assertEquals("drfedas", node2.asText())
        assertEquals("", result2.remainString)

        val result3 = JsonLiteralImpl.Value.greedyCreate(" 600")

        assertNull(result3.literal)
        assertEquals(" 600", result3.remainString)

        val result4 = JsonLiteralImpl.Value.greedyCreate("42")

        assertNotNull(result4.literal)
        assertContentEquals(
            listOf("42"),
            result4.literal.getChildren().map { jsonLiteral -> jsonLiteral.asString() })
        val node4 = result4.literal.asJsonNode()
        assertEquals(42, node4.asInt())
        assertEquals(42, node4.asNumber())
        assertEquals("", result4.remainString)

        val result5 = JsonLiteralImpl.Value.greedyCreate("42.8e5")

        assertNotNull(result5.literal)
        assertContentEquals(
            listOf("42.8e5"),
            result5.literal.getChildren().map { jsonLiteral -> jsonLiteral.asString() })
        val node5 = result5.literal.asJsonNode()
        assertFalse(node5.isInt())
        assertEquals(42.8e5, node5.asNumber())
        assertEquals("", result5.remainString)
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
    internal fun jObjectCreateTest() {
        val result = JsonLiteralImpl.JObject.greedyCreate("{\"Hi-Joji!\" :42},")

        assertNotNull(result.literal)
        assertContentEquals(
            listOf("{", "\"Hi-Joji!\" :42", "}"),
            result.literal.getChildren().map { jsonLiteral -> jsonLiteral.asString() })
        assertEquals(",", result.remainString)

        val result2 = JsonLiteralImpl.JObject.greedyCreate("{\"Universe\" :42,}")

        assertNull(result2.literal)
        assertEquals("{\"Universe\" :42,}", result2.remainString)
    }

    @Test
    internal fun objectMemberCreateTest() {
        val result = JsonLiteralImpl.ObjectMember.greedyCreate("\"Hi-Joji!\" :42,")

        assertNotNull(result.literal)
        assertContentEquals(
            listOf("\"Hi-Joji!\"", " :", "42"),
            result.literal.getChildren().map { jsonLiteral -> jsonLiteral.asString() })
        assertEquals(",", result.remainString)
    }

    @Test
    internal fun jArrayCreateTest() {
        val result = JsonLiteralImpl.JArray.greedyCreate("[\"Hi-Joji!\", 42],")

        assertNotNull(result.literal)
        assertContentEquals(
            listOf("[", "\"Hi-Joji!\"", ", ", "42", "]"),
            result.literal.getChildren().map { jsonLiteral -> jsonLiteral.asString() })
        assertEquals(",", result.remainString)

        val result2 = JsonLiteralImpl.JArray.greedyCreate("[\"Universe\", 42,]")

        assertNull(result2.literal)
        assertEquals("[\"Universe\", 42,]", result2.remainString)
    }

    @Test
    internal fun numberCreateTest() {
        val result = JsonLiteralImpl.Number.greedyCreate("-80.3e6")

        assertNotNull(result.literal)

        assertContentEquals(
            listOf("-", "80", ".3", "e6"),
            result.literal.getChildren().map { jsonLiteral -> jsonLiteral.asString() })
        assertEquals("", result.remainString)

        val result2 = JsonLiteralImpl.Number.greedyCreate("30.3e")

        assertNotNull(result2.literal)
        assertContentEquals(
            listOf("30", ".3"),
            result2.literal.getChildren().map { jsonLiteral -> jsonLiteral.asString() })
        assertEquals("e", result2.remainString)

        val result3 = JsonLiteralImpl.Number.greedyCreate("-6e98.")

        assertNotNull(result3.literal)
        assertContentEquals(
            listOf("-", "6", "e98"),
            result3.literal.getChildren().map { jsonLiteral -> jsonLiteral.asString() })
        assertEquals(".", result3.remainString)

        val result4 = JsonLiteralImpl.Number.greedyCreate("-0")

        assertNotNull(result4.literal)
        assertContentEquals(
            listOf("-", "0"),
            result4.literal.getChildren().map { jsonLiteral -> jsonLiteral.asString() })
        assertEquals("", result4.remainString)

        val result5 = JsonLiteralImpl.Number.greedyCreate("-.")

        assertNull(result5.literal)
        assertEquals("-.", result5.remainString)
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
    internal fun abnfHexDigCreateTest() {
        val result = JsonLiteralImpl.ABNFHexDig.greedyCreate("5")

        assertNotNull(result.literal)
        assertContentEquals(
            listOf("5"),
            result.literal.getChildren().map { jsonLiteral -> jsonLiteral.asString() })
        assertEquals("", result.remainString)

        val result2 = JsonLiteralImpl.ABNFHexDig.greedyCreate("A")

        assertNotNull(result2.literal)
        assertContentEquals(
            listOf("A"),
            result2.literal.getChildren().map { jsonLiteral -> jsonLiteral.asString() })
        assertEquals("", result2.remainString)

        val result3 = JsonLiteralImpl.ABNFHexDig.greedyCreate("f")

        assertNotNull(result3.literal)
        assertContentEquals(
            listOf("f"),
            result3.literal.getChildren().map { jsonLiteral -> jsonLiteral.asString() })
        assertEquals("", result3.remainString)

        val result4 = JsonLiteralImpl.ABNFHexDig.greedyCreate("g")

        assertNull(result4.literal)
        assertEquals("g", result4.remainString)
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

    @Test
    internal fun fracCreateTest() {
        val result = JsonLiteralImpl.Frac.greedyCreate(".80")

        assertNotNull(result.literal)
        assertContentEquals(
            listOf(".", "8", "0"),
            result.literal.getChildren().map { jsonLiteral -> jsonLiteral.asString() })
        assertEquals("", result.remainString)

        val result2 = JsonLiteralImpl.Frac.greedyCreate(".09e")

        assertNotNull(result2.literal)
        assertContentEquals(
            listOf(".", "0", "9"),
            result2.literal.getChildren().map { jsonLiteral -> jsonLiteral.asString() })
        assertEquals("e", result2.remainString)

        val result3 = JsonLiteralImpl.Frac.greedyCreate(".-23-")

        assertNull(result3.literal)
        assertEquals(".-23-", result3.remainString)

        val result4 = JsonLiteralImpl.Exp.greedyCreate(" .6 ")

        assertNull(result4.literal)
        assertEquals(" .6 ", result4.remainString)
    }

    @Test
    internal fun intCreateTest() {
        val result = JsonLiteralImpl.Int.greedyCreate("0")

        assertNotNull(result.literal)
        assertContentEquals(
            listOf("0"),
            result.literal.getChildren().map { jsonLiteral -> jsonLiteral.asString() })
        assertEquals("", result.remainString)

        val result2 = JsonLiteralImpl.Int.greedyCreate("29e")

        assertNotNull(result2.literal)
        assertContentEquals(
            listOf("2", "9"),
            result2.literal.getChildren().map { jsonLiteral -> jsonLiteral.asString() })
        assertEquals("e", result2.remainString)

        val result3 = JsonLiteralImpl.Int.greedyCreate("03")

        assertNotNull(result3.literal)
        assertContentEquals(
            listOf("0"),
            result3.literal.getChildren().map { jsonLiteral -> jsonLiteral.asString() })
        assertEquals("3", result3.remainString)

        val result4 = JsonLiteralImpl.Int.greedyCreate("A030")

        assertNull(result4.literal)
        assertEquals("A030", result4.remainString)
    }

    @Test
    internal fun jStringCreateTest() {
        listOf(
            Pair("\"\\\\\"", "\\"),
            Pair("\"abcd\"", "abcd"),
            Pair("\"\\\"\"", "\""),
            Pair("\"\\u66AF\"", "暯")
        ).forEach {
            val result = JsonLiteralImpl.JString.greedyCreate(it.first)

            assertNotNull(result.literal)
            assertEquals(it.first, result.literal.asString())
            assertEquals(it.second, result.literal.getTheString())
            assertEquals("", result.remainString)
        }

        val result2 = JsonLiteralImpl.JString.greedyCreate("\"\\uF345")

        assertNull(result2.literal)
        assertEquals("\"\\uF345", result2.remainString)
    }

    @Test
    internal fun jcharCreateTest() {
        listOf("\\\\", "a", "\\\"", "\\u66AF").forEach {
            val result = JsonLiteralImpl.JChar.greedyCreate(it)

            assertNotNull(result.literal)
            assertEquals(it, result.literal.asString())
            assertEquals("", result.remainString)
        }

        val result2 = JsonLiteralImpl.JChar.greedyCreate("\\u345")

        assertNull(result2.literal)
        assertEquals("\\u345", result2.remainString)
    }

    @Test
    internal fun escapeCreateTest() {
        val result = JsonLiteralImpl.Escape.greedyCreate("\\")

        assertNotNull(result.literal)
        assertEquals("\\", result.literal.asString())
        assertEquals("", result.remainString)

        val result2 = JsonLiteralImpl.Escape.greedyCreate(" \\")

        assertNull(result2.literal)
        assertEquals(" \\", result2.remainString)

        val result3 = JsonLiteralImpl.Escape.greedyCreate("\\\\\\")

        assertNotNull(result3.literal)
        assertEquals("\\", result3.literal.asString())
        assertEquals("\\\\", result3.remainString)
    }

    @Test
    internal fun quotationMarkCreateTest() {
        val result = JsonLiteralImpl.QuotationMark.greedyCreate("\"")

        assertNotNull(result.literal)
        assertEquals("\"", result.literal.asString())
        assertEquals("", result.remainString)

        val result2 = JsonLiteralImpl.QuotationMark.greedyCreate(" \"")

        assertNull(result2.literal)
        assertEquals(" \"", result2.remainString)

        val result3 = JsonLiteralImpl.QuotationMark.greedyCreate("\"\"\"")

        assertNotNull(result3.literal)
        assertEquals("\"", result3.literal.asString())
        assertEquals("\"\"", result3.remainString)
    }

    @Test
    internal fun unescapedCreateTest() {
        val result = JsonLiteralImpl.Unescaped.greedyCreate("jshf")

        assertNotNull(result.literal)
        assertEquals("j", result.literal.asString())
        assertEquals("shf", result.remainString)

        val result2 = JsonLiteralImpl.Unescaped.greedyCreate("\"")

        assertNull(result2.literal)
        assertEquals("\"", result2.remainString)

        val result3 = JsonLiteralImpl.Unescaped.greedyCreate("[]")

        assertNotNull(result3.literal)
        assertEquals("[", result3.literal.asString())
        assertEquals("]", result3.remainString)
    }

    private fun literalTest(
        literalCreator: (String) -> GreedyCreateResult<JsonLiteral>,
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
        literalCreator: (String) -> GreedyCreateResult<JsonLiteral>
    ) {
        literalTest(literalCreator, theChar.toString(), listOf("", theChar.toString(), ""), "")
        literalTest(literalCreator, " \t\n$theChar \n", listOf(" \t\n", theChar.toString(), " \n"), "")
        literalTest(literalCreator, " \t" + theChar + "Hoge [\n ", listOf(" \t", theChar.toString(), ""), "Hoge [\n ")
    }
}