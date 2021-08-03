package snowesamosc.kotlinjsonparser.factory

import snowesamosc.kotlinjsonparser.node.JsonNode

internal sealed class JsonLiteralImpl(
    private val children: List<JsonLiteral>,
    /**
     * 子が存在しない際に文字列表現で使用するテキスト
     */
    private val originalString: String? = null
) : JsonLiteral {

    override fun asString(): String {
        return if (hasChildren()) {
            children.joinToString { asString() }
        } else originalString.toString()
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
    internal class ABNFString(originalString: String)
        : JsonLiteralImpl(emptyList(), originalString) {
        override fun getName(): String = "ABNFString"
    }

    internal class WS private constructor(
        originalString: String
    ) : JsonLiteralImpl(emptyList(), originalString) {
        override fun getName(): String = "WS"

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