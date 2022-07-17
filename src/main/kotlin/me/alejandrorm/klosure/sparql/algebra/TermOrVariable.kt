package me.alejandrorm.klosure.sparql.algebra

import me.alejandrorm.klosure.model.*
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.Variable

sealed class TermOrVariable {
    abstract fun resolve(solution: SolutionMapping): TermOrVariable

    abstract fun isBound(): Boolean

    class IriTerm(val iri: IriId) : TermOrVariable() {
        override fun isBound(): Boolean = true
        override fun resolve(solution: SolutionMapping): TermOrVariable = this
    }

    class LiteralTerm(val literalId: LiteralId) : TermOrVariable() {
        override fun isBound(): Boolean = true
        override fun resolve(solution: SolutionMapping): TermOrVariable = this
    }

    // this is a term that has been bound to a concrete blank node in the graph, not a blank node in the query
    class BlankTerm(val blankId: BlankId) : TermOrVariable() {
        override fun isBound(): Boolean = true
        override fun resolve(solution: SolutionMapping): TermOrVariable = this
    }

    class TripleTerm(val tripleId: TripleId): TermOrVariable() {
        override fun isBound(): Boolean = true
        override fun resolve(solution: SolutionMapping): TermOrVariable = this
    }

    class VariableTerm(val variable: Variable) : TermOrVariable() {
        override fun isBound(): Boolean = false
        override fun resolve(solution: SolutionMapping): TermOrVariable {
            return if (solution.boundVariables.containsKey(variable)) {
                when(val id = solution.boundVariables[variable]!!.id) {
                    is IriId -> IriTerm(id)
                    is TripleId -> IriTerm(IriId(id.predicate))
                    is LiteralId -> LiteralTerm(id)
                    is BlankId -> BlankTerm(id)
                }
            } else {
                this
            }
        }
    }
}
