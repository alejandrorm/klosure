package me.alejandrorm.klosure.parser.sparql

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import me.alejandrorm.klosure.model.*
import me.alejandrorm.klosure.parser.turtle.ParserTest
import me.alejandrorm.klosure.parser.turtle.TurtleStarParser
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.Variable
import me.alejandrorm.klosure.sparql.algebra.SolutionSet
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.semanticweb.owlapi.model.IRI
import java.io.BufferedReader

@Serializable
data class TestCase(val data: String, val query: String, val expected: String)

@Serializable
data class TestCases(val cases: List<TestCase>)

data class ExpectedResult(val variables: List<Variable>, val solutions: Sequence<SolutionMapping>)

class ParserTest {

    private fun jsonToSolutions(json: String): ExpectedResult {
        val obj = Json.parseToJsonElement(json)
        val vars = obj.jsonObject["head"]!!.jsonObject["vars"]!!.jsonArray.map { Variable(it.toString()) }.toList()
        val results = obj.jsonObject["results"]!!.jsonObject["bindings"]!!.jsonArray.map {
            SolutionMapping(
                vars.toSet(),
                it.jsonObject.entries.associate { entry ->
                    Variable(entry.key) to parseValue(entry.value)
                }
            )
        }
        return ExpectedResult(vars, results.asSequence())
    }

    private fun parseValue(obj: JsonElement): NodeId {
        val type = obj.jsonObject["type"]!!.jsonPrimitive.content
        return if (type == "triple") {
            val subject = parseValue(obj.jsonObject["value"]!!.jsonObject["subject"]!!)
            val predicate = parseValue(obj.jsonObject["value"]!!.jsonObject["predicate"]!!)
            val `object` = parseValue(obj.jsonObject["value"]!!.jsonObject["object"]!!)
            TripleId(subject, (predicate as IriId).iri, `object`)
        } else {
            if (type == "uri" || type == "iri") {
                IriId(IRI.create(obj.jsonObject["value"]!!.jsonPrimitive.content))
            } else { // literal
                val datatype = obj.jsonObject["datatype"]?.jsonPrimitive?.content?.let { IRI.create(it) }
                LiteralId(obj.jsonObject["value"]!!.jsonPrimitive.content, datatype)
            }
        }
    }

    private fun getFileContent(fileName: String): String {
        val stream =
            ParserTest::class.java.getResourceAsStream("/me/alejandrorm/klosure/parser/data/sparql/$fileName")!!
        return stream.bufferedReader().use(BufferedReader::readText)
    }

    @TestFactory
    fun basicSparqlStarQueryTestSuite(): List<DynamicTest> {
        val stream =
            ParserTest::class.java.getResourceAsStream("/me/alejandrorm/klosure/parser/data/sparql/test_suite.json")!!
        val content = stream.bufferedReader().use(BufferedReader::readText)
        val testCases = Json.parseToJsonElement(content).jsonObject["cases"]!!.jsonArray.map { it.jsonObject }.map {
            TestCase(
                it["data"]!!.jsonPrimitive.content,
                it["query"]!!.jsonPrimitive.content,
                it["expected"]!!.jsonPrimitive.content
            )
        }
        return testCases.map { testCase ->
            DynamicTest.dynamicTest("when reading $testCase") {
                val expected = jsonToSolutions(getFileContent(testCase.expected))

                val graph = Graph()
                val parser =
                    TurtleStarParser(ParserTest::class.java.getResourceAsStream("/me/alejandrorm/klosure/parser/data/sparql/${testCase.data}")!!)
                parser.graph = graph
                parser.turtleDoc()

                val query =
                    SparqlStarParser(ParserTest::class.java.getResourceAsStream("/me/alejandrorm/klosure/parser/data/sparql/${testCase.query}")!!).QueryUnit()

                val result = query.eval(graph)

                SolutionSet.compareEqualSet(expected.solutions, expected.variables, result.bindings)
            }
        }
    }
}
