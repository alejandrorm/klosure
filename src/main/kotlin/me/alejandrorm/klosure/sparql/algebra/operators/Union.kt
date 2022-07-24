package me.alejandrorm.klosure.sparql.algebra.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.sparql.SolutionMapping

class Union(val operators: List<AlgebraOperator>) : AlgebraOperator {
    override fun eval(solutions: Sequence<SolutionMapping>, graph: Graph): Sequence<SolutionMapping> {
        return if (operators.count() == 1) {
            operators[0].eval(solutions, graph)
        } else {
            return addAll(solutions, operators, graph)
        }
    }

    // FIXME operators should not be evaluated on the solutions of the first member of the union
    private fun addAll(
        solutions: Sequence<SolutionMapping>,
        operators: List<AlgebraOperator>,
        graph: Graph
    ): Sequence<SolutionMapping> {
        return if (operators.isEmpty()) emptySequence() else {
            operators.first().eval(solutions, graph) + addAll(solutions, operators.drop(1), graph)
        }
    }
}
