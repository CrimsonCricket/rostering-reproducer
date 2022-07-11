package nl.pompkracht.rosteringengine.reproducer

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UnassignedShiftPenaltyTest {

    private lateinit var fixture: RosterConstraintProviderFixture

    @BeforeEach
    fun setUp() {
        fixture = RosterConstraintProviderFixture()
    }

    @Test
    fun `Each unassigned shift is penalized`() {
        fixture.run {
            verifier.verifyThat(RosterConstraintProvider::unassignedShiftPenalty).givenSolution(roster.also {
                assignments[0].worker = workers[0]
                assignments[1].worker = workers[1]
                assignment(2, 1).worker = workers[1]
            }).penalizesBy(assignments.size - 3)
        }
    }
}
