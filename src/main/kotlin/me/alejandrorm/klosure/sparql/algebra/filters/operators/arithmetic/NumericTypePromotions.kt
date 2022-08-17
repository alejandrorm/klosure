package me.alejandrorm.klosure.sparql.algebra.filters.operators.arithmetic

import me.alejandrorm.klosure.model.LiteralId
import me.alejandrorm.klosure.model.literals.ByteValue
import me.alejandrorm.klosure.model.literals.DecimalValue
import me.alejandrorm.klosure.model.literals.DoubleValue
import me.alejandrorm.klosure.model.literals.FloatValue
import me.alejandrorm.klosure.model.literals.IntValue
import me.alejandrorm.klosure.model.literals.IntegerValue
import me.alejandrorm.klosure.model.literals.LongValue
import me.alejandrorm.klosure.model.literals.NumberValue
import me.alejandrorm.klosure.model.literals.ShortValue
import java.math.BigDecimal
import java.math.BigInteger

object NumericTypePromotions {
    enum class NumericType {
        DECIMAL,
        INTEGER,
        BYTE,
        SHORT,
        INT,
        LONG,
        FLOAT,
        DOUBLE
    }

    fun getNumericType(v: NumberValue): NumericType {
        return when (v) {
            is DecimalValue -> NumericType.DECIMAL
            is IntegerValue -> NumericType.INTEGER
            is ByteValue -> NumericType.BYTE
            is ShortValue -> NumericType.SHORT
            is IntValue -> NumericType.INT
            is LongValue -> NumericType.LONG
            is FloatValue -> NumericType.FLOAT
            is DoubleValue -> NumericType.DOUBLE
        }
    }

    fun getMostGeneralType(left: NumericType, right: NumericType): NumericType {
        if (left == right) {
            return left
        }

        if (left == NumericType.DECIMAL || right == NumericType.DECIMAL) {
            return NumericType.DECIMAL
        }

        if (left == NumericType.DOUBLE || right == NumericType.DOUBLE) {
            return NumericType.DOUBLE
        }

        if (left == NumericType.FLOAT || right == NumericType.FLOAT) {
            return NumericType.FLOAT
        }

        if (left == NumericType.INTEGER || right == NumericType.INTEGER) {
            return NumericType.INTEGER
        }

        if (left == NumericType.LONG || right == NumericType.LONG) {
            return NumericType.LONG
        }

        if (left == NumericType.INT || right == NumericType.INT) {
            return NumericType.INT
        }

        if (left == NumericType.SHORT || right == NumericType.SHORT) {
            return NumericType.SHORT
        }

        if (left == NumericType.BYTE || right == NumericType.BYTE) {
            return NumericType.BYTE
        }

        throw IllegalArgumentException("Unsupported numeric types: $left, $right")
    }

    fun getMostGenericType(values: List<LiteralId>): NumericType =
        values.fold(NumericType.BYTE) { acc, value ->
            getMostGeneralType(acc, getNumericType(value.value as NumberValue))
        }

    fun upcastToDecimal(value: NumberValue): BigDecimal {
        return when (value) {
            is ByteValue -> BigDecimal(value.value.toInt())
            is ShortValue -> BigDecimal(value.value.toInt())
            is IntValue -> BigDecimal(value.value)
            is LongValue -> BigDecimal(value.value)
            is FloatValue -> BigDecimal(value.value.toDouble())
            is DoubleValue -> BigDecimal(value.value)
            is DecimalValue -> value.value
            is IntegerValue -> BigDecimal(value.value)
        }
    }

    fun upcastToInteger(value: NumberValue): BigInteger {
        return when (value) {
            is ByteValue -> BigInteger.valueOf(value.value.toLong())
            is ShortValue -> BigInteger.valueOf(value.value.toLong())
            is IntValue -> BigInteger.valueOf(value.value.toLong())
            is LongValue -> BigInteger.valueOf(value.value)
            is IntegerValue -> value.value
            else -> throw IllegalArgumentException("can't upcast this to integer $value")
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Number> upcastList(values: List<LiteralId>, type: NumericType): List<T> {
        return when (type) {
            NumericType.DECIMAL -> values.map { upcastToDecimal(it.value as NumberValue) as T }
            NumericType.INTEGER -> values.map { upcastToInteger(it.value as NumberValue) as T }
            NumericType.BYTE -> values.map { (it.value as NumberValue).value as T }
            NumericType.SHORT -> values.map { (it.value as NumberValue).value.toShort() as T }
            NumericType.INT -> values.map { (it.value as NumberValue).value.toInt() as T }
            NumericType.LONG -> values.map { (it.value as NumberValue).value.toLong() as T }
            NumericType.FLOAT -> values.map { (it.value as NumberValue).value.toFloat() as T }
            NumericType.DOUBLE -> values.map { (it.value as NumberValue).value.toDouble() as T }
        }
    }
}
