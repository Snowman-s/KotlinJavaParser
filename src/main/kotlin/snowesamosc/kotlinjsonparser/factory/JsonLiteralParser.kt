package snowesamosc.kotlinjsonparser.factory

import snowesamosc.kotlinjsonparser.JsonException
import snowesamosc.kotlinjsonparser.node.JsonNode

class JsonLiteralParser {
    fun parse(str: String): JsonNode {
        val result = JsonLiteralImpl.JsonText.greedyCreate(str)

        if (result.literal == null || result.remainString.isNotEmpty()) {
            throw JsonException("cannot parse str")
        }

        return result.literal.asJsonNode()
            ?: //これは起きないはず。
            throw JsonException("error exists this code")
    }
}