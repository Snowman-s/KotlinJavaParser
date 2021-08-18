package snowesamosc.kotlinjsonparser.factory

import snowesamosc.kotlinjsonparser.node.JsonNode

internal interface JsonLiteral {
    fun asString(): String

    fun hasChildren(): Boolean
    fun getChildren(): List<JsonLiteral>

    fun isJsonNode(): Boolean
    fun asJsonNode(): JsonNode?
}