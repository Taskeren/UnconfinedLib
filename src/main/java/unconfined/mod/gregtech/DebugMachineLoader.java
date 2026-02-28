package unconfined.mod.gregtech;

import gregtech.api.enums.SoundResource;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicMachineWithRecipe;
import gregtech.api.recipe.RecipeMaps;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import unconfined.core.UnconfinedMixinPlugin;

@Log4j2
public class DebugMachineLoader {

    @Getter
    private static IMetaTileEntity chemicalReactor, electrolyzer;

    public static void init() {
        if (UnconfinedMixinPlugin.isMultiFluidBasicImplementedByDefault()) {
            log.info("Registering MultiFluid Chemical Reactor via MTEBasicMachineWithRecipe (from GregTech)");
            chemicalReactor = new MTEBasicMachineWithRecipe(
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
            electrolyzer = new MTEBasicMachineWithRecipe(
                12031,
                "unconfined.machine.electrolyzer",
                "Electrolyzer (Demo)",
                VoltageIndex.UHV,
                "Demo for multi-fluid basic machines.",
                GTPPRecipeMaps.electrolyzerNonCellRecipes,
                3,
                3,
                128 * 1000,
                SoundResource.NONE,
                MTEBasicMachineWithRecipe.SpecialEffects.TOP_SMOKE,
                "ELECTROLYZER"
            );
        } else {
            log.info("Registering MultiFluid Chemical Reactor via MultiFluidBasicMachineWithRecipe (from UnconfinedLib)");
            chemicalReactor = new MultiFluidBasicMachineWithRecipe(
                12030,
                "unconfined.machine.chemical_reactor",
                "Chemical Reactor (Demo)",
                VoltageIndex.UHV,
                "Demo for multi-fluid basic machines.",
                RecipeMaps.multiblockChemicalReactorRecipes,
                3,
                3,
                3,
                3,
                128 * 1000,
                SoundResource.NONE,
                MTEBasicMachineWithRecipe.SpecialEffects.TOP_SMOKE,
                "CHEMICAL_REACTOR"
            );
            electrolyzer = new MultiFluidBasicMachineWithRecipe(
                12031,
                "unconfined.machine.electrolyzer",
                "Electrolyzer (Demo)",
                VoltageIndex.UHV,
                "Demo for multi-fluid basic machines.",
                GTPPRecipeMaps.electrolyzerNonCellRecipes,
                3,
                3,
                3,
                3,
                128 * 1000,
                SoundResource.NONE,
                MTEBasicMachineWithRecipe.SpecialEffects.TOP_SMOKE,
                "ELECTROLYZER"
            );
        }
    }

}
