package unconfined.mod.gregtech;

import gregtech.api.enums.SoundResource;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.metatileentity.implementations.MTEBasicMachineWithRecipe;
import gregtech.api.recipe.RecipeMaps;

public class MultiFluidChemicalReactorLoader {

    public static void init() {
        new MTEBasicMachineWithRecipe(
            12030,
            "unconfined.machine.chemical_reactor",
            "Chemical Reactor (Demo)",
            VoltageIndex.UHV,
            "Demo for multi-fluid basic machines.",
            RecipeMaps.multiblockChemicalReactorRecipes,
            3,
            3,
            128 * 1000,
            SoundResource.NONE,
            MTEBasicMachineWithRecipe.SpecialEffects.TOP_SMOKE,
            "CHEMICAL_REACTOR"
        );
    }

}
