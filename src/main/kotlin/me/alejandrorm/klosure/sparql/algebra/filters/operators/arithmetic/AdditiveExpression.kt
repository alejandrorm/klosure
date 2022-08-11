package me.alejandrorm.klosure.sparql.algebra.filters.operators.arithmetic

import me.alejandrorm.klosure.model.LiteralId
import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.model.literals.*
import me.alejandrorm.klosure.sparql.SolutionMapping
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

class AdditiveExpression(val expressions: List<AdditiveOperatorOperand>): Expression {
    override fun toString(): String {
        return "+${expressions.joinToString(separator = " ")}"
    }

    override fun eval(solution: SolutionMapping): NodeId? {
        val values = expressions.map { it.operand.eval(solution) }

        if (values.isEmpty()) throw IllegalArgumentException("Additive expression cannot be evaluated without operands")

        if (values.size == 1) {
            return values[0]
        }

        if (values.any { it == null || it !is LiteralId || it.value !is NumberValue }) {
            return null
        }

        @Suppress("UNCHECKED_CAST")
        val literalValues = values as List<LiteralId>

        when(val upperBoundType = getMostGenericType(literalValues)) {
            NumericTypePromotions.NumericType.BYTE -> {
                val byteValues: List<Byte> = upcastList(literalValues, upperBoundType)
                val v = byteValues.zip(expressions).fold(0.toByte()) { acc, value ->
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
                val v = shortValues.zip(expressions).fold(0.toShort()) { acc, value ->
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
                val v = intValues.zip(expressions).fold(0) { acc, value ->
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
                val v = longValues.zip(expressions).fold(0L) { acc, value ->
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
                val v = floatValues.zip(expressions).fold(0f) { acc, value ->
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
                val v = doubleValues.zip(expressions).fold(0.0) { acc, value ->
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
                val v = decimalValues.zip(expressions).fold(0.0.toBigDecimal()) { acc, value ->
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
                val v = integerValues.zip(expressions).fold(0.toBigInteger()) { acc, value ->
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