package me.alejandrorm.klosure.sparql.algebra.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.literals.DataTypes
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.filters.Expression
import me.alejandrorm.klosure.sparql.algebra.filters.getEffectiveBooleanValue
import me.alejandrorm.klosure.sparql.algebra.filters.operators.AndExpression

class LeftJoin(val operator: AlgebraOperator) : AlgebraOperator {

    private val nonFilterOperator: AlgebraOperator
    private val filterExpression: Expression?

    init {
        if (operator is Join) {
            nonFilterOperator = Join(operator.operators.filter { it !is Filter })
            val filters = operator.operators.filterIsInstance<Filter>()

            filterExpression = when (filters.size) {
                0 -> null
                1 -> filters[0].expression
                else ->
                    // TODO should this be a reduceRight or reduceLeft?
                    filters.map { it.expression }.reduce { acc, filter ->
                        AndExpression(acc, filter)
                    }
            }
        } else {
            nonFilterOperator = operator
            filterExpression = null
        }
    }

    override fun toString(): String =
        "LeftJoin($operator)"

    override fun eval(
        solutions: Sequence<SolutionMapping>,
        graph: Graph
    ): Sequence<SolutionMapping> {
        return join(solutions, nonFilterOperator, graph)
    }

    private fun join(
        l1: Sequence<SolutionMapping>,
        operator: AlgebraOperator,
        graph: Graph
    ): Sequence<SolutionMapping> = sequence {
        val l2 = operator.eval(sequenceOf(SolutionMapping.EmptySolutionMapping), graph).toList()

        for (solution1 in l1) {
            var yielded = false
            for (solution2 in l2) {
                if (solution1.isCompatible(solution2)) {
                    val mergedSolution = solution1.merge(solution2)
                    if (filterExpression == null || getEffectiveBooleanValue(
                            filterExpression.eval(
                                mergedSolution,
                                graph
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
}
