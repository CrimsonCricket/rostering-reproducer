package nl.pompkracht.rosteringengine.reproducer

import java.time.DayOfWeek
import java.time.Duration

data class ShiftConflict(val shiftOne: ShiftId, val shiftTwo: ShiftId)
data class WorkerAvailabilityConflict(val shiftId: ShiftId, val workerId: WorkerId)
data class WorkerDayOfWeek(val workerId: WorkerId, val dayOfWeek: DayOfWeek)
data class WorkerDayPart(val workerId: WorkerId, val dayPart: DayPart)
data class WorkerTotalHoursTarget(val workerId: WorkerId, val totalHoursTarget: Int)
data class WorkerUnavailabilityConflict(val shiftId: ShiftId, val workerId: WorkerId)
data class WorkerWorkWeek(val workerId: WorkerId, val workWeek: WorkWeek)
data class ShiftWorkWeek(val shiftId: ShiftId, val workWeek: WorkWeek)
data class ShiftDuration(val shiftId: ShiftId, val duration: Duration)
data class ShiftDayOfWeek(val shiftId: ShiftId, val dayOfWeek: DayOfWeek)
data class ShiftDayPart(val shiftId: ShiftId, val dayPart: DayPart)
