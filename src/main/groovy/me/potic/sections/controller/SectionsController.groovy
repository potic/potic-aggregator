package me.potic.sections.controller

import com.codahale.metrics.annotation.Timed
import groovy.util.logging.Slf4j
import me.potic.sections.SectionFetcher
import me.potic.sections.domain.Section
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

import java.security.Principal

import static me.potic.sections.util.Utils.maskForLog

@RestController
@Slf4j
class SectionsController {

    @Autowired
    Collection<SectionFetcher> sectionFetchers

    @Timed(name = 'api.section')
    @CrossOrigin
    @GetMapping(path = '/section')
    @ResponseBody ResponseEntity<List<Section>> sectionsList(final Principal principal) {
        log.info "receive GET request for /section with token=${maskForLog(principal.token)}"

        try {
            List<Section> sections = sectionFetchers*.section().sort({ sectionFetcher -> -sectionFetcher.priority })
            return new ResponseEntity<List<Section>>(sections, HttpStatus.OK)
        } catch (e) {
            log.error "GET request for /section with token=${maskForLog(principal.token)} failed: $e.message", e
            return new ResponseEntity<List<Section>>(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}
