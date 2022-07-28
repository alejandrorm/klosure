package me.alejandrorm.klosure.sparql.algebra.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.Variable

class Project(val variables: List<Variable>): AlgebraOperator {
    private val variablesSet = variables.toSet()

    override fun toString(): String {
        return "Project(${variables.joinToString(", ")})"
    }

    override fun eval(solutions: Sequence<SolutionMapping>, graph: Graph): Sequence<SolutionMapping> {
        return if (variables.isEmpty()) solutions
        else
            solutions.map { solution ->
                SolutionMapping(solution.variables.intersect(variablesSet),
                    solution.boundVariables.filterKeys { variablesSet.contains(it) }) }
    }
}
