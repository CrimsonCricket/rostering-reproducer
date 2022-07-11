package nl.pompkracht.rosteringengine.reproducer

import nl.pompkracht.rosteringengine.reproducer.ServiceType.BAKERY
import nl.pompkracht.rosteringengine.reproducer.ServiceType.SHOP
import org.optaplanner.test.api.score.stream.ConstraintVerifier
import java.time.DayOfWeek.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.LocalTime.MIDNIGHT
import java.time.ZoneId
import java.util.*

@Suppress("MemberVisibilityCanBePrivate")
class RosterConstraintProviderFixture {
    val timeZone: ZoneId = ZoneId.of("Europe/Amsterdam")
    val locale = Locale("nl", "NL")
    fun LocalDateTime.toInstant(): Instant = atZone(timeZone).toInstant()
    fun Instant.plusWeeks(weeks: Int): Instant = atZone(timeZone).plusWeeks(weeks.toLong()).toInstant()
    fun DateTimeRange.plusWeeks(weeks: Int) = copy(start.plusWeeks(weeks), end.plusWeeks(weeks))
    fun Shift.pluWeeks(weeks: Int) = copy(ShiftId(), dateTimeRange.plusWeeks(weeks), serviceType)

    val mondayMidnight = LocalDateTime.of(2022, 6, 27, 0, 0).toInstant()
    val monday6AM = LocalDateTime.of(2022, 6, 27, 6, 0).toInstant()
    val mondayNoon = LocalDateTime.of(2022, 6, 27, 12, 0).toInstant()
    val monday2PM = LocalDateTime.of(2022, 6, 27, 14, 0).toInstant()
    val monday8PM = LocalDateTime.of(2022, 6, 27, 20, 0).toInstant()
    val monday10PM = LocalDateTime.of(2022, 6, 27, 22, 0).toInstant()
    val tuesday6AM = LocalDateTime.of(2022, 6, 28, 6, 0).toInstant()
    val tuesdayNoon = LocalDateTime.of(2022, 6, 28, 12, 0).toInstant()
    val tuesday2PM = LocalDateTime.of(2022, 6, 28, 14, 0).toInstant()
    val tuesday8PM = LocalDateTime.of(2022, 6, 28, 20, 0).toInstant()
    val tuesday10PM = LocalDateTime.of(2022, 6, 28, 22, 0).toInstant()
    val wednesday6AM = LocalDateTime.of(2022, 6, 29, 6, 0).toInstant()
    val wednesday8AM = LocalDateTime.of(2022, 6, 29, 8, 0).toInstant()
    val wednesdayNoon = LocalDateTime.of(2022, 6, 29, 12, 0).toInstant()
    val wednesday2PM = LocalDateTime.of(2022, 6, 29, 14, 0).toInstant()
    val wednesday8PM = LocalDateTime.of(2022, 6, 29, 20, 0).toInstant()
    val wednesday10PM = LocalDateTime.of(2022, 6, 29, 22, 0).toInstant()
    val thursday6AM = LocalDateTime.of(2022, 6, 30, 6, 0).toInstant()
    val thursdayNoon = LocalDateTime.of(2022, 6, 30, 12, 0).toInstant()
    val thursday2PM = LocalDateTime.of(2022, 6, 30, 14, 0).toInstant()
    val thursday8PM = LocalDateTime.of(2022, 6, 30, 20, 0).toInstant()
    val thursday10PM = LocalDateTime.of(2022, 6, 30, 22, 0).toInstant()
    val friday6AM = LocalDateTime.of(2022, 7, 1, 6, 0).toInstant()
    val fridayNoon = LocalDateTime.of(2022, 7, 1, 12, 0).toInstant()
    val friday2PM = LocalDateTime.of(2022, 7, 1, 14, 0).toInstant()
    val friday8PM = LocalDateTime.of(2022, 7, 1, 20, 0).toInstant()
    val friday10PM = LocalDateTime.of(2022, 7, 1, 22, 0).toInstant()
    val saturdayMidnight = LocalDateTime.of(2022, 7, 2, 0, 0).toInstant()
    val saturday6AM = LocalDateTime.of(2022, 7, 2, 6, 0).toInstant()
    val saturdayNoon = LocalDateTime.of(2022, 7, 2, 12, 0).toInstant()
    val saturday2PM = LocalDateTime.of(2022, 7, 2, 14, 0).toInstant()
    val saturday8PM = LocalDateTime.of(2022, 7, 2, 20, 0).toInstant()
    val saturday10PM = LocalDateTime.of(2022, 7, 2, 22, 0).toInstant()
    val sunday6AM = LocalDateTime.of(2022, 7, 3, 6, 0).toInstant()
    val sundayNoon = LocalDateTime.of(2022, 7, 3, 12, 0).toInstant()
    val sunday2PM = LocalDateTime.of(2022, 7, 3, 14, 0).toInstant()
    val sunday8PM = LocalDateTime.of(2022, 7, 3, 20, 0).toInstant()
    val sunday10PM = LocalDateTime.of(2022, 7, 3, 22, 0).toInstant()

