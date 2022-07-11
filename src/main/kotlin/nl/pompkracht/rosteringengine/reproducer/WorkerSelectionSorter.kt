package nl.pompkracht.rosteringengine.reproducer

import org.optaplanner.core.api.score.director.ScoreDirector
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorter

class WorkerSelectionSorter : SelectionSorter<Roster, Worker?> {

    override fun sort(scoreDirector: ScoreDirector<Roster>, selectionList: MutableList<Worker?>) {
        selectionList.sortBy { it?.hoursPerWeek }
    }
}
