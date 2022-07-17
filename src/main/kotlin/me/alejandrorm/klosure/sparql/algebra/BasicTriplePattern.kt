package me.alejandrorm.klosure.sparql.algebra

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.IriId
import me.alejandrorm.klosure.model.TripleId
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.Variable

class BasicTriplePattern(val subject: TermOrVariable, val predicate: TermOrVariable, val obj: TermOrVariable) : TriplePattern {

    private val terms = arrayOf(subject, predicate, obj)

    override fun getVariables(): Set<Variable> {
        return terms.filterIsInstance<TermOrVariable.VariableTerm>().map { it.variable }.toSet()
    }

    override fun eval(solutions: Iterable<SolutionMapping>, graph: Graph): Iterable<SolutionMapping> {
        return solutions.flatMap { eval(it, graph) }
    }

    override fun eval(solution: SolutionMapping, graph: Graph): Iterable<SolutionMapping> {
        val resolvedTriple = getKnowns(solution)
        return when (resolvedTriple.type) {
            PatternKnowns.SUB_PRE_OBJ -> getSPO(solution, resolvedTriple, graph)
            PatternKnowns.SUB_PRE -> getSP(solution, resolvedTriple, graph)
            PatternKnowns.SUB -> getS(solution, resolvedTriple, graph)
            PatternKnowns.PRE_OBJ -> getPO(solution, resolvedTriple, graph)
            PatternKnowns.PRE -> getP(solution, resolvedTriple, graph)
            PatternKnowns.OBJ -> getO(solution, resolvedTriple, graph)
            PatternKnowns.SUB_OBJ -> getSO(solution, resolvedTriple, graph)
            PatternKnowns.NONE -> getNone(solution, resolvedTriple, graph)
        }
    }

    private fun getKnowns(currentSolution: SolutionMapping): ResolvedTriple {
        var mask = 0
        val s = terms[0].resolve(currentSolution)
        if (s.isBound()) mask += 4
        val p = terms[1].resolve(currentSolution)
        if (p.isBound()) mask += 2
        val o = terms[2].resolve(currentSolution)
        if (o.isBound()) mask += 1

        return ResolvedTriple(s, p, o, PatternKnowns.values()[mask])
    }

    private fun getSPO(currentSolution: SolutionMapping, triple: ResolvedTriple, graph: Graph): Iterable<SolutionMapping> {
        val concretePre = (triple.predicate.getTerm() as IriId).iri
        val concreteSub = triple.subject.getTerm()
        val concreteObj = triple.obj.getTerm()

        val tripleId = TripleId(concreteSub, concretePre, concreteObj)
        return if (graph.getNode(tripleId) != null)
            listOf(currentSolution)
        else
            emptyList()
    }

    private fun getSP(currentSolution: SolutionMapping, triple: ResolvedTriple, graph: Graph): Iterable<SolutionMapping> {
        val concreteSub = triple.subject.getTerm()
        val concretePre = (triple.predicate.getTerm() as IriId).iri

        val it = graph.getNode(concreteSub)?.getOutgoingEdges(concretePre)
        it ?: return emptyList()
        return it.asSequence().map { edge ->
            currentSolution.bind((obj as TermOrVariable.VariableTerm).variable, (edge.id as TripleId).rdfObject)
        }.asIterable()
    }

    private fun getS(currentSolution: SolutionMapping, triple: ResolvedTriple, graph: Graph): Iterable<SolutionMapping> {
        val concreteSub = triple.subject.getTerm()

        val it = graph.getNode(concreteSub)?.getOutgoingEdges()
        it ?: return emptyList()
        return it.asSequence().map { edge ->
            // FIXME predicate and object could be the same variable
            val tripleId = edge.id as TripleId
            currentSolution.bind((obj as TermOrVariable.VariableTerm).variable, tripleId.rdfObject)
                .bind((predicate as TermOrVariable.VariableTerm).variable, IriId(tripleId.predicate))
        }.asIterable()
    }