    val shiftsOfFirstWeek = listOf(
        /* 0 */Shift(ShiftId(), DateTimeRange(monday6AM, monday2PM), SHOP),
        /* 1 */Shift(ShiftId(), DateTimeRange(monday2PM, monday10PM), SHOP),
        /* 2 */Shift(ShiftId(), DateTimeRange(monday10PM, tuesday6AM), SHOP),
        /* 3 */Shift(ShiftId(), DateTimeRange(monday6AM, monday2PM), BAKERY),
        /* 4 */Shift(ShiftId(), DateTimeRange(mondayNoon, monday8PM), BAKERY),
        /* 5 */Shift(ShiftId(), DateTimeRange(tuesday6AM, tuesday2PM), SHOP),
        /* 6 */Shift(ShiftId(), DateTimeRange(tuesday2PM, tuesday10PM), SHOP),
        /* 7 */Shift(ShiftId(), DateTimeRange(tuesday10PM, wednesday6AM), SHOP),
        /* 8 */Shift(ShiftId(), DateTimeRange(tuesday6AM, tuesday2PM), BAKERY),
        /* 9 */Shift(ShiftId(), DateTimeRange(tuesdayNoon, tuesday8PM), BAKERY),
        /* 10 */Shift(ShiftId(), DateTimeRange(wednesday8AM, wednesdayNoon), BAKERY),
        /* 11 */Shift(ShiftId(), DateTimeRange(wednesday6AM, wednesday2PM), SHOP),
        /* 12 */Shift(ShiftId(), DateTimeRange(wednesday2PM, wednesday10PM), SHOP),
        /* 13 */Shift(ShiftId(), DateTimeRange(wednesday10PM, thursday6AM), SHOP),
        /* 14 */Shift(ShiftId(), DateTimeRange(wednesday6AM, wednesday2PM), BAKERY),
        /* 15 */Shift(ShiftId(), DateTimeRange(wednesdayNoon, wednesday8PM), BAKERY),
        /* 16 */Shift(ShiftId(), DateTimeRange(thursday6AM, thursday2PM), SHOP),
        /* 17 */Shift(ShiftId(), DateTimeRange(thursday2PM, thursday10PM), SHOP),
        /* 18 */Shift(ShiftId(), DateTimeRange(thursday10PM, friday6AM), SHOP),
        /* 19 */Shift(ShiftId(), DateTimeRange(thursday6AM, thursday2PM), BAKERY),
        /* 20 */Shift(ShiftId(), DateTimeRange(thursdayNoon, thursday8PM), BAKERY),
        /* 21 */Shift(ShiftId(), DateTimeRange(friday6AM, friday2PM), SHOP),
        /* 22 */Shift(ShiftId(), DateTimeRange(friday2PM, friday10PM), SHOP),
        /* 23 */Shift(ShiftId(), DateTimeRange(friday10PM, saturday6AM), SHOP),
        /* 24 */Shift(ShiftId(), DateTimeRange(friday6AM, friday2PM), BAKERY),
        /* 25 */Shift(ShiftId(), DateTimeRange(fridayNoon, friday8PM), BAKERY),
        /* 26 */Shift(ShiftId(), DateTimeRange(saturday6AM, saturday2PM), SHOP),
        /* 27 */Shift(ShiftId(), DateTimeRange(saturday2PM, saturday10PM), SHOP),
        /* 28 */Shift(ShiftId(), DateTimeRange(saturday10PM, sunday6AM), SHOP),
        /* 29 */Shift(ShiftId(), DateTimeRange(saturday6AM, saturday2PM), BAKERY),
        /* 30 */Shift(ShiftId(), DateTimeRange(saturdayNoon, saturday8PM), BAKERY),
        /* 31 */Shift(ShiftId(), DateTimeRange(sunday6AM, sunday2PM), SHOP),
        /* 32 */Shift(ShiftId(), DateTimeRange(sunday2PM, sunday10PM), SHOP),
        /* 33 */Shift(ShiftId(), DateTimeRange(sunday10PM, monday6AM.plusWeeks(1)), SHOP),
        /* 34 */Shift(ShiftId(), DateTimeRange(sunday6AM, sunday2PM), BAKERY),
        /* 35 */Shift(ShiftId(), DateTimeRange(sundayNoon, sunday8PM), BAKERY)
    )


