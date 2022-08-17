package me.alejandrorm.klosure.model.literals

import me.alejandrorm.klosure.model.LiteralId
import org.semanticweb.owlapi.model.IRI

class DataTypes {
    companion object {
        @JvmStatic
        val STRING: IRI = IRI.create("http://www.w3.org/2001/XMLSchema#string")

        @JvmStatic
        val BOOLEAN: IRI = IRI.create("http://www.w3.org/2001/XMLSchema#boolean")

        @JvmStatic
        val DECIMAL: IRI = IRI.create("http://www.w3.org/2001/XMLSchema#decimal")

        @JvmStatic
        val INTEGER: IRI = IRI.create("http://www.w3.org/2001/XMLSchema#integer")

        @JvmStatic
        val FLOAT: IRI = IRI.create("http://www.w3.org/2001/XMLSchema#float")

        @JvmStatic
        val DOUBLE: IRI = IRI.create("http://www.w3.org/2001/XMLSchema#double")

        @JvmStatic
        val DATE: IRI = IRI.create("http://www.w3.org/2001/XMLSchema#date")

        @JvmStatic
        val TIME: IRI = IRI.create("http://www.w3.org/2001/XMLSchema#time")

        @JvmStatic
        val DATETIME: IRI = IRI.create("http://www.w3.org/2001/XMLSchema#dateTime")

        @JvmStatic
        val DATETIMESTAMP: IRI = IRI.create("http://www.w3.org/2001/XMLSchema#dateTimeStamp")

        @JvmStatic
        val GYEAR: IRI = IRI.create("http://www.w3.org/2001/XMLSchema#gYear")

        @JvmStatic
        val GMONTH: IRI = IRI.create("http://www.w3.org/2001/XMLSchema#gMonth")

        @JvmStatic
        val GDAY: IRI = IRI.create("http://www.w3.org/2001/XMLSchema#gDay")

        @JvmStatic
        val GYEARMONTH: IRI = IRI.create("http://www.w3.org/2001/XMLSchema#gYearMonth")

        @JvmStatic
        val GMONTHDAY: IRI = IRI.create("http://www.w3.org/2001/XMLSchema#gMonthDay")

        @JvmStatic
        val DURATION: IRI = IRI.create("http://www.w3.org/2001/XMLSchema#duration")

        @JvmStatic
        val YEARMONTHDURATION: IRI = IRI.create("http://www.w3.org/2001/XMLSchema#yearMonthDuration")

        @JvmStatic
        val DAYTIMEDURATION: IRI = IRI.create("http://www.w3.org/2001/XMLSchema#dayTimeDuration")

        @JvmStatic
        val BYTE: IRI = IRI.create("http://www.w3.org/2001/XMLSchema#byte")

        @JvmStatic
        val SHORT: IRI = IRI.create("http://www.w3.org/2001/XMLSchema#short")

        @JvmStatic
        val INT: IRI = IRI.create("http://www.w3.org/2001/XMLSchema#integer")

        @JvmStatic
        val LONG: IRI = IRI.create("http://www.w3.org/2001/XMLSchema#long")

        @JvmStatic
        val UNSIGNED_BYTE: IRI = IRI.create("http://www.w3.org/2001/XMLSchema#unsignedByte")

        @JvmStatic
        val UNSIGNED_SHORT: IRI = IRI.create("http://www.w3.org/2001/XMLSchema#unsignedShort")

        @JvmStatic
        val UNSIGNED_INT: IRI = IRI.create("http://www.w3.org/2001/XMLSchema#unsignedInt")

        @JvmStatic
        val UNSIGNED_LONG: IRI = IRI.create("http://www.w3.org/2001/XMLSchema#unsignedLong")

        @JvmStatic
        val POSITIVE_INTEGER: IRI = IRI.create("http://www.w3.org/2001/XMLSchema#positiveInteger")

        @JvmStatic
        val NON_NEGATIVE_INTEGER: IRI = IRI.create("http://www.w3.org/2001/XMLSchema#nonNegativeInteger")

        @JvmStatic
        val NEGATIVE_INTEGER: IRI = IRI.create("http://www.w3.org/2001/XMLSchema#negativeInteger")

        @JvmStatic
        val NON_POSITIVE_INTEGER: IRI = IRI.create("http://www.w3.org/2001/XMLSchema#nonPositiveInteger")

        @JvmStatic
        val HEXBINARY: IRI = IRI.create("http://www.w3.org/2001/XMLSchema#hexBinary")

        @JvmStatic
        val BASE64BINARY: IRI = IRI.create("http://www.w3.org/2001/XMLSchema#base64Binary")

        @JvmStatic
        val TRUE: LiteralId = LiteralId("true", BooleanValue(true))

        @JvmStatic
        val FALSE: LiteralId = LiteralId("false", BooleanValue(false))

        @JvmStatic
        fun getIntegerLiteral(v: String): LiteralId =
            LiteralId(v, IntegerValue(v.toBigInteger()))

        @JvmStatic
        fun getDecimalLiteral(v: String): LiteralId =
            LiteralId(v, DecimalValue(v.toBigDecimal()))

        @JvmStatic
        fun getRdfLiteral(v: String, type: IRI?, lang: String?): LiteralId {
            return getLiteralId(v, type, lang)
        }

        @JvmStatic
        fun getLiteralId(v: String, type: IRI?, lang: String?): LiteralId {
            return when (type) {
                null -> LiteralId(v, StringValue(v, lang))
                STRING -> LiteralId(v, StringValue(v, lang))
                BOOLEAN -> LiteralId(v, BooleanValue(stringToBoolean(v)))
                DECIMAL -> LiteralId(v, DecimalValue(v.toBigDecimal()))
                INTEGER -> LiteralId(v, IntegerValue(v.toBigInteger()))
                FLOAT -> LiteralId(v, FloatValue(v.toFloat()))
                DOUBLE -> LiteralId(v, DoubleValue(v.toDouble()))
                //        DATE -> LiteralId(v, DateValue(v))
                //        TIME -> LiteralId(v, TimeValue(v))
                //        DATETIME -> LiteralId(v, DateTimeValue(v))
                //        DATETIMESTAMP -> LiteralId(v, DateTimeStampValue(v))
                BYTE -> LiteralId(v, ByteValue(v.toByte()))
                SHORT -> LiteralId(v, ShortValue(v.toShort()))
                INT -> LiteralId(v, IntValue(v.toInt()))
                LONG -> LiteralId(v, LongValue(v.toLong()))
                //        UNSIGNED_BYTE -> LiteralId(v, UnsignedByteValue(v))
                //        UNSIGNED_SHORT -> LiteralId(v, UnsignedShortValue(v))
                //        UNSIGNED_INT -> LiteralId(v, UnsignedIntValue(v))
                //        UNSIGNED_LONG -> LiteralId(v, UnsignedLongValue(v))
                //        POSITIVE_INTEGER -> LiteralId(v, PositiveIntegerValue(v))
                //        NON_NEGATIVE_INTEGER -> LiteralId(v, NonNegativeIntegerValue(v))
                //        NEGATIVE_INTEGER -> LiteralId(v, NegativeIntegerValue(v))
                //        NON_POSITIVE_INTEGER -> LiteralId(v, NonPositiveIntegerValue(v))
                //        HEXBINARY -> LiteralId(v, HexBinaryValue(v))
                //        BASE64BINARY -> LiteralId(v, Base64BinaryValue(v))
                else -> LiteralId(v, UnknownTypeValue(v, type))
            }
        }

        @JvmStatic
        private fun stringToBoolean(s: String): Boolean =
            when (s.lowercase()) {
                "true" -> true
                "false" -> false
                "0" -> false
                "1" -> true
                else -> throw IllegalArgumentException("Unknown boolean value: $s")
            }
    }
}
