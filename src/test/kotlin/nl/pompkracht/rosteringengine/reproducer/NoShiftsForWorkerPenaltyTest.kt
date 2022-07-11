package nl.pompkracht.rosteringengine.reproducer

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NoShiftsForWorkerPenaltyTest {

    private lateinit var fixture: RosterConstraintProviderFixture

    @BeforeEach
    fun setUp() {
        fixture = RosterConstraintProviderFixture()
    }

    @Test
    fun `A each combination of work week and worker without any shifts is penalized`() {
        fixture.run {
            verifier.verifyThat(RosterConstraintProvider::noShiftsForWorkerPenalty).givenSolution(
                roster.also {
                    assignOneShiftToEachWorkerInEachWeek()
                    assignments.filter { it.worker == workers[5] }.forEach { it.worker = null }

                    // worker has a target of 24 hours per week
                    // total target over 4 weeks is 4 *24 = 96
                    // penalty : 96^2 = 9216
                }
            ).penalizesBy(9216)
        }
    }
}
