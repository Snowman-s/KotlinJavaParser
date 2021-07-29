package snowesamosc.kotlinjsonparser.factory

class JsonLiteralParser {

    enum class JsonLiteral(key: String) {
        BeginArray("\u005B"),
        BeginObject("\u007B"),
        EndArray("\u005D"),
        EndObject("\u007D"),
        NameSeparator("\u003A"),
        ValueSeparator("\u002C"),
        False("\u0066\u0061\u006c\u0073\u0065"),
        Null("\u006e\u0075\u006c\u006c"),
        True("\u0074\u0072\u0075\u0065"),
        DecimalPoint("\u002E"),
        Minus("\u002D"),
        Plus("\u002B"),
        Zero("\u0030"),
        Escape("\u005C"),
        QuotationMark("\u0022");

        val value: String = key
    }
}