package snowesamosc.kotlinjsonparser.node

internal object MissingNode : AbstractNode() {
    override fun find(key: String): JsonNode = MissingNode

    override fun isMissing(): Boolean = true

    override fun toString(): String = ""
}