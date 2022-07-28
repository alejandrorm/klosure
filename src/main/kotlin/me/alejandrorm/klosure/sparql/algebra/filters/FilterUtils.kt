package me.alejandrorm.klosure.sparql.algebra.filters

import me.alejandrorm.klosure.model.Node
import me.alejandrorm.klosure.model.LiteralId
import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.model.literals.BooleanValue
import me.alejandrorm.klosure.model.literals.NumberValue
import me.alejandrorm.klosure.model.literals.StringValue

fun getEffectiveBooleanValue(node: NodeId?): Boolean? {
    if (node == null) return null
    return when (node) {
        is LiteralId ->
            when (node.value) {
                is BooleanValue -> node.value.value
                is StringValue -> node.value.value.isNotEmpty()
                is NumberValue -> node.value.value != 0.0
                else -> null
            }
        else -> null
    }
}
