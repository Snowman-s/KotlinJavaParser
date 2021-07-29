package snowesamosc.kotlinjsonparser.node

internal class NumberNode(data: Number) : AbstractNode() {
    private val number: Number = data

    override fun isNumber(): Boolean = true

    override fun asNumber(): Number = number

    override fun asInt(): Int = number.toInt()

    override fun toString(): String = number.toString()
}