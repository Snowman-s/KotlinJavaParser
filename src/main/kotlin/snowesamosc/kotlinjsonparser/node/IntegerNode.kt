package snowesamosc.kotlinjsonparser.node

internal class IntegerNode(data: Int) : AbstractNode() {
    private val int: Int = data

    override fun isInt(): Boolean = true

    override fun asInt(): Int = int

    override fun isNumber(): Boolean = true

    override fun asNumber(): Number = int

    override fun toString(): String = int.toString()
}