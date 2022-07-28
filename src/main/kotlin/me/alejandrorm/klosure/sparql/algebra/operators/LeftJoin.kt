package me.alejandrorm.klosure.sparql.algebra.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.sparql.SolutionMapping

class LeftJoin(val operator: AlgebraOperator) : AlgebraOperator {
    override fun toString(): String =
        "LeftJoin($operator)"

    override fun eval(
        solutions: Sequence<SolutionMapping>,
        graph: Graph
    ): Sequence<SolutionMapping> {
        return solutions.flatMap { solution ->
            operator.eval(sequenceOf(solution), graph).ifEmpty { sequenceOf(solution) }
        }
    }
}
