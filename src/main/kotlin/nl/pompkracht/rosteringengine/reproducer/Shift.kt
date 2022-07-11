package nl.pompkracht.rosteringengine.reproducer

import nl.pompkracht.rosteringengine.reproducer.DayPart.*
import org.optaplanner.core.api.domain.lookup.PlanningId
import java.time.Duration
import java.time.LocalTime
import java.time.Year
import java.time.ZoneId
import java.time.temporal.ChronoUnit.MINUTES
import java.time.temporal.WeekFields
import java.util.*

data class Shift(
    @PlanningId @JvmField var shiftId: ShiftId,
    @JvmField var dateTimeRange: DateTimeRange,
    @JvmField var serviceType: ServiceType?
) {

    val dateTimeRangeIncludingMandatoryRest: DateTimeRange
        get() {
            val (start, end) = dateTimeRange
            val restingPeriod = Duration.ofHours(12)
            return dateTimeRange.copy(start = start.minus(restingPeriod), end = end.plus(restingPeriod))
        }

    val duration: Duration
        get() = dateTimeRange.duration(MINUTES)

    fun workWeek(timeZone: ZoneId, locale: Locale): WorkWeek {
        val startDate = dateTimeRange.start.atZone(timeZone).toLocalDate()
        val weekFields = WeekFields.of(locale)
        return WorkWeek(
            Year.of(startDate.get(weekFields.weekBasedYear())), startDate.get(weekFields.weekOfWeekBasedYear())
        )
    }

    fun dayPart(timeZone: ZoneId): DayPart {
        val startTime = dateTimeRange.start.atZone(timeZone).toLocalTime()
        if (startTime < LocalTime.of(2, 0)) return NIGHT
        if (startTime < LocalTime.of(10, 0)) return MORNING
        if (startTime < LocalTime.of(18, 0)) return AFTERNOON
        return NIGHT
    }
}
