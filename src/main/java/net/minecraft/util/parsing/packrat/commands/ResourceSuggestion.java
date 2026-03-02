package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import net.minecraft.resources.Identifier;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.SuggestionSupplier;

import java.util.stream.Stream;

public interface ResourceSuggestion extends SuggestionSupplier<StringReader> {
    Stream<Identifier> possibleResources();

    @Override
    default Stream<String> possibleValues(ParseState<StringReader> p_335480_) {
        return this.possibleResources().map(Identifier::toString);
    }
}
