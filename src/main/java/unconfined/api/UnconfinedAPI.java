package unconfined.api;

import net.minecraft.launchwrapper.Launch;

public final class UnconfinedAPI {

    private static final boolean DEBUG_MODE = Boolean.getBoolean("unconfined.debug");

    public static boolean isDevelopment() {
        return (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
    }

    public static boolean isDebugMode() {
        return DEBUG_MODE || isDevelopment();
    }

}
