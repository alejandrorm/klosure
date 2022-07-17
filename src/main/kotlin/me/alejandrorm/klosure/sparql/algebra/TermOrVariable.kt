package me.alejandrorm.klosure.sparql.algebra

import me.alejandrorm.klosure.model.Node
import me.alejandrorm.klosure.sparql.Variable

sealed class TermOrVariable {
    // FIXME Instead of NodeTerm, it should IriTerm and LiteralTerm
    class NodeTerm(val node: Node) : TermOrVariable()
    class VariableTerm(val variable: Variable) : TermOrVariable()
}
