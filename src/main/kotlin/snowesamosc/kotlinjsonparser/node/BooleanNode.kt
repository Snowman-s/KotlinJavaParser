package snowesamosc.kotlinjsonparser.node

internal class BooleanNode(data: Boolean) : AbstractNode() {
    private val bool: Boolean = data

    override fun isBoolean(): Boolean = true

    override fun asBoolean(): Boolean = bool

    override fun toString(): String = bool.toString()
}