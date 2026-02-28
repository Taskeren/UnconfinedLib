package unconfined.mod;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import unconfined.Unconfined;

import static unconfined.mod.UnconfinedLibMod.DEPENDENCIES;
import static unconfined.mod.UnconfinedLibMod.MODID;

@Mod(modid = MODID, version = Tags.VERSION, name = "UnconfinedLib", acceptedMinecraftVersions = "[1.7.10]", dependencies = DEPENDENCIES)
public class UnconfinedLibMod {

    public static final String MODID = "unconfinedlib";
    public static final String DEPENDENCIES = "required-after:gregtech;";

    public static final Logger LOG = LogManager.getLogger(MODID);

    @SidedProxy(clientSide = "unconfined.mod.ClientProxy", serverSide = "unconfined.mod.CommonProxy")
    public static CommonProxy proxy;

    public UnconfinedLibMod() {
        boolean standalone = Unconfined.isStandalone();
        LOG.info("Unconfined Standalone: {}", standalone);
        if (standalone) {
            ModContainer modContainer = Loader.instance().getIndexedModList().get(MODID);
            modContainer.getMetadata().name += " (Standalone)";
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }
}
