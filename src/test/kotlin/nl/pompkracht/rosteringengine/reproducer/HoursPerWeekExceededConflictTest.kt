package nl.pompkracht.rosteringengine.reproducer

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class HoursPerWeekExceededConflictTest {

    private lateinit var fixture: RosterConstraintProviderFixture

    @BeforeEach
    fun setUp() {
        fixture = RosterConstraintProviderFixture()
    }

    @Test
    fun `Total shift duration per week exceeding target by more than eight hours is penalized`() {
        fixture.run {
            verifier.verifyThat(RosterConstraintProvider::hoursPerWeekExceededConflict).givenSolution(roster.also {
                it.shiftAssignments[3].worker = workers[8] // 8 hours
                it.shiftAssignments[10].worker = workers[8] // 4 hours
                it.shiftAssignments[14].worker = workers[8] // 8 hours
                it.shiftAssignments[19].worker = workers[8] // 8 hours
                it.shiftAssignments[24].worker = workers[8] // 8 hours
                // total of 36 hours, target for worker is 24 hours -> exceeded by 12 hours
            }).penalizesBy(1)
        }
    }

    @Test
    fun `Total shift duration per week exceeding target by less than eight hours is not penalized`() {
        fixture.run {
            verifier.verifyThat(RosterConstraintProvider::hoursPerWeekExceededConflict).givenSolution(roster.also {
                it.shiftAssignments[3].worker = workers[8] // 8 hours
                it.shiftAssignments[10].worker = workers[8] // 4 hours
                it.shiftAssignments[14].worker = workers[8] // 8 hours
                it.shiftAssignments[19].worker = workers[8] // 8 hours
                // total of 28 hours, target for worker is 24 hours -> exceeded by 4 hours
            }).penalizesBy(0)
        }
    }

    @Test
    fun `Total shift duration per week under target is not penalized`() {
        fixture.run {
            verifier.verifyThat(RosterConstraintProvider::hoursPerWeekExceededConflict).givenSolution(roster.also {
                it.shiftAssignments[3].worker = workers[8] // 8 hours
                it.shiftAssignments[10].worker = workers[8] // 4 hours
                // total of 12 hours, target for worker is 24 hours -> under target by 12 hours
            }).penalizesBy(0)
        }
    }


    @Test
    fun `Total shift duration per week exceeding target by exactly eight hours is not penalized`() {
        fixture.run {
            verifier.verifyThat(RosterConstraintProvider::hoursPerWeekExceededConflict).givenSolution(roster.also {
                it.shiftAssignments[3].worker = workers[8] // 8 hours
                it.shiftAssignments[14].worker = workers[8] // 8 hours
                it.shiftAssignments[19].worker = workers[8] // 8 hours
                it.shiftAssignments[24].worker = workers[8] // 8 hours
                // total of 32 hours, target for worker is 24 hours -> exceeded by 8 hours
            }).penalizesBy(0)
        }
    }

    @Test
    fun `Total shift duration per week exceeding target by exactly 16 hours is penalized once`() {
        fixture.run {
            verifier.verifyThat(RosterConstraintProvider::hoursPerWeekExceededConflict).givenSolution(roster.also {
                it.shiftAssignments[3].worker = workers[8] // 8 hours
                it.shiftAssignments[14].worker = workers[8] // 8 hours
                it.shiftAssignments[19].worker = workers[8] // 8 hours
                it.shiftAssignments[24].worker = workers[8] // 8 hours
                it.shiftAssignments[29].worker = workers[8] // 8 hours
                // total of 40 hours, target for worker is 24 hours -> exceeded by 16 hours
            }).penalizesBy(1)
        }
    }

    @Test
    fun `Total shift duration per week exceeding target by more than 16 hours is penalized twice`() {
        fixture.run {
            verifier.verifyThat(RosterConstraintProvider::hoursPerWeekExceededConflict).givenSolution(roster.also {
                it.shiftAssignments[3].worker = workers[8] // 8 hours
                it.shiftAssignments[10].worker = workers[8] // 4 hours
                it.shiftAssignments[14].worker = workers[8] // 8 hours
                it.shiftAssignments[19].worker = workers[8] // 8 hours
                it.shiftAssignments[24].worker = workers[8] // 8 hours
                it.shiftAssignments[29].worker = workers[8] // 8 hours
                // total of 44 hours, target for worker is 24 hours -> exceeded by 20 hours
            }).penalizesBy(2)
        }
    }


    @Test
    fun `Total shift duration limit is reset with every new week`() {
        fixture.run {
            verifier.verifyThat(RosterConstraintProvider::hoursPerWeekExceededConflict).givenSolution(roster.also {
                // worker[8] has a target of 24 hours per week

                it.shiftAssignments[3].worker = workers[8] // 8 hours
                it.shiftAssignments[24].worker = workers[8] // 8 hours
                it.shiftAssignments[29].worker = workers[8] // 8 hours
                // 24 hours in week 0 -> not penalized

                assignment(1, 3).worker = workers[8] // 8 hours
                assignment(1, 24).worker = workers[8] // 8 hours
                assignment(1, 29).worker = workers[8] // 8 hours
                assignment(1, 34).worker = workers[8] // 8 hours
                // 32 hours in week 1 -> not penalized

                assignment(2, 3).worker = workers[8] // 8 hours
                assignment(2, 10).worker = workers[8] // 4 hours
                assignment(2, 24).worker = workers[8] // 8 hours
                assignment(2, 29).worker = workers[8] // 8 hours
                assignment(2, 34).worker = workers[8] // 8 hours
                // 36 hours in week 2 -> penalized 1

                assignment(3, 3).worker = workers[8] // 8 hours
                assignment(3, 24).worker = workers[8] // 8 hours
                assignment(3, 29).worker = workers[8] // 8 hours
                // 24 hours in week 3 -> not penalized

            }).penalizesBy(1)
        }
    }


}
