package me.alejandrorm.klosure.sparql.algebra.filters.builtins

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.filters.Expression
import me.alejandrorm.klosure.sparql.algebra.filters.getEffectiveBooleanValue

class If(val e1: Expression, val e2: Expression, val e3: Expression) : Expression {
    override fun toString(): String {
        return "if($e1, $e2, $e3)"
    }

    override fun eval(solution: SolutionMapping, graph: Graph): NodeId? {
        val v = getEffectiveBooleanValue(e1.eval(solution, graph)) ?: return null

        return if (v) e2.eval(solution, graph) else e3.eval(solution, graph)
    }
}