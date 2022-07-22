package me.alejandrorm.klosure.sparql.algebra.filters

import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.sparql.algebra.operators.TermOrVariable

interface BinaryOperator {
    fun eval(value1: TermOrVariable, value2: TermOrVariable): NodeId?
}
