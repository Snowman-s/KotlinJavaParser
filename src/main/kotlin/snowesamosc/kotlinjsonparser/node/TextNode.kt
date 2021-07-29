package snowesamosc.kotlinjsonparser.node

internal class TextNode(data: String) : AbstractNode() {
    private val text: String = data

    override fun isText(): Boolean = true

    override fun asText(): String = text

    override fun toString(): String = "\"" + text + "\""
}