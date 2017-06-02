import ch.qos.logback.classic.Level
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy

appender('STDOUT', ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = '%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n'
    }
}

appender('FILE', RollingFileAppender) {
    file = '/logs/potic-aggregator.log'

    encoder(PatternLayoutEncoder) {
        pattern = '%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n'
    }

    rollingPolicy(TimeBasedRollingPolicy) {
        FileNamePattern = "/logs/potic-aggregator.%d{yyyy-MM-dd}.log"
    }
}

String SERVICE_LOG_LEVEL = System.getenv('SERVICE_LOG_LEVEL') ?: 'INFO'
String ROOT_LOG_LEVEL = System.getenv('ROOT_LOG_LEVEL') ?: 'INFO'

root(Level.toLevel(ROOT_LOG_LEVEL), ['STDOUT', 'FILE' ])
logger('me.potic.aggregator', Level.toLevel(SERVICE_LOG_LEVEL), [ 'STDOUT', 'FILE' ], false)
