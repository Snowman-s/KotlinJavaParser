package snowesamosc.kotlinjsonparser.factory

import snowesamosc.kotlinjsonparser.node.JsonNode

internal sealed class JsonLiteralImpl(
    private val originalString: String,
    private val children: List<JsonLiteral>
) : JsonLiteral {

    override fun asString(): String = originalString

    override fun hasChildren(): Boolean = children.isNotEmpty()

    override fun getChildren(): List<JsonLiteral> = children

    override fun isJsonNode(): Boolean {
        TODO("Not yet implemented")
    }

    override fun asJsonNode(): JsonNode? {
        TODO("Not yet implemented")
    }

    override fun toString(): String {
        return getName() + if (hasChildren()) {
            getChildren().toString()
        } else {
            ""
        }
    }

    abstract fun getName(): String
}