package me.alejandrorm.klosure.sparql.algebra

import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.Variable
import java.lang.IllegalStateException

sealed class TermOrVariable {
    abstract fun resolve(solution: SolutionMapping): TermOrVariable

    abstract fun isBound(): Boolean

    abstract fun getTerm(): NodeId

    class NodeOrIriTerm(val nodeId: NodeId) : TermOrVariable() {
        override fun isBound(): Boolean = true
        override fun resolve(solution: SolutionMapping): TermOrVariable = this
        override fun getTerm() = nodeId
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
    }
}