    val shifts =
        shiftsOfFirstWeek + shiftsOfFirstWeek.map { it.pluWeeks(1) } + shiftsOfFirstWeek.map { it.pluWeeks(2) } + shiftsOfFirstWeek.map {
            it.pluWeeks(3)
        }.toMutableList()

    val allWeekSchedule = listOf(
        WeeklyRecurringTimeRange(
            WeeklyRecurringTime(MONDAY, MIDNIGHT), WeeklyRecurringTime(TUESDAY, MIDNIGHT)
        ), WeeklyRecurringTimeRange(
            WeeklyRecurringTime(TUESDAY, MIDNIGHT), WeeklyRecurringTime(WEDNESDAY, MIDNIGHT)
        ), WeeklyRecurringTimeRange(
            WeeklyRecurringTime(WEDNESDAY, MIDNIGHT), WeeklyRecurringTime(THURSDAY, MIDNIGHT)
        ), WeeklyRecurringTimeRange(
            WeeklyRecurringTime(THURSDAY, MIDNIGHT), WeeklyRecurringTime(FRIDAY, MIDNIGHT)
        ), WeeklyRecurringTimeRange(
            WeeklyRecurringTime(FRIDAY, MIDNIGHT), WeeklyRecurringTime(SATURDAY, MIDNIGHT)
        ), WeeklyRecurringTimeRange(
            WeeklyRecurringTime(SATURDAY, MIDNIGHT), WeeklyRecurringTime(SUNDAY, MIDNIGHT)
        ), WeeklyRecurringTimeRange(
            WeeklyRecurringTime(SUNDAY, MIDNIGHT), WeeklyRecurringTime(MONDAY, MIDNIGHT)
        )
    )

    val dailyFrom4AMSchedule = listOf(
        WeeklyRecurringTimeRange(
            WeeklyRecurringTime(MONDAY, LocalTime.of(4, 0)), WeeklyRecurringTime(TUESDAY, MIDNIGHT)
        ), WeeklyRecurringTimeRange(
            WeeklyRecurringTime(TUESDAY, LocalTime.of(4, 0)), WeeklyRecurringTime(WEDNESDAY, MIDNIGHT)
        ), WeeklyRecurringTimeRange(
            WeeklyRecurringTime(WEDNESDAY, LocalTime.of(4, 0)), WeeklyRecurringTime(THURSDAY, MIDNIGHT)
        ), WeeklyRecurringTimeRange(
            WeeklyRecurringTime(THURSDAY, LocalTime.of(4, 0)), WeeklyRecurringTime(FRIDAY, MIDNIGHT)
        ), WeeklyRecurringTimeRange(
            WeeklyRecurringTime(FRIDAY, LocalTime.of(4, 0)), WeeklyRecurringTime(SATURDAY, MIDNIGHT)
        ), WeeklyRecurringTimeRange(
            WeeklyRecurringTime(SATURDAY, LocalTime.of(4, 0)), WeeklyRecurringTime(SUNDAY, MIDNIGHT)
        ), WeeklyRecurringTimeRange(
            WeeklyRecurringTime(SUNDAY, LocalTime.of(4, 0)), WeeklyRecurringTime(MONDAY, MIDNIGHT)
        )
    )

