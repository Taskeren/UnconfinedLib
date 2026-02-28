package unconfined.util.tier;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.VoltageIndex;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.intellij.lang.annotations.MagicConstant;
import unconfined.util.Assertions;

/// @see gregtech.api.enums.VoltageIndex
@RequiredArgsConstructor
public enum Voltage implements ITier {
    ULV(0),
    LV(1),
    MV(2),
    HV(3),
    EV(4),
    IV(5),
    LuV(6),
    ZPM(7),
    UV(8),
    UHV(9),
    UEV(10),
    UIV(11),
    UMV(12),
    UXV(13),
    MAX(14),
    MAX_PLUS(15),
    ;
    @Getter
    private final int value;

    public static Voltage fromInt(@MagicConstant(valuesFromClass = VoltageIndex.class) int voltageIndex) {
        Assertions.check(voltageIndex >= 0 && voltageIndex <= 15, "voltageIndex is expected be in [0, 15]");
        return values()[voltageIndex];
    }

    public long getVoltage() {
        return GTValues.V[value];
    }

    public long getPracticalVoltage() {
        return GTValues.VP[value];
    }

    @Override
    public String getName() {
        return GTValues.VN[value];
    }

    @Override
    public String getLongName() {
        return GTValues.VOLTAGE_NAMES[value];
    }

    public String getLocalizedLongName() {
        return GTValues.getLocalizedLongVoltageName(value);
    }

    @Override
    public String getTierColor() {
        return GTValues.TIER_COLORS[value];
    }
}
