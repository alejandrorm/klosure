package me.alejandrorm.klosure.sparql.algebra.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.Variable

class Project(val distinct: Boolean, val variables: List<ExpressionVariableBinding>) : AlgebraOperator {
    private val variablesSet = variables.map { it.variable }.toSet()

    override fun toString(): String {
        return "Project(${variables.joinToString(", ")})"
    }

    override fun eval(solutions: Sequence<SolutionMapping>, graph: Graph): Sequence<SolutionMapping> {
        return if (variables.isEmpty()) {
            solutions
        } else {
            if (distinct) {
                solutions.map { solution ->
                    SolutionMapping(
                        variablesSet,
                        variables.flatMap {
                            val value = it.expression.eval(solution, graph)
                            if (value == null) emptyList() else listOf(it.variable to value)
                        }.toMap()
                    )
                }.distinct()
            } else {
                solutions.map { solution ->
                    SolutionMapping(
                        variablesSet,
                        variables.flatMap {
                            val value = it.expression.eval(solution, graph)
                            if (value == null) emptyList() else listOf(it.variable to value)
                        }.toMap()
                    )
                }
            }
        }
    }
}
