package unconfined.util;

import unconfined.mod.UnconfinedLibMod;

import java.util.function.Supplier;

public final class McUtils {

    public static <R> R runDist(Supplier<R> serverSide, Supplier<R> clientSide) {
        return UnconfinedLibMod.proxy.runSided(serverSide, clientSide);
    }
}
