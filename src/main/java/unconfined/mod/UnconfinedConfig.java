package unconfined.mod;

import lombok.Getter;
import net.minecraftforge.common.config.Configuration;
import org.jetbrains.annotations.ApiStatus;
import unconfined.api.UnconfinedAPI;
import unconfined.util.config.ConfigClassHandler;

import java.io.File;

public final class UnconfinedConfig implements ConfigClassHandler.ConfigBearer {

    public static final UnconfinedConfig INSTANCE = new UnconfinedConfig();

    @Getter(onMethod_ = @ApiStatus.Internal)
    private final Configuration config;
    @Getter(onMethod_ = @ApiStatus.Internal)
    private final ConfigClassHandler handler;

    public UnconfinedConfig() {
        this.config = new Configuration(new File("config/unconfined.cfg"));
        this.handler = new ConfigClassHandler(this);
    }

    private static final String CAT_MULTI_FLUID_BASIC = "multi-fluid-basic";

    @ConfigClassHandler.ConfigValue
    public boolean isReplacingExistingMachines() {
        return config.getBoolean(
            "replace",
            CAT_MULTI_FLUID_BASIC,
            UnconfinedAPI.isStandaloneMode(),
            "true to enable multi-fluid support to existing machines (Chemical Reactors and Electrolyzers)"
        );
    }

}
