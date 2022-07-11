package nl.pompkracht.rosteringengine.reproducer

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ShiftsInSameDayPartPenaltyTest {

    private lateinit var fixture: RosterConstraintProviderFixture

    @BeforeEach
    fun setUp() {
        fixture = RosterConstraintProviderFixture()
    }

    @Test
    fun `Shifts in the same day part assigned to the same worker are penalized`() {
        fixture.run {
            verifier.verifyThat(RosterConstraintProvider::shiftsInSameDayPartPenalty).givenSolution(roster.also {
                it.shiftAssignments[0].worker = workers[0] // morning
                it.shiftAssignments[5].worker = workers[0] // morning
                it.shiftAssignments[12].worker = workers[0] // afternoon
                it.shiftAssignments[18].worker = workers[0] // night
                it.shiftAssignments[23].worker = workers[0] // night
                it.shiftAssignments[28].worker = workers[0] // night

                // 2x morning -> penalizes (2*8)^2 = 256
                // 1x afternoon -> penalizes (1*8)^2 = 64
                // 3 x night -> penalizes (3*8)^2 = 576

            }).penalizesBy(896)
        }
    }

    @Test
    fun `Shifts in the same day part are penalized separately for each worker`() {
        fixture.run {
            verifier.verifyThat(RosterConstraintProvider::shiftsInSameDayPartPenalty).givenSolution(roster.also {
                it.shiftAssignments[0].worker = workers[0] // morning
                it.shiftAssignments[5].worker = workers[0] // morning
                it.shiftAssignments[12].worker = workers[0] // afternoon
                it.shiftAssignments[18].worker = workers[0] // night
                it.shiftAssignments[23].worker = workers[0] // night
                it.shiftAssignments[28].worker = workers[0] // night
                // 2x morning -> penalizes (2*8)^2 = 256
                // 1x afternoon -> penalizes (1*8)^2 = 64
                // 3 x night -> penalizes (3*8)^2 = 576


                it.shiftAssignments[1].worker = workers[1] // afternoon
                it.shiftAssignments[15].worker = workers[1] // afternoon

                // 2 x afternoon -> penalizes (2*8)^2 = 256

            }).penalizesBy(1152)
        }
    }
}
