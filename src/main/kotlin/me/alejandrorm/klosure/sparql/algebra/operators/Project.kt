package me.alejandrorm.klosure.sparql.algebra.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.Graphs
import me.alejandrorm.klosure.sparql.SolutionMapping

data class ProjectArguments(val distinct: Boolean, val variables: List<ExpressionVariableBinding>)

class Project(val args: ProjectArguments,
              val op: AlgebraOperator) : AlgebraOperator {
    private val variablesSet = args.variables.map { it.variable }.toSet()

    override fun toString(): String {
        return "Project('${if (args.variables.isEmpty()) "*" else args.variables.joinToString(", ")}', $op)"
    }

    override fun eval(solutions: Sequence<SolutionMapping>, activeGraph: Graph, graphs: Graphs): Sequence<SolutionMapping> {
        return if (args.variables.isEmpty()) {
            op.eval(solutions, activeGraph, graphs)
        } else {
            if (args.distinct) {
                op.eval(solutions, activeGraph, graphs).map { solution ->
                    SolutionMapping(
                        variablesSet,
                        args.variables.flatMap {
                            val value = it.expression.eval(solution, activeGraph)
                            if (value == null) emptyList() else listOf(it.variable to value)
                        }.toMap()
                    )
                }.distinct()
            } else {
                op.eval(solutions, activeGraph, graphs).map { solution ->
                    SolutionMapping(
                        variablesSet,
                        args.variables.flatMap {
                            val value = it.expression.eval(solution, activeGraph)
                            if (value == null) emptyList() else listOf(it.variable to value)
                        }.toMap()
                    )
                }
            }
        }
    }

    override fun hasFilter(): Boolean = false
}
