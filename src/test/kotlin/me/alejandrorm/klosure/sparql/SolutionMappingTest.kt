package me.alejandrorm.klosure.sparql

import me.alejandrorm.klosure.model.BlankId
import me.alejandrorm.klosure.model.Node
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class SolutionMappingTest {
    @Test
    fun testFreeVariablesAfterNoBinding() {
        val v1 = Variable("v1")
        val v2 = Variable("v2")
        val v3 = Variable("v3")

        val solutionMapping = SolutionMapping(setOf(v1, v2, v3), emptyMap())

        Assertions.assertEquals(setOf(v1, v2, v3), solutionMapping.getFreeVariables())
        Assertions.assertEquals(setOf(v1, v2, v3), solutionMapping.variables)
        Assertions.assertEquals(emptyMap<Variable, Node>(), solutionMapping.boundVariables)
    }

    @Test
    fun testFreeVariablesAfterBinding() {
        val v1 = Variable("v1")
        val v2 = Variable("v2")
        val v3 = Variable("v3")
        val v4 = Variable("v4")

        val n2 = Node(BlankId("a"))
        val n4 = Node(BlankId("b"))
        val solutionMapping = SolutionMapping(setOf(v1, v2, v3, v4),
            mapOf(v2 to n2, v4 to n4))

        Assertions.assertEquals(setOf(v1, v3), solutionMapping.getFreeVariables())
        Assertions.assertEquals(setOf(v1, v2, v3, v4), solutionMapping.variables)
        Assertions.assertEquals(mapOf(v2 to n2, v4 to n4), solutionMapping.boundVariables)
    }

    @Test
    fun testBinding() {
        val v1 = Variable("v1")
        val v2 = Variable("v2")
        val v3 = Variable("v3")
        val v4 = Variable("v4")

        val n1 = Node(BlankId("a"))
        val n2 = Node(BlankId("b"))
        val n3 = Node(BlankId("c"))
        val n4 = Node(BlankId("d"))

        val solutionMapping0 = SolutionMapping(setOf(v1, v2, v3, v4), emptyMap())
        val solutionMapping1 = solutionMapping0.bind(v1, n1)
        val solutionMapping2 = solutionMapping1.bind(v2, n2)
        val solutionMapping3 = solutionMapping2.bind(v3, n3)
        val solutionMapping4 = solutionMapping3.bind(v4, n4)

        Assertions.assertEquals(setOf(v1, v2, v3, v4), solutionMapping0.variables)
        Assertions.assertEquals(setOf(v1, v2, v3, v4), solutionMapping0.getFreeVariables())
        Assertions.assertEquals(emptyMap<Variable, Node>(), solutionMapping0.boundVariables)

        Assertions.assertEquals(setOf(v1, v2, v3, v4), solutionMapping1.variables)
        Assertions.assertEquals(setOf(v2, v3, v4), solutionMapping1.getFreeVariables())
        Assertions.assertEquals(mapOf(v1 to n1), solutionMapping1.boundVariables)

        Assertions.assertEquals(setOf(v1, v2, v3, v4), solutionMapping2.variables)
        Assertions.assertEquals(setOf(v3, v4), solutionMapping2.getFreeVariables())
        Assertions.assertEquals(mapOf(v1 to n1, v2 to n2), solutionMapping2.boundVariables)

        Assertions.assertEquals(setOf(v1, v2, v3, v4), solutionMapping3.variables)
        Assertions.assertEquals(setOf(v4), solutionMapping3.getFreeVariables())
        Assertions.assertEquals(mapOf(v1 to n1, v2 to n2, v3 to n3), solutionMapping3.boundVariables)

        Assertions.assertEquals(setOf(v1, v2, v3, v4), solutionMapping4.variables)
        Assertions.assertEquals(emptySet<Variable>(), solutionMapping4.getFreeVariables())
        Assertions.assertEquals(mapOf(v1 to n1, v2 to n2, v3 to n3, v4 to n4), solutionMapping4.boundVariables)
    }
}