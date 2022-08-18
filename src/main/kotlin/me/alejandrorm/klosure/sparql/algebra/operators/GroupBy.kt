package me.alejandrorm.klosure.sparql.algebra.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.Graphs
import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.sparql.GroupedSolutionMapping
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.Variable
import me.alejandrorm.klosure.sparql.algebra.filters.Expression

sealed class GroupCondition

data class VarGroupCondition(val variable: Variable) : GroupCondition()

data class BuiltinGroupCondition(val expression: Expression) : GroupCondition()

data class ExpressionGroupCondition(val expression: Expression, val variable: Variable) : GroupCondition()

class GroupBy(val groupConditions: List<GroupCondition>) {
    private val groupedVariables = groupConditions.indices.map {
        when (val c = groupConditions[it]) {
            is VarGroupCondition -> c.variable to c
            is BuiltinGroupCondition -> Variable("_col_${it + 1})") to c
            is ExpressionGroupCondition -> c.variable to c
        }
    }

    override fun toString(): String {
        return "GroupBy($groupConditions)"
    }

    fun eval(
        solutions: Sequence<SolutionMapping>,
        activeGraph: Graph,
        graphs: Graphs
    ): Sequence<GroupedSolutionMapping> {
        return solutions.groupBy { solution ->
            groupedVariables.map {
                when (val c = it.second) {
                    is VarGroupCondition -> it.first to solution.boundVariables[c.variable]
                    is BuiltinGroupCondition -> it.first to c.expression.eval(solution, activeGraph, graphs)
                    is ExpressionGroupCondition -> it.first to c.expression.eval(solution, activeGraph, graphs)
                }
            }
        }.entries.map { solution ->
            val variables = solution.key.map { it.first }.toSet()

            @Suppress("UNCHECKED_CAST")
            val boundVariables = solution.key.filter { it.second != null }.toMap() as Map<Variable, NodeId>

            GroupedSolutionMapping(SolutionMapping(variables, boundVariables), solution.value.asSequence())
        }.asSequence()
    }
}
