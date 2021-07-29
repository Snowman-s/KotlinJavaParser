package snowesamosc.kotlinjsonparser.node

internal class ObjectNode(data: Map<String, JsonNode>) : AbstractNode() {
    constructor(builder: Builder) : this(builder.data)

    override fun get(key: String): JsonNode {
        TODO("Not yet implemented")
    }

    override fun find(key: String): JsonNode {
        TODO("Not yet implemented")
    }

    override fun toString(): String {
        TODO("Not yet implemented")
    }

    /**
     * ObjectNodeのビルダー
     */
    class Builder {
        val data: Map<String, JsonNode> = HashMap()

        fun append(key: String, child: JsonNode): Builder {
            return this
        }

        fun build(): ObjectNode {
            return ObjectNode(emptyMap())
        }
    }
}