package unconfined.mod;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import unconfined.api.UnconfinedAPI;
import unconfined.mod.command.UnconfinedCommand;
import unconfined.mod.gregtech.DebugMachineLoader;
import unconfined.mod.gregtech.ExistingMachineReplacingLoader;

import java.util.function.Supplier;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        UnconfinedConfig.INSTANCE.getHandler().loadAll();
    }

    public void init(FMLInitializationEvent event) {
        if (UnconfinedAPI.isDebugMode()) {
            DebugMachineLoader.init();
        }
        if (UnconfinedConfig.INSTANCE.isReplacingExistingMachines()) {
            ExistingMachineReplacingLoader.init();
        }
    }

    public void postInit(FMLPostInitializationEvent event) {
    }

    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new UnconfinedCommand());
    }

    public <T> T runSided(Supplier<T> serverSide, Supplier<T> clientSide) {
        return serverSide.get();
    }
}
