package snowesamosc.kotlinjsonparser.factory

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class JsonLiteralImplTest {
    @Test
    internal fun wsCreateTest() {
        val result = JsonLiteralImpl.WS.greedyCreate("")

        assertNull(result.literal)
        assertEquals("", result.str)

        val result2 = JsonLiteralImpl.WS.greedyCreate(" \t\n ")

        assertNotNull(result2.literal)
        assertEquals(" \t\n ", result2.literal.asString())
        assertEquals("", result2.str)

        val result3 = JsonLiteralImpl.WS.greedyCreate(" \tHoge ")

        assertNotNull(result3.literal)
        assertEquals(" \t", result3.literal.asString())
        assertEquals("Hoge ", result3.str)
    }
}