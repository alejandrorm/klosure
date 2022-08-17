package me.alejandrorm.klosure.sparql.algebra.operators

import me.alejandrorm.klosure.sparql.GroupedSolutionMapping

class Limit(val limit: Int, val offset: Int) {
    override fun toString(): String {
        return "Limit(limit=$limit,offset=$offset)"
    }

    fun eval(solutions: Sequence<GroupedSolutionMapping>): Sequence<GroupedSolutionMapping> {
        return solutions.drop(offset).take(limit)
    }
}
