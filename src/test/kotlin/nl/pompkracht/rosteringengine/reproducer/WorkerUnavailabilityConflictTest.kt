package nl.pompkracht.rosteringengine.reproducer

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class WorkerUnavailabilityConflictTest {

    private lateinit var fixture: RosterConstraintProviderFixture

    @BeforeEach
    fun setUp() {
        fixture = RosterConstraintProviderFixture()
    }

    @Test
    fun `Shift assigned to worker with conflicting unavailability is penalized`() {
        fixture.run {
            verifier.verifyThat(RosterConstraintProvider::workerUnavailabilityConflict).givenSolution(roster.also {
                // shift is on saturday of first week, worker is unavailable in first weekend
                it.shiftAssignments[26].worker = workers[6]
            }).penalizesBy(1)
        }
    }


    @Test
    fun `Shift assigned to worker without conflicting unavailability is not penalized`() {
        fixture.run {
            verifier.verifyThat(RosterConstraintProvider::workerUnavailabilityConflict).givenSolution(roster.also {
                // shift is on saturday of second week, worker is unavailable in first weekend
                assignment(1, 26).worker = workers[6]
            }).penalizesBy(0)
        }
    }

    @Test
    fun `Each shift with conflicting unavailability is penalized`() {
        fixture.run {
            verifier.verifyThat(RosterConstraintProvider::workerUnavailabilityConflict).givenSolution(roster.also {
                // worker is unavailable in first weekend
                it.shiftAssignments[11].worker = workers[6] // wednesday of first week -> no conflict
                it.shiftAssignments[26].worker = workers[6] // saturday of first week -> conflict
                it.shiftAssignments[31].worker = workers[6] // sunday of first week -> conflict
                assignment(1, 26).worker = workers[6] // saturday of second week -> no conflict
            }).penalizesBy(2)
        }
    }
}
