package me.alejandrorm.klosure.sparql.algebra.filters

import me.alejandrorm.klosure.model.*

fun getEffectiveBooleanValue(node: Node?): BooleanLiteral? {
    if (node == null) return null
    return when (node) {
        is IntegerLiteral ->
            if (node.value != 0L) TrueLiteral else FalseLiteral
        is DoubleLiteral ->
            if (!node.value.isNaN() && node.value != 0.0) TrueLiteral else FalseLiteral
        is BigDecimalLiteral ->
            if (node.value.toDouble() != 0.0) TrueLiteral else FalseLiteral
        is BigIntegerLiteral ->
            if (node.value.toLong() != 0L) TrueLiteral else FalseLiteral
        is StringLiteral ->
            if (node.nodeId.literal.isNotEmpty()) TrueLiteral else FalseLiteral
        is BooleanLiteral -> node
        else -> null
    }
}
