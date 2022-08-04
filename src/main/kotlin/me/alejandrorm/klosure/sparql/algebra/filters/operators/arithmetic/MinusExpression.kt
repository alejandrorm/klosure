package me.alejandrorm.klosure.sparql.algebra.filters.operators.arithmetic

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.LiteralId
import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.model.literals.*
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.filters.Expression
import me.alejandrorm.klosure.sparql.algebra.filters.operators.arithmetic.NumericTypePromotions.upcastToDecimal
import me.alejandrorm.klosure.sparql.algebra.filters.operators.arithmetic.NumericTypePromotions.upcastToInteger
import java.math.BigDecimal
import java.math.BigInteger

class MinusExpression(val e: Expression): Expression {
    override fun toString(): String {
        return "MINUS($e)"
    }

    override fun eval(solution: SolutionMapping, graph: Graph): NodeId? {
        val value = e.eval(solution, graph)

        if (value == null || value !is LiteralId || value.value !is NumberValue) {
            return null
        }

        when(NumericTypePromotions.getNumericType(value.value)) {
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