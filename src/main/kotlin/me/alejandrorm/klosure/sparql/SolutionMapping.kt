package me.alejandrorm.klosure.sparql

import me.alejandrorm.klosure.model.NodeId

class SolutionMapping(val variables: Set<Variable>, val boundVariables: Map<Variable, NodeId>) {

    companion object {
        @JvmStatic
        val EmptySolutionMapping = SolutionMapping(emptySet(), emptyMap())
    }

    fun filterOutAnonymousVariables(): SolutionMapping {
        return SolutionMapping(
            variables.filterNot { it.isBlankNode }.toSet(),
            boundVariables.filterNot { it.key.isBlankNode }
        )
    }

    fun getFreeVariables(): Set<Variable> = variables.filter { !boundVariables.containsKey(it) }.toSet()

    fun bind(variable: Variable, value: NodeId): SolutionMapping {
        return SolutionMapping(variables + variable, boundVariables + (variable to value))
    }

    fun addVariable(variable: Variable): SolutionMapping {
        return SolutionMapping(variables + variable, boundVariables)
    }

    fun isCompatible(solution: SolutionMapping): Boolean =
        boundVariables.entries.all { entry -> (solution.boundVariables[entry.key] ?: entry.value) == entry.value }

    fun merge(solution: SolutionMapping): SolutionMapping = SolutionMapping(
        variables + solution.variables,
        boundVariables + solution.boundVariables
    )

    override fun toString(): String {
        return "Solution($boundVariables)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SolutionMapping

        if (variables != other.variables) return false
        if (boundVariables != other.boundVariables) return false

        return true
    }

    override fun hashCode(): Int {
        var result = variables.hashCode()
        result = 31 * result + boundVariables.hashCode()
        return result
    }
}
