package me.alejandrorm.klosure.sparql.algebra.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.sparql.SolutionMapping

class Join(val operators: List<AlgebraOperator>) : AlgebraOperator {
    override fun toString(): String {
        return "Join(${operators.joinToString(", ")})"
    }

    override fun eval(
        solutions: Sequence<SolutionMapping>,
        graph: Graph
    ): Sequence<SolutionMapping> {
        return operators.fold(solutions) { acc, gp -> gp.eval(acc, graph) }
    }
}
