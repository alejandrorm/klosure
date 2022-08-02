package me.alejandrorm.klosure.sparql.algebra.filters.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.IriId
import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.model.TripleId
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.filters.Expression

class QuotedTripleExpression(val subject: Expression, val verb: Expression, val obj: Expression) : Expression {
    override fun toString(): String {
        return "QtExpression($subject $verb $obj)"
    }

    override fun eval(solution: SolutionMapping, graph: Graph): NodeId? {
        val s = subject.eval(solution, graph)
        val v = verb.eval(solution, graph)
        val o = obj.eval(solution, graph)

        return if (s == null || v == null || o == null || v !is IriId) {
            null
        } else {
            TripleId(s, v.iri, o)
        }
    }
}