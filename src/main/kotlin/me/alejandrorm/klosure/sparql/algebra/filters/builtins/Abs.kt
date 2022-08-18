package me.alejandrorm.klosure.sparql.algebra.filters.builtins

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
import me.alejandrorm.klosure.sparql.algebra.filters.operators.arithmetic.NumericTypePromotions
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.math.absoluteValue

class Abs(val e: Expression) : CompositeExpression(listOf(e)) {
    override fun toString(): String {
        return "abs($e)"
    }

    override fun eval(solution: SolutionMapping, activeGraph: Graph, graphs: Graphs): NodeId? {
        val v = e.eval(solution, activeGraph, graphs) ?: return null
        if (v !is LiteralId || v.value !is NumberValue) return null

        val absV = when (NumericTypePromotions.getNumericType(v.value)) {
            NumericTypePromotions.NumericType.BYTE -> {
                val x = (v.value.value as Byte)
                if (x >= 0) return v else ByteValue((-x).toByte())
            }
            NumericTypePromotions.NumericType.SHORT -> {
                val x = (v.value.value as Short)
                if (x >= 0) return v else ShortValue((-x).toShort())
            }
            NumericTypePromotions.NumericType.INT -> {
                val x = v.value.value as Int
                if (x >= 0) return v else IntValue(-x)
            }
            NumericTypePromotions.NumericType.LONG -> LongValue((v.value.value as Long).absoluteValue)
            NumericTypePromotions.NumericType.FLOAT -> FloatValue((v.value.value as Float).absoluteValue)
            NumericTypePromotions.NumericType.DOUBLE -> DoubleValue((v.value.value as Double).absoluteValue)
            NumericTypePromotions.NumericType.INTEGER -> IntegerValue((v.value.value as BigInteger).abs())
            NumericTypePromotions.NumericType.DECIMAL -> DecimalValue((v.value.value as BigDecimal).abs())
        }

        return LiteralId(absV.value.toString(), absV)
    }
}
