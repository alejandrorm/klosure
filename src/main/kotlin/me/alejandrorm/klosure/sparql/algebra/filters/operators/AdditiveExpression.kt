package me.alejandrorm.klosure.sparql.algebra.filters.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.filters.Expression

enum class AdditiveOperator(val symbol: String) {
    PLUS("+"),
    MINUS("-")
}

data class AdditiveOperatorOperand(val operator: AdditiveOperator, val operand: Expression)

class AdditiveExpression(val expressions: List<AdditiveOperatorOperand>): Expression {
    override fun toString(): String {
        return "+${expressions.joinToString(separator = " + ")}"
    }

    override fun eval(solution: SolutionMapping, graph: Graph): NodeId? {
        TODO("Not yet implemented")
    }
}