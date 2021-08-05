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

        override fun asString(): String = originalString

        companion object Factory {
            fun greedyCreate(str: String): GreedyCreateResult {
                val nodeBuilder = StringBuilder()
                val originalStringBuilder = StringBuilder(str)

                CharLoop@
                for (c in str.codePoints()) {
                    when (c) {
                        0x20, 0x09, 0x0A, 0x0D -> {
                            nodeBuilder.appendCodePoint(c)
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

    internal class BeginArray private constructor(
        children: List<JsonLiteral>
    ) : JsonLiteralImpl(children) {
        override fun getName(): String = "BeginArray"

        companion object Factory {
            fun greedyCreate(str: String): GreedyCreateResult {
                val pair = separateOneCharLiteral(str, 0x5B)
                val children = pair.first
                val remain = pair.second

                if (children.isEmpty()) return GreedyCreateResult(str, null)
                val beginArray = BeginArray(children)

                return GreedyCreateResult(remain, beginArray)
            }
        }
    }

    internal class BeginObject private constructor(
        children: List<JsonLiteral>
    ) : JsonLiteralImpl(children) {
        override fun getName(): String = "BeginObject"

        companion object Factory {
            fun greedyCreate(str: String): GreedyCreateResult {
                val pair = separateOneCharLiteral(str, 0x7B)
                val children = pair.first
                val remain = pair.second

                if (children.isEmpty()) return GreedyCreateResult(str, null)
                val beginObject = BeginObject(children)

                return GreedyCreateResult(remain, beginObject)
            }
        }
    }

    internal class EndArray private constructor(
        children: List<JsonLiteral>
    ) : JsonLiteralImpl(children) {
        override fun getName(): String = "EndArray"

        companion object Factory {
            fun greedyCreate(str: String): GreedyCreateResult {
                val pair = separateOneCharLiteral(str, 0x5D)
                val children = pair.first
                val remain = pair.second

                if (children.isEmpty()) return GreedyCreateResult(str, null)
                val endArray = EndArray(children)

                return GreedyCreateResult(remain, endArray)
            }
        }
    }

    internal class EndObject private constructor(
        children: List<JsonLiteral>
    ) : JsonLiteralImpl(children) {
        override fun getName(): String = "EndObject"

        companion object Factory {
            fun greedyCreate(str: String): GreedyCreateResult {
                val pair = separateOneCharLiteral(str, 0x7D)
                val children = pair.first
                val remain = pair.second

                if (children.isEmpty()) return GreedyCreateResult(str, null)
                val endObject = EndObject(children)

                return GreedyCreateResult(remain, endObject)
            }
        }
    }

    internal class NameSeparator private constructor(
        children: List<JsonLiteral>
    ) : JsonLiteralImpl(children) {
        override fun getName(): String = "NameSeparator"

        companion object Factory {
            fun greedyCreate(str: String): GreedyCreateResult {
                val pair = separateOneCharLiteral(str, 0x3A)
                val children = pair.first
                val remain = pair.second

                if (children.isEmpty()) return GreedyCreateResult(str, null)
                val nameSeparator = NameSeparator(children)

                return GreedyCreateResult(remain, nameSeparator)
            }
        }
    }

    internal class ValueSeparator private constructor(
        children: List<JsonLiteral>
    ) : JsonLiteralImpl(children) {
        override fun getName(): String = "ValueSeparator"

        companion object Factory {
            fun greedyCreate(str: String): GreedyCreateResult {
                val pair = separateOneCharLiteral(str, 0x2C)
                val children = pair.first
                val remain = pair.second

                if (children.isEmpty()) return GreedyCreateResult(str, null)
                val valueSeparator = ValueSeparator(children)

                return GreedyCreateResult(remain, valueSeparator)
            }
        }
    }
}

/**
 * 一文字のみを持つリテラル(begin-array等)のために、与えられた文字列を分割する。
 *
 * @return 分割されたchildrenのリストと、残りの文字列。
 */
internal fun separateOneCharLiteral(str: String, theCodePoint: Int): Pair<List<JsonLiteral>, String> {
    var remainString = str

    val ws1ResultSet = JsonLiteralImpl.WS.greedyCreate(remainString)
    val ws1 = ws1ResultSet.literal!!
    remainString = ws1ResultSet.remainString

    if (remainString.isEmpty() || remainString.codePointAt(0) != theCodePoint) return Pair(emptyList(), str)
    val text = JsonLiteralImpl.ABNFString(StringBuilder().appendCodePoint(theCodePoint).toString())
    remainString = remainString.substring(1)

    val ws2ResultSet = JsonLiteralImpl.WS.greedyCreate(remainString)
    val ws2 = ws2ResultSet.literal!!
    remainString = ws2ResultSet.remainString

    return Pair(listOf(ws1, text, ws2), remainString)
}