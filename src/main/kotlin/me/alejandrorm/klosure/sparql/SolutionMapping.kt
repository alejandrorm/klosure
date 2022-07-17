package me.alejandrorm.klosure.sparql

import me.alejandrorm.klosure.model.Node

class SolutionMapping(val variables: Set<Variable>, val boundVariables: Map<Variable, Node>) {

    fun getFreeVariables(): Set<Variable> = variables.filter { !boundVariables.containsKey(it) }.toSet()

    fun bind(variable: Variable, value: Node): SolutionMapping {
        return SolutionMapping(variables, boundVariables + (variable to value))
    }
}
