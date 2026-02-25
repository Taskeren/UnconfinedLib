package unconfined.api;

import net.minecraft.launchwrapper.Launch;

public final class UnconfinedAPI {

    public static boolean isDevelopment() {
        return (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
    }

}
