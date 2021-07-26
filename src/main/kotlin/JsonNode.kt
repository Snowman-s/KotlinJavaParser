interface JsonNode {
    fun isArray(): Boolean
    fun asArray(): Array<JsonNode>

    fun isNumber(): Boolean
    fun isInt(): Boolean
    fun asInt(): Int
    fun isFraction(): Boolean
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

    object MissingNode : JsonNode {
        override fun isArray(): Boolean = false

        override fun asArray(): Array<JsonNode> = emptyArray()

        override fun isNumber(): Boolean = false

        override fun isInt(): Boolean = false

        override fun asInt(): Int = 0

        override fun isFraction(): Boolean = false

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
}