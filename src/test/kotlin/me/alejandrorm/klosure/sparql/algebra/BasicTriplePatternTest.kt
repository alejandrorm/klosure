package me.alejandrorm.klosure.sparql.algebra

import TurtleStarParser
import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.IriId
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.Variable
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.semanticweb.owlapi.model.IRI
import java.io.StringReader

class BasicTriplePatternTest {

    private var graph: Graph = Graph()

    companion object {}

    @BeforeEach
    fun setUpGraph() {
        val ttl = """
            PREFIX : <http://example/>
            :s1 :p1 :o1 .
            :s1 :p1 :o2 .
            :s1 :p2 :o3 .
            :o3 :p3 :o4 .
        """.trimIndent()

        val parser = TurtleStarParser(StringReader(ttl))
        graph = Graph()
        parser.graph = graph
        parser.turtleDoc()
    }

    @Test
    fun testMatchNoVariables() {
        val pattern = BasicTriplePattern(
            TermOrVariable.NodeTerm(graph.getNode(IriId(IRI.create("http://example/s1")))!!),
            TermOrVariable.NodeTerm(graph.getNode(IriId(IRI.create("http://example/p1")))!!),
            TermOrVariable.NodeTerm(graph.getNode(IriId(IRI.create("http://example/o1")))!!)
        )
        val initialSolution = SolutionMapping(pattern.getVariables(), emptyMap())
        val solutions = pattern.eval(initialSolution, graph)

        Assertions.assertEquals(setOf(initialSolution), solutions.toSet())
    }

    @Test
    fun testNoMatch() {
        val pattern = BasicTriplePattern(
            TermOrVariable.NodeTerm(graph.getNode(IriId(IRI.create("http://example/s1")))!!),
            TermOrVariable.NodeTerm(graph.getNode(IriId(IRI.create("http://example/p1")))!!),
            TermOrVariable.NodeTerm(graph.getNode(IriId(IRI.create("http://example/o4")))!!)
        )
        val initialSolution = SolutionMapping(pattern.getVariables(), emptyMap())
        val solutions = pattern.eval(initialSolution, graph)

        Assertions.assertEquals(emptySet<SolutionMapping>(), solutions.toSet())
    }

    @Test
    fun testMatchSubjectAndPredicate() {
        val pattern = BasicTriplePattern(
            TermOrVariable.NodeTerm(graph.getNode(IriId(IRI.create("http://example/s1")))!!),
            TermOrVariable.NodeTerm(graph.getNode(IriId(IRI.create("http://example/p1")))!!),
            TermOrVariable.VariableTerm(Variable("x"))
        )
        val initialSolution = SolutionMapping(pattern.getVariables(), emptyMap())
        val solutions = pattern.eval(initialSolution, graph)

        Assertions.assertEquals(
            setOf(
                SolutionMapping(
                    setOf(Variable("x")),
                    mapOf(Variable("x") to graph.getNode(IriId(IRI.create("http://example/o1")))!!)
                ),
                SolutionMapping(
                    setOf(Variable("x")),
                    mapOf(Variable("x") to graph.getNode(IriId(IRI.create("http://example/o2")))!!)
                )
            ), solutions.toSet()
        )
    }

    // TODO test other matching cases

    // TODO test all matching cases when initial solution binds some variables
}