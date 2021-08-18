package snowesamosc.kotlinjsonparser.factory

import snowesamosc.kotlinjsonparser.node.JsonNode
import snowesamosc.kotlinjsonparser.node.MissingNode

class JsonLiteralParser {
    fun parse(str: String): JsonNode {
        val result = JsonLiteralImpl.JsonText.greedyCreate(str)

        if (result.literal == null || result.remainString.isNotEmpty()) {
            throw IllegalArgumentException("cannot parse str")
        }

        return result.literal.asJsonNode()
            ?: //これは起きないはず。
            MissingNode
    }
}