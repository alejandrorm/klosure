package me.alejandrorm.klosure.sparql.algebra.filters.operators.arithmetic

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.Graphs
import me.alejandrorm.klosure.model.LiteralId
import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.model.literals.ByteValue
import me.alejandrorm.klosure.model.literals.DecimalValue
import me.alejandrorm.klosure.model.literals.DoubleValue
import me.alejandrorm.klosure.model.literals.FloatValue
import me.alejandrorm.klosure.model.literals.IntValue
import me.alejandrorm.klosure.model.literals.IntegerValue
import me.alejandrorm.klosure.model.literals.LongValue
import me.alejandrorm.klosure.model.literals.NumberValue
import me.alejandrorm.klosure.model.literals.ShortValue
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.aggregates.CompositeExpression
import me.alejandrorm.klosure.sparql.algebra.filters.Expression
import me.alejandrorm.klosure.sparql.algebra.filters.operators.arithmetic.NumericTypePromotions.upcastToDecimal
import me.alejandrorm.klosure.sparql.algebra.filters.operators.arithmetic.NumericTypePromotions.upcastToInteger

class MinusExpression(val e: Expression) : CompositeExpression(listOf(e)) {
    override fun toString(): String {
        return "MINUS($e)"
    }

    override fun eval(solution: SolutionMapping, activeGraph: Graph, graphs: Graphs): NodeId? {
        return eval(e.eval(solution,activeGraph,graphs))
    }

    override fun evalGroup(
        solution: SolutionMapping,
        group: Sequence<SolutionMapping>,
        activeGraph: Graph,
        graphs: Graphs
    ): NodeId? {
        return eval(e.evalGroup(solution, group,activeGraph,graphs))
    }

    private fun eval(value: NodeId?): NodeId? {
        if (value == null || value !is LiteralId || value.value !is NumberValue) {
            return null
        }

        when (NumericTypePromotions.getNumericType(value.value)) {
            NumericTypePromotions.NumericType.BYTE -> {
                val v = (-value.value.value.toByte()).toByte()
                return LiteralId(v.toString(), ByteValue(v))
            }
            NumericTypePromotions.NumericType.SHORT -> {
                val v = (-value.value.value.toShort()).toShort()
                return LiteralId(v.toString(), ShortValue(v))
            }
            NumericTypePromotions.NumericType.INT -> {
                val v = -value.value.value.toInt()
                return LiteralId(v.toString(), IntValue(v))
            }
            NumericTypePromotions.NumericType.LONG -> {
                val v = -value.value.value.toLong()
                return LiteralId(v.toString(), LongValue(v))
            }
            NumericTypePromotions.NumericType.FLOAT -> {
                val v = -value.value.value.toFloat()
                return LiteralId(v.toString(), FloatValue(v))
            }
            NumericTypePromotions.NumericType.DOUBLE -> {
                val v = -value.value.value.toDouble()
                return LiteralId(v.toString(), DoubleValue(v))
            }
            NumericTypePromotions.NumericType.DECIMAL -> {
                val v = -upcastToDecimal(value.value)
                return LiteralId(v.toString(), DecimalValue(v))
            }
            NumericTypePromotions.NumericType.INTEGER -> {
                val v = -upcastToInteger(value.value)
                return LiteralId(v.toString(), IntegerValue(v))
            }
        }
    }
}
