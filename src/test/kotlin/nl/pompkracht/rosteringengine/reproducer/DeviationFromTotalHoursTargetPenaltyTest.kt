package nl.pompkracht.rosteringengine.reproducer

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DeviationFromTotalHoursTargetPenaltyTest {

    private lateinit var fixture: RosterConstraintProviderFixture

    @BeforeEach
    fun setUp() {
        fixture = RosterConstraintProviderFixture()
    }


    @Test
    fun `Total hours under target is penalized`() {
        fixture.run {
            verifier.verifyThat(RosterConstraintProvider::deviationFromTotalHoursTargetPenalty)
                .givenSolution(roster.also {
                    assignment(0, 0).worker = workers[0] // 8 hours
                    assignment(0, 5).worker = workers[0] // 8 hours
                    assignment(0, 11).worker = workers[0] // 8 hours
                    assignment(1, 0).worker = workers[0] // 8 hours
                    assignment(1, 5).worker = workers[0] // 8 hours
                    assignment(1, 11).worker = workers[0] // 8 hours
                    assignment(2, 0).worker = workers[0] // 8 hours
                    assignment(2, 5).worker = workers[0] // 8 hours
                    assignment(2, 11).worker = workers[0] // 8 hours
                    assignment(3, 0).worker = workers[0] // 8 hours
                    assignment(3, 5).worker = workers[0] // 8 hours

                    // total of 88 hours,
                    // worker target is 32 hours * 4 weeks -> 128 hours
                    // penalty is (88 - 128) ^ 2
                }).penalizesBy(1600)
        }
    }


    @Test
    fun `Hours per week on target is not penalized`() {
        fixture.run {
            verifier.verifyThat(RosterConstraintProvider::deviationFromTotalHoursTargetPenalty)
                .givenSolution(roster.also {
                    assignment(0, 0).worker = workers[0] // 8 hours
                    assignment(0, 5).worker = workers[0] // 8 hours
                    assignment(0, 11).worker = workers[0] // 8 hours
                    assignment(0, 16).worker = workers[0] // 8 hours

                    assignment(1, 0).worker = workers[0] // 8 hours
                    assignment(1, 5).worker = workers[0] // 8 hours
                    assignment(1, 11).worker = workers[0] // 8 hours
                    assignment(1, 16).worker = workers[0] // 8 hours


                    assignment(2, 0).worker = workers[0] // 8 hours
                    assignment(2, 5).worker = workers[0] // 8 hours
                    assignment(2, 11).worker = workers[0] // 8 hours
                    assignment(2, 16).worker = workers[0] // 8 hours

                    assignment(3, 0).worker = workers[0] // 8 hours
                    assignment(3, 5).worker = workers[0] // 8 hours
                    assignment(3, 11).worker = workers[0] // 8 hours
                    assignment(3, 16).worker = workers[0] // 8 hours

                    // total of 128 hours,
                    // worker target is 32 hours * 4 weeks -> 128 hours
                }).penalizesBy(0)
        }
    }

    @Test
    fun `Hours per week above target is penalized`() {
        fixture.run {
            verifier.verifyThat(RosterConstraintProvider::deviationFromTotalHoursTargetPenalty)
                .givenSolution(roster.also {

                    assignment(0, 0).worker = workers[0] // 8 hours
                    assignment(0, 5).worker = workers[0] // 8 hours
                    assignment(0, 11).worker = workers[0] // 8 hours
                    assignment(0, 16).worker = workers[0] // 8 hours

                    assignment(1, 0).worker = workers[0] // 8 hours
                    assignment(1, 5).worker = workers[0] // 8 hours
                    assignment(1, 11).worker = workers[0] // 8 hours
                    assignment(1, 16).worker = workers[0] // 8 hours


                    assignment(2, 0).worker = workers[0] // 8 hours
                    assignment(2, 5).worker = workers[0] // 8 hours
                    assignment(2, 11).worker = workers[0] // 8 hours
                    assignment(2, 16).worker = workers[0] // 8 hours

                    assignment(3, 0).worker = workers[0] // 8 hours
                    assignment(3, 5).worker = workers[0] // 8 hours
                    assignment(3, 11).worker = workers[0] // 8 hours
                    assignment(3, 16).worker = workers[0] // 8 hours
                    assignment(3, 21).worker = workers[0] // 8 hours

                    // total of 136 hours,
                    // worker target is 32 hours * 4 weeks -> 128 hours

                }).penalizesBy(64)
        }
    }


    @Test
    fun `Deviation from target is considered separately for each worker`() {
        fixture.run {
            verifier.verifyThat(RosterConstraintProvider::deviationFromTotalHoursTargetPenalty)
                .givenSolution(roster.also {
                    assignment(0, 0).worker = workers[0] // 8 hours
                    assignment(0, 5).worker = workers[0] // 8 hours
                    assignment(0, 11).worker = workers[0] // 8 hours
                    assignment(1, 0).worker = workers[0] // 8 hours
                    assignment(1, 5).worker = workers[0] // 8 hours
                    assignment(1, 11).worker = workers[0] // 8 hours
                    assignment(2, 0).worker = workers[0] // 8 hours
                    assignment(2, 5).worker = workers[0] // 8 hours
                    assignment(2, 11).worker = workers[0] // 8 hours
                    assignment(3, 0).worker = workers[0] // 8 hours
                    assignment(3, 5).worker = workers[0] // 8 hours
                    // total of 88 hours,
                    // worker target is 32 hours per week * 4 weeks -> 128 hours
                    // penalty is (88 - 128) ^ 2 = 1600

                    assignment(0, 3).worker = workers[8] // 8 hours
                    assignment(1, 3).worker = workers[8] // 8 hours
                    assignment(2, 3).worker = workers[8] // 8 hours
                    assignment(3, 3).worker = workers[8] // 8 hours

                    // total of 32 hours
                    // worker target is 24 hours per week * 4 weeks -> 96 hours
                    // penalty is (32 - 96) ^ 2 = 4096

                }).penalizesBy(5696)
        }
    }
}
