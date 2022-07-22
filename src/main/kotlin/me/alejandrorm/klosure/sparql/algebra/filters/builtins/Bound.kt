package me.alejandrorm.klosure.sparql.algebra.filters.builtins

import me.alejandrorm.klosure.model.FalseLiteral
import me.alejandrorm.klosure.model.Node
import me.alejandrorm.klosure.model.TrueLiteral
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.Variable
import me.alejandrorm.klosure.sparql.algebra.filters.Expression

class Bound(val variable: Variable) : Expression {
    override fun eval(solution: SolutionMapping): Node {
        return if (solution.boundVariables.containsKey(variable)) TrueLiteral else FalseLiteral
    }
}
