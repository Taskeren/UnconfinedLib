package unconfined.api;

import net.minecraft.launchwrapper.Launch;

import static unconfined.Unconfined.isStandalone;

public final class UnconfinedAPI {

    private static final boolean DEBUG_MODE = Boolean.getBoolean("unconfined.debug");

    public static boolean isDevelopment() {
        return (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
    }

    public static boolean isDebugMode() {
        return DEBUG_MODE || isDevelopment();
    }

    /// In STANDALONE mode, non-experimental features are enabled by default, and can be toggle off in the config.
    /// But once the configuration has been generated, switching this won't make any change.
    public static boolean isStandaloneMode() {
        return isStandalone() || isDebugMode();
    }

}
