package me.alejandrorm.klosure.sparql.algebra.filters

import me.alejandrorm.klosure.model.Node
import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.sparql.SolutionMapping

interface Expression {
    fun eval(solution: SolutionMapping): NodeId?
}
