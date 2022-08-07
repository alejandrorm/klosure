package me.alejandrorm.klosure.sparql.algebra.filters.builtins

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.IriId
import me.alejandrorm.klosure.model.LiteralId
import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.filters.Expression

class Datatype(val e: Expression): Expression {
    override fun toString(): String {
        return "DATATYPE($e)"
    }

    override fun eval(solution: SolutionMapping, graph: Graph): NodeId? {
        val v = e.eval(solution, graph) ?: return null
        return if (v is LiteralId) IriId(v.value.type) else null
    }
}