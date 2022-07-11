package nl.pompkracht.rosteringengine.reproducer

import nl.pompkracht.rosteringengine.reproducer.ServiceType.SHOP
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.DayOfWeek.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WorkerTest {

    private lateinit var fixture: WorkerFixture

    @BeforeEach
    fun setUp() {
        fixture = WorkerFixture()
    }


    @Test
    fun `Fits schedule for date-time range exactly matching one schedule item`() {
        fixture.run {
            assertTrue(worker.fitsSchedule(monday6AMTo2PM, timeZone))
        }
    }

    @Test
    fun `Fits schedule for date-time range that is covered by 2 adjacent schedule items`() {
        fixture.run {
            assertTrue(worker.fitsSchedule(wednesday10PMToThursday6AM, timeZone))
        }
    }

    @Test
    fun `Does not fit schedule for date-time range that is partly uncovered at the end`() {
        fixture.run {
            assertFalse(worker.fitsSchedule(monday6AMTo4PM, timeZone))
        }
    }

    @Test
    fun `Does not fit schedule for date-time range that is partly uncovered at the start`() {
        fixture.run {
            assertFalse(worker.fitsSchedule(wednesday4PMToThursday6AM, timeZone))
        }
    }

    @Test
    fun `Does not fit schedule for date-time range that is partly uncovered in the middle`() {
        fixture.run {
            assertFalse(worker.fitsSchedule(monday6AMToWednesday10PM, timeZone))
        }
    }

    @Test
    fun `Has conflicting unavailability for time range overlapping one unavailability item`() {
        fixture.run {
            assertTrue(worker.hasConflictingUnavailability(DateTimeRange(monday6AM, monday4PM)))
        }
    }

    @Test
    fun `Has no conflicting unavailability for date-time range adjacent to unavailability item items`() {
        fixture.run {
            assertFalse(worker.hasConflictingUnavailability(DateTimeRange(monday4PM, wednesday10PM)))
        }
    }
}


@Suppress("MemberVisibilityCanBePrivate")
class WorkerFixture {
    val timeZone: ZoneId = ZoneId.of("Europe/Amsterdam")
    fun LocalDateTime.toInstant(): Instant = atZone(timeZone).toInstant()
    val monday6AM = LocalDateTime.of(2022, 7, 4, 6, 0).toInstant()
    val monday2PM = LocalDateTime.of(2022, 7, 4, 14, 0).toInstant()
    val monday4PM = LocalDateTime.of(2022, 7, 4, 16, 0).toInstant()
    val wednesday4PM = LocalDateTime.of(2022, 7, 6, 16, 0).toInstant()
    val wednesday10PM = LocalDateTime.of(2022, 7, 6, 22, 0).toInstant()
    val thursday6AM = LocalDateTime.of(2022, 7, 7, 6, 0).toInstant()

    val monday6AMTo2PM = DateTimeRange(monday6AM, monday2PM)
    val monday6AMTo4PM = DateTimeRange(monday6AM, monday4PM)
    val monday6AMToWednesday10PM = DateTimeRange(monday6AM, wednesday10PM)
    val wednesday4PMToThursday6AM = DateTimeRange(wednesday4PM, thursday6AM)
    val wednesday10PMToThursday6AM = DateTimeRange(wednesday10PM, thursday6AM)


    val worker = Worker(
        WorkerId(), setOf(SHOP), hoursPerWeek = 38, schedule = listOf(
            WeeklyRecurringTimeRange(
                WeeklyRecurringTime(MONDAY, LocalTime.of(6, 0)), WeeklyRecurringTime(MONDAY, LocalTime.of(14, 0)),
            ),
            WeeklyRecurringTimeRange(
                WeeklyRecurringTime(WEDNESDAY, LocalTime.of(20, 0)), WeeklyRecurringTime(THURSDAY, LocalTime.of(0, 0)),
            ),
            WeeklyRecurringTimeRange(
                WeeklyRecurringTime(THURSDAY, LocalTime.of(0, 0)), WeeklyRecurringTime(THURSDAY, LocalTime.of(8, 0)),
            ),
        ), unavailability = listOf(
            DateTimeRange(monday2PM, monday4PM), DateTimeRange(wednesday10PM, thursday6AM)
        )
    )


}
