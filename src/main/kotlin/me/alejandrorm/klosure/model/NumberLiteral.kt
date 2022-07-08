package me.alejandrorm.klosure.model

abstract class NumberLiteral(value: String): LiteralNode(LiteralId(value)) {
    companion object {
        @JvmStatic
        fun createLong(value: String): IntegerLiteral {
            return IntegerLiteral(value.toLong())
        }

        @JvmStatic
        fun createDouble(value: String): DoubleLiteral {
            return DoubleLiteral(value.toDouble())
        }

        @JvmStatic
        fun createBigInteger(value: String): BigIntegerLiteral {
            return BigIntegerLiteral(value.toBigInteger())
        }

        @JvmStatic
        fun createBigDecimal(value: String): BigDecimalLiteral {
            return BigDecimalLiteral(value.toBigDecimal())
        }
    }
}