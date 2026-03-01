package unconfined.util;

import lombok.SneakyThrows;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagEnd;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.ApiStatus;
import unconfined.Unconfined;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/// A collection of convert methods for legacy and modern NBTs.
public final class NBTConvert {

    private static final Field FIELD_NBTTAGCOMPOUND_TAGMAP;
    private static final Field FIELD_NBTTAGLIST_TAGLIST;

    static {
        try {
            FIELD_NBTTAGCOMPOUND_TAGMAP = NBTTagCompound.class.getDeclaredField("tagMap");
            FIELD_NBTTAGCOMPOUND_TAGMAP.setAccessible(true);
            FIELD_NBTTAGLIST_TAGLIST = NBTTagList.class.getDeclaredField("tagList");
            FIELD_NBTTAGLIST_TAGLIST.setAccessible(true);
        } catch (Throwable throwable) {
            Unconfined.log.error("Failed to initialize NBTConvert", throwable);
            throw new RuntimeException("Failed to initialize NBTConvert", throwable);
        }
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @ApiStatus.Experimental
    static Map<String, NBTBase> internalMap(NBTTagCompound tag) {
        return (Map<String, NBTBase>) FIELD_NBTTAGCOMPOUND_TAGMAP.get(tag);
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @ApiStatus.Experimental
    static List<NBTBase> internalList(NBTTagList tag) {
        return (List<NBTBase>) FIELD_NBTTAGLIST_TAGLIST.get(tag);
    }

    public static Tag toModern(NBTBase tag) {
        return switch (tag) {
            case NBTTagCompound t -> toModern(t);
            case NBTTagList t -> toModern(t);
            case NBTTagByteArray t -> toModern(t);
            case NBTTagIntArray t -> toModern(t);
            case NBTTagByte t -> toModern(t);
            case NBTTagShort t -> toModern(t);
            case NBTTagInt t -> toModern(t);
            case NBTTagLong t -> toModern(t);
            case NBTTagFloat t -> toModern(t);
            case NBTTagDouble t -> toModern(t);
            case NBTTagString t -> toModern(t);
            case NBTTagEnd t -> toModern(t);
            default -> throw new RuntimeException("Invalid tag type: " + tag);
        };
    }

    public static CompoundTag toModern(NBTTagCompound tag) {
        Map<String, NBTBase> map = internalMap(tag);
        CompoundTag copy = new CompoundTag(map.size());
        map.forEach((k, v) -> copy.put(k, toModern(v)));
        return copy;
    }

    public static ListTag toModern(NBTTagList tag) {
        List<NBTBase> list = internalList(tag);
        ListTag copy = new ListTag(list.size());
        for (NBTBase value : list) {
            copy.add(toModern(value));
        }
        return copy;
    }

    public static ByteArrayTag toModern(NBTTagByteArray tag) {
        return new ByteArrayTag(ArrayUtils.clone(tag.func_150292_c()));
    }

    public static IntArrayTag toModern(NBTTagIntArray tag) {
        return new IntArrayTag(ArrayUtils.clone(tag.func_150302_c()));
    }

    public static ByteTag toModern(NBTTagByte tag) {
        return ByteTag.valueOf(tag.func_150290_f());
    }

    public static ShortTag toModern(NBTTagShort tag) {
        return ShortTag.valueOf(tag.func_150289_e());
    }

    public static IntTag toModern(NBTTagInt tag) {
        return IntTag.valueOf(tag.func_150287_d());
    }

    public static LongTag toModern(NBTTagLong tag) {
        return LongTag.valueOf(tag.func_150291_c());
    }

    public static FloatTag toModern(NBTTagFloat tag) {
        return FloatTag.valueOf(tag.func_150288_h());
    }

    public static DoubleTag toModern(NBTTagDouble tag) {
        return DoubleTag.valueOf(tag.func_150286_g());
    }

    public static StringTag toModern(NBTTagString tag) {
        return StringTag.valueOf(tag.func_150285_a_());
    }

    @SuppressWarnings("unused")
    public static EndTag toModern(NBTTagEnd tag) {
        return EndTag.INSTANCE;
    }

    public static NBTBase toLegacy(Tag tag) {
        return switch (tag) {
            case CompoundTag t -> toLegacy(t);
            case ListTag t -> toLegacy(t);
            case ByteArrayTag t -> toLegacy(t);
            case IntArrayTag t -> toLegacy(t);
            case LongArrayTag t -> toLegacy(t);
            case ByteTag t -> toLegacy(t);
            case ShortTag t -> toLegacy(t);
            case IntTag t -> toLegacy(t);
            case LongTag t -> toLegacy(t);
            case FloatTag t -> toLegacy(t);
            case DoubleTag t -> toLegacy(t);
            case StringTag t -> toLegacy(t);
            case EndTag t -> toLegacy(t);
        };
    }

    public static NBTTagCompound toLegacy(CompoundTag tag) {
        NBTTagCompound copy = new NBTTagCompound();
        for (Map.Entry<String, Tag> entry : tag.entrySet()) {
            copy.setTag(entry.getKey(), toLegacy(entry.getValue()));
        }
        return copy;
    }

    public static NBTTagList toLegacy(ListTag list) {
        NBTTagList copy = new NBTTagList();
        for (Tag tag : list) {
            copy.appendTag(toLegacy(tag));
        }
        return copy;
    }

    public static NBTTagByteArray toLegacy(ByteArrayTag tag) {
        return new NBTTagByteArray(ArrayUtils.clone(tag.getAsByteArray()));
    }

    public static NBTTagIntArray toLegacy(IntArrayTag tag) {
        return new NBTTagIntArray(ArrayUtils.clone(tag.getAsIntArray()));
    }

    /// @deprecated Long array is not supported in legacy.
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public static NBTTagIntArray toLegacy(LongArrayTag tag) {
        long[] longArray = tag.getAsLongArray();
        int[] intArray = new int[tag.size()];
        for (int i = 0; i < intArray.length; i++) intArray[i] = (int) longArray[i];

        return new NBTTagIntArray(intArray);
    }

    public static NBTTagByte toLegacy(ByteTag tag) {
        return new NBTTagByte(tag.byteValue());
    }

    public static NBTTagShort toLegacy(ShortTag tag) {
        return new NBTTagShort(tag.shortValue());
    }

    public static NBTTagInt toLegacy(IntTag tag) {
        return new NBTTagInt(tag.intValue());
    }

    public static NBTTagLong toLegacy(LongTag tag) {
        return new NBTTagLong(tag.longValue());
    }

    public static NBTTagFloat toLegacy(FloatTag tag) {
        return new NBTTagFloat(tag.floatValue());
    }

    public static NBTTagDouble toLegacy(DoubleTag tag) {
        return new NBTTagDouble(tag.doubleValue());
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static NBTTagString toLegacy(StringTag tag) {
        return new NBTTagString(tag.asString().get());
    }

    @SuppressWarnings("unused")
    public static NBTTagEnd toLegacy(EndTag tag) {
        return new NBTTagEnd();
    }
}
