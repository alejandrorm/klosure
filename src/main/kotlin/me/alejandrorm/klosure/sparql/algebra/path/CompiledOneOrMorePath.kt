package me.alejandrorm.klosure.sparql.algebra.path

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.Variable
import me.alejandrorm.klosure.sparql.algebra.TermOrVariable
import me.alejandrorm.klosure.sparql.algebra.TriplePattern
import java.util.UUID

class CompiledOneOrMorePath(
    val head: TermOrVariable,
    val path: Path,
    val tail: TermOrVariable
) : CompiledPath, TriplePattern {

    override fun eval(solution: SolutionMapping, graph: Graph): Iterable<SolutionMapping> {
        val s = path.eval(head, tail, solution, graph)
        if (s.count() > 0) return s

        val v = TermOrVariable.VariableTerm(Variable(UUID.randomUUID().toString(), true))
        val solutions = path.eval(head, v, listOf(solution), graph)
        return solutions.flatMap {
            evalHelper(it, v, graph, setOf(head.resolve(it).getTerm()))
        }
    }

    /*
      p(h, t)

      p(h, x0)
      p(x0, t)

      p(h, x)
      p(x0, y0)

     */

    private fun evalHelper(
        solution: SolutionMapping,
        variable: TermOrVariable.VariableTerm,
        graph: Graph,
        boundValues: Set<NodeId>
    ): Iterable<SolutionMapping> {
        val s = path.eval(variable, tail, solution, graph)
        if (s.count() > 0) return s

        val v = TermOrVariable.VariableTerm(Variable(UUID.randomUUID().toString(), true))

        val solutions = path.eval(variable, v, solution, graph)
        return solutions.flatMap {
            val boundValue = variable.resolve(it).getTerm()
            if (boundValues.contains(boundValue)) emptyList()
            else evalHelper(it, v, graph, boundValues + boundValue)
        }
    }

    override fun eval(solutions: Iterable<SolutionMapping>, graph: Graph): Iterable<SolutionMapping> {
        return solutions.flatMap { eval(it, graph) }
    }

    override fun getVariables(): Set<Variable> = TODO() // setOf(head, tail)
}
