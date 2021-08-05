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

    /**
     * ABNFルール(RFC5234)のDIGITを表す。
     */
    internal class ABNFDigit private constructor(
        private val originalString: String
    ) : JsonLiteralImpl(emptyList()) {
        override fun getName(): String = "ABNFDigit"

        override fun asString(): String = originalString

        companion object Factory {
            fun greedyCreate(str: String): GreedyCreateResult {
                val nodeStringBuilder = StringBuilder()
                val originalStringBuilder = StringBuilder(str)

                if (str.isEmpty()) return GreedyCreateResult(str, null)

                val firstCodePoint = str.codePointAt(0)
                originalStringBuilder.deleteAt(0)

                if (firstCodePoint in 0x30..0x39) {
                    nodeStringBuilder.appendCodePoint(firstCodePoint)
                    return GreedyCreateResult(originalStringBuilder.toString(), ABNFDigit(nodeStringBuilder.toString()))
                }

                return GreedyCreateResult(str, null)
            }
        }
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

    internal class False private constructor(
        private val originalString: String
    ) : JsonLiteralImpl(emptyList()) {
        override fun getName(): String = "False"

        override fun asString(): String = originalString

        companion object Factory {
            fun greedyCreate(str: String): GreedyCreateResult {
                val falseString = "\u0066\u0061\u006c\u0073\u0065"

                if (!str.startsWith(falseString)) {
                    return GreedyCreateResult(str, null)
                }

                return GreedyCreateResult(str.removePrefix(falseString), False(falseString))
            }
        }
    }

    internal class Null private constructor(
        private val originalString: String
    ) : JsonLiteralImpl(emptyList()) {
        override fun getName(): String = "Null"

        override fun asString(): String = originalString

        companion object Factory {
            fun greedyCreate(str: String): GreedyCreateResult {
                val nullString = "\u006e\u0075\u006c\u006c"

                if (!str.startsWith(nullString)) {
                    return GreedyCreateResult(str, null)
                }

                return GreedyCreateResult(str.removePrefix(nullString), Null(nullString))
            }
        }
    }

    internal class True private constructor(
        private val originalString: String
    ) : JsonLiteralImpl(emptyList()) {
        override fun getName(): String = "True"

        override fun asString(): String = originalString

        companion object Factory {
            fun greedyCreate(str: String): GreedyCreateResult {
                val trueString = "\u0074\u0072\u0075\u0065"

                if (!str.startsWith(trueString)) {
                    return GreedyCreateResult(str, null)
                }

                return GreedyCreateResult(str.removePrefix(trueString), True(trueString))
            }
        }
    }

    internal class DecimalPoint private constructor(
        private val originalString: String
    ) : JsonLiteralImpl(emptyList()) {
        override fun getName(): String = "DecimalPoint"

        override fun asString(): String = originalString

        companion object Factory {
            fun greedyCreate(str: String): GreedyCreateResult {
                if (str.isEmpty() || str.codePointAt(0) != 0x2E) return GreedyCreateResult(str, null)

                return GreedyCreateResult(str.removePrefix("."), DecimalPoint("."))
            }
        }
    }

    internal class Digit19 private constructor(
        private val originalString: String
    ) : JsonLiteralImpl(emptyList()) {
        override fun getName(): String = "Digit1_9"

        override fun asString(): String = originalString

        companion object Factory {
            fun greedyCreate(str: String): GreedyCreateResult {
                val nodeStringBuilder = StringBuilder()
                val originalStringBuilder = StringBuilder(str)

                if (str.isEmpty()) return GreedyCreateResult(str, null)

                val firstCodePoint = str.codePointAt(0)
                originalStringBuilder.deleteAt(0)

                if (firstCodePoint in 0x31..0x39) {
                    nodeStringBuilder.appendCodePoint(firstCodePoint)
                    return GreedyCreateResult(originalStringBuilder.toString(), Digit19(nodeStringBuilder.toString()))
                }

                return GreedyCreateResult(str, null)
            }
        }
    }

    internal class Minus private constructor(
        private val originalString: String
    ) : JsonLiteralImpl(emptyList()) {
        override fun getName(): String = "Minus"

        override fun asString(): String = originalString

        companion object Factory {
            fun greedyCreate(str: String): GreedyCreateResult {
                if (str.isEmpty() || str.codePointAt(0) != 0x2D) return GreedyCreateResult(str, null)

                return GreedyCreateResult(str.removePrefix("-"), Minus("-"))
            }
        }
    }

    internal class Plus private constructor(
        private val originalString: String
    ) : JsonLiteralImpl(emptyList()) {
        override fun getName(): String = "Plus"

        override fun asString(): String = originalString

        companion object Factory {
            fun greedyCreate(str: String): GreedyCreateResult {
                if (str.isEmpty() || str.codePointAt(0) != 0x2B) return GreedyCreateResult(str, null)

                return GreedyCreateResult(str.removePrefix("+"), Plus("+"))
            }
        }
    }

    internal class Zero private constructor(
        private val originalString: String
    ) : JsonLiteralImpl(emptyList()) {
        override fun getName(): String = "Zero"

        override fun asString(): String = originalString

        companion object Factory {
            fun greedyCreate(str: String): GreedyCreateResult {
                if (str.isEmpty() || str.codePointAt(0) != 0x30) return GreedyCreateResult(str, null)

                return GreedyCreateResult(str.removePrefix("0"), Zero("0"))
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