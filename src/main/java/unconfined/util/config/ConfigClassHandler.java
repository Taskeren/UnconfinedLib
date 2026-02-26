package unconfined.util.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minecraftforge.common.config.Configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import static unconfined.util.Utils.runIfFalse;

@RequiredArgsConstructor
@Log4j2
public class ConfigClassHandler {
    private final ConfigBearer configBearer;

    /// The configuration class that contains both [Configuration] instance and the config value getting methods.
    public interface ConfigBearer {
        Configuration getConfig();
    }

    /// Methods in [ConfigBearer] annotated with [ConfigValue] will be treated as a config value getter.
    /// It should be 0-argument, non-static methods.
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ConfigValue {
    }

    public void loadAll() {
        Class<?> clazz = configBearer.getClass();
        Arrays.stream(clazz.getDeclaredMethods())
            .filter(it -> it.isAnnotationPresent(ConfigValue.class))
            .filter(it -> runIfFalse(
                !Modifier.isStatic(it.getModifiers()),
                () -> log.warn("Invalid config value getter method {}: non-static required.", it.getName())
            ))
            .filter(it -> runIfFalse(
                it.getParameterCount() == 0,
                () -> log.warn(
                    "Invalid config value getter method {}: 0 arguments required ({}).",
                    it.getName(),
                    it.getParameterCount()
                )
            ))
            .forEach(it -> {
                try {
                    it.invoke(configBearer);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    log.warn("Failed to invoke config value getter method {}", it.getName(), e);
                }
            });
        configBearer.getConfig().save();
    }
}
