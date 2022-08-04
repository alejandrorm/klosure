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

enum class MultiplicativeOperator(val symbol: String) {
    TIMES("*"),
    BY("/")
}

data class MultiplicativeOperatorOperand(val operator: MultiplicativeOperator, val operand: Expression)

class MultiplicativeExpression(val firstExpression: Expression, val expressions: List<MultiplicativeOperatorOperand>) :
    Expression {
    override fun toString(): String {
        return "+${expressions.joinToString(separator = " + ")}"
    }

    override fun eval(solution: SolutionMapping, graph: Graph): NodeId? {
        val first = firstExpression.eval(solution, graph)
        val values = expressions.map { it.operand.eval(solution, graph) }

        if (values.isEmpty()) {
            return first
        }

        if (first == null || first !is LiteralId || first.value !is NumberValue ||
            values.any { it == null || it !is LiteralId || it.value !is NumberValue }) {
            return null
        }

        @Suppress("UNCHECKED_CAST")
        val literalValues = values as List<LiteralId>
        val upperBoundType = NumericTypePromotions.getMostGeneralType(
            NumericTypePromotions.getNumericType(first.value), NumericTypePromotions.getMostGenericType(literalValues))
        val number = first.value.value

        when (upperBoundType) {
            NumericTypePromotions.NumericType.BYTE -> {
                val byteValues: List<Byte> = NumericTypePromotions.upcastList(literalValues, upperBoundType)
                val v = byteValues.zip(expressions).fold(number.toByte()) { acc, value ->
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
                val v = shortValues.zip(expressions).fold(number.toShort()) { acc, value ->
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
                val v = intValues.zip(expressions).fold(number.toInt()) { acc, value ->
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
                val v = longValues.zip(expressions).fold(number.toLong()) { acc, value ->
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
                val v = floatValues.zip(expressions).fold(number.toFloat()) { acc, value ->
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
                val v = doubleValues.zip(expressions).fold(number.toDouble()) { acc, value ->
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
                val v = decimalValues.zip(expressions).fold(upcastToDecimal(first.value)) { acc, value ->
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
                val v = integerValues.zip(expressions).fold(upcastToInteger(first.value)) { acc, value ->
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