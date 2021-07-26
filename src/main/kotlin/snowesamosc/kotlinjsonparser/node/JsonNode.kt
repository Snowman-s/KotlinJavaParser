package snowesamosc.kotlinjsonparser.node

interface JsonNode {
    fun isArray(): Boolean
    fun asArray(): Array<JsonNode>

    fun isNumber(): Boolean
    fun isInt(): Boolean
    fun asInt(): Int
    fun asDouble(): Double

    fun isBoolean(): Boolean
    fun asBoolean(): Boolean

    fun isText(): Boolean
    fun asText(): String

    fun isNull(): Boolean

    fun get(key: String): JsonNode
    fun find(key: String): JsonNode

    fun isMissing(): Boolean

    override fun toString(): String
}