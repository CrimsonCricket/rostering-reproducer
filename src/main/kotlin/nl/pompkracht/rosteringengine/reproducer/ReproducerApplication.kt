package nl.pompkracht.rosteringengine.reproducer

import mu.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger { }

@SpringBootApplication
class ReproducerApplication

fun main(args: Array<String>) {
    runApplication<ReproducerApplication>(*args)
}


@Component
class BenchmarkStarter(private val runner: BenchmarkRunner) : ApplicationListener<ApplicationReadyEvent> {

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        logger.info { "Starting benchmark runner" }
        runner.runBenchmarks()
    }
}
