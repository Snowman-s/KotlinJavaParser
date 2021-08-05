package snowesamosc.kotlinjsonparser.factory

import snowesamosc.kotlinjsonparser.node.JsonNode

internal sealed class JsonLiteralImpl(
    private val children: List<JsonLiteral>
) : JsonLiteral {

    /**
     * このオブジェクトの文字列表現
     */
    override fun asString(): String {
        return children.joinToString { asString() }
    }

    override fun hasChildren(): Boolean = children.isNotEmpty()

    override fun getChildren(): List<JsonLiteral> = children

    override fun isJsonNode(): Boolean = false

    override fun asJsonNode(): JsonNode? = null

    override fun toString(): String {
        return getName() + if (hasChildren()) {
            getChildren().toString()
        } else {
            ""
        }
    }

    abstract fun getName(): String

    /**
     * ABNFルール(RFC5234)に則った、%表記での文字列を表す。
     *
     * 他のJSONリテラルの子として使用するが、そのリテラルが文字列のみなら使用しない。
     */
    internal class ABNFString(private val originalString: String) : JsonLiteralImpl(emptyList()) {
        override fun getName(): String = "ABNFString"

        override fun asString(): String = originalString
    }

    internal class WS private constructor(
        private val originalString: String
    ) : JsonLiteralImpl(emptyList()) {
        override fun getName(): String = "WS"

        override fun toString(): String = originalString

        companion object Factory {
            fun greedyCreate(str: String): GreedyCreateResult {
                val nodeBuilder = StringBuilder()
                val originalStringBuilder = StringBuilder(str)

                CharLoop@
                for (c in str) {
                    when (c) {
                        '\u0020', '\u0009', '\u000A', '\u000D' -> {
                            nodeBuilder.append(c)
                            originalStringBuilder.deleteCharAt(0)
                        }
                        else -> break@CharLoop
                    }
                }

                val ws = WS(nodeBuilder.toString())

                return GreedyCreateResult(originalStringBuilder.toString(), ws)
            }
        }
    }
}