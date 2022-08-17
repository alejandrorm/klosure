package me.alejandrorm.klosure.sparql.algebra.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.Graphs
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.filters.Expression
import me.alejandrorm.klosure.sparql.algebra.filters.getEffectiveBooleanValue
import me.alejandrorm.klosure.sparql.algebra.filters.operators.AndExpression

class FakeLeftJoin(val operator: AlgebraOperator) : AlgebraOperator {
    override fun eval(
        solutions: Sequence<SolutionMapping>,
        activeGraph: Graph,
        graphs: Graphs
    ): Sequence<SolutionMapping> {
        throw IllegalStateException("This operator should not be actually called, create a LeftJoin instead")
    }

    override fun hasFilter(): Boolean {
        throw IllegalStateException("This operator should not be actually called, create a LeftJoin instead")
    }
}

class LeftJoin(val left: AlgebraOperator, val right: AlgebraOperator) : AlgebraOperator {

    private val nonFilterOperator: AlgebraOperator
    private val filterExpression: Expression?

    init {
        when (right) {
            is Join -> {
                val nonFilterOperations = right.operators.filter { it !is Filter }
                nonFilterOperator =
                    when (nonFilterOperations.size) {
                        0 -> Identity()
                        1 -> nonFilterOperations[0]
                        else -> Join(nonFilterOperations)
                    }
                val filters = right.operators.filterIsInstance<Filter>()
                filterExpression = when (filters.size) {
                    0 -> null
                    1 -> filters[0].expression
                    else ->
                        filters.map { it.expression }.reduce { acc, filter ->
                            AndExpression(acc, filter)
                        }
                }
            }
            is Filter -> {
                nonFilterOperator = Identity()
                filterExpression = right.expression
            }
            else -> {
                nonFilterOperator = right
                filterExpression = null
            }
        }
    }

    override fun toString(): String =
        "LeftJoin($left, $nonFilterOperator, ${filterExpression ?: "true"})"

    override fun eval(
        solutions: Sequence<SolutionMapping>,
        activeGraph: Graph,
        graphs: Graphs
    ): Sequence<SolutionMapping> {
        return join(solutions, activeGraph, graphs)
    }

    private fun join(
        solutions: Sequence<SolutionMapping>,
        graph: Graph,
        graphs: Graphs
    ): Sequence<SolutionMapping> = sequence {
        val l1 = left.eval(solutions, graph, graphs)
        val l2 =
            if (nonFilterOperator is GraphGraphPattern) {
                nonFilterOperator.specialEval(
                    solutions,
                    sequenceOf(SolutionMapping.EmptySolutionMapping),
                    graphs
                ).toList()
            } else {
                nonFilterOperator.eval(sequenceOf(SolutionMapping.EmptySolutionMapping), graph, graphs).toList()
            }

        for (solution1 in l1) {
            var yielded = false
            for (solution2 in l2) {
                if (solution1.isCompatible(solution2)) {
                    val mergedSolution = solution1.merge(solution2)
                    if (filterExpression == null || getEffectiveBooleanValue(
                            filterExpression.eval(
                                    mergedSolution
                                )
                        ) == true
                    ) {
                        yielded = true
                        yield(mergedSolution)
                    }
                }
            }
            if (!yielded) {
                yield(solution1)
            }
        }
    }

    override fun hasFilter(): Boolean {
        return filterExpression != null
    }
}
