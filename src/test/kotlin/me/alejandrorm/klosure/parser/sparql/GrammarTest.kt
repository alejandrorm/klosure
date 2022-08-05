package me.alejandrorm.klosure.parser.sparql

import me.alejandrorm.klosure.parser.sparql.test.SparqlStarTestParser
import org.junit.jupiter.api.Test

class GrammarTest {

    @Test
    fun test() {
        val stream =
            ParserTest::class.java.getResourceAsStream("/me/alejandrorm/klosure/parser/data/sparql/1.0-w3c/algebra/join-combo-1.rq")!!
        SparqlStarTestParser(stream).QueryUnit()
    }
}