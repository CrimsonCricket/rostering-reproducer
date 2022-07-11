package nl.pompkracht.rosteringengine.reproducer

import org.optaplanner.core.api.domain.lookup.PlanningId
import java.time.ZoneId

data class Worker(
    @PlanningId @JvmField var workerId: WorkerId,
    @JvmField var serviceTypes: Set<ServiceType>,
    val hoursPerWeek: Int,
    val schedule: List<WeeklyRecurringTimeRange>,
    val unavailability: List<DateTimeRange>
) {

    fun fitsSchedule(dateTimeRange: DateTimeRange, timeZone: ZoneId) =
        schedule.fold(listOf(dateTimeRange)) { missingAvailability, scheduleItem ->
            missingAvailability.flatMap {
                scheduleItem.subtractedFrom(
                    it, timeZone
                )
            }
        }.isEmpty()


    fun hasConflictingUnavailability(dateTimeRange: DateTimeRange) =
        unavailability.any { it.overlapsWith(dateTimeRange) }
}
