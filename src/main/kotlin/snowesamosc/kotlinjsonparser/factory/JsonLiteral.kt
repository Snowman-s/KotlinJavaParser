package snowesamosc.kotlinjsonparser.factory

import snowesamosc.kotlinjsonparser.node.JsonNode

interface JsonLiteral {
    fun asString(): String

    fun hasChildren(): Boolean
    fun getChildren(): List<JsonLiteral>

    fun isJsonNode(): Boolean
    fun asJsonNode(): JsonNode?
}