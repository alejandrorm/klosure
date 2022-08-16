package me.alejandrorm.klosure.sparql.algebra.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.Graphs
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.aggregates.AggregateExpression

data class ProjectArguments(val distinct: Boolean, val variables: List<ExpressionVariableBinding>)

class Project(val args: ProjectArguments, val sm: SolutionModifiers,
              val op: AlgebraOperator) : AlgebraOperator {
    private val variablesSet = args.variables.map { it.variable }.toSet()

    override fun toString(): String {
        return "Project('${if (args.variables.isEmpty()) "*" else args.variables.joinToString(", ")}', $op)"
    }

    override fun eval(solutions: Sequence<SolutionMapping>, activeGraph: Graph, graphs: Graphs): Sequence<SolutionMapping> {
        return if (args.variables.isEmpty()) {
            sm.eval(op.eval(solutions, activeGraph, graphs)).map {
                if (it.groups.any()) {
                    throw IllegalArgumentException("Projecting variables not aggregated or grouped by")
                } else {
                    SolutionMapping(emptySet(), it.boundVariables.boundVariables)
                }
            }
        } else {
            if (args.distinct) {
                getMultisetSolutions(solutions, activeGraph, graphs).distinct()
            } else {
                getMultisetSolutions(solutions, activeGraph, graphs)
            }
        }
    }

    override fun hasFilter(): Boolean = false

    private fun getMultisetSolutions(solutions: Sequence<SolutionMapping>,
                                     activeGraph: Graph, graphs: Graphs): Sequence<SolutionMapping> {
        return sm.eval(op.eval(solutions, activeGraph, graphs)).map { solution ->
            SolutionMapping(
                variablesSet,
                args.variables.flatMap {
                    val expression = it.expression
                    // TODO report error if expression on variables not aggregated or grouped by
                    val value = expression.evalGroup(solution.boundVariables, solution.groups)

                    if (value == null) emptyList() else listOf(it.variable to value)
                }.toMap()
            )
        }
    }
}
