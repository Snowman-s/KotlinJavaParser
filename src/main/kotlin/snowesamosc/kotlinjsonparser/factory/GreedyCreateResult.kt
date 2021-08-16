package snowesamosc.kotlinjsonparser.factory

internal class GreedyCreateResult<out T: JsonLiteral>(val remainString: String, val literal: T?)