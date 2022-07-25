package me.alejandrorm.klosure.sparql.algebra.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.IriId
import me.alejandrorm.klosure.model.TripleId
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.Variable

class BasicTriplePattern(
    val subject: TermOrVariable,
    val predicate: TermOrVariable,
    val obj: TermOrVariable
) :
    TriplePattern {

    private val terms = arrayOf(subject, predicate, obj)

    override fun getVariables(): Set<Variable> {
        return terms.filterIsInstance<TermOrVariable.VariableTerm>().map { it.variable }.toSet()
    }

    override fun eval(
        solutions: Sequence<SolutionMapping>,
        graph: Graph
    ): Sequence<SolutionMapping> {
        return solutions.flatMap { eval(it, graph) }
    }

    override fun eval(solution: SolutionMapping, graph: Graph): Sequence<SolutionMapping> {
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

    private fun getSPO(
        currentSolution: SolutionMapping,
        triple: ResolvedTriple,
        graph: Graph
    ): Sequence<SolutionMapping> {
        val concretePre = (triple.predicate.getTerm() as IriId).iri
        val concreteSub = triple.subject.getTerm()
        val concreteObj = triple.obj.getTerm()

        val tripleId = TripleId(concreteSub, concretePre, concreteObj)
        return if (graph.getNode(tripleId) != null) {
            sequenceOf(currentSolution)
        } else {
            emptySequence()
        }
    }

    private fun getSP(
        currentSolution: SolutionMapping,
        triple: ResolvedTriple,
        graph: Graph
    ): Sequence<SolutionMapping> {
        val concreteSub = triple.subject.getTerm()
        val concretePre = (triple.predicate.getTerm() as IriId).iri

        val it = graph.getNode(concreteSub)?.getOutgoingEdges(concretePre)
        it ?: return emptySequence()
        return it.asSequence().map { edge ->
            triple.obj.match(currentSolution, (edge.id as TripleId).obj)
        }.filterNotNull()
    }

    private fun getS(
        currentSolution: SolutionMapping,
        triple: ResolvedTriple,
        graph: Graph
    ): Sequence<SolutionMapping> {
        val concreteSub = triple.subject.getTerm()

        val it = graph.getNode(concreteSub)?.getOutgoingEdges()
        it ?: return emptyList<SolutionMapping>().asSequence()

        return it.asSequence().map { edge ->
            val tripleId = edge.id as TripleId

            triple.obj.match(currentSolution, tripleId.obj)?.let {
                triple.predicate.resolve(it).match(it, IriId(tripleId.predicate))
            }
        }.filterNotNull()
    }

    private fun getPO(
        currentSolution: SolutionMapping,
        triple: ResolvedTriple,
        graph: Graph
    ): Sequence<SolutionMapping> {
        val concretePredicate = (triple.predicate.getTerm() as IriId).iri
        val concreteObj = triple.obj.getTerm()

        val it = graph.getNode(concreteObj)?.getIncomingEdges(concretePredicate)
        it ?: return emptySequence()
        return it.asSequence().map { edge ->
            triple.subject.match(currentSolution, (edge.id as TripleId).subject)
        }.filterNotNull()
    }

    private fun getP(
        currentSolution: SolutionMapping,
        triple: ResolvedTriple,
        graph: Graph
    ): Sequence<SolutionMapping> {
        val concretePre = (triple.predicate.getTerm() as IriId).iri

        val it = graph.getPredicateNodes(concretePre)

        val objVariable = (obj as TermOrVariable.VariableTerm).variable
        val subjectVariable = (subject as TermOrVariable.VariableTerm).variable

        // TODO change with 'match'
        return if (objVariable == subjectVariable) {
            it.asSequence().filter { edge ->
                val tripleId = edge.id as TripleId
                tripleId.obj == tripleId.subject
            }.map {
                currentSolution.bind(objVariable, (it.id as TripleId).obj)
            }
        } else {
            it.map { edge ->
                currentSolution.bind(
                    subjectVariable,
                    (edge.id as TripleId).subject
                )
                    .bind(
                        objVariable,
                        edge.id.obj
                    )
            }.asSequence()
        }
    }

    private fun getO(
        currentSolution: SolutionMapping,
        triple: ResolvedTriple,
        graph: Graph
    ): Sequence<SolutionMapping> {
        val concreteObj = triple.obj.getTerm()
        val it = graph.getNode(concreteObj)?.getIncomingEdges()
        it ?: return emptySequence()

        val subjectVariable = (subject as TermOrVariable.VariableTerm).variable
        val predicateVariable = (predicate as TermOrVariable.VariableTerm).variable
        // TODO change with 'match'

        return if (subjectVariable == predicateVariable) {
            it.asSequence().filter { edge ->
                val tripleId = edge.id as TripleId
                tripleId.subject == IriId(tripleId.predicate)
            }.map {
                currentSolution.bind(subjectVariable, (it.id as TripleId).subject)
            }
        } else {
            it.asSequence().map { edge ->
                val tripleId = edge.id as TripleId
                currentSolution.bind(subjectVariable, tripleId.subject)
                    .bind(predicateVariable, IriId(tripleId.predicate))
            }
        }
    }

    private fun getSO(
        currentSolution: SolutionMapping,
        triple: ResolvedTriple,
        graph: Graph
    ): Sequence<SolutionMapping> {
        val concreteSubject = triple.subject.getTerm()
        val concreteObj = triple.obj.getTerm()
        val it = graph.getNode(concreteSubject)?.getOutgoingEdges()
        it ?: return emptySequence()
        return it.asSequence().filter { predicateNode ->
            (predicateNode.id as TripleId).obj == concreteObj
        }.map { edge ->
            currentSolution.bind(
                (predicate as TermOrVariable.VariableTerm).variable,
                IriId((edge.id as TripleId).predicate)
            )
        }
    }

    private fun getNone(
        currentSolution: SolutionMapping,
        triple: ResolvedTriple,
        graph: Graph
    ): Sequence<SolutionMapping> {
        return graph.getAllAssertedTriples().asSequence().map { edge ->
            val tripleId = edge.id as TripleId

            triple.subject.match(currentSolution, tripleId.subject)?.let {
                triple.predicate.resolve(it).match(it, IriId(tripleId.predicate))
            }?.let {
                triple.obj.resolve(it).match(it, tripleId.obj)
            }
        }.filterNotNull()
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
        val subject: TermOrVariable,
        val predicate: TermOrVariable,
        val obj: TermOrVariable,
        val type: PatternKnowns
    )
}
