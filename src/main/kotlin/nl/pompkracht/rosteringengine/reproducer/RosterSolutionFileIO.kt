package nl.pompkracht.rosteringengine.reproducer

import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
import org.optaplanner.persistence.jackson.impl.domain.solution.JacksonSolutionFileIO
import org.springframework.stereotype.Component

@Component
class RosterSolutionFileIO : JacksonSolutionFileIO<Roster>(
    Roster::class.java,
    ObjectMapper().findAndRegisterModules().configure(WRITE_DATES_AS_TIMESTAMPS, false)
        .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
)
