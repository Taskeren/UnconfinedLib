package net.minecraft.util.parsing.packrat.commands;

import com.google.common.base.CharMatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.parsing.packrat.DelayedException;
import net.minecraft.util.parsing.packrat.Dictionary;
import net.minecraft.util.parsing.packrat.ErrorCollector;
import net.minecraft.util.parsing.packrat.ErrorEntry;
import net.minecraft.util.parsing.packrat.NamedRule;
import net.minecraft.util.parsing.packrat.ParseState;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record Grammar<T>(Dictionary<StringReader> rules,
                         NamedRule<StringReader, T> top) implements CommandArgumentParser<T> {
    public Grammar {
        rules.checkAllBound();
    }

    public Optional<T> parse(ParseState<StringReader> parseState) {
        return parseState.parseTopRule(this.top);
    }

    @Override
    public T parseForCommands(StringReader reader) throws CommandSyntaxException {
        ErrorCollector.LongestOnly<StringReader> longestonly = new ErrorCollector.LongestOnly<>();
        StringReaderParserState stringreaderparserstate = new StringReaderParserState(longestonly, reader);
        Optional<T> optional = this.parse(stringreaderparserstate);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            List<ErrorEntry<StringReader>> list = longestonly.entries();
            List<Exception> list1 = list.stream().<Exception>mapMulti((p_409181_, p_409182_) -> {
                if (p_409181_.reason() instanceof DelayedException<?> delayedexception) {
                    p_409182_.accept(delayedexception.create(reader.getString(), p_409181_.cursor()));
                } else if (p_409181_.reason() instanceof Exception exception1) {
                    p_409182_.accept(exception1);
                }
            }).toList();

            for (Exception exception : list1) {
                if (exception instanceof CommandSyntaxException commandsyntaxexception) {
                    throw commandsyntaxexception;
                }
            }

            if (list1.size() == 1 && list1.get(0) instanceof RuntimeException runtimeexception) {
                throw runtimeexception;
            } else {
                throw new IllegalStateException("Failed to parse: " + list.stream()
                    .map(ErrorEntry::toString)
                    .collect(Collectors.joining(", ")));
            }
        }
    }

    @Override
    public CompletableFuture<Suggestions> parseForSuggestions(SuggestionsBuilder builder) {
        StringReader stringreader = new StringReader(builder.getInput());
        stringreader.setCursor(builder.getStart());
        ErrorCollector.LongestOnly<StringReader> longestonly = new ErrorCollector.LongestOnly<>();
        StringReaderParserState stringreaderparserstate = new StringReaderParserState(longestonly, stringreader);
        this.parse(stringreaderparserstate);
        List<ErrorEntry<StringReader>> list = longestonly.entries();
        if (list.isEmpty()) {
            return builder.buildFuture();
        } else {
            SuggestionsBuilder suggestionsbuilder = builder.createOffset(longestonly.cursor());

            for (ErrorEntry<StringReader> errorentry : list) {
                if (errorentry.suggestions() instanceof ResourceSuggestion resourcesuggestion) {
                    Internal.suggestResource(
                        resourcesuggestion.possibleResources(),
                        suggestionsbuilder
                    );
                } else {
                    Internal.suggest(
                        errorentry.suggestions().possibleValues(stringreaderparserstate), suggestionsbuilder
                    );
                }
            }

            return suggestionsbuilder.buildFuture();
        }
    }

    // copied from SharedSuggestionProvider
    @SuppressWarnings("all")
    private static class Internal {
        static CharMatcher MATCH_SPLITTER = CharMatcher.anyOf("._/");

        static CompletableFuture<Suggestions> suggest(Stream<String> strings, SuggestionsBuilder builder) {
            String s = builder.getRemaining().toLowerCase(Locale.ROOT);
            strings.filter(p_82975_ -> matchesSubStr(s, p_82975_.toLowerCase(Locale.ROOT))).forEach(builder::suggest);
            return builder.buildFuture();
        }

        static boolean matchesSubStr(String input, String substring) {
            int i = 0;

            while (!substring.startsWith(input, i)) {
                int j = MATCH_SPLITTER.indexIn(substring, i);
                if (j < 0) {
                    return false;
                }

                i = j + 1;
            }

            return true;
        }

        static CompletableFuture<Suggestions> suggestResource(Stream<Identifier> resourceLocations, SuggestionsBuilder builder) {
            return suggestResource(resourceLocations::iterator, builder);
        }

        static CompletableFuture<Suggestions> suggestResource(Iterable<Identifier> resources, SuggestionsBuilder builder) {
            String s = builder.getRemaining().toLowerCase(Locale.ROOT);
            filterResources(resources, s, p_468523_ -> p_468523_, p_465813_ -> builder.suggest(p_465813_.toString()));
            return builder.buildFuture();
        }

        static <T> void filterResources(Iterable<T> resources, String input, Function<T, Identifier> locationFunction, Consumer<T> resourceConsumer) {
            boolean flag = input.indexOf(58) > -1;

            for (T t : resources) {
                Identifier identifier = locationFunction.apply(t);
                if (flag) {
                    String s = identifier.toString();
                    if (matchesSubStr(input, s)) {
                        resourceConsumer.accept(t);
                    }
                } else if (matchesSubStr(input, identifier.getNamespace()) || matchesSubStr(
                    input,
                    identifier.getPath()
                )) {
                    resourceConsumer.accept(t);
                }
            }
        }
    }
}
