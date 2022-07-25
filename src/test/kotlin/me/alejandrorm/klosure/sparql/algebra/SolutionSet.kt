package me.alejandrorm.klosure.sparql.algebra

import me.alejandrorm.klosure.parser.TripleComparator
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.Variable
import org.hamcrest.MatcherAssert

class SolutionSet(
    val variables: List<Variable>,
    val values: List<String>
) {
    constructor(variables: List<String>, values: String) : this(variables.map { Variable(it) }, values.split("\n"))

    fun toStringList(solutions: Sequence<SolutionMapping>): List<String> {
        return solutions.asSequence().map { solution ->
            variables.joinToString(" ") { variable ->
                if (solution.boundVariables.containsKey(variable)) solution.boundVariables[variable].toString()
                else "null"
            }
        }.toList()
    }

    companion object {
        @JvmStatic
        fun compareEqualSet(actual: Sequence<SolutionMapping>, expected: String) {
            val parts = expected.split(Regex("\n"), 2)
            val variablePattern = Regex("\\?([a-zA-z0-9]+)")
            val expectedSolution = SolutionSet(variablePattern.findAll(parts[0]).map { it.groups[1]!!.value }.toList(), parts[1])

            MatcherAssert.assertThat(
                expectedSolution.values.toSet(),
                TripleComparator.areEqualTriples(expectedSolution.toStringList(actual).toSet())
            )
        }

        @JvmStatic
        fun compareEqualSet(actual: Sequence<SolutionMapping>, variables: List<Variable>, expected: Sequence<SolutionMapping>) {
            val expectedSolution = SolutionSet(
                variables,
                expected.map {
                        solution ->
                    variables.joinToString(" ") { variable ->
                        if (solution.boundVariables.containsKey(variable)) solution.boundVariables[variable].toString()
                        else "null"
                    }
                }.toList()
            )

            MatcherAssert.assertThat(
                expectedSolution.values.toSet(),
                TripleComparator.areEqualTriples(expectedSolution.toStringList(actual).toSet())
            )
        }
    }
}
