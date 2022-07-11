package nl.pompkracht.rosteringengine.reproducer

import org.optaplanner.core.api.domain.entity.PlanningEntity
import org.optaplanner.core.api.domain.entity.PlanningPin
import org.optaplanner.core.api.domain.lookup.PlanningId
import org.optaplanner.core.api.domain.solution.ProblemFactProperty
import org.optaplanner.core.api.domain.variable.PlanningVariable

@PlanningEntity(difficultyWeightFactoryClass = ShiftAssignmentDifficultyWeightFactory::class)
class ShiftAssignment(
    @PlanningId @JvmField var shiftAssignmentId: ShiftAssignmentId,
    @ProblemFactProperty @JvmField var shift: Shift,
    @PlanningVariable(valueRangeProviderRefs = ["workersRange"], nullable = true) @JvmField var worker: Worker? = null,
    @PlanningPin @JvmField var pinned: Boolean = false,
    @JvmField var numberOfConflicts: Int? = null
) {

    override fun toString(): String {
        return "ShiftAssignment(shiftAssignmentId=$shiftAssignmentId, shift=$shift, worker=$worker)"
    }
}
