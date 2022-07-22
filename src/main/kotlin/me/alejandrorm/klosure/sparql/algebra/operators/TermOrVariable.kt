package me.alejandrorm.klosure.sparql.algebra.operators

import me.alejandrorm.klosure.model.IriId
import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.model.TripleId
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.Variable
import java.lang.IllegalStateException

sealed class TermOrVariable {
    abstract fun resolve(solution: SolutionMapping): TermOrVariable

    abstract fun isBound(): Boolean

    abstract fun getTerm(): NodeId

    abstract fun match(solution: SolutionMapping, nodeId: NodeId): SolutionMapping?

    class QuotedTriple(val subject: TermOrVariable, val predicate: TermOrVariable, val obj: TermOrVariable) : TermOrVariable() {

        override fun resolve(solution: SolutionMapping): TermOrVariable {
            return QuotedTriple(subject.resolve(solution), predicate.resolve(solution), obj.resolve(solution))
        }

        override fun isBound(): Boolean {
            return subject.isBound() && predicate.isBound() && obj.isBound()
        }

        override fun match(solution: SolutionMapping, nodeId: NodeId): SolutionMapping? {
            if (nodeId is TripleId) {
                // could resolve before calling match, but it seems redundant, matches are going fail in the
                // variable.match check
                val s1 = subject.match(solution, nodeId.subject)
                val s2 = s1?.let { predicate.match(s1, IriId(nodeId.predicate)) }
                return s2?.let { obj.match(s2, nodeId.obj) }
            }
            return null
        }

        override fun getTerm(): NodeId {
            return TripleId(subject.getTerm(), (predicate.getTerm() as IriId).iri, obj.getTerm())
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as QuotedTriple

            if (subject != other.subject) return false
            if (predicate != other.predicate) return false
            if (obj != other.obj) return false

            return true
        }

        override fun hashCode(): Int {
            var result = subject.hashCode()
            result = 31 * result + predicate.hashCode()
            result = 31 * result + obj.hashCode()
            return result
        }
    }

    class NodeOrIriTerm(val nodeId: NodeId) : TermOrVariable() {
        override fun isBound(): Boolean = true
        override fun resolve(solution: SolutionMapping): TermOrVariable = this
        override fun getTerm() = nodeId

        override fun match(solution: SolutionMapping, nodeId: NodeId): SolutionMapping? {
            return if (nodeId == this.nodeId) solution else null
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as NodeOrIriTerm

            if (nodeId != other.nodeId) return false

            return true
        }

        override fun hashCode(): Int {
            return nodeId.hashCode()
        }
    }

    class VariableTerm(val variable: Variable) : TermOrVariable() {
        override fun isBound(): Boolean = false
        override fun getTerm() = throw IllegalStateException()
        override fun resolve(solution: SolutionMapping): TermOrVariable {
            return if (solution.boundVariables.containsKey(variable)) {
                NodeOrIriTerm(solution.boundVariables[variable]!!)
            } else {
                this
            }
        }

        override fun match(solution: SolutionMapping, nodeId: NodeId): SolutionMapping? {
            return if ((solution.boundVariables[variable] ?: nodeId) == nodeId) {
                solution.bind(variable, nodeId)
            } else null
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as VariableTerm

            if (variable != other.variable) return false

            return true
        }

        override fun hashCode(): Int {
            return variable.hashCode()
        }
    }
}
