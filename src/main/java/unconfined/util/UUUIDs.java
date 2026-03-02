package unconfined.util;

import java.util.UUID;

public final class UUUIDs {

    public static int[] uuidToIntArray(UUID uuid) {
        return leastMostToIntArray(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
    }

    private static int[] leastMostToIntArray(long most, long least) {
        return new int[]{(int) (most >> 32), (int) most, (int) (least >> 32), (int) least};
    }
}
