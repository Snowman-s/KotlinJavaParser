package snowesamosc.kotlinjsonparser.factory

import snowesamosc.kotlinjsonparser.node.JsonNode
import snowesamosc.kotlinjsonparser.node.MissingNode

class JsonLiteralParser {
    /**
     * Jsonテキストを解釈します。
     *
     * 与えられた文字列をJsonテキスト形式として解釈し、各々のValueをJsonNodeとして返します。
     *
     * この実装は、RFC8259によります。
     *
     * @see JsonNode
     *
     * @param str Json形式の文字列
     * @return 最も上層のvalueを表すJsonNode
     *
     * @throws IllegalArgumentException 文字列が解釈出来なかった場合。
     * */
    fun parse(str: String): JsonNode {
        val result = JsonLiteralImpl.JsonText.greedyCreate(str)

        if (result.literal == null || result.remainString.isNotEmpty()) {
            throw IllegalArgumentException("cannot parse str")
        }

        return result.literal.asJsonNode()
            ?: //これは起きないはず。
            MissingNode
    }
}