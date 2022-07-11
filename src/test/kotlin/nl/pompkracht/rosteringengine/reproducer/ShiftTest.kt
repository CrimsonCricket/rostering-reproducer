package nl.pompkracht.rosteringengine.reproducer

import nl.pompkracht.rosteringengine.reproducer.DayPart.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.*
import java.util.*
import kotlin.test.assertEquals

class ShiftTest {

    private lateinit var fixture: ShiftFixture

    @BeforeEach
    fun setUp() {
        fixture = ShiftFixture()
    }

    @Test
    fun `Mandatory rest period is twelve hours before and after shift date-time range`() {
        fixture.run {
            assertEquals(
                DateTimeRange(wednesday6PM, friday2AM),
                Shift(shiftId, DateTimeRange(thursday6AM, thursday2PM), serviceType).dateTimeRangeIncludingMandatoryRest
            )
        }
    }

    @Test
    fun `Duration is rounded down to minutes`() {
        fixture.run {
            assertEquals(
                Duration.ofHours(11).plusMinutes(30),
                Shift(shiftId, DateTimeRange(thursday230PM, friday2AM), serviceType).duration
            )
            assertEquals(
                Duration.ofHours(11).plusMinutes(30),
                Shift(shiftId, DateTimeRange(thursday230PM, friday2AM.plusSeconds(12)), serviceType).duration
            )
            assertEquals(
                Duration.ofHours(11).plusMinutes(29),
                Shift(shiftId, DateTimeRange(thursday230PM.plusSeconds(12), friday2AM), serviceType).duration
            )
        }
    }

    @Test
    fun `Work week is based on start date`() {
        fixture.run {
            assertEquals(
                WorkWeek(Year.of(2022), 26),
                Shift(shiftId, DateTimeRange(sunday10PM, monday6AM), serviceType).workWeek(timeZone, locale)
            )
        }
    }

    @Test
    fun `Work week year is week-based year`() {
        fixture.run {
            assertEquals(
                WorkWeek(Year.of(2021), 52),
                Shift(shiftId, DateTimeRange(saturday1Jan22At10PM, sunday2Jan22AtAM), serviceType).workWeek(
                    timeZone, locale
                )
            )
        }
    }

    @Test
    fun `Day part of shift starting before 2AM`() {
        fixture.run {
            assertEquals(
                NIGHT, Shift(shiftId, DateTimeRange(friday130AM, saturday8AM), serviceType).dayPart(timeZone)
            )
        }
    }

    @Test
    fun `Day part of shift starting exactly at 2AM`() {
        fixture.run {
            assertEquals(
                MORNING, Shift(shiftId, DateTimeRange(friday2AM, saturday8AM), serviceType).dayPart(timeZone)
            )
        }
    }

    @Test
    fun `Day part of shift starting between 2AM and 10 AM`() {
        fixture.run {
            assertEquals(
                MORNING, Shift(shiftId, DateTimeRange(thursday6AM, thursday2PM), serviceType).dayPart(timeZone)
            )
        }
    }

    @Test
    fun `Day part of shift starting exactly at 10 AM`() {
        fixture.run {
            assertEquals(
                AFTERNOON, Shift(shiftId, DateTimeRange(wednesday10AM, wednesday6PM), serviceType).dayPart(timeZone)
            )
        }
    }

    @Test
    fun `Day part of shift starting between 10 AM and 6 PM`() {
        fixture.run {
            assertEquals(
                AFTERNOON, Shift(shiftId, DateTimeRange(thursday2PM, thursday230PM), serviceType).dayPart(timeZone)
            )
        }
    }

    @Test
    fun `Day part of shift starting exactly at 6 PM`() {
        fixture.run {
            assertEquals(
                NIGHT, Shift(shiftId, DateTimeRange(wednesday6PM, thursday6AM), serviceType).dayPart(timeZone)
            )
        }
    }

    @Test
    fun `Day part of shift starting after 6 PM`() {
        fixture.run {
            assertEquals(
                NIGHT, Shift(shiftId, DateTimeRange(sunday10PM, monday6AM), serviceType).dayPart(timeZone)
            )
        }
    }
}

@Suppress("MemberVisibilityCanBePrivate")
class ShiftFixture {
    val timeZone: ZoneId = ZoneId.of("Europe/Amsterdam")
    val locale = Locale("nl", "NL")
    fun LocalDateTime.toInstant(): Instant = atZone(timeZone).toInstant()
    val shiftId = ShiftId()
    val serviceType = ServiceType.SHOP

    val saturday1Jan22At10PM = LocalDateTime.of(2022, 1, 1, 22, 0).toInstant()
    val sunday2Jan22AtAM = LocalDateTime.of(2022, 1, 2, 6, 0).toInstant()
    val wednesday10AM = LocalDateTime.of(2022, 6, 29, 10, 0).toInstant()
    val wednesday6PM = LocalDateTime.of(2022, 6, 29, 18, 0).toInstant()
    val thursday6AM = LocalDateTime.of(2022, 6, 30, 6, 0).toInstant()
    val thursday2PM = LocalDateTime.of(2022, 6, 30, 14, 0).toInstant()
    val thursday230PM = LocalDateTime.of(2022, 6, 30, 14, 30).toInstant()
    val friday130AM = LocalDateTime.of(2022, 7, 1, 1, 30).toInstant()
    val friday2AM = LocalDateTime.of(2022, 7, 1, 2, 0).toInstant()
    val saturday8AM = LocalDateTime.of(2022, 7, 2, 8, 0).toInstant()

    val sunday10PM = LocalDateTime.of(2022, 7, 3, 22, 0).toInstant()
    val monday6AM = LocalDateTime.of(2022, 7, 4, 6, 0).toInstant()

}
