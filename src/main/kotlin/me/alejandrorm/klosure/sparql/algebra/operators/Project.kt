package me.alejandrorm.klosure.sparql.algebra.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.Graphs
import me.alejandrorm.klosure.sparql.SolutionMapping

data class ProjectArguments(val distinct: Boolean, val variables: List<ExpressionVariableBinding>)

class Project(
    val args: ProjectArguments,
    val sm: SolutionModifiers,
    val op: AlgebraOperator
) : AlgebraOperator {
    private val variablesSet = args.variables.map { it.variable }.toSet()

    private val hasAggregates = args.variables.any { it.expression.isAggregate() }
    private val allAggregates = args.variables.all { it.expression.isAggregate() }
    private val hasGroups = sm.groupBy != null

    init {
        if (hasAggregates && !hasGroups && !allAggregates) {
            throw IllegalArgumentException("Projection cannot have some aggregates without having a group by")
        }
    }
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

    private fun getMultisetSolutions(
        solutions: Sequence<SolutionMapping>,
        activeGraph: Graph,
        graphs: Graphs
    ): Sequence<SolutionMapping> {
        val results = sm.eval(op.eval(solutions, activeGraph, graphs))
        if (allAggregates && !hasGroups) {
            // FIXME: solution group is being iterated multiple times
            val solutionGroup = results.map { it.boundVariables }
            return sequenceOf(
                SolutionMapping(
                    emptySet(),
                    args.variables.flatMap {
                        val expression = it.expression
                        // TODO report error if expression on variables not aggregated or grouped by
                        val value = expression.evalGroup(SolutionMapping.EmptySolutionMapping, solutionGroup)

                        if (value == null) emptyList() else listOf(it.variable to value)
                    }.toMap()
                )
            )
        } else {
            return results.map { solution ->
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
}
