package unconfined.core;

import lombok.Getter;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.config.Configuration;
import org.jetbrains.annotations.ApiStatus;
import unconfined.util.config.ConfigClassHandler;

import java.io.File;

public final class UnconfinedCoreConfig implements ConfigClassHandler.ConfigBearer {

    private static final String UNCONFINED_CORE_CONFIG_PATH = "config/unconfined.core.cfg";
    public static final UnconfinedCoreConfig INSTANCE = new UnconfinedCoreConfig();

    @Getter(onMethod_ = @ApiStatus.Internal)
    private final Configuration config;
    @Getter(onMethod_ = @ApiStatus.Internal)
    private final ConfigClassHandler handler;

    private UnconfinedCoreConfig() {
        this.config = new Configuration(new File(UNCONFINED_CORE_CONFIG_PATH));
        this.handler = new ConfigClassHandler(this);
    }

    private static boolean isDevelopment() {
        // not use the one in the API to prevent unintentional class loading
        return (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
    }

    private static final String CAT_MULTI_FLUID_BASIC = "multi-fluid-basic";

    @ConfigClassHandler.ConfigValue
    public boolean isMultiFluidEnabled() {
        return config.getBoolean(
            "enable",
            CAT_MULTI_FLUID_BASIC,
            true,
            "true to enable multi-fluid basic machine basic injection"
        );
    }

    @ConfigClassHandler.ConfigValue
    public boolean isMultiFluidBasicImplementedByDefault() {
        return config.getBoolean(
            "implemented-by-default",
            CAT_MULTI_FLUID_BASIC,
            isDevelopment(),
            "true to enable multi-fluid basic machine implementation by default"
        );
    }

}
