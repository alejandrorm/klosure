package me.alejandrorm.klosure.sparql.algebra

import me.alejandrorm.klosure.model.EntailmentTypes
import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.IriId
import me.alejandrorm.klosure.parser.turtle.TurtleStarParser
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.Variable
import me.alejandrorm.klosure.sparql.algebra.operators.BasicTriplePattern
import me.alejandrorm.klosure.sparql.algebra.operators.TermOrVariable
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.semanticweb.owlapi.model.IRI
import java.io.StringReader

class BasicTriplePatternTest {

    private var graph: Graph = Graph(EntailmentTypes.SIMPLE)

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
            :s9 :s9 :o9 .
            :xx1 :yy1 <<:s0 :p0 :o0>> .
            :xx1 :yy1 <<:s0 :p0 <<:s00 :p00 :o00>>>> .
        """.trimIndent()

        val parser = TurtleStarParser(StringReader(ttl))
        graph = Graph(EntailmentTypes.SIMPLE)
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
        val pattern = BasicTriplePattern(
            TermOrVariable.VariableTerm(Variable("x")),
            TermOrVariable.VariableTerm(Variable("y")),
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/o2")))
        )

        val initialSolution = SolutionMapping(pattern.getVariables(), emptyMap())
        val solutions = pattern.eval(initialSolution, graph)

        SolutionSet.compareEqualSet(
            solutions,
            """
            ?x ?y
            <http://example/s1> <http://example/p1>
            _:a <http://example/p1>
            """.trimIndent()
        )
    }

    @Test
    fun testMatchObjectTwoVariablesWithNoMatch() {
        val pattern = BasicTriplePattern(
            TermOrVariable.VariableTerm(Variable("x")),
            TermOrVariable.VariableTerm(Variable("y")),
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/o10")))
        )

        val initialSolution = SolutionMapping(pattern.getVariables(), emptyMap())
        val solutions = pattern.eval(initialSolution, graph)

        Assertions.assertEquals(emptySet<SolutionMapping>(), solutions.toSet())
    }

    @Test
    fun testMatchObjectOneVariableWithMatch() {
        val pattern = BasicTriplePattern(
            TermOrVariable.VariableTerm(Variable("x")),
            TermOrVariable.VariableTerm(Variable("x")),
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/o9")))
        )

        val initialSolution = SolutionMapping(pattern.getVariables(), emptyMap())
        val solutions = pattern.eval(initialSolution, graph)

        SolutionSet.compareEqualSet(
            solutions,
            """
            ?x
            <http://example/s9>
            """.trimIndent()
        )
    }

    @Test
    fun testMatchObjectOneVariableWithNoMatch() {
        val pattern = BasicTriplePattern(
            TermOrVariable.VariableTerm(Variable("x")),
            TermOrVariable.VariableTerm(Variable("x")),
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/o1")))
        )

        val initialSolution = SolutionMapping(pattern.getVariables(), emptyMap())
        val solutions = pattern.eval(initialSolution, graph)

        Assertions.assertEquals(emptySet<SolutionMapping>(), solutions.toSet())
    }

    // TODO test other matching cases

    @Test
    fun testMatchSubjectBound() {
        val pattern = BasicTriplePattern(
            TermOrVariable.VariableTerm(Variable("x")),
            TermOrVariable.VariableTerm(Variable("y")),
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/o1")))
        )

        val initialSolution = SolutionMapping(pattern.getVariables(), mapOf(Variable("x") to IriId(IRI.create("http://example/s1"))))
        val solutions = pattern.eval(initialSolution, graph)

        SolutionSet.compareEqualSet(
            solutions,
            """
            ?x ?y
            <http://example/s1> <http://example/p1>
            """.trimIndent()
        )
    }

    @Test
    fun testNoMatchSubjectBound() {
        val pattern = BasicTriplePattern(
            TermOrVariable.VariableTerm(Variable("x")),
            TermOrVariable.VariableTerm(Variable("y")),
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/o2")))
        )

        val initialSolution = SolutionMapping(pattern.getVariables(), mapOf(Variable("x") to IriId(IRI.create("http://example/x1"))))
        val solutions = pattern.eval(initialSolution, graph)

        Assertions.assertEquals(emptySet<SolutionMapping>(), solutions.toSet())
    }

    @Test
    fun testMatchObjectBound() {
        val pattern = BasicTriplePattern(
            TermOrVariable.VariableTerm(Variable("x")),
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/p1"))),
            TermOrVariable.VariableTerm(Variable("y"))
        )

        val initialSolution = SolutionMapping(pattern.getVariables(), mapOf(Variable("y") to IriId(IRI.create("http://example/o2"))))
        val solutions = pattern.eval(initialSolution, graph)

        SolutionSet.compareEqualSet(
            solutions,
            """
            ?x ?y
            <http://example/s1> <http://example/o2>
            _:a <http://example/o2>
            """.trimIndent()
        )
    }

    @Test
    fun testNoMatchObjectBound() {
    }

    @Test
    fun testMatchPredicateBound() {
    }

    @Test
    fun testNoMatchPredicateBound() {
    }

    @Test
    fun testMatchSubjectObjectBound() {
    }

    @Test
    fun testNoMatchSubjectObjectBound() {
    }

    @Test
    fun testMatchSubjectPredicateBound() {
    }

    @Test
    fun testNoMatchSubjectPredicateBound() {
    }

    @Test
    fun testMatchPredicateObjectBound() {
    }

    @Test
    fun testNoMatchPredicateObjectBound() {
    }
    // TODO test with blank nodes in the pattern

    // TODO test with asserted vs quoted

    @Test
    fun testQuotedNoNestedVariables() {
        val pattern = BasicTriplePattern(
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/xx1"))),
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/yy1"))),
            TermOrVariable.VariableTerm(Variable("x"))
        )

        val initialSolution = SolutionMapping(pattern.getVariables(), emptyMap())
        val solutions = pattern.eval(initialSolution, graph)

        SolutionSet.compareEqualSet(
            solutions,
            """
            ?x
            <<<http://example/s0> <http://example/p0> <http://example/o0>>>
            <<<http://example/s0> <http://example/p0> <<<http://example/s00> <http://example/p00> <http://example/o00>>>>>
            """.trimIndent()
        )
    }

    @Test
    fun testQuotedNestedSubjectSubjectMatchOneVariable() {
        val pattern = BasicTriplePattern(
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/xx1"))),
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/yy1"))),
            TermOrVariable.QuotedTriple(
                TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/s0"))),
                TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/p0"))),
                TermOrVariable.VariableTerm(Variable("x"))
            )
        )

        val initialSolution = SolutionMapping(pattern.getVariables(), emptyMap())
        val solutions = pattern.eval(initialSolution, graph)

        SolutionSet.compareEqualSet(
            solutions,
            """
            ?x
            <http://example/o0>
            <<<http://example/s00> <http://example/p00> <http://example/o00>>>
            """.trimIndent()
        )
    }

    @Test
    fun testQuotedNestedSubjectSubjectSubjectMatchOneVariable() {
        val pattern = BasicTriplePattern(
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/xx1"))),
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/yy1"))),
            TermOrVariable.QuotedTriple(
                TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/s0"))),
                TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/p0"))),
                TermOrVariable.QuotedTriple(
                    TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/s00"))),
                    TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/p00"))),
                    TermOrVariable.VariableTerm(Variable("x"))
                )
            )
        )

        val initialSolution = SolutionMapping(pattern.getVariables(), emptyMap())
        val solutions = pattern.eval(initialSolution, graph)

        SolutionSet.compareEqualSet(
            solutions,
            """
            ?x
            <http://example/o00>
            """.trimIndent()
        )
    }

    @Test
    fun testQuotedNestedSubjectSubjectMatchTwoVariables() {
        val pattern = BasicTriplePattern(
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/xx1"))),
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/yy1"))),
            TermOrVariable.QuotedTriple(
                TermOrVariable.VariableTerm(Variable("x")),
                TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/p0"))),
                TermOrVariable.VariableTerm(Variable("y"))
            )
        )

        val initialSolution = SolutionMapping(pattern.getVariables(), emptyMap())
        val solutions = pattern.eval(initialSolution, graph)

        SolutionSet.compareEqualSet(
            solutions,
            """
            ?x ?y
            <http://example/s0> <http://example/o0>
            <http://example/s0> <<<http://example/s00> <http://example/p00> <http://example/o00>>>
            """.trimIndent()
        )
    }

    @Test
    fun testQuotedNestedSubjectSubjectNoMatchTwoVariables() {
        val pattern = BasicTriplePattern(
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/xx1"))),
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/yy1"))),
            TermOrVariable.QuotedTriple(
                TermOrVariable.VariableTerm(Variable("x")),
                TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/p0"))),
                TermOrVariable.VariableTerm(Variable("x"))
            )
        )

        val initialSolution = SolutionMapping(pattern.getVariables(), emptyMap())
        val solutions = pattern.eval(initialSolution, graph)

        Assertions.assertEquals(emptySet<SolutionMapping>(), solutions.toSet())
    }

    @Test
    fun testQuotedNestedSubjectSubjectNoMatchOneVariable() {
        val pattern = BasicTriplePattern(
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/xx1"))),
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/yy1"))),
            TermOrVariable.QuotedTriple(
                TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/s1"))),
                TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://example/p0"))),
                TermOrVariable.VariableTerm(Variable("x"))
            )
        )

        val initialSolution = SolutionMapping(pattern.getVariables(), emptyMap())
        val solutions = pattern.eval(initialSolution, graph)

        Assertions.assertEquals(emptySet<SolutionMapping>(), solutions.toSet())
    }
}
