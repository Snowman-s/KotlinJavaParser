package snowesamosc.kotlinjsonparser.factory

import snowesamosc.kotlinjsonparser.node.JsonNode

class JsonLiteralParser {
    sealed class JsonLiteral {
        abstract fun asString(): String

        abstract fun hasChildren(): Boolean
        abstract fun getChildren(): List<JsonLiteral>

        abstract fun isJsonNode(): Boolean
        abstract fun asJsonNode(): JsonNode?

        internal abstract fun asName(): String

        override fun toString(): String {
            return asName() + if (hasChildren()) {
                getChildren().toString()
            } else {
                ""
            }
        }
    }
}