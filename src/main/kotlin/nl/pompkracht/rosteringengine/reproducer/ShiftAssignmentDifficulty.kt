package nl.pompkracht.rosteringengine.reproducer


data class ShiftAssignmentDifficulty(val shiftAssignmentId: ShiftAssignmentId, val difficulty: Int) :
    Comparable<ShiftAssignmentDifficulty> {

    override fun compareTo(other: ShiftAssignmentDifficulty): Int {
        return comparator.compare(this, other)
    }

    companion object {
        val comparator = compareBy<ShiftAssignmentDifficulty> { it.difficulty }.thenBy { it.shiftAssignmentId }
    }
}
