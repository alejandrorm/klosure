package me.alejandrorm.klosure.sparql.algebra.filters.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.filters.Expression

enum class MultiplicativeOperator(val symbol: String) {
    TIMES("*"),
    BY("/")
}

data class MultiplicativeOperatorOperand(val operator: MultiplicativeOperator, val operand: Expression)

class MultiplicativeExpression(val firstExpression: Expression, val expressions: List<MultiplicativeOperatorOperand>): Expression {
    override fun toString(): String {
        return "+${expressions.joinToString(separator = " + ")}"
    }

    override fun eval(solution: SolutionMapping, graph: Graph): NodeId? {
        TODO("Not yet implemented")
    }
}