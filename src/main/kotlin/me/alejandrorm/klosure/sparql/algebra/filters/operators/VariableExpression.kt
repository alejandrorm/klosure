package me.alejandrorm.klosure.sparql.algebra.filters.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.Graphs
import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.Variable
import me.alejandrorm.klosure.sparql.algebra.aggregates.NonAggregateExpression

class VariableExpression(val variable: Variable) : NonAggregateExpression {
    override fun eval(solution: SolutionMapping, activeGraph: Graph, graphs: Graphs): NodeId? {
        return solution.boundVariables[variable]
    }

    override fun toString(): String {
        return variable.toString()
    }
}
