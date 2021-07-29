package snowesamosc.kotlinjsonparser.node

import snowesamosc.kotlinjsonparser.JsonException

internal class ObjectNode(data: Map<String, JsonNode>) : AbstractNode() {
    constructor(builder: Builder) : this(builder.data)

    private val map: Map<String, JsonNode> = data

    override fun get(key: String): JsonNode {
        return map[key] ?: throw JsonException("this node does not have \"$key\"")
    }

    override fun find(key: String): JsonNode {
        return map.getOrDefault(key, MissingNode)
    }

    override fun toString(): String {
        val builder = StringBuilder("{")

        map.onEachIndexed { index, entry ->
            if (index != 0) {
                builder.append(", ")
            }
            builder.append("\"").append(entry.key).append("\"").append(":").append(entry.value)
        }

        builder.append("}")

        return builder.toString()
    }

    /**
     * ObjectNodeのビルダー
     */
    class Builder {
        var data: Map<String, JsonNode> = HashMap()

        fun append(key: String, child: JsonNode): Builder {
            data = data.plus(Pair(key, child))

            return this
        }

        fun build(): ObjectNode {
            return ObjectNode(data)
        }
    }
}