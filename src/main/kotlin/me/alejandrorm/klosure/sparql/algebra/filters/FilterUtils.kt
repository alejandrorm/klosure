package me.alejandrorm.klosure.sparql.algebra.filters

import me.alejandrorm.klosure.model.BigDecimalLiteral
import me.alejandrorm.klosure.model.BigIntegerLiteral
import me.alejandrorm.klosure.model.BooleanLiteral
import me.alejandrorm.klosure.model.DoubleLiteral
import me.alejandrorm.klosure.model.FalseLiteral
import me.alejandrorm.klosure.model.IntegerLiteral
import me.alejandrorm.klosure.model.Node
import me.alejandrorm.klosure.model.StringLiteral
import me.alejandrorm.klosure.model.TrueLiteral

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