    private fun getPO(currentSolution: SolutionMapping, triple: ResolvedTriple, graph: Graph): Iterable<SolutionMapping> {
        TODO()
//        val preIri =
//            when (predicate) {
//                // TODO make this more readable by moving this stuff to TermOrVariable
//                is TermOrVariable.NodeTerm -> (predicate.node.id as TripleId).predicate
//                is TermOrVariable.VariableTerm -> (currentSolution.boundVariables[predicate.variable]!!.id as TripleId).predicate
//            }
//        val obj =
//            when (obj) {
//                is TermOrVariable.NodeTerm -> obj.node
//                is TermOrVariable.VariableTerm -> currentSolution.boundVariables[obj.variable]!!
//            }
//
//        val it = graph.getNode(obj.id)?.getIncomingEdges(preIri)
//        it ?: return emptyList()
//        return it.asSequence().map { edge ->
//            currentSolution.bind(
//                (subject as TermOrVariable.VariableTerm).variable,
//                (graph.getNode((edge.id as TripleId).subject)!!)
//            )
//        }.asIterable()
    }

    private fun getP(currentSolution: SolutionMapping, triple: ResolvedTriple, graph: Graph): Iterable<SolutionMapping> {
        TODO()
//        val preIri =
//            when (predicate) {
//                // TODO make this more readable by moving this stuff to TermOrVariable
//                is TermOrVariable.NodeTerm -> (predicate.node.id as TripleId).predicate
//                is TermOrVariable.VariableTerm -> (currentSolution.boundVariables[predicate.variable]!!.id as TripleId).predicate
//            }
//
//        val it = graph.getPredicateNodes(preIri)
//        return it.map { edge ->
//            // FIXME subject and object could be the same variable and then need to be the same node
//            currentSolution.bind(
//                (subject as TermOrVariable.VariableTerm).variable,
//                (graph.getNode((edge.id as TripleId).subject)!!)
//            )
//                .bind(
//                    (obj as TermOrVariable.VariableTerm).variable,
//                    (graph.getNode(edge.id.rdfObject)!!)
//                )
//        }.asIterable()
    }

    private fun getO(currentSolution: SolutionMapping, triple: ResolvedTriple, graph: Graph): Iterable<SolutionMapping> {
TODO()
//        val obj =
//            when (obj) {
//                is TermOrVariable.NodeTerm -> obj.node
//                is TermOrVariable.VariableTerm -> currentSolution.boundVariables[obj.variable]!!
//            }
//        val it = graph.getNode(obj.id)?.getIncomingEdges()
//        it ?: return emptyList()
//        return it.asSequence().map { edge ->
//            // FIXME predicate and subject could be the same variable
//            val tripleId = edge.id as TripleId
//            currentSolution.bind((subject as TermOrVariable.VariableTerm).variable, (graph.getNode(tripleId.subject)!!))
//                .bind((predicate as TermOrVariable.VariableTerm).variable, edge)
//        }.asIterable()
    }

    private fun getSO(currentSolution: SolutionMapping, triple: ResolvedTriple, graph: Graph): Iterable<SolutionMapping> {
TODO()
//        val sub =
//            when (subject) {
//                is TermOrVariable.NodeTerm -> subject.node
//                is TermOrVariable.VariableTerm -> currentSolution.boundVariables[subject.variable]!!
//            }
//        val obj =
//            when (obj) {
//                is TermOrVariable.NodeTerm -> obj.node
//                is TermOrVariable.VariableTerm -> currentSolution.boundVariables[obj.variable]!!
//            }
//        val it = graph.getNode(sub.id)?.getOutgoingEdges()
//        it ?: return emptyList()
//        return it.asSequence().filter { predicateNode ->
//            (predicateNode.id as TripleId).rdfObject == obj
//        }.map { edge ->
//            currentSolution.bind((predicate as TermOrVariable.VariableTerm).variable, edge)
//        }.asIterable()
    }

    private fun getNone(currentSolution: SolutionMapping, triple: ResolvedTriple, graph: Graph): Iterable<SolutionMapping> {
        TODO()
        // check for on of five cases
        // all variables different, all variables the same, or two of them the same
    }

    private enum class PatternKnowns {
        NONE,
        OBJ,
        PRE,
        PRE_OBJ,
        SUB,
        SUB_OBJ,
        SUB_PRE,
        SUB_PRE_OBJ
    }

    private data class ResolvedTriple(
        val subject : TermOrVariable,
        val predicate: TermOrVariable,
        val obj: TermOrVariable,
        val type: PatternKnowns
    )
}
