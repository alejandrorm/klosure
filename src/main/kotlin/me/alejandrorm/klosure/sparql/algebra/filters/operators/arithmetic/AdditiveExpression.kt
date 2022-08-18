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
import me.alejandrorm.klosure.sparql.algebra.filters.operators.arithmetic.NumericTypePromotions.getMostGenericType
import me.alejandrorm.klosure.sparql.algebra.filters.operators.arithmetic.NumericTypePromotions.upcastList
import java.math.BigDecimal
import java.math.BigInteger

enum class AdditiveOperator(val symbol: String) {
    PLUS("+"),
    MINUS("-");

    override fun toString(): String {
        return symbol
    }
}

data class AdditiveOperatorOperand(val operator: AdditiveOperator, val operand: Expression) {
    override fun toString(): String {
        return "$operator$operand"
    }
}

class AdditiveExpression(val expressionOps: List<AdditiveOperatorOperand>) :
    CompositeExpression(expressionOps.map { it.operand }) {
    override fun toString(): String {
        return "+${expressionOps.joinToString(separator = " ")}"
    }

    override fun eval(solution: SolutionMapping, activeGraph: Graph, graphs: Graphs): NodeId? {
        return eval(expressionOps.map { it.operand.eval(solution, activeGraph, graphs) })
    }

    override fun evalGroup(
        solution: SolutionMapping,
        group: Sequence<SolutionMapping>,
        activeGraph: Graph,
        graphs: Graphs
    ): NodeId? {
        return eval(expressionOps.map { it.operand.evalGroup(solution, group, activeGraph, graphs) })
    }

    private fun eval(values: List<NodeId?>): NodeId? {
        if (values.isEmpty()) throw IllegalArgumentException("Additive expression cannot be evaluated without operands")

        if (values.size == 1) {
            return values[0]
        }

        if (values.any { it == null || it !is LiteralId || it.value !is NumberValue }) {
            return null
        }

        @Suppress("UNCHECKED_CAST")
        val literalValues = values as List<LiteralId>

        when (val upperBoundType = getMostGenericType(literalValues)) {
            NumericTypePromotions.NumericType.BYTE -> {
                val byteValues: List<Byte> = upcastList(literalValues, upperBoundType)
                val v = byteValues.zip(expressionOps).fold(0.toByte()) { acc, value ->
                    if (value.second.operator == AdditiveOperator.PLUS) {
                        (acc + value.first).toByte()
                    } else {
                        (acc - value.first).toByte()
                    }
                }

                return LiteralId(v.toString(), ByteValue(v))
            }
            NumericTypePromotions.NumericType.SHORT -> {
                val shortValues: List<Short> = upcastList(literalValues, upperBoundType)
                val v = shortValues.zip(expressionOps).fold(0.toShort()) { acc, value ->
                    if (value.second.operator == AdditiveOperator.PLUS) {
                        (acc + value.first).toShort()
                    } else {
                        (acc - value.first).toShort()
                    }
                }

                return LiteralId(v.toString(), ShortValue(v))
            }
            NumericTypePromotions.NumericType.INT -> {
                val intValues: List<Int> = upcastList(literalValues, upperBoundType)
                val v = intValues.zip(expressionOps).fold(0) { acc, value ->
                    if (value.second.operator == AdditiveOperator.PLUS) {
                        acc + value.first
                    } else {
                        acc - value.first
                    }
                }
                return LiteralId(v.toString(), IntValue(v))
            }
            NumericTypePromotions.NumericType.LONG -> {
                val longValues: List<Long> = upcastList(literalValues, upperBoundType)
                val v = longValues.zip(expressionOps).fold(0L) { acc, value ->
                    if (value.second.operator == AdditiveOperator.PLUS) {
                        acc + value.first
                    } else {
                        acc - value.first
                    }
                }
                return LiteralId(v.toString(), LongValue(v))
            }
            NumericTypePromotions.NumericType.FLOAT -> {
                val floatValues: List<Float> = upcastList(literalValues, upperBoundType)
                val v = floatValues.zip(expressionOps).fold(0f) { acc, value ->
                    if (value.second.operator == AdditiveOperator.PLUS) {
                        acc + value.first
                    } else {
                        acc - value.first
                    }
                }
                return LiteralId(v.toString(), FloatValue(v))
            }
            NumericTypePromotions.NumericType.DOUBLE -> {
                val doubleValues: List<Double> = upcastList(literalValues, upperBoundType)
                val v = doubleValues.zip(expressionOps).fold(0.0) { acc, value ->
                    if (value.second.operator == AdditiveOperator.PLUS) {
                        acc + value.first
                    } else {
                        acc - value.first
                    }
                }
                return LiteralId(v.toString(), DoubleValue(v))
            }
            NumericTypePromotions.NumericType.DECIMAL -> {
                val decimalValues: List<BigDecimal> = upcastList(literalValues, upperBoundType)
                val v = decimalValues.zip(expressionOps).fold(0.0.toBigDecimal()) { acc, value ->
                    if (value.second.operator == AdditiveOperator.PLUS) {
                        acc + value.first
                    } else {
                        acc - value.first
                    }
                }
                return LiteralId(v.toString(), DecimalValue(v))
            }
            NumericTypePromotions.NumericType.INTEGER -> {
                val integerValues: List<BigInteger> = upcastList(literalValues, upperBoundType)
                val v = integerValues.zip(expressionOps).fold(0.toBigInteger()) { acc, value ->
                    if (value.second.operator == AdditiveOperator.PLUS) {
                        acc + value.first
                    } else {
                        acc - value.first
                    }
                }
                return LiteralId(v.toString(), IntegerValue(v))
            }
        }
    }
}
