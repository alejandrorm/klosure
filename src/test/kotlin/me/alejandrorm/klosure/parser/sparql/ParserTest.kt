package me.alejandrorm.klosure.parser.sparql

import me.alejandrorm.klosure.sparql.SolutionMapping
import kotlinx.serialization.json.*
import kotlinx.serialization.*
import me.alejandrorm.klosure.model.IriId
import me.alejandrorm.klosure.model.LiteralId
import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.model.TripleId
import me.alejandrorm.klosure.parser.turtle.ParserTest
import me.alejandrorm.klosure.sparql.Variable
import org.junit.jupiter.api.Test
import org.semanticweb.owlapi.model.IRI
import java.io.BufferedReader

@Serializable
data class TestCase(val data: String, val query: String, val expected: String)

@Serializable
data class TestCases(val cases: List<TestCase>)

class ParserTest {

    private fun jsonToSolutions(json: String): Sequence<SolutionMapping> {
        val obj = Json.parseToJsonElement(json) // ..decodeFromString<Results>(json)
        val vars = obj.jsonObject["head"]!!.jsonObject["vars"]!!.jsonArray.map { Variable(it.toString()) }.toSet()
        val results = obj.jsonObject["results"]!!.jsonObject["bindings"]!!.jsonArray.map {
            SolutionMapping(vars, it.jsonObject.entries.associate { entry ->
                Variable(entry.key) to parseValue(entry.value)
            })
        }
        println(results)
        return emptySequence()
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
            } else {  // literal
                val datatype = obj.jsonObject["datatype"]?.jsonPrimitive?.content?.let { IRI.create(it) }
                LiteralId(obj.jsonObject["value"]!!.jsonPrimitive.content, datatype)
            }
        }
    }

    @Test
    fun test1() {
        val fileName = "sparql-star-expr-02.srj"
        val stream = ParserTest::class.java.getResourceAsStream("/me/alejandrorm/klosure/parser/data/sparql/$fileName")!!
        val content = stream.bufferedReader().use(BufferedReader::readText)
        jsonToSolutions(content)
    }
}