package unconfined.mod.gregtech;

import gregtech.api.enums.SoundResource;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.metatileentity.implementations.MTEBasicMachineWithRecipe;
import gregtech.api.recipe.RecipeMaps;
import lombok.extern.log4j.Log4j2;
import unconfined.core.UnconfinedMixinPlugin;

@Log4j2
public class MultiFluidChemicalReactorLoader {

    public static void init() {
        if (UnconfinedMixinPlugin.isMultiFluidBasicImplementedByDefault()) {
            log.info("Registering MultiFluid Chemical Reactor via MTEBasicMachineWithRecipe (from GregTech)");
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
        } else {
            log.info("Registering MultiFluid Chemical Reactor via MultiFluidBasicMachineWithRecipe (from UnconfinedLib)");
            new MultiFluidBasicMachineWithRecipe(
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

}
