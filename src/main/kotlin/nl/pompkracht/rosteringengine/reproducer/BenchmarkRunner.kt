package nl.pompkracht.rosteringengine.reproducer

import org.optaplanner.benchmark.api.PlannerBenchmarkFactory
import org.springframework.stereotype.Component

@Component
class BenchmarkRunner {

    private val benchmarkFactory = PlannerBenchmarkFactory.createFromXmlResource(
        "nl/pompkracht/rosteringengine/reproducer/config/rosterBenchmarkConfig.xml"
    )

    fun runBenchmarks() {
        benchmarkFactory.buildPlannerBenchmark().benchmarkAndShowReportInBrowser()
    }


}
