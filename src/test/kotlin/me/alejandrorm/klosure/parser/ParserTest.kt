package me.alejandrorm.klosure.parser

import TurtleStarParser
import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.PredicateNode
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.io.InputStream
import java.util.function.Predicate
import kotlin.streams.toList

class ParserTest {
    private val acceptedTurtleFileNames = listOf(
        "turtle-star-syntax-basic-01",
        "turtle-star-syntax-basic-02")
        //"turtle-star-syntax-bnode-01")

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
        return triplesToNTSet(parser.graph.getAllTriples())
    }

    private fun readNTFile(stream: InputStream): Set<String> {
        val reader = stream.bufferedReader()
        return reader.lines().toList().toSet()
    }

    @TestFactory
    fun testTurtleToNT() = acceptedTurtleFileNames.map { fileName ->
        DynamicTest.dynamicTest("when reading $fileName.ttl expect $fileName.nt") {
            val (turtleFile, tripleFile) = getTurtleFile(fileName)
            Assertions.assertEquals(readNTFile(tripleFile), readTurtleFile(turtleFile))
        }
    }
}