package me.alejandrorm.klosure.sparql.algebra.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.sparql.SolutionMapping

class LeftJoin(val operator: AlgebraOperator) : AlgebraOperator {
    override fun toString(): String =
        "LeftJoin($operator)"

    override fun eval(
        solutions: Sequence<SolutionMapping>,
        graph: Graph
    ): Sequence<SolutionMapping> {
        // TODO: inefficient implementation, materializes the entire sequence
        val l1 = solutions.toList()

        val j = join(l1, operator, graph).toList()

        // TODO after fixing the join function, don't need the minute here
        return j.asSequence() + minus(l1, j)

    //        return solutions.flatMap { solution ->
//            operator.eval(sequenceOf(solution), graph).ifEmpty { sequenceOf(solution) }
//        }
    }

    private fun minus(l1: List<SolutionMapping>, l2: List<SolutionMapping>): Sequence<SolutionMapping> = sequence {
//        println("LEFT JOIN SECOND PASS")
        for (solution in l1) {
            if (!l2.any { solution.isCompatible(it) }) {
//                println("yielding $solution")
                yield(solution)
            }
        }
    }

    private fun join(l1: List<SolutionMapping>, operator: AlgebraOperator, graph: Graph):Sequence<SolutionMapping>  = sequence {
        val l2 = operator.eval(sequenceOf(SolutionMapping.EmptySolutionMapping), graph).toList()

//        println("LEFT JOIN FIRST PASS")
        for(solution1 in l1) {
            // TODO var yielded = false
            for(solution2 in l2) {
                if (solution1.isCompatible(solution2)) {
                    // TODO yielded = true
//                    println("yielding ${solution1.merge(solution2)}")
                    yield(solution1.merge(solution2))
                }
            }
            // TODO: if not yielded from the inner look, yield(solution1)
            // that way you don't need to convert l1 to a list, can keep it as sequence
        }
    }
}
