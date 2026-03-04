package unconfined.mod.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.joml.Vector2i;
import org.jspecify.annotations.Nullable;
import org.lwjgl.input.Mouse;
import unconfined.util.Assertions;

public final class RenderUtils {

    private static @Nullable Minecraft minecraft;
    private static @Nullable ScaledResolution scaledResolution;

    public static Minecraft getMinecraft() {
        if (minecraft != null) return minecraft;
        else return minecraft = Assertions.checkNotNull(Minecraft.getMinecraft());
    }

    public static ScaledResolution getScaledResolution() {
        Minecraft minecraft = getMinecraft();
        if (scaledResolution == null
            || scaledResolution.getScaledWidth() != minecraft.displayWidth
            || scaledResolution.getScaledHeight() != minecraft.displayHeight) {
            return scaledResolution = new ScaledResolution(minecraft, minecraft.displayWidth, minecraft.displayHeight);
        }
        return scaledResolution;
    }

    public static Vector2i getMouseVecRaw() {
        int x = Mouse.getX();
        int y = Mouse.getY();
        return new Vector2i(x, y);
    }

    public static Vector2i getMouseVec() {
        Vector2i vec = getMouseVecRaw();
        Minecraft mc = getMinecraft();
        ScaledResolution sr = getScaledResolution();
        vec.x = vec.x * sr.getScaledWidth() / mc.displayWidth;
        vec.y = sr.getScaledHeight() - vec.y * sr.getScaledHeight() / mc.displayHeight - 1;
        return vec;
    }

}
