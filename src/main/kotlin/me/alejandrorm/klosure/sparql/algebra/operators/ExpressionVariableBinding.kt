package me.alejandrorm.klosure.sparql.algebra.operators

import me.alejandrorm.klosure.sparql.Variable
import me.alejandrorm.klosure.sparql.algebra.filters.Expression

data class ExpressionVariableBinding(val expression: Expression, val variable: Variable) {
    override fun toString(): String {
        return "$expression AS $variable"
    }
}
