package snowesamosc.kotlinjsonparser.node

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class ObjectNodeTest {
    @Test
    internal fun objectNodeBuildTest() {
        val builder: ObjectNode.Builder = ObjectNode.Builder()

        val objectNode: ObjectNode =
            builder.append("child", TextNode("hoge"))
                .build()

        assertEquals("hoge", objectNode.get("child").asText())
    }
}