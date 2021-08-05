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
        literalTest("[", listOf("", "[", ""), "")
        literalTest(" \t\n[ \n", listOf(" \t\n", "[", " \n"), "")
        literalTest(" \t[Hoge [\n ", listOf(" \t", "[", ""), "Hoge [\n ")
    }

    private fun literalTest(originalString: String, childrenStrList: List<String>, remain: String) {
        val result = JsonLiteralImpl.BeginArray.greedyCreate(originalString)
        assertNotNull(result.literal)
        assertTrue { result.literal.hasChildren() }
        assertContentEquals(childrenStrList,
            result.literal.getChildren().map { jsonLiteral -> jsonLiteral.asString() })
        assertEquals(remain, result.remainString)
    }
}