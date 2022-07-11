package nl.pompkracht.rosteringengine.reproducer

import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore.*
import org.optaplanner.core.api.score.stream.*
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream
import java.time.Duration
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.pow
import kotlin.time.DurationUnit
import kotlin.time.toKotlinDuration

class RosterConstraintProvider : ConstraintProvider {

    override fun defineConstraints(constraintFactory: ConstraintFactory): Array<Constraint> {
        return arrayOf(
            serviceTypeConflict(constraintFactory),
            simultaneousAssigmentConflict(constraintFactory),
            hoursPerWeekExceededConflict(constraintFactory),
            workerAvailabilityConflict(constraintFactory),
            workerUnavailabilityConflict(constraintFactory),
            unassignedShiftPenalty(constraintFactory),
            noShiftsForWorkerPenalty(constraintFactory),
            deviationFromTotalHoursTargetPenalty(constraintFactory),
            shiftsOnSameWorkdayPenalty(constraintFactory),
            shiftsInSameDayPartPenalty(constraintFactory)
        )
    }

    fun serviceTypeConflict(constraintFactory: ConstraintFactory): Constraint =
        constraintFactory.forEach(ShiftAssignment::class.java).filter {
            val shiftServiceType = it.shift.serviceType ?: return@filter false
            val workerServiceTypes = it.worker?.serviceTypes ?: return@filter false
            !workerServiceTypes.contains(shiftServiceType)
        }.penalize("Service type conflict", ONE_HARD)

    fun simultaneousAssigmentConflict(constraintFactory: ConstraintFactory): Constraint =
        constraintFactory.forEach(ShiftConflict::class.java)
            .join(ShiftAssignment::class.java, Joiners.equal({ it -> it.shiftOne }, { it.shift.shiftId })).ifExists(
                ShiftAssignment::class.java,
                Joiners.equal({ conflict, _ -> conflict.shiftTwo }, { it.shift.shiftId }),
                Joiners.filtering { _, assignmentOne, assignmentTwo ->
                    assignmentOne.worker?.workerId?.let { it == assignmentTwo.worker?.workerId } ?: false
                }).penalize("Assignment of other shift during this shift or during resting period", ONE_HARD)

    fun hoursPerWeekExceededConflict(constraintFactory: ConstraintFactory): Constraint =
        hoursPerWeekDeviations(constraintFactory).penalize(
            "Hours per week exceeded by more than $HOURS_PER_WEEK_EXCEEDED_LIMIT hours", ONE_HARD
        ) {
            max(0, ceil(it.toKotlinDuration().div(HOURS_PER_WEEK_EXCEEDED_LIMIT_DURATION)).toInt() - 1)
        }

    private fun hoursPerWeekDeviations(constraintFactory: ConstraintFactory): UniConstraintStream<Duration> =
        constraintFactory.forEach(ShiftAssignment::class.java)
            .join(ShiftWorkWeek::class.java, Joiners.equal({ it -> it.shift.shiftId }, { it.shiftId })).join(
                ShiftDuration::class.java, Joiners.equal({ assignment, _ -> assignment.shift.shiftId }, { it.shiftId })
            ).groupBy({ assignment, shiftWorkWeek, _ ->
                WorkerWorkWeek(
                    requireNotNull(assignment.worker) { "Assignment not initialized" }.workerId, shiftWorkWeek.workWeek
                )
            }, ConstraintCollectors.sumDuration { _, _, shiftDuration -> shiftDuration.duration })
            .join(Worker::class.java, Joiners.equal({ workerWorkWeek, _ -> workerWorkWeek.workerId }, { it.workerId }))
            .map { _, duration, worker -> duration.minus(Duration.ofHours(worker.hoursPerWeek.toLong())) }


    fun workerAvailabilityConflict(constraintFactory: ConstraintFactory): Constraint =
        constraintFactory.forEach(ShiftAssignment::class.java).ifExists(
            WorkerAvailabilityConflict::class.java,
            Joiners.equal({ it -> it.shift.shiftId }, { it.shiftId }),
            Joiners.equal({ it -> it.worker?.workerId }, { it.workerId })
        ).penalize("Missing worker availability", ONE_HARD)


