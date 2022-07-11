package nl.pompkracht.rosteringengine.reproducer

import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory

class ShiftAssignmentDifficultyWeightFactory : SelectionSorterWeightFactory<Roster, ShiftAssignment> {

    override fun createSorterWeight(solution: Roster, selection: ShiftAssignment): ShiftAssignmentDifficulty {
        solution.determineNumberOfConflictsForAssignments()
        return ShiftAssignmentDifficulty(
            selection.shiftAssignmentId,
            requireNotNull(selection.numberOfConflicts) { "Number of conflicts not determined" }
        )
    }
}
