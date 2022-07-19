package me.alejandrorm.klosure.sparql.algebra

import TurtleStarParser
import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.IriId
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.Variable
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.semanticweb.owlapi.model.IRI
import java.io.StringReader

class BasicTriplePatternTest {

    private var graph: Graph = Graph()

    @BeforeEach
    fun setUpGraph() {
        val ttl = """
            PREFIX : <http://example/>
            :s1 :p1 :o1 .
            :s1 :p1 :o2 .
            :s1 :p2 :o3 .
            :s4 :q1 :o5 .
            :s4 :q2 :o5 .
            :o3 :p3 :o4 .
            []  :p1 :o2 .
            :s5 :i1 :i1 .
            :s5 :i1 :i2 .
            :s5 :t1 :i1 .
            :s5 :t2 :t2 .
            :s6 :q6 :s6 .
            [] :q6 [] .
            :s7 :q6 :s8 .
        """.trimIndent()

        val parser = TurtleStarParser(StringReader(ttl))
        graph = Graph()
        parser.graph = graph
        parser.turtleDoc()
    }

    @Test
    fun testMatchNoVariables() {
        val pattern = BasicTriplePattern(
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/s1"))),
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/p1"))),
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/o1")))
        )

        val initialSolution = SolutionMapping(pattern.getVariables(), emptyMap())
        val solutions = pattern.eval(initialSolution, graph)

        Assertions.assertEquals(setOf(initialSolution), solutions.toSet())
    }

    @Test
    fun testNoMatch() {
        val pattern = BasicTriplePattern(
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/s1"))),
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/p1"))),
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/o4")))
        )

        val initialSolution = SolutionMapping(pattern.getVariables(), emptyMap())
        val solutions = pattern.eval(initialSolution, graph)

        Assertions.assertEquals(emptySet<SolutionMapping>(), solutions.toSet())
    }

    @Test
    fun testMatchSubjectAndPredicateWithMatch() {
        val pattern = BasicTriplePattern(
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/s1"))),
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/p1"))),
            TermOrVariable.VariableTerm(Variable("x"))
        )

        val initialSolution = SolutionMapping(pattern.getVariables(), emptyMap())
        val solutions = pattern.eval(initialSolution, graph)

        SolutionSet.compareEqualSet(
            solutions,
            """
            ?x
            <http://example/o1>
            <http://example/o2>
            """.trimIndent()
        )
    }

    @Test
    fun testMatchSubjectAndPredicateWithNoMatch() {
        val pattern = BasicTriplePattern(
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/s1"))),
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/p3"))),
            TermOrVariable.VariableTerm(Variable("x"))
        )

        val initialSolution = SolutionMapping(pattern.getVariables(), emptyMap())
        val solutions = pattern.eval(initialSolution, graph)

        Assertions.assertEquals(emptySet<SolutionMapping>(), solutions.toSet())
    }

    @Test
    fun testMatchObjectAndPredicateWithMatch() {
        val pattern = BasicTriplePattern(
            TermOrVariable.VariableTerm(Variable("x")),
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/p1"))),
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/o2")))
        )

        val initialSolution = SolutionMapping(pattern.getVariables(), emptyMap())
        val solutions = pattern.eval(initialSolution, graph)

        SolutionSet.compareEqualSet(
            solutions,
            """
            ?x
            <http://example/s1>
            _:a
            """.trimIndent()
        )
    }

    @Test
    fun testMatchObjectAndPredicateWithNoMatch() {
        val pattern = BasicTriplePattern(
            TermOrVariable.VariableTerm(Variable("x")),
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/q1"))),
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/o1")))
        )

        val initialSolution = SolutionMapping(pattern.getVariables(), emptyMap())
        val solutions = pattern.eval(initialSolution, graph)

        Assertions.assertEquals(emptySet<SolutionMapping>(), solutions.toSet())
    }

    @Test
    fun testMatchSubjectAndObjectWithMatch() {
        val pattern = BasicTriplePattern(
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/s4"))),
            TermOrVariable.VariableTerm(Variable("x")),
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/o5")))
        )

        val initialSolution = SolutionMapping(pattern.getVariables(), emptyMap())
        val solutions = pattern.eval(initialSolution, graph)

        SolutionSet.compareEqualSet(
            solutions,
            """
            ?x
            <http://example/q1>
            <http://example/q2>
            """.trimIndent()
        )
    }

    @Test
    fun testMatchSubjectAndObjectWithNoMatch() {
        val pattern = BasicTriplePattern(
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/s4"))),
            TermOrVariable.VariableTerm(Variable("x")),
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/o7")))
        )

        val initialSolution = SolutionMapping(pattern.getVariables(), emptyMap())
        val solutions = pattern.eval(initialSolution, graph)
        Assertions.assertEquals(emptySet<SolutionMapping>(), solutions.toSet())
    }

