package me.alejandrorm.klosure.sparql.algebra.filters.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.LiteralId
import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.model.literals.*
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.filters.Expression

class EqualExpression(val left: Expression, val right: Expression) : Expression {
    override fun toString(): String {
        return "$left = $right"
    }

    override fun eval(solution: SolutionMapping, graph: Graph): NodeId? {
        val l = left.eval(solution, graph)
        val r = right.eval(solution, graph)
        if (l == null || r == null) {
            return null
        }
        if (l is LiteralId && r is LiteralId) {
            if (l.value is NumberValue && r.value is NumberValue) {
                // TODO: casting
                val x1 = l.value.value
                val c2 = r.value.value
                println(x1 == c2)
                return if (l.value.value == r.value.value) DataTypes.TRUE else DataTypes.FALSE
            }
            if (l.value is StringValue && r.value is StringValue) {
                return if (l.value.value == r.value.value) DataTypes.TRUE else DataTypes.FALSE
            }
            if (l.value is BooleanValue && r.value is BooleanValue) {
                return if (l.value.value == r.value.value) DataTypes.TRUE else DataTypes.FALSE
            }
            if (l.value is DateTimeValue && r.value is DateTimeValue) {
                // TODO: timezone stuff
                return if (l.value.value == r.value.value) DataTypes.TRUE else DataTypes.FALSE
            }
        }
        return if (l == r) DataTypes.TRUE else DataTypes.FALSE
    }
}