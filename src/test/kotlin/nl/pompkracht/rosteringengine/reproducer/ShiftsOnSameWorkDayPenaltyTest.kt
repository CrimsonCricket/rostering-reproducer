package nl.pompkracht.rosteringengine.reproducer

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ShiftsOnSameWorkDayPenaltyTest {

    private lateinit var fixture: RosterConstraintProviderFixture

    @BeforeEach
    fun setUp() {
        fixture = RosterConstraintProviderFixture()
    }

    @Test
    fun `Shifts for the same worker on the same work day are penalized`() {
        fixture.run {
            verifier.verifyThat(RosterConstraintProvider::shiftsOnSameWorkdayPenalty).givenSolution(roster.also {
                assignment(0, 0).worker = workers[0] // monday
                assignment(1, 0).worker = workers[0] // monday
                assignment(2, 16).worker = workers[0] // thursday
                assignment(3, 0).worker = workers[0] // monday

                // 3 shifts on monday -> penalty (3*8)^2 = 576
                // 1 shift on thursday -> penalty (1*8)^2 = 64
            }).penalizesBy(640)
        }
    }

    @Test
    fun `Shifts on the same work day are considered separately for each worker`() {

        fixture.run {
            verifier.verifyThat(RosterConstraintProvider::shiftsOnSameWorkdayPenalty).givenSolution(roster.also {
                assignment(0, 0).worker = workers[0] // monday
                assignment(1, 0).worker = workers[0] // monday
                assignment(2, 16).worker = workers[0] // thursday
                assignment(3, 0).worker = workers[0] // monday

                // 3 shifts on monday -> penalty (3*8)^2 = 576
                // 1 shift on thursday -> penalty (1*8)^2 = 64


                assignment(0, 5).worker = workers[1] // tuesday
                assignment(0, 11).worker = workers[1] // wednesday
                assignment(0, 17).worker = workers[1] // thursday

                assignment(1, 5).worker = workers[1] // tuesday
                assignment(1, 11).worker = workers[1] // wednesday
                assignment(1, 17).worker = workers[1] // thursday

                // 2 shifts on tuesday -> penalty (2*8)^2 = 256
                // 2 shifts on wednesday -> penalty (2*8)^2 = 256
                // 2 shifts on thursday -> penalty (2*8)^2 = 256

            }).penalizesBy(1408)

        }
    }
}
