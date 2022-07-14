package me.alejandrorm.klosure.parser

import TurtleStarParser
import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.PredicateNode
import me.alejandrorm.klosure.parser.TripleComparator.Companion.areEqualTriples
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.io.InputStream
import java.util.function.Predicate
import kotlin.streams.toList
import org.hamcrest.MatcherAssert.assertThat

class ParserTest {
    private val acceptedTurtleFileNames = listOf(
        "turtle-star-syntax-basic-01",
        "turtle-star-syntax-basic-02",
        "turtle-star-syntax-bnode-01",
        "turtle-star-syntax-bnode-02",
        "turtle-star-syntax-bnode-03",
        "turtle-star-syntax-compound",
        "turtle-star-syntax-inside-01")

    private fun getTurtleFile(fileName: String): Pair<InputStream, InputStream> {
        val turtleFile =
            ParserTest::class.java.getResourceAsStream("/me/alejandrorm/klosure/parser/data/turtle_accepted/$fileName.ttl")!!
        val tripleFile =
            ParserTest::class.java.getResourceAsStream("/me/alejandrorm/klosure/parser/data/turtle_accepted/$fileName.nt")!!
        return Pair(turtleFile, tripleFile)
    }

    private fun triplesToNTSet(triples: Iterator<PredicateNode>): Set<String> {
        return triples.asSequence().map { it.toString() }.toSet()
    }

    private fun readTurtleFile(stream: InputStream): Set<String> {
        val parser = TurtleStarParser(stream)
        parser.graph = Graph()
        parser.turtleDoc()
        return triplesToNTSet(parser.graph.getAllAssertedTriples())
    }

    private fun readNTFile(stream: InputStream): Set<String> {
        val reader = stream.bufferedReader()
        return reader.lines().toList().toSet()
    }

    @TestFactory
    fun testTurtleToNT() = acceptedTurtleFileNames.map { fileName ->
        DynamicTest.dynamicTest("when reading $fileName.ttl expect $fileName.nt") {
            val (turtleFile, tripleFile) = getTurtleFile(fileName)
            assertThat(readNTFile(tripleFile), areEqualTriples(readTurtleFile(turtleFile)))
        }
    }
}