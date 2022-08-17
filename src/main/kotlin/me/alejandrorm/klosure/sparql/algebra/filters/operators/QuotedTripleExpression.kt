package me.alejandrorm.klosure.sparql.algebra.filters.operators

import me.alejandrorm.klosure.model.IriId
import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.model.TripleId
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.aggregates.CompositeExpression
import me.alejandrorm.klosure.sparql.algebra.filters.Expression

class QuotedTripleExpression(val subject: Expression, val verb: TermOrVariableExpression, val obj: Expression) :
    CompositeExpression(listOf(subject, verb, obj)) {
    override fun toString(): String {
        return "QtExpression($subject $verb $obj)"
    }

    override fun eval(solution: SolutionMapping): NodeId? {
        val s = subject.eval(solution)
        val v = verb.eval(solution)
        val o = obj.eval(solution)

        return if (s == null || v == null || o == null || v !is IriId) {
            null
        } else {
            TripleId(s, v.iri, o)
        }
    }
}