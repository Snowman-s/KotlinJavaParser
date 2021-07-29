package snowesamosc.kotlinjsonparser.node

internal object NullNode : AbstractNode() {
    override fun isNull(): Boolean = true

    override fun toString(): String = "null"
}