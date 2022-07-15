package me.alejandrorm.klosure.parser

import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import java.util.*
import kotlin.NoSuchElementException

class TripleComparator(private val actual: Set<String>) : TypeSafeMatcher<Set<String>>() {

    private val message = StringBuilder()
    private val blankNodeId = Regex("(_:[a-z0-9-]+)")

    companion object {
        @JvmStatic
        fun areEqualTriples(triples: Set<String>): TripleComparator {
            return TripleComparator(triples)
        }
    }

    private class Permutator(originalList: List<String>) {

        private val stack = Stack<Pair<List<String>, List<String>>>()

        init {
            stack.push(Pair(originalList, listOf()))
        }

        fun next(): List<String> {
            while (!stack.isEmpty()) {
                val (toDo, done) = stack.pop()
                if (toDo.isEmpty()) {
                    return done
                }

                for (a in toDo) {
                    stack.push(Pair(toDo - a, done + a))
                }
            }

            throw NoSuchElementException()
        }

        fun hasNext(): Boolean {
            return !stack.isEmpty()
        }
    }

    private fun initialize(triples: Set<String>): Set<String> {
        val blankNodes = mutableSetOf<String>()
        triples.forEach { triple ->
            blankNodeId.findAll(triple).forEach { match ->
                blankNodes.add(match.groupValues[0])
            }
        }
        return blankNodes
    }

    override fun matchesSafely(expected: Set<String>): Boolean {
        val blankNodes = initialize(actual)
        val permutator = Permutator(blankNodes.toList())
        while (permutator.hasNext()) {
            val permutation = permutator.next()
            val triples = actual.map { triple ->
                var t = triple
                for (i in permutation.indices) {
                    t = t.replace(permutation[i], "_:" + ('a' + i))
                }
                t
            }.toSet()

            if (expected == triples) {
                return true
            }
        }

        message.append("Expected: ")
        message.append(expected.joinToString("\n"))
        message.append("\nActual: ")
        message.append(actual.joinToString("\n"))
        return false
    }

    override fun describeTo(description: Description) {
        description.appendText(message.toString())
    }
}
