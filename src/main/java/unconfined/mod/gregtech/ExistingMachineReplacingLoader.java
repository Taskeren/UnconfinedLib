package unconfined.mod.gregtech;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.MachineType;
import gregtech.api.enums.MetaTileEntityIDs;
import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicMachineWithRecipe;
import gregtech.api.recipe.RecipeMaps;
import gregtech.loaders.preload.LoaderMetaTileEntities;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import lombok.extern.log4j.Log4j2;
import unconfined.util.UnconfinedUtils;
import unconfined.util.tier.Tier;

import java.util.EnumMap;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;

import static gregtech.api.enums.MetaTileEntityIDs.CHEMICAL_REACTOR_IV;
import static gregtech.api.enums.MetaTileEntityIDs.CHEMICAL_REACTOR_LV;
import static gregtech.api.enums.MetaTileEntityIDs.CHEMICAL_REACTOR_LuV;
import static gregtech.api.enums.MetaTileEntityIDs.CHEMICAL_REACTOR_UMV;
import static gregtech.api.enums.MetaTileEntityIDs.ELECTROLYSER_IV;
import static gregtech.api.enums.MetaTileEntityIDs.ELECTROLYSER_LV;
import static gregtech.api.enums.MetaTileEntityIDs.ELECTROLYZER_LuV;
import static gregtech.api.enums.MetaTileEntityIDs.ELECTROLYZER_UMV;

@SuppressWarnings("JavadocReference")
@Log4j2
public class ExistingMachineReplacingLoader {

    public static final EnumMap<Tier, IMetaTileEntity> CHEMICAL_REACTOR = UnconfinedUtils.ofTieredMap();
    public static final EnumMap<Tier, IMetaTileEntity> ELECTROLYZER = UnconfinedUtils.ofTieredMap();

    public static void init() {
        cr();
        el();
    }

    private interface MachineGenerator {
        IMetaTileEntity generate(int id, Tier tier);
    }

    private static void unset(int i) {
        IMetaTileEntity existing = GregTechAPI.METATILEENTITIES[i];
        if (existing != null) {
            log.info("Unsetting GregTechAPI.METATILEENTITIES[{}] = {} ({})", i, existing, existing.getClass());
        }
        GregTechAPI.METATILEENTITIES[i] = null;
    }

    private static void replace(EnumMap<Tier, IMetaTileEntity> map, int fromId, int toId, Tier fromTier, MachineGenerator generator) {
        int length = toId - fromId + 1;
        for (int i = 0; i < length; i++) {
            int currId = fromId + i;
            Tier currTier = fromTier.offset(i);
            unset(currId);
            IMetaTileEntity newMTE = generator.generate(currId, currTier);
            map.put(currTier, newMTE);
        }
    }

    private static void replace(EnumMap<Tier, IMetaTileEntity> map, MetaTileEntityIDs fromId, MetaTileEntityIDs toId, Tier fromTier, MachineGenerator generator) {
        replace(map, fromId.ID, toId.ID, fromTier, generator);
    }

    private static String pad(Tier tier) {
        String s = String.valueOf(tier.getValue());
        if (s.length() < 2) {
            s = "0" + s;
        }
        return s;
    }

    /// @see gregtech.api.enums.MetaTileEntityIDs#CHEMICAL_REACTOR_LV
    /// @see LoaderMetaTileEntities#registerChemicalReactor()
    private static void cr() {
        ToIntFunction<Tier> slot = (tier) -> {
            if (tier.compareTo(Tier.HV) < 0) return 2;
            return 3;
        };
        MachineGenerator cr = (id, tier) -> new MultiFluidBasicMachineWithRecipe(
            id,
            "basicmachine.chemicalreactor.tier." + pad(tier),
            tier.getName() + " Chemical Reactor",
            tier.getValue(),
            MachineType.CHEMICAL_REACTOR.tooltipDescription(),
            RecipeMaps.multiblockChemicalReactorRecipes,
            2,
            2,
            slot.applyAsInt(tier),
            slot.applyAsInt(tier),
            true,
            SoundResource.GTCEU_LOOP_CHEMICAL,
            MTEBasicMachineWithRecipe.SpecialEffects.NONE,
            "CHEMICAL_REACTOR"
        );
        // LV-IV
        replace(
            CHEMICAL_REACTOR,
            CHEMICAL_REACTOR_LV,
            CHEMICAL_REACTOR_IV,
            Tier.LV,
            cr
        );
        // LuV-UMV
        replace(
            CHEMICAL_REACTOR,
            CHEMICAL_REACTOR_LuV,
            CHEMICAL_REACTOR_UMV,
            Tier.LuV,
            cr
        );
    }

    /// @see MetaTileEntityIDs#ELECTROLYSER_LV
    /// @see MetaTileEntityIDs#ELECTROLYZER_LuV
    /// @see LoaderMetaTileEntities#registerElectrolyzer()
    private static void el() {
        ToIntBiFunction<Tier, Boolean> slot = (tier, isOutput) -> {
            // LV-MV: 1 input 2 output
            if (tier.compareTo(Tier.HV) < 0) return isOutput ? 2 : 1;
            // HV-IV: 2 input 3 output
            if (tier.compareTo(Tier.LuV) < 0) return isOutput ? 3 : 2;
            // Luv+: 3 input 3 output
            return 3;
        };
        MachineGenerator el = (id, tier) -> new MultiFluidBasicMachineWithRecipe(
            id,
            "basicmachine.electrolyzer.tier." + pad(tier),
            tier.getName() + " Electrolyzer",
            tier.getValue(),
            MachineType.ELECTROLYZER.tooltipDescription(),
            GTPPRecipeMaps.electrolyzerNonCellRecipes,
            2,
            6,
            slot.applyAsInt(tier, false),
            slot.applyAsInt(tier, true),
            true,
            SoundResource.GTCEU_LOOP_ELECTROLYZER,
            MTEBasicMachineWithRecipe.SpecialEffects.NONE,
            "ELECTROLYZER"
        );
        // LV-IV
        replace(
            ELECTROLYZER,
            ELECTROLYSER_LV,
            ELECTROLYSER_IV,
            Tier.LV,
            el
        );
        // Luv-UMV
        replace(
            ELECTROLYZER,
            ELECTROLYZER_LuV,
            ELECTROLYZER_UMV,
            Tier.LuV,
            el
        );
    }

}
