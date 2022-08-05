package me.alejandrorm.klosure.sparql.algebra.filters.builtins

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.LiteralId
import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.model.literals.*
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.filters.Expression
import me.alejandrorm.klosure.sparql.algebra.filters.operators.arithmetic.NumericTypePromotions
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.math.absoluteValue

class Abs(val e: Expression): Expression
{
    override fun toString(): String {
        return "abs($e)"
    }

    override fun eval(solution: SolutionMapping, graph: Graph): NodeId? {
        val v = e.eval(solution, graph) ?: return null
        if (v !is LiteralId || v.value !is NumberValue) return null

        val absV = when(NumericTypePromotions.getNumericType(v.value)) {
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