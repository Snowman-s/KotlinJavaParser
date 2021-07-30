package snowesamosc.kotlinjsonparser.factory

class JsonLiteralParser {

    enum class JsonCharacter(key: Char) {
        BeginArray('\u005B'),
        BeginObject('\u007B'),
        EndArray('\u005D'),
        EndObject('\u007D'),
        NameSeparator('\u003A'),
        ValueSeparator('\u002C'),
        DecimalPoint('\u002E'),
        Minus('\u002D'),
        Plus('\u002B'),
        Zero('\u0030'),
        Escape('\u005C'),
        QuotationMark('\u0022');

        val value: Char = key
    }
}