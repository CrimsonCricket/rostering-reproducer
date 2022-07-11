package nl.pompkracht.rosteringengine.reproducer

import java.util.*
import java.util.UUID.randomUUID

abstract class AbstractId(val identifier: UUID = randomUUID()) : Comparable<AbstractId> {

    override fun toString() = identifier.toString()

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractId) return false

        if (identifier != other.identifier) return false

        return true
    }

    final override fun hashCode(): Int {
        return identifier.hashCode()
    }

    final override fun compareTo(other: AbstractId): Int {
        return identifier.compareTo(other.identifier)
    }
}
