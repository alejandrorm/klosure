package me.alejandrorm.klosure.parser.sparql

import kotlinx.serialization.json.*
import me.alejandrorm.klosure.model.*
import me.alejandrorm.klosure.model.literals.DataTypes
import me.alejandrorm.klosure.parser.turtle.ParserTest
import me.alejandrorm.klosure.parser.turtle.TurtleStarParser
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.Variable
import me.alejandrorm.klosure.sparql.algebra.SolutionSet
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.semanticweb.owlapi.model.IRI
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.BufferedReader
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.math.E


data class TestCase(val name: String, val data: String, val query: String,
                    val expected: String, val entailment: EntailmentTypes,
                    val checkOrder: Boolean)

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

    private fun xmlToSolutions(xml: InputStream): ExpectedResult {
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc: Document = builder.parse(xml)
        val variablesNodes = (doc.getElementsByTagName("head").item(0) as Element).getElementsByTagName("variable")
        val vars = (0 until variablesNodes.length).map {
            Variable(variablesNodes.item(it).attributes.getNamedItem("name").textContent)
        }.toList()

        val resultsNodes = (doc.getElementsByTagName("results").item(0) as Element).getElementsByTagName("result")
        val solutions = (0 until resultsNodes.length).map {
            val bindingNodes = (resultsNodes.item(it) as Element).getElementsByTagName("binding")
            val bindings = (0 until bindingNodes.length).associate { i ->
                val node = bindingNodes.item(i)
                val name = node.attributes.getNamedItem("name").textContent
                var value = node.firstChild
                while (value.nodeName == "#text") value = value.nextSibling

                val id = when (value.nodeName) {
                    "literal" -> DataTypes.getLiteralId(value.textContent,
                        value.attributes.getNamedItem("datatype")?.let { d -> IRI.create(d.textContent) },
                        value.attributes.getNamedItem("xml:lang")?.textContent
                    )
                    "uri" -> IriId(IRI.create(value.textContent))
                    "bnode" -> BlankId(value.textContent)
                    else -> throw IllegalArgumentException("Unsupported value type: ${value.localName}")
                }
                Variable(name) to id
            }
            SolutionMapping(vars.toSet(), bindings)
        }
        return ExpectedResult(vars, solutions.asSequence())
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
                val lang = obj.jsonObject["lang"]?.jsonPrimitive?.content
                DataTypes.getLiteralId(obj.jsonObject["value"]!!.jsonPrimitive.content, datatype, lang)
            }
        }
    }

    private fun getFileContent(fileName: String): String {
        val stream =
            ParserTest::class.java.getResourceAsStream("/me/alejandrorm/klosure/parser/data/sparql/star/$fileName")!!
        return stream.bufferedReader().use(BufferedReader::readText)
    }

    @Test
    fun singleTest() {
        val dataFile = "1.1-w3c/property-path/pp05.ttl"
        val queryFile = "1.1-w3c/property-path/pp05.rq"
        val expectedFile = "1.1-w3c/property-path/pp05.srx"

        val expected =
            xmlToSolutions(ParserTest::class.java.getResourceAsStream("/me/alejandrorm/klosure/parser/data/sparql/${expectedFile}")!!)

        val graph = Graph(EntailmentTypes.SIMPLE)
        val parser =
            TurtleStarParser(ParserTest::class.java.getResourceAsStream("/me/alejandrorm/klosure/parser/data/sparql/${dataFile}")!!)
        parser.graph = graph
        parser.turtleDoc()

        val query =
            SparqlStarParser(ParserTest::class.java.getResourceAsStream("/me/alejandrorm/klosure/parser/data/sparql/${queryFile}")!!).QueryUnit()

        println(query.toString())
        val result = query.eval(graph)

        SolutionSet.compareEqualSet(result.bindings, expected.variables, expected.solutions)
    }


    @TestFactory
    fun basicSparqlQueryTestSuite(): List<DynamicTest> {
        val folders = listOf(
            "1.0-w3c/algebra",
            "1.0-w3c/basic",
            //"1.1-w3c/entailment",
            //"1.1-w3c/bind",
            "1.1-w3c/property-path",
            "1.1/aggregates",
            "1.1/bindings",
            "1.1/bsbm",
            "1.1/builtin",
            "1.1/expressions",
            "1.1/negation",
            "1.1/property-paths",
            "1.1/subquery"
            //"1.2"
        )

        return folders.flatMap { folder ->
            val stream =
                ParserTest::class.java.getResourceAsStream("/me/alejandrorm/klosure/parser/data/sparql/$folder/sparql_test_suite.json")!!
            val content = stream.bufferedReader().use(BufferedReader::readText)
            val testCases = Json.parseToJsonElement(content).jsonObject["cases"]!!.jsonArray.map { it.jsonObject }.map {
                println(folder)
                println(it.jsonObject)
                TestCase(
                    it["name"]?.jsonPrimitive?.content ?: "",
                    it["data"]!!.jsonPrimitive.content,
                    it["query"]!!.jsonPrimitive.content,
                    it["expected"]!!.jsonPrimitive.content,
                    it["entailment"]?.jsonPrimitive?.content?.let { t -> EntailmentTypes.valueOf(t) } ?: EntailmentTypes.SIMPLE,
                    it["checkOrder"]?.jsonPrimitive?.content?.let { t -> t == "true" } ?: false
                )
            }
            testCases.map { testCase ->
                DynamicTest.dynamicTest("when reading $folder/$testCase") {
                    val expected =
                        xmlToSolutions(ParserTest::class.java.getResourceAsStream("/me/alejandrorm/klosure/parser/data/sparql/$folder/${testCase.expected}")!!)

                    val graph = Graph(testCase.entailment)
                    val parser =
                        TurtleStarParser(ParserTest::class.java.getResourceAsStream("/me/alejandrorm/klosure/parser/data/sparql/$folder/${testCase.data}")!!)
                    parser.graph = graph
                    parser.turtleDoc()

                    val query =
                        SparqlStarParser(ParserTest::class.java.getResourceAsStream("/me/alejandrorm/klosure/parser/data/sparql/$folder/${testCase.query}")!!).QueryUnit()

                    val result = query.eval(graph)

                    SolutionSet.compareEqualSet(result.bindings, expected.variables, expected.solutions)
                }
            }
        }
    }

    @TestFactory
    fun basicSparqlStarQueryTestSuite(): List<DynamicTest> {
        val stream =
            ParserTest::class.java.getResourceAsStream("/me/alejandrorm/klosure/parser/data/sparql/star/sparqlstar_test_suite.json")!!
        val content = stream.bufferedReader().use(BufferedReader::readText)
        val testCases = Json.parseToJsonElement(content).jsonObject["cases"]!!.jsonArray.map { it.jsonObject }.map {
            TestCase(
                it["name"]?.jsonPrimitive?.content ?: "",
                it["data"]!!.jsonPrimitive.content,
                it["query"]!!.jsonPrimitive.content,
                it["expected"]!!.jsonPrimitive.content,
                it["entailment"]?.jsonPrimitive?.content?.let { t -> EntailmentTypes.valueOf(t) } ?: EntailmentTypes.SIMPLE,
                it["checkOrder"]?.jsonPrimitive?.content?.let { t -> t == "true" } ?: false
            )
        }
        return testCases.map { testCase ->
            DynamicTest.dynamicTest("when reading $testCase") {
                val expected = jsonToSolutions(getFileContent(testCase.expected))

                val graph = Graph(testCase.entailment)
                val parser =
                    TurtleStarParser(ParserTest::class.java.getResourceAsStream("/me/alejandrorm/klosure/parser/data/sparql/star/${testCase.data}")!!)
                parser.graph = graph
                parser.turtleDoc()

                val query =
                    SparqlStarParser(ParserTest::class.java.getResourceAsStream("/me/alejandrorm/klosure/parser/data/sparql/star/${testCase.query}")!!).QueryUnit()

                val result = query.eval(graph)

                SolutionSet.compareEqualSet(result.bindings, expected.variables, expected.solutions)
            }
        }
    }
}
