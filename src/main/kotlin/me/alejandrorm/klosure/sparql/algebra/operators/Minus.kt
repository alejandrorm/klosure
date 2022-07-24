package me.alejandrorm.klosure.sparql.algebra.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.sparql.SolutionMapping

class Minus(val operator: AlgebraOperator) : AlgebraOperator {
    // FIXME operator should not be evaluated in the solutions of the left member of the MINUS
    override fun eval(solutions: Sequence<SolutionMapping>, graph: Graph): Sequence<SolutionMapping> {
        return solutions.minus(operator.eval(solutions, graph))
    }
}
