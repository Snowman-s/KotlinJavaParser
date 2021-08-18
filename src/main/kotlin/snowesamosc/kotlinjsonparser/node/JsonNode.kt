package snowesamosc.kotlinjsonparser.node

interface JsonNode {
    /**
     * このNodeが、arrayのNodeであるかを返します。
     */
    fun isArray(): Boolean

    /**
     * このNodeが配列に変換可能なら、変換したものを返します。
     *
     * @throws IllegalStateException このNodeが配列に変換できない場合。
     */
    fun asArray(): Array<JsonNode>

    /**
     * このNodeが、numberのNodeであるかを返します。
     */
    fun isNumber(): Boolean

    /**
     * このNodeが、numberのNodeであり、かつ小数部や指数部が存在しないかを検査します。
     */
    fun isInt(): Boolean

    /**
     * このNodeが整数に変換可能なら、変換したものを返します。
     *
     * @throws IllegalStateException このNodeが整数に変換できない場合。
     */
    fun asInt(): Int

    /**
     * このNodeが数値に変換可能なら、変換したものを返します。
     *
     * @throws IllegalStateException このNodeが数値に変換できない場合。
     */
    fun asNumber(): Number

    /**
     * このNodeが、booleanのNodeであるかを返します。
     */
    fun isBoolean(): Boolean

    /**
     * このNodeが真偽値に変換可能なら、変換したものを返します。
     *
     * @throws IllegalStateException このNodeが真偽値に変換できない場合。
     */
    fun asBoolean(): Boolean

    /**
     * このNodeが、textのNodeであるかを返します。
     */
    fun isText(): Boolean
    fun asText(): String

    /**
     * このNodeが、nullのNodeであるかを返します。
     */
    fun isNull(): Boolean

    /**
     * このNodeが、objectのNodeであるかを返します。
     */
    fun isObject(): Boolean

    /**
     * このNodeの、このkeyに対応する子が取得可能なら、その子を返します。
     *
     * @throws IllegalStateException keyに対応する子が存在しない場合。
     */
    fun get(key: String): JsonNode

    /**
     * このNodeの、このkeyに対応する子が取得可能なら、その子を返します。
     *
     * そうでないならisMissing()がtrueであるようなNodeを返します。
     */
    fun find(key: String): JsonNode

    /**
     * このNodeは本来存在しないならtrue, そうでなければfalse
     *
     * @see JsonNode.find
     */
    fun isMissing(): Boolean

    override fun toString(): String
}