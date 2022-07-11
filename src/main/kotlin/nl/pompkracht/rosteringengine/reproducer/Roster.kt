package nl.pompkracht.rosteringengine.reproducer

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty
import org.optaplanner.core.api.domain.solution.PlanningScore
import org.optaplanner.core.api.domain.solution.PlanningSolution
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore
import java.time.ZoneId
import java.util.*

@PlanningSolution
@Suppress("unused", "MemberVisibilityCanBePrivate")
data class Roster(
    @PlanningEntityCollectionProperty @JvmField var shiftAssignments: List<ShiftAssignment>,
    @ProblemFactCollectionProperty @ValueRangeProvider(id = "workersRange") @JvmField var workers: List<Worker>,
    @JvmField var timeZone: ZoneId,
    @JvmField var locale: Locale,
    @PlanningScore @JvmField var score: HardMediumSoftScore? = null
) {

    val shifts: List<Shift>
        @ProblemFactCollectionProperty get() = shiftAssignments.map { it.shift }

    val shiftConflicts: List<ShiftConflict>
        @ProblemFactCollectionProperty get() = shifts.flatMapIndexed { index, shift ->
            shifts.drop(index + 1).filter { it.dateTimeRangeIncludingMandatoryRest.overlapsWith(shift.dateTimeRange) }
                .map { ShiftConflict(shift.shiftId, it.shiftId) }
        }

    val shiftDurations: List<ShiftDuration>
        @ProblemFactCollectionProperty get() = shifts.map { ShiftDuration(it.shiftId, it.duration) }

    val shiftWorkWeeks: List<ShiftWorkWeek>
        @ProblemFactCollectionProperty get() = shifts.map { ShiftWorkWeek(it.shiftId, it.workWeek(timeZone, locale)) }

    val shiftDaysOfWeek: List<ShiftDayOfWeek>
        @ProblemFactCollectionProperty get() = shifts.map {
            ShiftDayOfWeek(it.shiftId, it.dateTimeRange.start.atZone(timeZone).dayOfWeek)
        }

    val shiftDayParts: List<ShiftDayPart>
        @ProblemFactCollectionProperty get() = shifts.map { ShiftDayPart(it.shiftId, it.dayPart(timeZone)) }

    val workerTotalHoursTargets: List<WorkerTotalHoursTarget>
        @ProblemFactCollectionProperty get() {
            val firstDay = shifts.minOf { it.dateTimeRange.start }.atZone(timeZone).toLocalDate()
            val lastDay = shifts.maxOf { it.dateTimeRange.start }.atZone(timeZone).toLocalDate()
            val numberOfDays = firstDay.until(lastDay).days + 1
            return workers.map { WorkerTotalHoursTarget(it.workerId, (it.hoursPerWeek * numberOfDays) / 7) }
        }

    val workerAvailabilityConflicts: List<WorkerAvailabilityConflict>
        @ProblemFactCollectionProperty get() = shifts.flatMap { shift ->
            workers.filter {
                !it.fitsSchedule(
                    shift.dateTimeRange, timeZone
                )
            }.map { WorkerAvailabilityConflict(shift.shiftId, it.workerId) }
        }

    val workerUnavailabilityConflicts: List<WorkerUnavailabilityConflict>
        @ProblemFactCollectionProperty get() = shifts.flatMap { shift ->
            workers.filter {
                it.hasConflictingUnavailability(
                    shift.dateTimeRange
                )
            }.map { WorkerUnavailabilityConflict(shift.shiftId, it.workerId) }
        }


    fun determineNumberOfConflictsForAssignments() {
        if (shiftAssignments.isEmpty() || shiftAssignments[0].numberOfConflicts != null) return
        synchronized(this) {
            if (shiftAssignments.isEmpty() || shiftAssignments[0].numberOfConflicts != null) return

            val shiftConflictFacts = shiftConflicts
            val workerFacts = workers
            val availabilityConflictFacts = workerAvailabilityConflicts
            val unavailabilityConflictFacts = workerUnavailabilityConflicts

            shiftAssignments.forEach { assignment ->
                val shift = assignment.shift
                val shiftId = shift.shiftId

                val shiftConflicts = shiftConflictFacts.count { it.shiftTwo == shiftId || it.shiftTwo == shiftId }
                val serviceTypeConflicts = shift.serviceType?.let { serviceType ->
                    workerFacts.count { !it.serviceTypes.contains(serviceType) }
                } ?: 0
                val availabilityConflicts = availabilityConflictFacts.count { it.shiftId == shiftId }
                val unavailabilityConflict = unavailabilityConflictFacts.count { it.shiftId == shiftId }
                assignment.numberOfConflicts =
                    shiftConflicts + serviceTypeConflicts + availabilityConflicts + unavailabilityConflict
            }
        }
    }
}
