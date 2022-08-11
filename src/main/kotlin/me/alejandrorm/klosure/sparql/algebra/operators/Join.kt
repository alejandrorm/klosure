package me.alejandrorm.klosure.sparql.algebra.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.Graphs
import me.alejandrorm.klosure.sparql.SolutionMapping

class Join(val operators: List<AlgebraOperator>) : AlgebraOperator {

    private val operatorsReordered: List<AlgebraOperator> = operators.filter { it !is Filter } +
            operators.filterIsInstance<Filter>()

    override fun toString(): String {
        return "Join(${operators.joinToString(", ")})"
    }

    override fun eval(
        solutions: Sequence<SolutionMapping>,
        activeGraph: Graph,
        graphs: Graphs
    ): Sequence<SolutionMapping> {
        return operatorsReordered.fold(solutions) { acc, op ->
            topLevelJoin(acc, op, activeGraph, graphs)
        }
    }

    private fun topLevelJoin(l1: Sequence<SolutionMapping>,
                             operator: AlgebraOperator,
                             graph: Graph,
                             graphs: Graphs
    ): Sequence<SolutionMapping> {
        return if (operator is Filter) {
            operator.eval(l1, graph, graphs)
        } else {
            return join(l1, operator, graph, graphs)
        }
    }

    private fun join(
        l1: Sequence<SolutionMapping>,
        operator: AlgebraOperator,
        graph: Graph,
        graphs: Graphs
    ): Sequence<SolutionMapping> = sequence {
        val l2 =
            if (operator is GraphGraphPattern)
                operator.specialEval(l1, sequenceOf(SolutionMapping.EmptySolutionMapping), graphs).toList()
            else
                operator.eval(sequenceOf(SolutionMapping.EmptySolutionMapping), graph, graphs).toList()

        for (solution1 in l1) {
            for (solution2 in l2) {
                if (solution1.isCompatible(solution2)) {
                    yield(solution1.merge(solution2))
                }
            }
        }
    }

    override fun hasFilter(): Boolean = operators.any { it.hasFilter() }
}
