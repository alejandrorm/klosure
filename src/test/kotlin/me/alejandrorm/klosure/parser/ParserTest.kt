package me.alejandrorm.klosure.parser

import TurtleStarParser
import ParseException
import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.PredicateNode
import me.alejandrorm.klosure.parser.TripleComparator.Companion.areEqualTriples
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.io.InputStream
import kotlin.streams.toList
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.assertThrows

class ParserTest {
    private val acceptedTurtleFileNames = listOf(
        "turtle-star-syntax-basic-01",
        "turtle-star-syntax-basic-02",
        "turtle-star-syntax-bnode-01",
        "turtle-star-syntax-bnode-02",
        "turtle-star-syntax-bnode-03",
        "turtle-star-syntax-compound",
        "turtle-star-syntax-inside-01",
        "turtle-star-syntax-inside-02",
        "turtle-star-syntax-nested-01",
        "turtle-star-syntax-nested-02",
        "turtle-star-annotation-1",
        "turtle-star-annotation-2",
        "nt-ttl-star-bnode-1",
        "nt-ttl-star-syntax-4")

    private val rejectedTurtleFileNames = listOf(
        "turtle-star-syntax-bad-01.ttl",
        "turtle-star-syntax-bad-02.ttl",
        "turtle-star-syntax-bad-03.ttl",
        "turtle-star-syntax-bad-04.ttl",
        "turtle-star-syntax-bad-05.ttl",
        "turtle-star-syntax-bad-06.ttl",
        "turtle-star-syntax-bad-07.ttl",
        "turtle-star-syntax-bad-08.ttl",
        "turtle-star-syntax-bad-ann-1.ttl",
        "turtle-star-syntax-bad-ann-2.ttl",
    )

    private fun readTurtleRejectedFile(fileName: String): Set<String> {
        val turtleFile =
            ParserTest::class.java.getResourceAsStream("/me/alejandrorm/klosure/parser/data/turtle_rejected/$fileName")!!
        return readTurtleFile(turtleFile)
    }

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

    @TestFactory
    fun testTurtleParserOnBadSyntax() = rejectedTurtleFileNames.map { fileName ->
        DynamicTest.dynamicTest("when reading $fileName expect failure") {
            assertThrows<ParseException> {
                readTurtleRejectedFile(fileName)
            }
        }
    }
}