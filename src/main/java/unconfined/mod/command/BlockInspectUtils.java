package unconfined.mod.command;

import org.jspecify.annotations.Nullable;
import unconfined.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

class BlockInspectUtils {

    public static List<Class<?>> getImplementedInterfaces(Class<?> clazz) {
        return Utils.make(
            new ArrayList<>(),
            list -> gatherClassImplementedInterfaces(clazz, ifaces -> list.addAll(Arrays.asList(ifaces)), null)
        );
    }

    private static void gatherClassImplementedInterfaces(Class<?> clazz, Consumer<Class<?>[]> gather, @Nullable Class<?> ignoreAt) {
        while (true) {
            if (clazz == ignoreAt) return;
            Class<?>[] ifaces = clazz.getInterfaces();
            if (ifaces.length != 0) gather.accept(ifaces);
            if (clazz != Object.class) {
                clazz = clazz.getSuperclass();
                continue;
            }
            return;
        }
    }

}