    val mondayTuesdayThursday8To5Schedule = listOf(
        WeeklyRecurringTimeRange(
            WeeklyRecurringTime(MONDAY, LocalTime.of(8, 0)), WeeklyRecurringTime(MONDAY, LocalTime.of(17, 0))
        ), WeeklyRecurringTimeRange(
            WeeklyRecurringTime(TUESDAY, LocalTime.of(8, 0)), WeeklyRecurringTime(TUESDAY, LocalTime.of(17, 0))
        ), WeeklyRecurringTimeRange(
            WeeklyRecurringTime(THURSDAY, LocalTime.of(8, 0)), WeeklyRecurringTime(THURSDAY, LocalTime.of(17, 0))
        )
    )

    val mondayToThursday6To2Schedule = listOf(
        WeeklyRecurringTimeRange(
            WeeklyRecurringTime(MONDAY, LocalTime.of(6, 0)), WeeklyRecurringTime(MONDAY, LocalTime.of(14, 0))
        ), WeeklyRecurringTimeRange(
            WeeklyRecurringTime(TUESDAY, LocalTime.of(6, 0)), WeeklyRecurringTime(TUESDAY, LocalTime.of(14, 0))
        ), WeeklyRecurringTimeRange(
            WeeklyRecurringTime(WEDNESDAY, LocalTime.of(6, 0)), WeeklyRecurringTime(WEDNESDAY, LocalTime.of(14, 0))
        ), WeeklyRecurringTimeRange(
            WeeklyRecurringTime(THURSDAY, LocalTime.of(6, 0)), WeeklyRecurringTime(THURSDAY, LocalTime.of(14, 0))
        )
    )

    val firstWeekendUnavailable = listOf(DateTimeRange(saturdayMidnight, mondayMidnight.plusWeeks(1)))

    val workers = listOf(
        /*  0 */Worker(WorkerId(), setOf(SHOP), 32, allWeekSchedule, unavailability = emptyList()),
        /*  1 */Worker(WorkerId(), setOf(SHOP), 40, allWeekSchedule, unavailability = emptyList()),
        /*  2 */Worker(WorkerId(), setOf(SHOP), 38, dailyFrom4AMSchedule, unavailability = emptyList()),
        /*  3 */Worker(WorkerId(), setOf(SHOP), 28, dailyFrom4AMSchedule.drop(1), unavailability = emptyList()),
        /*  4 */Worker(WorkerId(), setOf(SHOP), 32, allWeekSchedule, unavailability = emptyList()),
        /*  5 */Worker(WorkerId(), setOf(SHOP), 24, mondayTuesdayThursday8To5Schedule, unavailability = emptyList()),
        /*  6 */Worker(WorkerId(), setOf(SHOP, BAKERY), 38, allWeekSchedule, unavailability = firstWeekendUnavailable),
        /*  7 */Worker(WorkerId(), setOf(SHOP, BAKERY), 20, dailyFrom4AMSchedule, unavailability = emptyList()),
        /*  8 */Worker(WorkerId(), setOf(BAKERY), 24, allWeekSchedule, unavailability = emptyList()),
        /*  9 */Worker(WorkerId(), setOf(BAKERY), 38, dailyFrom4AMSchedule, unavailability = emptyList()),
        /* 10 */Worker(WorkerId(), setOf(BAKERY), 32, allWeekSchedule, unavailability = emptyList()),
        /* 11 */Worker(WorkerId(), emptySet(), 32, mondayToThursday6To2Schedule, unavailability = emptyList())
    )

    val assignments = shifts.map { ShiftAssignment(ShiftAssignmentId(), it) }
    fun assignment(week: Int, index: Int) = assignments[(week * shiftsOfFirstWeek.size) + index]

    val roster = Roster(assignments, workers, timeZone, locale)
    val verifier: ConstraintVerifier<RosterConstraintProvider, Roster> =
        ConstraintVerifier.build(RosterConstraintProvider(), Roster::class.java, ShiftAssignment::class.java)

    fun assignOneShiftToEachWorkerInEachWeek() {
        repeat(4) {
            assignment(it, 0).worker = workers[0]
            assignment(it, 1).worker = workers[1]
            assignment(it, 2).worker = workers[2]
            assignment(it, 5).worker = workers[3]
            assignment(it, 6).worker = workers[4]
            assignment(it, 10).worker = workers[5]
            assignment(it, 7).worker = workers[6]
            assignment(it, 8).worker = workers[7]
            assignment(it, 3).worker = workers[8]
            assignment(it, 4).worker = workers[9]
            assignment(it, 9).worker = workers[10]
            assignment(it, 19).worker = workers[11]
        }
    }

}
