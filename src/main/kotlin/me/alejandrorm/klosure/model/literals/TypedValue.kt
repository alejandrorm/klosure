package me.alejandrorm.klosure.model.literals

import me.alejandrorm.klosure.model.literals.DataTypes.Companion.BOOLEAN
import me.alejandrorm.klosure.model.literals.DataTypes.Companion.BYTE
import me.alejandrorm.klosure.model.literals.DataTypes.Companion.DATE
import me.alejandrorm.klosure.model.literals.DataTypes.Companion.DATETIME
import me.alejandrorm.klosure.model.literals.DataTypes.Companion.DATETIMESTAMP
import me.alejandrorm.klosure.model.literals.DataTypes.Companion.DECIMAL
import me.alejandrorm.klosure.model.literals.DataTypes.Companion.DOUBLE
import me.alejandrorm.klosure.model.literals.DataTypes.Companion.FLOAT
import me.alejandrorm.klosure.model.literals.DataTypes.Companion.INT
import me.alejandrorm.klosure.model.literals.DataTypes.Companion.INTEGER
import me.alejandrorm.klosure.model.literals.DataTypes.Companion.LONG
import me.alejandrorm.klosure.model.literals.DataTypes.Companion.SHORT
import me.alejandrorm.klosure.model.literals.DataTypes.Companion.STRING
import me.alejandrorm.klosure.model.literals.DataTypes.Companion.TIME
import org.semanticweb.owlapi.model.IRI
import java.math.BigDecimal
import java.math.BigInteger

sealed class TypedValue(open val value: Any, open val type: IRI) : Comparable<TypedValue> {
    override fun toString(): String {
        return "\"${value}\"^^<$type>"
    }
}

data class UnknownTypeValue(override val value: Any, override val type: IRI) : TypedValue(value, type) {
    override fun toString(): String {
        return super.toString()
    }

    override fun compareTo(other: TypedValue): Int {
        return value.toString().compareTo(other.value.toString())
    }
}

data class StringValue(override val value: String, val lang: String?) : TypedValue(value, STRING) {
    override fun toString(): String {
        return if (lang == null) {
            "\"${value}\""
        } else {
            "\"${value}\"@$lang"
        }
    }

    override fun compareTo(other: TypedValue): Int {
        return value.compareTo(other.value.toString())
    }
}

data class BooleanValue(override val value: Boolean) : TypedValue(value, BOOLEAN) {
    override fun toString(): String {
        return if (value) "true" else "false"
    }

    override fun compareTo(other: TypedValue): Int {
        return when (other) {
            is BooleanValue -> value.compareTo(other.value)
            else -> value.toString().compareTo(other.value.toString())
        }
    }
}

sealed class NumberValue(override val value: Number, type: IRI) : TypedValue(value, type) {
    override fun toString(): String {
        return value.toString()
    }

    override fun compareTo(other: TypedValue): Int {
        return when (other) {
            // TODO: this is not correct, but it works for now
            is NumberValue -> value.toDouble().compareTo(other.value.toDouble())
            else -> value.toString().compareTo(other.value.toString())
        }
    }
}

data class DecimalValue(override val value: BigDecimal) : NumberValue(value, DECIMAL) {
    override fun toString(): String {
        return super.toString()
    }

    override fun equals(other: Any?): Boolean {
        return if (other is DecimalValue) {
            value.compareTo(other.value) == 0
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return value.toDouble().hashCode()
    }
}

data class IntegerValue(override val value: BigInteger) : NumberValue(value, INTEGER) {
    override fun toString(): String {
        return super.toString()
    }
}

data class DoubleValue(override val value: Double) : NumberValue(value, DOUBLE) {
    override fun toString(): String {
        return super.toString()
    }
}

data class FloatValue(override val value: Float) : NumberValue(value, FLOAT) {
    override fun toString(): String {
        return super.toString()
    }
}

data class DateValue(override val value: String) : TypedValue(value, DATE) {
    override fun toString(): String {
        return super.toString()
    }

    override fun compareTo(other: TypedValue): Int {
        // TODO
        return value.compareTo(other.value.toString())
    }
}

data class TimeValue(override val value: String) : TypedValue(value, TIME) {
    override fun toString(): String {
        return super.toString()
    }

    override fun compareTo(other: TypedValue): Int {
        // TODO
        return value.compareTo(other.value.toString())
    }
}

data class DateTimeValue(override val value: String) : TypedValue(value, DATETIME) {
    override fun toString(): String {
        return super.toString()
    }

    override fun compareTo(other: TypedValue): Int {
        // TODO
        return value.compareTo(other.value.toString())
    }
}

data class DateTimeStampValue(override val value: String) : TypedValue(value, DATETIMESTAMP) {
    override fun toString(): String {
        return super.toString()
    }

    override fun compareTo(other: TypedValue): Int {
        // TODO
        return value.compareTo(other.value.toString())
    }
}

data class ByteValue(override val value: Byte) : NumberValue(value, BYTE) {
    override fun toString(): String {
        return super.toString()
    }
}

data class ShortValue(override val value: Short) : NumberValue(value, SHORT) {
    override fun toString(): String {
        return super.toString()
    }
}

data class IntValue(override val value: Int) : NumberValue(value, INT) {
    override fun toString(): String {
        return super.toString()
    }
}

data class LongValue(override val value: Long) : NumberValue(value, LONG) {
    override fun toString(): String {
        return super.toString()
    }
}
