package me.alejandrorm.klosure.sparql.algebra.path

import TurtleStarParser
import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.IriId
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.Variable
import me.alejandrorm.klosure.sparql.algebra.SolutionSet
import me.alejandrorm.klosure.sparql.algebra.operators.TermOrVariable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.semanticweb.owlapi.model.IRI
import java.io.StringReader
import kotlin.test.assertEquals

class PathTest {

    private var graph: Graph = Graph()

    @BeforeEach
    fun setUpGraph() {
        val ttl = """
            PREFIX : <http://example/>
            @prefix  foaf: <http://xmlns.com/foaf/0.1/> .
            :a0 foaf:knows :a1 .
            :a1 foaf:knows :a2 .
            :a2 foaf:knows :a3 .
            :a3 foaf:knows :a4 .
            :b0 foaf:knows :b1 .
            :b0 foaf:knows :b2 .
            :b2 foaf:knows :b3 .
            :b3 foaf:knows :b2 .
            :b3 foaf:knows :b4 .
            :a0 :p :a1 .
            :a0 :p :b1 .
            :a1 :q :a2 .
            :a1 :q :b2 .
            :a2 :r :a3 .
            :a2 :r :b3 .
        """.trimIndent()

        val parser = TurtleStarParser(StringReader(ttl))
        graph = Graph()
        parser.graph = graph
        parser.turtleDoc()
    }

    @Test
    fun basicIri() {
        val term1 = TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/a0")))
        val var1 = TermOrVariable.VariableTerm(Variable("x"))
        val path = IriPath(IRI.create("http://xmlns.com/foaf/0.1/knows")).compile(term1, var1)

        val s = path.eval(SolutionMapping.EmptySolutionMapping, graph)

        SolutionSet.compareEqualSet(
            s.map { it.filterOutAnonymousVariables() },
            """
            ?x
            <http://example/a1>
            """.trimIndent()
        )
    }

    @Test
    fun basicSequenceTailVariable() {
        val p = IriPath(IRI.create("http://example/p"))
        val q = IriPath(IRI.create("http://example/q"))
        val r = IriPath(IRI.create("http://example/r"))

        val term1 = TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/a0")))
        val var1 = TermOrVariable.VariableTerm(Variable("x"))

        val seq = SequencePath(listOf(p, q, r)).compile(term1, var1)

        val s = seq.eval(SolutionMapping.EmptySolutionMapping, graph)

        SolutionSet.compareEqualSet(
            s.map { it.filterOutAnonymousVariables() },
            """
            ?x
            <http://example/a3>
            <http://example/b3>
            """.trimIndent()
        )
    }

    @Test
    fun basicSequenceHeadVariable() {
        val p = IriPath(IRI.create("http://example/p"))
        val q = IriPath(IRI.create("http://example/q"))
        val r = IriPath(IRI.create("http://example/r"))

        val term1 = TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/b3")))
        val var1 = TermOrVariable.VariableTerm(Variable("x"))

        val seq = SequencePath(listOf(p, q, r)).compile(var1, term1)

        val s = seq.eval(SolutionMapping.EmptySolutionMapping, graph)

        SolutionSet.compareEqualSet(
            s.map { it.filterOutAnonymousVariables() },
            """
            ?x
            <http://example/a0>
            """.trimIndent()
        )
    }

    @Test
    fun basicOneOrMore() {
        val p = OneOrMorePath(IriPath(IRI.create("http://xmlns.com/foaf/0.1/knows")))
            .compile(
                TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/a0"))),
                TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/a4")))
            )

        val s = p.eval(SolutionMapping.EmptySolutionMapping, graph)

        assertEquals(setOf(SolutionMapping.EmptySolutionMapping), s.map { it.filterOutAnonymousVariables() }.toSet())
    }

    @Test
    fun basicOneOrMoreNoMatch() {
        val p = OneOrMorePath(IriPath(IRI.create("http://xmlns.com/foaf/0.1/knows")))
            .compile(
                TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/a0"))),
                TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/b0")))
            )

        val s = p.eval(SolutionMapping.EmptySolutionMapping, graph)

        assertEquals(emptySet(), s.map { it.filterOutAnonymousVariables() }.toSet())
    }

    @Test
    fun oneStepOneOrMore() {
        val p = OneOrMorePath(IriPath(IRI.create("http://xmlns.com/foaf/0.1/knows")))
            .compile(
                TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/a0"))),
                TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/a1")))
            )

        val s = p.eval(SolutionMapping.EmptySolutionMapping, graph)

        assertEquals(setOf(SolutionMapping.EmptySolutionMapping), s.map { it.filterOutAnonymousVariables() }.toSet())
    }

    @Test
    fun loopDetectionOneOrMore() {
        val p = OneOrMorePath(IriPath(IRI.create("http://xmlns.com/foaf/0.1/knows")))
            .compile(
                TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/b0"))),
                TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/b4")))
            )

        val s = p.eval(SolutionMapping.EmptySolutionMapping, graph)

        assertEquals(setOf(SolutionMapping.EmptySolutionMapping), s.map { it.filterOutAnonymousVariables() }.toSet())
    }
}
