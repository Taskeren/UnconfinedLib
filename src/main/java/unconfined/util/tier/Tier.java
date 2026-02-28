package unconfined.util.tier;

import gregtech.api.enums.VoltageIndex;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import lombok.Getter;
import net.minecraft.util.EnumChatFormatting;
import org.intellij.lang.annotations.MagicConstant;
import org.jspecify.annotations.Nullable;
import unconfined.util.Assertions;

import java.util.Objects;

public enum Tier implements ITier {
    STEAM(false),
    HIGH_PRESSURE_STEAM(true),
    ULV(Voltage.ULV),
    LV(Voltage.LV),
    MV(Voltage.MV),
    HV(Voltage.HV),
    EV(Voltage.EV),
    IV(Voltage.IV),
    LuV(Voltage.LuV),
    ZPM(Voltage.ZPM),
    UV(Voltage.UV),
    UHV(Voltage.UHV),
    UEV(Voltage.UEV),
    UIV(Voltage.UIV),
    UMV(Voltage.UMV),
    UXV(Voltage.UXV),
    MAX(Voltage.MAX),
    ;
    @Getter
    private boolean steam;
    @Getter
    private boolean highPressure;
    @Getter
    private @Nullable Voltage voltage;

    private static final Int2ObjectMap<Tier> VOLTAGE_TIERS;

    static {
        VOLTAGE_TIERS = new Int2ObjectArrayMap<>();
        for (Tier value : values()) {
            if (value.getVoltage() != null) {
                VOLTAGE_TIERS.put(value.getVoltage().ordinal(), value);
            }
        }
    }

    Tier(boolean highPressure) {
        this.steam = true;
        this.highPressure = highPressure;
    }

    Tier(Voltage voltage) {
        this.voltage = voltage;
    }

    public static Tier fromVoltage(Voltage voltage) {
        return VOLTAGE_TIERS.get(voltage.getValue());
    }

    public static Tier fromVoltageIndex(@MagicConstant(valuesFromClass = VoltageIndex.class) int voltageIndex) {
        return fromVoltage(Voltage.fromInt(voltageIndex));
    }

    public Voltage getVoltageOrThrow() {
        return Objects.requireNonNull(getVoltage(), "voltage");
    }


    @Override
    public int getValue() {
        if(isSteam()) {
            return Voltage.ULV.getValue();
        }
        return getVoltageOrThrow().getValue();
    }

    @Override
    public String getName() {
        if (isSteam()) {
            return isHighPressure() ? "HP Steam" : "Steam";
        }
        return getVoltageOrThrow().getName();
    }

    @Override
    public String getLongName() {
        if (isSteam()) {
            return isHighPressure() ? "High-Pressure Steam" : "HP";
        }
        return getVoltageOrThrow().getLongName();
    }

    @Override
    public String getTierColor() {
        if(isSteam()) {
            return EnumChatFormatting.GRAY.toString();
        }
        return getVoltageOrThrow().getTierColor();
    }


    public Tier offset(int value) {
        int ordinal = ordinal() + value;
        Assertions.check(ordinal >= 0 && ordinal < values().length, "Invalid ordinal");
        return values()[ordinal];
    }
}