    @Test
    fun testMatchSubjectTwoVariablesWithMatch() {
        val pattern = BasicTriplePattern(
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/s1"))),
            TermOrVariable.VariableTerm(Variable("x")),
            TermOrVariable.VariableTerm(Variable("y"))
        )

        val initialSolution = SolutionMapping(pattern.getVariables(), emptyMap())
        val solutions = pattern.eval(initialSolution, graph)

        SolutionSet.compareEqualSet(
            solutions,
            """
            ?x ?y
            <http://example/p1> <http://example/o1>
            <http://example/p1> <http://example/o2>
            <http://example/p2> <http://example/o3>
            """.trimIndent()
        )
    }

    @Test
    fun testMatchSubjectTwoVariablesWithNoMatch() {
        val pattern = BasicTriplePattern(
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/NotInGraph"))),
            TermOrVariable.VariableTerm(Variable("x")),
            TermOrVariable.VariableTerm(Variable("y"))
        )

        val initialSolution = SolutionMapping(pattern.getVariables(), emptyMap())
        val solutions = pattern.eval(initialSolution, graph)

        Assertions.assertEquals(emptySet<SolutionMapping>(), solutions.toSet())
    }

    @Test
    fun testMatchSubjectOneVariableWithMatch() {
        val pattern = BasicTriplePattern(
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/s5"))),
            TermOrVariable.VariableTerm(Variable("x")),
            TermOrVariable.VariableTerm(Variable("x"))
        )

        val initialSolution = SolutionMapping(pattern.getVariables(), emptyMap())
        val solutions = pattern.eval(initialSolution, graph)

        SolutionSet.compareEqualSet(
            solutions,
            """
            ?x
            <http://example/i1>
            <http://example/t2>
            """.trimIndent()
        )
    }

    @Test
    fun testMatchSubjectOneVariableWithNoMatch() {
        val pattern = BasicTriplePattern(
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/s1"))),
            TermOrVariable.VariableTerm(Variable("x")),
            TermOrVariable.VariableTerm(Variable("x"))
        )

        val initialSolution = SolutionMapping(pattern.getVariables(), emptyMap())
        val solutions = pattern.eval(initialSolution, graph)

        Assertions.assertEquals(emptySet<SolutionMapping>(), solutions.toSet())
    }

    @Test
    fun testMatchPredicateOneVariableWithMatch() {
        val pattern = BasicTriplePattern(
            TermOrVariable.VariableTerm(Variable("x")),
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/q6"))),
            TermOrVariable.VariableTerm(Variable("x"))
        )

        val initialSolution = SolutionMapping(pattern.getVariables(), emptyMap())
        val solutions = pattern.eval(initialSolution, graph)

        SolutionSet.compareEqualSet(
            solutions,
            """
            ?x
            <http://example/s6>
            """.trimIndent()
        )
    }

    @Test
    fun testMatchPredicateOneVariableWithNoMatch() {
        val pattern = BasicTriplePattern(
            TermOrVariable.VariableTerm(Variable("x")),
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/p1"))),
            TermOrVariable.VariableTerm(Variable("x"))
        )

        val initialSolution = SolutionMapping(pattern.getVariables(), emptyMap())
        val solutions = pattern.eval(initialSolution, graph)

        Assertions.assertEquals(emptySet<SolutionMapping>(), solutions.toSet())
    }

    @Test
    fun testMatchPredicateTwoVariablesWithMatch() {
        val pattern = BasicTriplePattern(
            TermOrVariable.VariableTerm(Variable("x")),
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/q6"))),
            TermOrVariable.VariableTerm(Variable("y"))
        )

        val initialSolution = SolutionMapping(pattern.getVariables(), emptyMap())
        val solutions = pattern.eval(initialSolution, graph)

        SolutionSet.compareEqualSet(
            solutions,
            """
            ?x ?y
            <http://example/s6> <http://example/s6>
            _:a _:b
            <http://example/s7> <http://example/s8>
            """.trimIndent()
        )
    }

    @Test
    fun testMatchPredicateTwoVariablesWithNoMatch() {
        val pattern = BasicTriplePattern(
            TermOrVariable.VariableTerm(Variable("x")),
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/r6"))),
            TermOrVariable.VariableTerm(Variable("y"))
        )

        val initialSolution = SolutionMapping(pattern.getVariables(), emptyMap())
        val solutions = pattern.eval(initialSolution, graph)

        Assertions.assertEquals(emptySet<SolutionMapping>(), solutions.toSet())
    }

    @Test
    fun testMatchObjectTwoVariablesWithMatch() {
    }

    @Test
    fun testMatchObjectTwoVariablesWithNoMatch() {
        // Assertions.assertEquals(emptySet<SolutionMapping>(), solutions.toSet())
    }

    @Test
    fun testMatchObjectOneVariableWithMatch() {
    }

    @Test
    fun testMatchObjectOneVariableWithNoMatch() {
        // Assertions.assertEquals(emptySet<SolutionMapping>(), solutions.toSet())
    }

    // TODO test other matching cases

    // TODO test all matching cases when initial solution binds some variables

    // TODO test with blank nodes in the pattern

    // TODO test with asserted vs quoted
}
