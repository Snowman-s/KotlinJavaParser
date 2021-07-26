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
        override fun isArray(): Boolean {
            TODO("Not yet implemented")
        }

        override fun asArray(): Array<JsonNode> {
            TODO("Not yet implemented")
        }

        override fun isNumber(): Boolean {
            TODO("Not yet implemented")
        }

        override fun isInt(): Boolean {
            TODO("Not yet implemented")
        }

        override fun asInt(): Int {
            TODO("Not yet implemented")
        }

        override fun isFraction(): Boolean {
            TODO("Not yet implemented")
        }

        override fun asDouble(): Double {
            TODO("Not yet implemented")
        }

        override fun isBoolean(): Boolean {
            TODO("Not yet implemented")
        }

        override fun asBoolean(): Boolean {
            TODO("Not yet implemented")
        }

        override fun isText(): Boolean {
            TODO("Not yet implemented")
        }

        override fun asText(): String {
            TODO("Not yet implemented")
        }

        override fun isNull(): Boolean {
            TODO("Not yet implemented")
        }

        override fun get(key: String): JsonNode {
            TODO("Not yet implemented")
        }

        override fun find(key: String): JsonNode {
            TODO("Not yet implemented")
        }

        override fun isMissing(): Boolean {
            TODO("Not yet implemented")
        }

        override fun toString(): String {
            TODO("Not yet implemented")
        }
    }
}