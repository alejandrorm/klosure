package me.alejandrorm.klosure.sparql

data class Variable(val name: String, val isBlankNode: Boolean = false) {
    override fun toString(): String {
        return if (isBlankNode) "_$name" else "?$name"
    }
}
