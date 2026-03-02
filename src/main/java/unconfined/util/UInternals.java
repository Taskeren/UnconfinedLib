package unconfined.util;

import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.ApiStatus;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Consumer;

@ApiStatus.Internal
@Log4j2
public class UInternals {

    public static final boolean IS_RUNNING_WITH_JDWP = java.lang.management.ManagementFactory.getRuntimeMXBean()
        .getInputArguments()
        .stream()
        .anyMatch(str -> str.startsWith("-agentlib:jdwp"));
    @SuppressWarnings("FieldMayBeFinal")
    private static Consumer<String> thePauser = _ -> {
    };

    public static void logAndPauseIfInIde(String error) {
        log.error(error);
        if (IS_RUNNING_WITH_JDWP) {
            doPause(error);
        }
    }

    public static void logAndPauseIfInIde(String error, Throwable throwable) {
        log.error(error, throwable);
        if (IS_RUNNING_WITH_JDWP) {
            doPause(error);
        }
    }

    private static void doPause(String message) {
        Instant instant = Instant.now();
        log.warn("Did you remember to set a breakpoint here?");
        if (!(Duration.between(instant, Instant.now()).toMillis() > 500L)) {
            thePauser.accept(message);
        }
    }
}
