package nl.pompkracht.rosteringengine.reproducer

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SimultaneousAssignmentConflictTest {

    private lateinit var fixture: RosterConstraintProviderFixture

    @BeforeEach
    fun setUp() {
        fixture = RosterConstraintProviderFixture()
    }

    @Test
    fun `Overlapping shifts assigned to the same worker are penalized`() {
        fixture.run {
            verifier.verifyThat(RosterConstraintProvider::simultaneousAssigmentConflict).givenSolution(roster.also {
                it.shiftAssignments[0].worker = workers[6] // monday 06:00-14:00
                it.shiftAssignments[4].worker = workers[6] // monday 12:00-20:00
            }).penalizesBy(1)
        }
    }

    @Test
    fun `Shifts within 12 hours from each other, assigned to the same worker, are penalized`() {
        fixture.run {
            verifier.verifyThat(RosterConstraintProvider::simultaneousAssigmentConflict).givenSolution(roster.also {
                it.shiftAssignments[0].worker = workers[6] // monday 06:00-14:00
                it.shiftAssignments[2].worker = workers[6] // monday 22:00-06:00
            }).penalizesBy(1)
        }
    }

    @Test
    fun `Shifts with more than 12 hours between them, assigned to the same worker, are not penalized`() {
        fixture.run {
            verifier.verifyThat(RosterConstraintProvider::simultaneousAssigmentConflict).givenSolution(roster.also {
                it.shiftAssignments[0].worker = workers[6] // monday 06:00-14:00
                it.shiftAssignments[5].worker = workers[6] // tuesday 06:00-14:00
            }).penalizesBy(0)
        }
    }

    @Test
    fun `Shifts with exactly 12 hours between them, assigned to the same worker, are not penalized`() {
        fixture.run {
            verifier.verifyThat(RosterConstraintProvider::simultaneousAssigmentConflict).givenSolution(roster.also {
                it.shiftAssignments[9].worker = workers[6] // tuesday 12:00-20:00
                it.shiftAssignments[10].worker = workers[6] // wednesday 08:00-12:00
            }).penalizesBy(0)
        }
    }
}
