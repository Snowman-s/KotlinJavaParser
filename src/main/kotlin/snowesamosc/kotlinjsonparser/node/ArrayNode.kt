package snowesamosc.kotlinjsonparser.node

internal class ArrayNode(data: Array<JsonNode>) : AbstractNode() {
    private val array = data

    override fun isArray(): Boolean = true

    override fun asArray(): Array<JsonNode> = array

    override fun toString(): String {
        val builder = StringBuilder("[")

        for (i in array.indices) {
            if (i != 0) {
                builder.append(", ")
            }
            builder.append(array[i].toString())
        }

        builder.append("]")

        return builder.toString()
    }
}