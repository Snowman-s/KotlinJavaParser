package snowesamosc.kotlinjsonparser.node

import snowesamosc.kotlinjsonparser.JsonValueNotFoundException

object MissingNode : JsonNode {
    override fun isArray(): Boolean = false

    override fun asArray(): Array<JsonNode> = emptyArray()

    override fun isNumber(): Boolean = false

    override fun isInt(): Boolean = false

    override fun asInt(): Int = 0

    override fun asDouble(): Double = 0.0

    override fun isBoolean(): Boolean = false

    override fun asBoolean(): Boolean = false

    override fun isText(): Boolean = false

    override fun asText(): String = ""

    override fun isNull(): Boolean = false

    override fun get(key: String): JsonNode = throw JsonValueNotFoundException("\"$key\" is not exist")

    override fun find(key: String): JsonNode = MissingNode

    override fun isMissing(): Boolean = true

    override fun toString(): String = "missing"
}