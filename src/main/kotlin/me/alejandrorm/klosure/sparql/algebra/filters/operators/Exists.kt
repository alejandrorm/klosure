package me.alejandrorm.klosure.sparql.algebra.filters.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.Graphs
import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.model.literals.BooleanValue
import me.alejandrorm.klosure.model.literals.DataTypes
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.aggregates.NonAggregateExpression
import me.alejandrorm.klosure.sparql.algebra.operators.AlgebraOperator

class Exists(val operator: AlgebraOperator) : NonAggregateExpression {
    override fun eval(solution: SolutionMapping, activeGraph: Graph, graphs: Graphs): NodeId {
        return if (operator.eval(sequenceOf(solution), activeGraph, graphs).any()) {
            DataTypes.TRUE
        } else {
            DataTypes.FALSE
        }
    }
}
