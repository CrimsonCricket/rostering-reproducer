package nl.pompkracht.rosteringengine.reproducer

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ServiceTypeConflictTest {

    private lateinit var fixture: RosterConstraintProviderFixture

    @BeforeEach
    fun setUp() {
        fixture = RosterConstraintProviderFixture()
    }

    @Test
    fun `Shop shift assigned to worker with only bakery capability is penalized`() {
        fixture.run {
            verifier.verifyThat(RosterConstraintProvider::serviceTypeConflict).givenSolution(roster.also {
                it.shiftAssignments[0].worker = workers[9] // shift:shop, worker:bakery
            }).penalizesBy(1)
        }
    }

    @Test
    fun `Shop shift assigned to worker without any capability is penalized`() {
        fixture.run {
            verifier.verifyThat(RosterConstraintProvider::serviceTypeConflict).givenSolution(roster.also {
                it.shiftAssignments[0].worker = workers[11] // shift:shop, worker:none
            }).penalizesBy(1)
        }
    }

    @Test
    fun `Shop shift assigned to worker with only shop capability is not penalized`() {
        fixture.run {
            verifier.verifyThat(RosterConstraintProvider::serviceTypeConflict).givenSolution(roster.also {
                it.shiftAssignments[0].worker = workers[0] // shift:shop, worker:shop
            }).penalizesBy(0)
        }
    }

    @Test
    fun `Shop shift assigned to worker with both shop and bakery capabilities is not penalized`() {
        fixture.run {
            verifier.verifyThat(RosterConstraintProvider::serviceTypeConflict).givenSolution(roster.also {
                it.shiftAssignments[0].worker = workers[6] // shift:shop, worker:shop,bakery
            }).penalizesBy(0)
        }
    }

    @Test
    fun `Bakery shift assigned to worker with only shop capability is penalized`() {
        fixture.run {
            verifier.verifyThat(RosterConstraintProvider::serviceTypeConflict).givenSolution(roster.also {
                it.shiftAssignments[3].worker = workers[0]
            }).penalizesBy(1)
        }
    }
}

