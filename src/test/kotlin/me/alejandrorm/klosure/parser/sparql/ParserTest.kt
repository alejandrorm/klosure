package me.alejandrorm.klosure.parser.sparql

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import me.alejandrorm.klosure.model.BlankId
import me.alejandrorm.klosure.model.EntailmentTypes
import me.alejandrorm.klosure.model.Graphs
import me.alejandrorm.klosure.model.IriId
import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.model.TripleId
import me.alejandrorm.klosure.model.literals.DataTypes
import me.alejandrorm.klosure.parser.turtle.ParserTest
import me.alejandrorm.klosure.parser.turtle.TurtleStarParser
import me.alejandrorm.klosure.sparql.AskQueryResult
import me.alejandrorm.klosure.sparql.SelectQueryResult
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
import kotlin.test.assertEquals

data class TestCase(
    val name: String,
    val data: String,
    val query: String,
    val expected: String,
    val entailment: EntailmentTypes,
    val checkOrder: Boolean
)

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

        if (doc.getElementsByTagName("boolean").length > 0) {
            val boolean = doc.getElementsByTagName("boolean").item(0) as Element
            return if (boolean.firstChild.textContent == "true") {
                ExpectedResult(listOf(), sequenceOf(SolutionMapping(setOf(), mapOf())))
            } else {
                ExpectedResult(listOf(), sequenceOf())
            }
        }

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

                val id = parseValue(value)
                Variable(name) to id
            }
            SolutionMapping(vars.toSet(), bindings)
        }
        return ExpectedResult(vars, solutions.asSequence())
    }

    private fun parseValue(value: org.w3c.dom.Node): NodeId {
        fun filter(node: org.w3c.dom.Node): org.w3c.dom.Node {
            var current = node
            while (current.nodeName == "#text") current = current.nextSibling
            return current
        }
        return when (value.nodeName) {
            "literal" -> DataTypes.getLiteralId(
                value.textContent,
                value.attributes.getNamedItem("datatype")?.let { d -> IRI.create(d.textContent) },
                value.attributes.getNamedItem("xml:lang")?.textContent
            )
            "uri" -> IriId(IRI.create(value.textContent))
            "bnode" -> BlankId(value.textContent)
            "triple" -> {
                val subject = parseValue(filter((value as Element).getElementsByTagName("subject").item(0).firstChild))
                val predicate = parseValue(filter(value.getElementsByTagName("predicate").item(0).firstChild))
                val `object` = parseValue(filter(value.getElementsByTagName("object").item(0).firstChild))
                TripleId(subject, (predicate as IriId).iri, `object`)
            }
            else -> throw IllegalArgumentException("Unsupported value type: ${value.nodeName}")
        }
    }

    private fun parseValue(obj: JsonElement): NodeId {
        return when (obj.jsonObject["type"]!!.jsonPrimitive.content) {
            "triple" -> {
                val subject = parseValue(obj.jsonObject["value"]!!.jsonObject["subject"]!!)
                val predicate = parseValue(obj.jsonObject["value"]!!.jsonObject["predicate"]!!)
                val `object` = parseValue(obj.jsonObject["value"]!!.jsonObject["object"]!!)
                TripleId(subject, (predicate as IriId).iri, `object`)
            }
            "uri" -> IriId(IRI.create(obj.jsonObject["value"]!!.jsonPrimitive.content))
            "iri" -> IriId(IRI.create(obj.jsonObject["value"]!!.jsonPrimitive.content))
            "literal" -> {
                val datatype = obj.jsonObject["datatype"]?.jsonPrimitive?.content?.let { IRI.create(it) }
                val lang = obj.jsonObject["lang"]?.jsonPrimitive?.content
                DataTypes.getLiteralId(obj.jsonObject["value"]!!.jsonPrimitive.content, datatype, lang)
            }
            else -> throw IllegalArgumentException("Unsupported value type: ${obj.jsonObject["type"]!!.jsonPrimitive.content}")
        }
    }

    private fun getFileContent(fileName: String): String {
        val stream =
            ParserTest::class.java.getResourceAsStream("/me/alejandrorm/klosure/parser/data/sparql/star/$fileName")!!
        return stream.bufferedReader().use(BufferedReader::readText)
    }

    @Test
    fun singleTest() {
        val dataFile = "1.1/aggregates/data-01.ttl"
        val queryFile = "1.1/aggregates/sparql11-count-01.rq"
        val expectedFile = "1.1/aggregates/sparql11-count-01.srx"

        val expected =
            xmlToSolutions(ParserTest::class.java.getResourceAsStream("/me/alejandrorm/klosure/parser/data/sparql/$expectedFile")!!)

        val graphs = Graphs(EntailmentTypes.SIMPLE)
        val parser =
            TurtleStarParser(ParserTest::class.java.getResourceAsStream("/me/alejandrorm/klosure/parser/data/sparql/$dataFile")!!)
        parser.graph = graphs.getDefaultGraph()
        parser.turtleDoc()

        val query =
            SparqlStarParser(ParserTest::class.java.getResourceAsStream("/me/alejandrorm/klosure/parser/data/sparql/$queryFile")!!).QueryUnit()

        println(query.toString())

        when (val result = query.eval(graphs)) {
            is SelectQueryResult -> SolutionSet.compareEqualSet(result.results, expected.variables, expected.solutions)
            is AskQueryResult -> assertEquals(result.result, expected.solutions.any())
        }
    }

    @TestFactory
    fun basicSparqlQueryTestSuite(): List<DynamicTest> {
        val folders = listOf(
            "1.0-w3c/algebra",
            "1.0-w3c/basic",
            // "1.1-w3c/entailment",
            // "1.1-w3c/bind",
            "1.1-w3c/property-path",
            "1.1/aggregates",
            "1.1/bindings",
            "1.1/bsbm",
            "1.1/builtin",
            "1.1/expressions",
            "1.1/negation",
            "1.1/property-paths",
            "1.1/subquery"
            // "1.2"
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
                    it["entailment"]?.jsonPrimitive?.content?.let { t -> EntailmentTypes.valueOf(t) }
                        ?: EntailmentTypes.SIMPLE,
                    it["checkOrder"]?.jsonPrimitive?.content?.let { t -> t == "true" } ?: false
                )
            }
            testCases.map { testCase ->
                DynamicTest.dynamicTest("when reading $folder/$testCase") {
                    val expected =
                        xmlToSolutions(ParserTest::class.java.getResourceAsStream("/me/alejandrorm/klosure/parser/data/sparql/$folder/${testCase.expected}")!!)

                    val graphs = Graphs(testCase.entailment)
                    val parser =
                        TurtleStarParser(ParserTest::class.java.getResourceAsStream("/me/alejandrorm/klosure/parser/data/sparql/$folder/${testCase.data}")!!)
                    parser.graph = graphs.getDefaultGraph()
                    parser.turtleDoc()

                    val query =
                        SparqlStarParser(ParserTest::class.java.getResourceAsStream("/me/alejandrorm/klosure/parser/data/sparql/$folder/${testCase.query}")!!).QueryUnit()

                    when (val result = query.eval(graphs)) {
                        is SelectQueryResult -> SolutionSet.compareEqualSet(result.results, expected.variables, expected.solutions)
                        is AskQueryResult -> assertEquals(result.result, expected.solutions.any())
                    }
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
                it["entailment"]?.jsonPrimitive?.content?.let { t -> EntailmentTypes.valueOf(t) }
                    ?: EntailmentTypes.SIMPLE,
                it["checkOrder"]?.jsonPrimitive?.content?.let { t -> t == "true" } ?: false
            )
        }
        return testCases.map { testCase ->
            DynamicTest.dynamicTest("when reading $testCase") {
                val expected = jsonToSolutions(getFileContent(testCase.expected))

                val graphs = Graphs(testCase.entailment)
                val parser =
                    TurtleStarParser(ParserTest::class.java.getResourceAsStream("/me/alejandrorm/klosure/parser/data/sparql/star/${testCase.data}")!!)
                parser.graph = graphs.getDefaultGraph()
                parser.turtleDoc()

                val query =
                    SparqlStarParser(ParserTest::class.java.getResourceAsStream("/me/alejandrorm/klosure/parser/data/sparql/star/${testCase.query}")!!).QueryUnit()

                when (val result = query.eval(graphs)) {
                    is SelectQueryResult -> SolutionSet.compareEqualSet(result.results, expected.variables, expected.solutions)
                    is AskQueryResult -> assertEquals(result.result, expected.solutions.any())
                }
            }
        }
    }
}
