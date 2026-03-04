package unconfined.mod.client;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.gtnewhorizon.gtnhlib.eventbus.Phase;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiOpenEvent;
import unconfined.mod.client.screen.ChatScreen;

@EventBusSubscriber(phase = Phase.CONSTRUCT)
public class UnconfinedListenerClient {

    @SubscribeEvent
    public static void onGui(GuiOpenEvent event) {
        GuiScreen gui = event.gui;
        if (gui instanceof GuiChat guiChat && !(gui instanceof ChatScreen)) {
            event.setCanceled(true);
            Minecraft.getMinecraft().displayGuiScreen(new ChatScreen(guiChat));
        }
    }

}
