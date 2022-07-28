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

sealed class TypedValue(open val value: Any, open val type: IRI) {
    override fun toString(): String {
        return "\"${value.toString()}\"^^<${type.toString()}>"
    }
}

data class UnknownTypeValue(override val value: Any,override val type: IRI) : TypedValue(value, type) {
    override fun toString(): String {
        return super.toString()
    }
}

data class StringValue(override val value: String, val lang: String?) : TypedValue(value, STRING) {
    override fun toString(): String {
        return if (lang == null) {
            "\"${value}\""
        } else {
            "\"${value}\"@${lang}"
        }
    }
}

data class BooleanValue(override val value: Boolean) : TypedValue(value, BOOLEAN) {
    override fun toString(): String {
        return if (value) "true" else "false"
    }
}

sealed class NumberValue(override val value: Number, type: IRI): TypedValue(value, type) {
    override fun toString(): String {
        return value.toString()
    }
}

data class DecimalValue(override val value: BigDecimal) : NumberValue(value, DECIMAL) {
    override fun toString(): String {
        return super.toString()
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
}

data class TimeValue(override val value: String) : TypedValue(value, TIME) {
    override fun toString(): String {
        return super.toString()
    }
}

data class DateTimeValue(override val value: String) : TypedValue(value, DATETIME) {
    override fun toString(): String {
        return super.toString()
    }
}

data class DateTimeStampValue(override val value: String) : TypedValue(value, DATETIMESTAMP) {
    override fun toString(): String {
        return super.toString()
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
