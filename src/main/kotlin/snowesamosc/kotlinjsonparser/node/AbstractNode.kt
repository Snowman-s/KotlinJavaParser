package snowesamosc.kotlinjsonparser.node

import snowesamosc.kotlinjsonparser.JsonException

internal abstract class AbstractNode : JsonNode {
    override fun isArray(): Boolean = false

    override fun asArray(): Array<JsonNode> = throw JsonException("this node is not array")

    override fun isNumber(): Boolean = false

    override fun isInt(): Boolean = false

    override fun asInt(): Int = throw JsonException("this node is not int")

    override fun asNumber(): Number = throw JsonException("this node is not double")

    override fun isBoolean(): Boolean = false

    override fun asBoolean(): Boolean = throw JsonException("this node is not boolean")

    override fun isText(): Boolean = false

    override fun asText(): String = throw JsonException("this node is not text")

    override fun isNull(): Boolean = false

    override fun isObject(): Boolean = false

    override fun get(key: String): JsonNode = throw JsonException("this node does not have \"$key\"")

    override fun find(key: String): JsonNode = MissingNode

    override fun isMissing(): Boolean = false
}