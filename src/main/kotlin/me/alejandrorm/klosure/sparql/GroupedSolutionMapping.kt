package me.alejandrorm.klosure.sparql

import me.alejandrorm.klosure.model.NodeId

class GroupedSolutionMapping(val variables: Set<Variable>, val boundVariables: Map<Variable, NodeId>,
                             val group: Sequence<SolutionMapping>) {
}