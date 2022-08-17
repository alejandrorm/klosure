package me.alejandrorm.klosure.sparql.algebra.filters.operators

import me.alejandrorm.klosure.model.LiteralId
import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.model.literals.*
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.aggregates.CompositeExpression
import me.alejandrorm.klosure.sparql.algebra.filters.Expression

class DifferentExpression(val left: Expression, val right: Expression) : CompositeExpression(listOf(left, right)) {
    override fun toString(): String {
        return "($left != $right)"
    }

    override fun eval(solution: SolutionMapping): NodeId? {
        val l = left.eval(solution)
        val r = right.eval(solution)

        return eval(l, r)
    }

    override fun evalGroup(solution: SolutionMapping, group: Sequence<SolutionMapping>): NodeId? {
        val l = left.evalGroup(solution, group)
        val r = right.evalGroup(solution, group)

        return eval(l, r)
    }

    private fun eval(l: NodeId?, r: NodeId?): NodeId? {
        if (l == null || r == null) {
            return null
        }
        if (l is LiteralId && r is LiteralId) {
            if (l.value is NumberValue && r.value is NumberValue) {
                // TODO: casting
                return if (l.value.value != r.value.value) DataTypes.TRUE else DataTypes.FALSE
            }
            if (l.value is StringValue && r.value is StringValue) {
                return if (l.value.value != r.value.value) DataTypes.TRUE else DataTypes.FALSE
            }
            if (l.value is BooleanValue && r.value is BooleanValue) {
                return if (l.value.value != r.value.value) DataTypes.TRUE else DataTypes.FALSE
            }
            if (l.value is DateTimeValue && r.value is DateTimeValue) {
                // TODO: date comparisons, timezone comparisons, etc.
                return if (l.value.value != r.value.value) DataTypes.TRUE else DataTypes.FALSE
            }
        }
        return if (l != r) DataTypes.TRUE else DataTypes.FALSE
    }
}