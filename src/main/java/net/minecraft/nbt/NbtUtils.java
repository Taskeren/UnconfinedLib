package net.minecraft.nbt;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import unconfined.util.chat.Component;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class NbtUtils {
    private NbtUtils() {
    }

    public static String prettyPrint(Tag tag) {
        return prettyPrint(tag, false);
    }

    public static String prettyPrint(Tag tag, boolean prettyPrintArray) {
        return prettyPrint(new StringBuilder(), tag, 0, prettyPrintArray).toString();
    }

    public static StringBuilder prettyPrint(StringBuilder stringBuilder, Tag tag, int indentLevel, boolean prettyPrintArray) {
        return switch (tag) {
            case PrimitiveTag primitivetag -> stringBuilder.append(primitivetag);
            case EndTag endtag -> stringBuilder;
            case ByteArrayTag bytearraytag -> {
                byte[] abyte = bytearraytag.getAsByteArray();
                int i1 = abyte.length;
                indent(indentLevel, stringBuilder).append("byte[").append(i1).append("] {\n");
                if (prettyPrintArray) {
                    indent(indentLevel + 1, stringBuilder);

                    for (int k1 = 0; k1 < abyte.length; k1++) {
                        if (k1 != 0) {
                            stringBuilder.append(',');
                        }

                        if (k1 % 16 == 0 && k1 / 16 > 0) {
                            stringBuilder.append('\n');
                            indent(indentLevel + 1, stringBuilder);
                        } else if (k1 != 0) {
                            stringBuilder.append(' ');
                        }

                        stringBuilder.append(String.format(Locale.ROOT, "0x%02X", abyte[k1] & 255));
                    }
                } else {
                    indent(indentLevel + 1, stringBuilder).append(" // Skipped, supply withBinaryBlobs true");
                }

                stringBuilder.append('\n');
                indent(indentLevel, stringBuilder).append('}');
                yield stringBuilder;
            }
            case ListTag listtag -> {
                int l = listtag.size();
                indent(indentLevel, stringBuilder).append("list").append("[").append(l).append("] [");
                if (l != 0) {
                    stringBuilder.append('\n');
                }

                for (int j1 = 0; j1 < l; j1++) {
                    if (j1 != 0) {
                        stringBuilder.append(",\n");
                    }

                    indent(indentLevel + 1, stringBuilder);
                    prettyPrint(stringBuilder, listtag.get(j1), indentLevel + 1, prettyPrintArray);
                }

                if (l != 0) {
                    stringBuilder.append('\n');
                }

                indent(indentLevel, stringBuilder).append(']');
                yield stringBuilder;
            }
            case IntArrayTag intarraytag -> {
                int[] aint = intarraytag.getAsIntArray();
                int l1 = 0;

                for (int i3 : aint) {
                    l1 = Math.max(l1, String.format(Locale.ROOT, "%X", i3).length());
                }

                int j2 = aint.length;
                indent(indentLevel, stringBuilder).append("int[").append(j2).append("] {\n");
                if (prettyPrintArray) {
                    indent(indentLevel + 1, stringBuilder);

                    for (int k2 = 0; k2 < aint.length; k2++) {
                        if (k2 != 0) {
                            stringBuilder.append(',');
                        }

                        if (k2 % 16 == 0 && k2 / 16 > 0) {
                            stringBuilder.append('\n');
                            indent(indentLevel + 1, stringBuilder);
                        } else if (k2 != 0) {
                            stringBuilder.append(' ');
                        }

                        stringBuilder.append(String.format(Locale.ROOT, "0x%0" + l1 + "X", aint[k2]));
                    }
                } else {
                    indent(indentLevel + 1, stringBuilder).append(" // Skipped, supply withBinaryBlobs true");
                }

                stringBuilder.append('\n');
                indent(indentLevel, stringBuilder).append('}');
                yield stringBuilder;
            }
            case CompoundTag compoundtag -> {
                List<String> list = Lists.newArrayList(compoundtag.keySet());
                Collections.sort(list);
                indent(indentLevel, stringBuilder).append('{');
                if (stringBuilder.length() - stringBuilder.lastIndexOf("\n") > 2 * (indentLevel + 1)) {
                    stringBuilder.append('\n');
                    indent(indentLevel + 1, stringBuilder);
                }

                int i2 = list.stream().mapToInt(String::length).max().orElse(0);
                String s = Strings.repeat(" ", i2);

                for (int j = 0; j < list.size(); j++) {
                    if (j != 0) {
                        stringBuilder.append(",\n");
                    }

                    String s1 = list.get(j);
                    indent(indentLevel + 1, stringBuilder).append('"')
                        .append(s1)
                        .append('"')
                        .append(s, 0, s.length() - s1.length())
                        .append(": ");
                    prettyPrint(stringBuilder, compoundtag.get(s1), indentLevel + 1, prettyPrintArray);
                }

                if (!list.isEmpty()) {
                    stringBuilder.append('\n');
                }

                indent(indentLevel, stringBuilder).append('}');
                yield stringBuilder;
            }
            case LongArrayTag longarraytag -> {
                long[] along = longarraytag.getAsLongArray();
                long i = 0L;

                for (long k : along) {
                    i = Math.max(i, String.format(Locale.ROOT, "%X", k).length());
                }

                long l2 = along.length;
                indent(indentLevel, stringBuilder).append("long[").append(l2).append("] {\n");
                if (prettyPrintArray) {
                    indent(indentLevel + 1, stringBuilder);

                    for (int j3 = 0; j3 < along.length; j3++) {
                        if (j3 != 0) {
                            stringBuilder.append(',');
                        }

                        if (j3 % 16 == 0 && j3 / 16 > 0) {
                            stringBuilder.append('\n');
                            indent(indentLevel + 1, stringBuilder);
                        } else if (j3 != 0) {
                            stringBuilder.append(' ');
                        }

                        stringBuilder.append(String.format(Locale.ROOT, "0x%0" + i + "X", along[j3]));
                    }
                } else {
                    indent(indentLevel + 1, stringBuilder).append(" // Skipped, supply withBinaryBlobs true");
                }

                stringBuilder.append('\n');
                indent(indentLevel, stringBuilder).append('}');
                yield stringBuilder;
            }
        };
    }

    private static StringBuilder indent(int indentLevel, StringBuilder stringBuilder) {
        int i = stringBuilder.lastIndexOf("\n") + 1;
        int j = stringBuilder.length() - i;

        stringBuilder.append(" ".repeat(Math.max(0, 2 * indentLevel - j)));

        return stringBuilder;
    }

    public static Component toPrettyComponent(Tag tag) {
        return new TextComponentTagVisitor("").visit(tag);
    }
}
