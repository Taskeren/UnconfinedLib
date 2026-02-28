package unconfined.util.tier;

import gregtech.api.enums.VoltageIndex;
import org.intellij.lang.annotations.MagicConstant;

public interface ITier {
    /// @return the tier in integer form
    @MagicConstant(valuesFromClass = VoltageIndex.class)
    int getValue();

    /// @return the short name (e.g., LV, LuV, UMV)
    String getName();

    /// @return the long name (e.g., Low Voltage, Ludicrous Voltage, Ultimate Mega Voltage)
    String getLongName();

    /// @return the color code (e.g., §a, §c§l)
    String getTierColor();

    default String getColoredName() {
        return getTierColor() + getName();
    }

    default String getColoredLongName() {
        return getTierColor() + getLongName();
    }
}
