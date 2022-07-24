package me.alejandrorm.klosure.sparql.algebra.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.sparql.SolutionMapping

interface AlgebraOperator {
    fun eval(solutions: Sequence<SolutionMapping>, graph: Graph): Sequence<SolutionMapping>
}