    fun workerUnavailabilityConflict(constraintFactory: ConstraintFactory): Constraint =
        constraintFactory.forEach(ShiftAssignment::class.java).ifExists(
            WorkerUnavailabilityConflict::class.java,
            Joiners.equal({ it -> it.shift.shiftId }, { it.shiftId }),
            Joiners.equal({ it -> it.worker?.workerId }, { it.workerId })
        ).penalize("Conflicting unavailability for assigned worker", ONE_HARD)

    fun unassignedShiftPenalty(constraintFactory: ConstraintFactory): Constraint =
        constraintFactory.forEachIncludingNullVars(ShiftAssignment::class.java).filter { it.worker == null }
            .penalize("Shift not assigned", ONE_MEDIUM)


    fun noShiftsForWorkerPenalty(constraintFactory: ConstraintFactory): Constraint =
        constraintFactory.forEach(WorkerTotalHoursTarget::class.java)
            .ifNotExists(ShiftAssignment::class.java, Joiners.equal({ it -> it.workerId }, { it.worker?.workerId }))
            .penalize("Worker not assigned any shifts", ONE_SOFT) {
                it.totalHoursTarget.toDouble().pow(2).toInt()
            }

    fun deviationFromTotalHoursTargetPenalty(constraintFactory: ConstraintFactory): Constraint =
        constraintFactory.forEach(WorkerTotalHoursTarget::class.java)
            .join(ShiftAssignment::class.java, Joiners.equal({ it -> it.workerId }, { it.worker?.workerId })).join(
                ShiftDuration::class.java, Joiners.equal({ _, assignment -> assignment.shift.shiftId }, { it.shiftId })
            ).groupBy({ workerTotalHoursTarget, _, _ -> workerTotalHoursTarget },
                ConstraintCollectors.sumDuration { _, _, shiftDuration -> shiftDuration.duration })
            .penalize("Deviation from total hours target", ONE_SOFT) { target, totalDuration ->
                totalDuration.minus(Duration.ofHours(target.totalHoursTarget.toLong())).toKotlinDuration()
                    .toDouble(DurationUnit.HOURS).pow(2).toInt()
            }

    fun shiftsOnSameWorkdayPenalty(constraintFactory: ConstraintFactory): Constraint =
        constraintFactory.forEach(ShiftAssignment::class.java)
            .join(ShiftDayOfWeek::class.java, Joiners.equal({ it -> it.shift.shiftId }, { it.shiftId }))
            .groupBy({ assignment, shiftDayOfWeek ->
                WorkerDayOfWeek(
                    requireNotNull(assignment.worker) { "Assignment not initialized" }.workerId,
                    shiftDayOfWeek.dayOfWeek
                )
            }, ConstraintCollectors.countBi())
            .penalize("Shifts on the same week day assigned to the same worker", ONE_SOFT) { _, count ->
                count.times(8).toDouble().pow(2).toInt()
            }

    fun shiftsInSameDayPartPenalty(constraintFactory: ConstraintFactory): Constraint =
        constraintFactory.forEach(ShiftAssignment::class.java)
            .join(ShiftDayPart::class.java, Joiners.equal({ it -> it.shift.shiftId }, { it.shiftId }))
            .groupBy({ assignment, shiftDayPart ->
                WorkerDayPart(
                    requireNotNull(assignment.worker) { "Assignment not initialized" }.workerId, shiftDayPart.dayPart
                )
            }, ConstraintCollectors.countBi())
            .penalize("Shifts in the same day part assigned to the same worker", ONE_SOFT) { _, count ->
                count.times(8).toDouble().pow(2).toInt()
            }


    companion object {
        const val HOURS_PER_WEEK_EXCEEDED_LIMIT = 8
        val HOURS_PER_WEEK_EXCEEDED_LIMIT_DURATION =
            Duration.ofHours(HOURS_PER_WEEK_EXCEEDED_LIMIT.toLong()).toKotlinDuration()

    }

}
