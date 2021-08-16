package snowesamosc.kotlinjsonparser.factory

import snowesamosc.kotlinjsonparser.node.BooleanNode
import snowesamosc.kotlinjsonparser.node.JsonNode
import snowesamosc.kotlinjsonparser.node.NullNode

internal sealed class JsonLiteralImpl(
    private val children: List<JsonLiteral>
) : JsonLiteral {

    /**
     * このオブジェクトの文字列表現
     */
    override fun asString(): String {
        return children.joinToString(separator = "") { it.asString() }
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
     * ABNFルール(RFC5234)に則った、%表記またはダブルクォートで囲まれた文字列を表す。
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
            fun greedyCreate(str: String): GreedyCreateResult<ABNFDigit> {
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

    /**
     * ABNFルール(RFC5234)のHEXDIGを表す。
     */
    internal class ABNFHexDig private constructor(
        children: List<JsonLiteral>
    ) : JsonLiteralImpl(children) {
        override fun getName(): String = "ABNFHexDig"

        companion object Factory {
            fun greedyCreate(str: String): GreedyCreateResult<ABNFHexDig> {
                val abnfDigitResult = ABNFDigit.greedyCreate(str)
                if (abnfDigitResult.literal != null) {
                    return GreedyCreateResult(abnfDigitResult.remainString, ABNFHexDig(listOf(abnfDigitResult.literal)))
                }

                val originalStringBuilder = StringBuilder(str)

                if (str.isEmpty()) return GreedyCreateResult(str, null)

                val firstCodePoint = str.codePointAt(0)
                originalStringBuilder.deleteAt(0)

                if (listOf('a', 'b', 'c', 'd', 'e', 'f').any {
                        it.code == firstCodePoint || it.uppercaseChar().code == firstCodePoint
                    }) {
                    return GreedyCreateResult(
                        originalStringBuilder.toString(),
                        ABNFHexDig(listOf(ABNFString(firstCodePoint.codeToStr())))
                    )
                }

                return GreedyCreateResult(str, null)
            }
        }
    }

    //JSON-text = ws value ws
    internal class JsonText private constructor(
        children: List<JsonLiteral>
    ) : JsonLiteralImpl(children) {
        override fun getName(): String = "JsonText"

        companion object Factory {
            fun greedyCreate(str: String): GreedyCreateResult<JsonText> {
                var remainString = str
                val children: MutableList<JsonLiteral> = mutableListOf()

                val wsResult1 = WS.greedyCreate(remainString)
                if (wsResult1.literal == null) {
                    return GreedyCreateResult(str, null)
                }

                children.add(wsResult1.literal)
                remainString = wsResult1.remainString

                val valueResult = Value.greedyCreate(remainString)
                if (valueResult.literal == null) {
                    return GreedyCreateResult(str, null)
                }

                children.add(valueResult.literal)
                remainString = valueResult.remainString

                val wsResult2 = WS.greedyCreate(remainString)
                if (wsResult2.literal == null) {
                    return GreedyCreateResult(str, null)
                }

                children.add(wsResult2.literal)
                remainString = wsResult2.remainString

                return GreedyCreateResult(remainString, JsonText(children))
            }
        }
    }

    internal class WS private constructor(
        private val originalString: String
    ) : JsonLiteralImpl(emptyList()) {
        override fun getName(): String = "WS"

        override fun asString(): String = originalString

        companion object Factory {
            fun greedyCreate(str: String): GreedyCreateResult<WS> {
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

    //value = false / null / true / object / array / number / string
    internal class Value private constructor(
        private val child: JsonLiteral
    ) : JsonLiteralImpl(listOf(child)) {
        override fun getName(): String = "Value"

        override fun isJsonNode(): Boolean {
            return true
        }

        override fun asJsonNode(): JsonNode {
            return when (child) {
                is False -> BooleanNode(false)
                is Null -> NullNode
                is True -> BooleanNode(true)
                else -> {
                    //最終的には起きなくなるはず
                    throw IllegalStateException()
                }
            }
        }

        companion object Factory {
            fun greedyCreate(str: String): GreedyCreateResult<Value> {
                val literalCreatorList = listOf<(String) -> GreedyCreateResult<JsonLiteral>>(
                    { False.greedyCreate(it) },
                    { Null.greedyCreate(it) },
                    { True.greedyCreate(it) },
                    { JObject.greedyCreate(it) },
                    { JArray.greedyCreate(it) },
                    { Number.greedyCreate(it) },
                    { JString.greedyCreate(it) }
                )

                literalCreatorList.forEach { creator ->
                    val result = creator.invoke(str)

                    if (result.literal != null) {
                        return GreedyCreateResult(result.remainString, Value(result.literal))
                    }
                }

                return GreedyCreateResult(str, null)
            }
        }
    }

    internal class BeginArray private constructor(
        children: List<JsonLiteral>
    ) : JsonLiteralImpl(children) {
        override fun getName(): String = "BeginArray"

        companion object Factory {
            fun greedyCreate(str: String): GreedyCreateResult<BeginArray> {
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
            fun greedyCreate(str: String): GreedyCreateResult<BeginObject> {
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
            fun greedyCreate(str: String): GreedyCreateResult<EndArray> {
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
            fun greedyCreate(str: String): GreedyCreateResult<EndObject> {
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
            fun greedyCreate(str: String): GreedyCreateResult<NameSeparator> {
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
            fun greedyCreate(str: String): GreedyCreateResult<ValueSeparator> {
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
            fun greedyCreate(str: String): GreedyCreateResult<False> {
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
            fun greedyCreate(str: String): GreedyCreateResult<Null> {
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
            fun greedyCreate(str: String): GreedyCreateResult<True> {
                val trueString = "\u0074\u0072\u0075\u0065"

                if (!str.startsWith(trueString)) {
                    return GreedyCreateResult(str, null)
                }

                return GreedyCreateResult(str.removePrefix(trueString), True(trueString))
            }
        }
    }

    //object = begin-object [ member *( value-separator member ) ] end-object
    internal class JObject private constructor(
        children: List<JsonLiteral>
    ) : JsonLiteralImpl(children) {
        override fun getName(): String = "Object"

        companion object Factory {
            fun greedyCreate(str: String): GreedyCreateResult<JObject> {
                var remainString = str
                val children: MutableList<JsonLiteral> = mutableListOf()

                val beginObjectResult = BeginObject.greedyCreate(remainString)
                if (beginObjectResult.literal == null) {
                    return GreedyCreateResult(str, null)
                }

                children.add(beginObjectResult.literal)
                remainString = beginObjectResult.remainString

                val firstObjectMemberResult = ObjectMember.greedyCreate(remainString)
                if (firstObjectMemberResult.literal != null) {
                    children.add(firstObjectMemberResult.literal)
                    remainString = firstObjectMemberResult.remainString

                    while (true) {
                        val valueSeparatorResult = ValueSeparator.greedyCreate(remainString)
                        if (valueSeparatorResult.literal == null) {
                            break
                        }

                        children.add(valueSeparatorResult.literal)
                        remainString = valueSeparatorResult.remainString

                        val objectMemberResult = ObjectMember.greedyCreate(remainString)
                        if (objectMemberResult.literal == null) {
                            return GreedyCreateResult(str, null)
                        }

                        children.add(objectMemberResult.literal)
                        remainString = objectMemberResult.remainString
                    }
                }

                val endObjectResult = EndObject.greedyCreate(remainString)
                if (endObjectResult.literal == null) {
                    return GreedyCreateResult(str, null)
                }

                children.add(endObjectResult.literal)
                remainString = endObjectResult.remainString

                return GreedyCreateResult(remainString, JObject(children))
            }
        }
    }

    //member = string name-separator value
    internal class ObjectMember private constructor(
        children: List<JsonLiteral>
    ) : JsonLiteralImpl(children) {
        override fun getName(): String = "ObjectMember"

        companion object Factory {
            fun greedyCreate(str: String): GreedyCreateResult<ObjectMember> {
                val children: MutableList<JsonLiteral> = mutableListOf()
                var remainString = str

                val stringResult = JString.greedyCreate(remainString)
                if (stringResult.literal == null) {
                    return GreedyCreateResult(str, null)
                }
                children.add(stringResult.literal)
                remainString = stringResult.remainString

                val nameSeparatorResult = NameSeparator.greedyCreate(remainString)
                if (nameSeparatorResult.literal == null) {
                    return GreedyCreateResult(str, null)
                }
                children.add(nameSeparatorResult.literal)
                remainString = nameSeparatorResult.remainString

                val valueResult = Value.greedyCreate(remainString)
                if (valueResult.literal == null) {
                    return GreedyCreateResult(str, null)
                }
                children.add(valueResult.literal)
                remainString = valueResult.remainString

                return GreedyCreateResult(remainString, ObjectMember(children))
            }
        }
    }

    //object = begin-array [ value *( value-separator value ) ] end-array
    internal class JArray private constructor(
        children: List<JsonLiteral>
    ) : JsonLiteralImpl(children) {
        override fun getName(): String = "Array"

        companion object Factory {
            fun greedyCreate(str: String): GreedyCreateResult<JArray> {
                var remainString = str
                val children: MutableList<JsonLiteral> = mutableListOf()

                val beginArrayResult = BeginArray.greedyCreate(remainString)
                if (beginArrayResult.literal == null) {
                    return GreedyCreateResult(str, null)
                }

                children.add(beginArrayResult.literal)
                remainString = beginArrayResult.remainString

                val firstValueResult = Value.greedyCreate(remainString)
                if (firstValueResult.literal != null) {
                    children.add(firstValueResult.literal)
                    remainString = firstValueResult.remainString

                    while (true) {
                        val valueSeparatorResult = ValueSeparator.greedyCreate(remainString)
                        if (valueSeparatorResult.literal == null) {
                            break
                        }

                        children.add(valueSeparatorResult.literal)
                        remainString = valueSeparatorResult.remainString

                        val valueResult = Value.greedyCreate(remainString)
                        if (valueResult.literal == null) {
                            return GreedyCreateResult(str, null)
                        }

                        children.add(valueResult.literal)
                        remainString = valueResult.remainString
                    }
                }

                val endObjectResult = EndArray.greedyCreate(remainString)
                if (endObjectResult.literal == null) {
                    return GreedyCreateResult(str, null)
                }

                children.add(endObjectResult.literal)
                remainString = endObjectResult.remainString

                return GreedyCreateResult(remainString, JArray(children))
            }
        }
    }

    internal class Number private constructor(
        children: List<JsonLiteral>
    ) : JsonLiteralImpl(children) {
        override fun getName(): String = "Number"

        companion object Factory {
            fun greedyCreate(str: String): GreedyCreateResult<Number> {
                val children: MutableList<JsonLiteral> = mutableListOf()
                var remainString = str

                val minusResult = Minus.greedyCreate(remainString)
                if (minusResult.literal != null) {
                    children.add(minusResult.literal)
                    remainString = minusResult.remainString
                }

                val intResult = Int.greedyCreate(remainString)
                if (intResult.literal == null) {
                    return GreedyCreateResult(str, null)
                }
                children.add(intResult.literal)
                remainString = intResult.remainString

                val fracResult = Frac.greedyCreate(remainString)
                if (fracResult.literal != null) {
                    children.add(fracResult.literal)
                    remainString = fracResult.remainString
                }

                val expResult = Exp.greedyCreate(remainString)
                if (expResult.literal != null) {
                    children.add(expResult.literal)
                    remainString = expResult.remainString
                }

                return GreedyCreateResult(remainString, Number(children))
            }
        }
    }

    internal class DecimalPoint private constructor(
        private val originalString: String
    ) : JsonLiteralImpl(emptyList()) {
        override fun getName(): String = "DecimalPoint"

        override fun asString(): String = originalString

        companion object Factory {
            fun greedyCreate(str: String): GreedyCreateResult<DecimalPoint> {
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
            fun greedyCreate(str: String): GreedyCreateResult<Digit19> {
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

    internal class E private constructor(
        private val originalString: String
    ) : JsonLiteralImpl(emptyList()) {
        override fun getName(): String = "E"

        override fun asString(): String = originalString

        companion object Factory {
            fun greedyCreate(str: String): GreedyCreateResult<E> {
                val nodeStringBuilder = StringBuilder()
                val originalStringBuilder = StringBuilder(str)

                if (str.isEmpty()) return GreedyCreateResult(str, null)

                val firstCodePoint = str.codePointAt(0)
                originalStringBuilder.deleteAt(0)

                if (firstCodePoint == 0x45 || firstCodePoint == 0x65) {
                    nodeStringBuilder.appendCodePoint(firstCodePoint)
                    return GreedyCreateResult(originalStringBuilder.toString(), E(nodeStringBuilder.toString()))
                }

                return GreedyCreateResult(str, null)
            }
        }
    }

    internal class Exp private constructor(
        children: List<JsonLiteral>
    ) : JsonLiteralImpl(children) {
        override fun getName(): String = "Exp"

        companion object Factory {
            fun greedyCreate(str: String): GreedyCreateResult<Exp> {
                val children: MutableList<JsonLiteral> = mutableListOf()
                var remainString = str

                val eResult = E.greedyCreate(remainString)
                if (eResult.literal == null) {
                    return GreedyCreateResult(str, null)
                }
                children.add(eResult.literal)
                remainString = eResult.remainString

                val minusResult = Minus.greedyCreate(remainString)
                if (minusResult.literal != null) {
                    children.add(minusResult.literal)
                    remainString = minusResult.remainString
                } else {
                    val plusResult = Plus.greedyCreate(remainString)
                    if (plusResult.literal != null) {
                        children.add(plusResult.literal)
                        remainString = plusResult.remainString
                    }
                }

                val firstDigitResult = ABNFDigit.greedyCreate(remainString)
                if (firstDigitResult.literal == null) {
                    return GreedyCreateResult(str, null)
                }
                children.add(firstDigitResult.literal)
                remainString = firstDigitResult.remainString

                while (true) {
                    val otherDigitResult = ABNFDigit.greedyCreate(remainString)
                    if (otherDigitResult.literal == null) {
                        break
                    }
                    children.add(otherDigitResult.literal)
                    remainString = otherDigitResult.remainString
                }

                return GreedyCreateResult(remainString, Exp(children))
            }
        }
    }

    internal class Frac private constructor(
        children: List<JsonLiteral>
    ) : JsonLiteralImpl(children) {
        override fun getName(): String = "Frac"

        companion object Factory {
            fun greedyCreate(str: String): GreedyCreateResult<Frac> {
                val children: MutableList<JsonLiteral> = mutableListOf()
                var remainString = str

                val decimalPointResult = DecimalPoint.greedyCreate(remainString)
                if (decimalPointResult.literal == null) {
                    return GreedyCreateResult(str, null)
                }
                children.add(decimalPointResult.literal)
                remainString = decimalPointResult.remainString

                val firstDigitResult = ABNFDigit.greedyCreate(remainString)
                if (firstDigitResult.literal == null) {
                    return GreedyCreateResult(str, null)
                }
                children.add(firstDigitResult.literal)
                remainString = firstDigitResult.remainString

                while (true) {
                    val otherDigitResult = ABNFDigit.greedyCreate(remainString)
                    if (otherDigitResult.literal == null) {
                        break
                    }
                    children.add(otherDigitResult.literal)
                    remainString = otherDigitResult.remainString
                }

                return GreedyCreateResult(remainString, Frac(children))
            }
        }
    }

    internal class Int private constructor(
        children: List<JsonLiteral>
    ) : JsonLiteralImpl(children) {
        override fun getName(): String = "Int"

        companion object Factory {
            fun greedyCreate(str: String): GreedyCreateResult<Int> {
                var remainString = str

                val zeroResult = Zero.greedyCreate(remainString)
                if (zeroResult.literal != null) {
                    return GreedyCreateResult(zeroResult.remainString, Int(listOf(zeroResult.literal)))
                }

                val children: MutableList<JsonLiteral> = mutableListOf()

                val firstDigitResult = Digit19.greedyCreate(remainString)
                if (firstDigitResult.literal == null) {
                    return GreedyCreateResult(str, null)
                }
                children.add(firstDigitResult.literal)
                remainString = firstDigitResult.remainString

                while (true) {
                    val otherDigitResult = ABNFDigit.greedyCreate(remainString)
                    if (otherDigitResult.literal == null) {
                        break
                    }
                    children.add(otherDigitResult.literal)
                    remainString = otherDigitResult.remainString
                }

                return GreedyCreateResult(remainString, Int(children))
            }
        }
    }

    internal class Minus private constructor(
        private val originalString: String
    ) : JsonLiteralImpl(emptyList()) {
        override fun getName(): String = "Minus"

        override fun asString(): String = originalString

        companion object Factory {
            fun greedyCreate(str: String): GreedyCreateResult<Minus> {
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
            fun greedyCreate(str: String): GreedyCreateResult<Plus> {
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
            fun greedyCreate(str: String): GreedyCreateResult<Zero> {
                if (str.isEmpty() || str.codePointAt(0) != 0x30) return GreedyCreateResult(str, null)

                return GreedyCreateResult(str.removePrefix("0"), Zero("0"))
            }
        }
    }

    internal class JString private constructor(
        children: List<JsonLiteral>
    ) : JsonLiteralImpl(children) {
        override fun getName(): String = "JString"

        companion object Factory {
            fun greedyCreate(str: String): GreedyCreateResult<JString> {
                var remainString = str
                val children: MutableList<JsonLiteral> = mutableListOf()

                val quotationMarkResult1 = QuotationMark.greedyCreate(remainString)
                if (quotationMarkResult1.literal == null) {
                    return GreedyCreateResult(str, null)
                }

                children.add(quotationMarkResult1.literal)
                remainString = quotationMarkResult1.remainString

                while (true) {
                    val jCharResult = JChar.greedyCreate(remainString)
                    if (jCharResult.literal == null) {
                        break
                    }

                    children.add(jCharResult.literal)
                    remainString = jCharResult.remainString
                }

                val quotationMarkResult2 = QuotationMark.greedyCreate(remainString)
                if (quotationMarkResult2.literal == null) {
                    return GreedyCreateResult(str, null)
                }

                children.add(quotationMarkResult2.literal)
                remainString = quotationMarkResult2.remainString

                return GreedyCreateResult(remainString, JString(children))
            }
        }
    }

    internal class JChar private constructor(
        children: List<JsonLiteral>
    ) : JsonLiteralImpl(children) {
        override fun getName(): String = "JChar"

        companion object Factory {
            fun greedyCreate(str: String): GreedyCreateResult<JChar> {
                var remainString = str

                val unescapedResult = Unescaped.greedyCreate(remainString)
                if (unescapedResult.literal != null) {
                    return GreedyCreateResult(unescapedResult.remainString, JChar(listOf(unescapedResult.literal)))
                }

                val children: MutableList<JsonLiteral> = mutableListOf()

                val escapeResult = Escape.greedyCreate(remainString)
                if (escapeResult.literal == null) {
                    return GreedyCreateResult(str, null)
                }
                children.add(escapeResult.literal)
                remainString = escapeResult.remainString

                if (remainString.isEmpty()) return GreedyCreateResult(str, null)

                val firstCodePoint = remainString.codePointAt(0)
                remainString = remainString.substring(1)

                return when (firstCodePoint) {
                    0x22, 0x5C, 0x2F, 0x62, 0x66, 0x6E, 0x72, 0x74 -> {
                        children.add(ABNFString(firstCodePoint.codeToStr()))
                        GreedyCreateResult(remainString, JChar(children))
                    }
                    0x75 -> {
                        //\uXXXX
                        children.add(ABNFString(firstCodePoint.codeToStr()))
                        for (i in 0..3) {
                            val hexdigResult = ABNFHexDig.greedyCreate(remainString)
                            if (hexdigResult.literal == null) {
                                return GreedyCreateResult(str, null)
                            }
                            children.add(hexdigResult.literal)
                            remainString = hexdigResult.remainString
                        }

                        GreedyCreateResult(remainString, JChar(children))
                    }
                    else -> {
                        GreedyCreateResult(str, null)
                    }
                }
            }
        }
    }

    internal class Escape private constructor(
        private val originalString: String
    ) : JsonLiteralImpl(emptyList()) {
        override fun getName(): String = "Escape"

        override fun asString(): String = originalString

        companion object Factory {
            fun greedyCreate(str: String): GreedyCreateResult<Escape> {
                val nodeStringBuilder = StringBuilder()
                val originalStringBuilder = StringBuilder(str)

                if (str.isEmpty()) return GreedyCreateResult(str, null)

                val firstCodePoint = str.codePointAt(0)
                originalStringBuilder.deleteAt(0)

                if (firstCodePoint == 0x5C) {
                    nodeStringBuilder.appendCodePoint(firstCodePoint)
                    return GreedyCreateResult(originalStringBuilder.toString(), Escape(nodeStringBuilder.toString()))
                }

                return GreedyCreateResult(str, null)
            }
        }
    }

    internal class QuotationMark private constructor(
        private val originalString: String
    ) : JsonLiteralImpl(emptyList()) {
        override fun getName(): String = "QuotationMark"

        override fun asString(): String = originalString

        companion object Factory {
            fun greedyCreate(str: String): GreedyCreateResult<QuotationMark> {
                val nodeStringBuilder = StringBuilder()
                val originalStringBuilder = StringBuilder(str)

                if (str.isEmpty()) return GreedyCreateResult(str, null)

                val firstCodePoint = str.codePointAt(0)
                originalStringBuilder.deleteAt(0)

                if (firstCodePoint == 0x22) {
                    nodeStringBuilder.appendCodePoint(firstCodePoint)
                    return GreedyCreateResult(
                        originalStringBuilder.toString(),
                        QuotationMark(nodeStringBuilder.toString())
                    )
                }

                return GreedyCreateResult(str, null)
            }
        }
    }

    internal class Unescaped private constructor(
        private val originalString: String
    ) : JsonLiteralImpl(emptyList()) {
        override fun getName(): String = "Unescaped"

        override fun asString(): String = originalString

        companion object Factory {
            fun greedyCreate(str: String): GreedyCreateResult<Unescaped> {
                val originalStringBuilder = StringBuilder(str)

                if (str.isEmpty()) return GreedyCreateResult(str, null)

                val firstCodePoint = str.codePointAt(0)
                originalStringBuilder.deleteAt(0)

                return when (firstCodePoint) {
                    in 0x20..0x21,
                    in 0x23..0x5B,
                    in 0x5D..0x10FFFF -> {
                        val unescaped = Unescaped(firstCodePoint.codeToStr())
                        GreedyCreateResult(originalStringBuilder.toString(), unescaped)
                    }
                    else -> GreedyCreateResult(str, null)
                }
            }
        }
    }
}

/**
 * 一文字のみを持つ "the six structural characters" (begin-array等)のために、与えられた文字列を分割する。
 *
 * @return 分割されたchildrenのリストと、残りの文字列。
 */
internal fun separateOneCharLiteral(str: String, theCodePoint: Int): Pair<List<JsonLiteral>, String> {
    var remainString = str

    val ws1ResultSet = JsonLiteralImpl.WS.greedyCreate(remainString)
    val ws1 = ws1ResultSet.literal!!
    remainString = ws1ResultSet.remainString

    if (remainString.isEmpty() || remainString.codePointAt(0) != theCodePoint) return Pair(emptyList(), str)
    val text = JsonLiteralImpl.ABNFString(theCodePoint.codeToStr())
    remainString = remainString.substring(1)

    val ws2ResultSet = JsonLiteralImpl.WS.greedyCreate(remainString)
    val ws2 = ws2ResultSet.literal!!
    remainString = ws2ResultSet.remainString

    return Pair(listOf(ws1, text, ws2), remainString)
}

internal fun Int.codeToStr() = StringBuilder().appendCodePoint(this).toString()