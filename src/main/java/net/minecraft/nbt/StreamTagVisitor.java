package net.minecraft.nbt;

public interface StreamTagVisitor {
    ValueResult visitEnd();

    ValueResult visit(String entry);

    ValueResult visit(byte entry);

    ValueResult visit(short entry);

    ValueResult visit(int entry);

    ValueResult visit(long entry);

    ValueResult visit(float entry);

    ValueResult visit(double entry);

    ValueResult visit(byte[] entry);

    ValueResult visit(int[] entry);

    ValueResult visit(long[] entry);

    ValueResult visitList(TagType<?> type, int size);

    EntryResult visitEntry(TagType<?> type);

    EntryResult visitEntry(TagType<?> type, String id);

    EntryResult visitElement(TagType<?> type, int size);

    ValueResult visitContainerEnd();

    ValueResult visitRootEntry(TagType<?> type);

    enum EntryResult {
        ENTER,
        SKIP,
        BREAK,
        HALT
    }

    enum ValueResult {
        CONTINUE,
        BREAK,
        HALT
    }
}
