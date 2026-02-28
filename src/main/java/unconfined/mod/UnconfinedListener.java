package unconfined.mod;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.gtnewhorizon.gtnhlib.eventbus.Phase;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import gregtech.api.gui.modularui.GTUITextures;
import lombok.extern.log4j.Log4j2;
import unconfined.api.event.RecipeMapBuildingEvent;

@Log4j2
@EventBusSubscriber(phase = Phase.CONSTRUCT) // register early enough to receive events from GT.
public class UnconfinedListener {

    @SubscribeEvent
    public static void onRecipeMapBuilding(RecipeMapBuildingEvent event) {
        if (!UnconfinedConfig.INSTANCE.isReplacingExistingMachines()) return;

        if (event.getUnlocalizedName().equals("gt.recipe.largechemicalreactor")) {
            log.info("Adding slot overlays to gt.recipe.largechemicalreactor");
            event.getBuilder().slotOverlays((index, isFluid, isOutput, isSpecial) -> {
                if (isFluid) {
                    return isOutput ? GTUITextures.OVERLAY_SLOT_VIAL_2 : GTUITextures.OVERLAY_SLOT_MOLECULAR_3;
                }
                if (isOutput) {
                    return GTUITextures.OVERLAY_SLOT_VIAL_1;
                }
                if (index == 0) {
                    return GTUITextures.OVERLAY_SLOT_MOLECULAR_1;
                }
                return GTUITextures.OVERLAY_SLOT_MOLECULAR_2;
            });
        } else if (event.getUnlocalizedName().equals("gtpp.recipe.multielectro")) {
            log.info("Adding slot overlays to gtpp.recipe.multielectro");
            event.getBuilder().slotOverlays((index, isFluid, isOutput, isSpecial) -> {
                if (isOutput) {
                    return null;
                }
                if (isFluid) {
                    return GTUITextures.OVERLAY_SLOT_CHARGER_FLUID;
                }
                if (index == 0) {
                    return GTUITextures.OVERLAY_SLOT_CHARGER;
                }
                return GTUITextures.OVERLAY_SLOT_CANISTER;
            });
        }
    }

}
