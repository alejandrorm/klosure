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
import java.math.BigDecimal
import java.math.BigInteger

enum class MultiplicativeOperator(val symbol: String) {
    TIMES("*"),
    BY("/")
}

data class MultiplicativeOperatorOperand(val operator: MultiplicativeOperator, val operand: Expression) {
    override fun toString(): String {
        return "$operator$operand"
    }
}

class MultiplicativeExpression(val firstExpression: Expression, val expressionOps: List<MultiplicativeOperatorOperand>) :
    CompositeExpression(expressionOps.map { it.operand } + firstExpression) {
    override fun toString(): String {
        return "$firstExpression+${expressionOps.joinToString(separator = " ")}"
    }

    override fun eval(solution: SolutionMapping, activeGraph: Graph, graphs: Graphs): NodeId? {
        return eval(firstExpression.eval(solution, activeGraph, graphs), expressionOps.map { it.operand.eval(solution, activeGraph, graphs) })
    }

    override fun evalGroup(
        solution: SolutionMapping,
        group: Sequence<SolutionMapping>,
        activeGraph: Graph,
        graphs: Graphs
    ): NodeId? {
        return eval(
            firstExpression.evalGroup(solution, group, activeGraph, graphs),
            expressionOps.map { it.operand.evalGroup(solution, group, activeGraph, graphs) }
        )
    }

    private fun eval(first: NodeId?, values: List<NodeId?>): NodeId? {
        if (values.isEmpty()) {
            return first
        }

        if (first == null || first !is LiteralId || first.value !is NumberValue ||
            values.any { it == null || it !is LiteralId || it.value !is NumberValue }
        ) {
            return null
        }

        @Suppress("UNCHECKED_CAST")
        val literalValues = values as List<LiteralId>
        val upperBoundType = NumericTypePromotions.getMostGeneralType(
            NumericTypePromotions.getNumericType(first.value),
            NumericTypePromotions.getMostGenericType(literalValues)
        )
        val number = first.value.value

        when (upperBoundType) {
            NumericTypePromotions.NumericType.BYTE -> {
                val byteValues: List<Byte> = NumericTypePromotions.upcastList(literalValues, upperBoundType)
                val v = byteValues.zip(expressionOps).fold(number.toByte()) { acc, value ->
                    if (value.second.operator == MultiplicativeOperator.TIMES) {
                        (acc * value.first).toByte()
                    } else {
                        (acc / value.first).toByte()
                    }
                }

                return LiteralId(v.toString(), ByteValue(v))
            }
            NumericTypePromotions.NumericType.SHORT -> {
                val shortValues: List<Short> = NumericTypePromotions.upcastList(literalValues, upperBoundType)
                val v = shortValues.zip(expressionOps).fold(number.toShort()) { acc, value ->
                    if (value.second.operator == MultiplicativeOperator.TIMES) {
                        (acc * value.first).toShort()
                    } else {
                        (acc / value.first).toShort()
                    }
                }

                return LiteralId(v.toString(), ShortValue(v))
            }
            NumericTypePromotions.NumericType.INT -> {
                val intValues: List<Int> = NumericTypePromotions.upcastList(literalValues, upperBoundType)
                val v = intValues.zip(expressionOps).fold(number.toInt()) { acc, value ->
                    if (value.second.operator == MultiplicativeOperator.TIMES) {
                        acc + value.first
                    } else {
                        acc - value.first
                    }
                }
                return LiteralId(v.toString(), IntValue(v))
            }
            NumericTypePromotions.NumericType.LONG -> {
                val longValues: List<Long> = NumericTypePromotions.upcastList(literalValues, upperBoundType)
                val v = longValues.zip(expressionOps).fold(number.toLong()) { acc, value ->
                    if (value.second.operator == MultiplicativeOperator.TIMES) {
                        acc * value.first
                    } else {
                        acc / value.first
                    }
                }
                return LiteralId(v.toString(), LongValue(v))
            }
            NumericTypePromotions.NumericType.FLOAT -> {
                val floatValues: List<Float> = NumericTypePromotions.upcastList(literalValues, upperBoundType)
                val v = floatValues.zip(expressionOps).fold(number.toFloat()) { acc, value ->
                    if (value.second.operator == MultiplicativeOperator.TIMES) {
                        acc * value.first
                    } else {
                        acc / value.first
                    }
                }
                return LiteralId(v.toString(), FloatValue(v))
            }
            NumericTypePromotions.NumericType.DOUBLE -> {
                val doubleValues: List<Double> = NumericTypePromotions.upcastList(literalValues, upperBoundType)
                val v = doubleValues.zip(expressionOps).fold(number.toDouble()) { acc, value ->
                    if (value.second.operator == MultiplicativeOperator.TIMES) {
                        acc * value.first
                    } else {
                        acc / value.first
                    }
                }
                return LiteralId(v.toString(), DoubleValue(v))
            }
            NumericTypePromotions.NumericType.DECIMAL -> {
                val decimalValues: List<BigDecimal> = NumericTypePromotions.upcastList(literalValues, upperBoundType)
                val v = decimalValues.zip(expressionOps).fold(upcastToDecimal(first.value)) { acc, value ->
                    if (value.second.operator == MultiplicativeOperator.TIMES) {
                        acc * value.first
                    } else {
                        acc / value.first
                    }
                }
                return LiteralId(v.toString(), DecimalValue(v))
            }
            NumericTypePromotions.NumericType.INTEGER -> {
                val integerValues: List<BigInteger> = NumericTypePromotions.upcastList(literalValues, upperBoundType)
                val v = integerValues.zip(expressionOps).fold(upcastToInteger(first.value)) { acc, value ->
                    if (value.second.operator == MultiplicativeOperator.TIMES) {
                        acc * value.first
                    } else {
                        acc / value.first
                    }
                }
                return LiteralId(v.toString(), IntegerValue(v))
            }
        }
    }
}
