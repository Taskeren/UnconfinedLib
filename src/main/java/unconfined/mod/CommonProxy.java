package unconfined.mod;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import unconfined.api.UnconfinedAPI;
import unconfined.mod.gregtech.MultiFluidChemicalReactorLoader;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());

        UnconfinedLibMod.LOG.info(Config.greeting);
        UnconfinedLibMod.LOG.info("I am MyMod at version " + Tags.VERSION);
    }

    public void init(FMLInitializationEvent event) {
        if (UnconfinedAPI.isDevelopment()) {
            MultiFluidChemicalReactorLoader.init();
        }
    }

    public void postInit(FMLPostInitializationEvent event) {
    }

    public void serverStarting(FMLServerStartingEvent event) {
    }
}
