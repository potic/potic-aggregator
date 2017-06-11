package me.potic.aggregator.service

import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

@Service
@Slf4j
class TimedService {

    public <T> T timed(String name, Closure<T> action) {
        long startTime = System.currentTimeMillis()
        try {
            return action.call()
        } finally {
            log.info "$name finished in ${(System.currentTimeMillis() - startTime)/1000}s"
        }
    }
}
