package unconfined.core;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

@Log4j2
public final class UnconfinedMixinPlugin implements IMixinConfigPlugin {
    private static final String UNC_MIXIN_PKG_PREFIX = "unconfined.core.mixins.";
    private final UnconfinedCoreConfig config = UnconfinedCoreConfig.INSTANCE;

    @Getter
    private static boolean multiFluidBasicImplementedByDefault;

    @Override
    public void onLoad(String mixinPackage) {
        log.info("Loading core configurations");
        config.getHandler().loadAll();
    }

    @Override
    public @Nullable String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        log.debug("Checking if should apply mixin {} to {}", mixinClassName, targetClassName);
        if (mixinClassName.equals(UNC_MIXIN_PKG_PREFIX + "multifluid.MTEBasicMachineImplMixin")) {
            return multiFluidBasicImplementedByDefault = config.isMultiFluidBasicImplementedByDefault();
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public @Nullable List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {
    }

    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {
    }
}
