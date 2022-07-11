package nl.pompkracht.rosteringengine.reproducer

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class WorkerAvailabilityConflictTest {

    private lateinit var fixture: RosterConstraintProviderFixture

    @BeforeEach
    fun setUp() {
        fixture = RosterConstraintProviderFixture()
    }

    @Test
    fun `Shift assigned to worker with missing availability is penalized`() {
        fixture.run {
            verifier.verifyThat(RosterConstraintProvider::workerAvailabilityConflict).givenSolution(roster.also {
                // shift: monday 22:00 to tuesday 06:00
                // availability: every day from 04:00 to 00:00
                // missing availability: tuesday 00:00 to 04:00

                it.shiftAssignments[2].worker = workers[2]
            })
        }.penalizesBy(1)
    }

    @Test
    fun `Shift assigned to worker with sufficient availability is not penalized`() {
        fixture.run {
            verifier.verifyThat(RosterConstraintProvider::workerAvailabilityConflict).givenSolution(roster.also {
                // worker[0] is always available
                it.shiftAssignments[2].worker = workers[0]
            })
        }.penalizesBy(0)
    }

    @Test
    fun `Each shift with insufficient availability is penalized`() {
        fixture.run {
            verifier.verifyThat(RosterConstraintProvider::workerAvailabilityConflict).givenSolution(roster.also {
                // worker[2] availability: every day from 04:00 to 00:00

                // shift: monday 22:00 to tuesday 06:00 -> missing availability: tuesday 00:00 to 04:00
                it.shiftAssignments[2].worker = workers[2]

                // shift: tuesday 22:00 to wednesday 06:00 -> missing availability: wednesday 00:00 to 04:00
                it.shiftAssignments[7].worker = workers[2]

                // shift: friday 16:00 to friday 22:00 -> sufficient availability
                it.shiftAssignments[22].worker = workers[2]
            })
        }.penalizesBy(2)
    }
}
