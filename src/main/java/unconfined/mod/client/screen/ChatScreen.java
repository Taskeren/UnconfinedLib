package unconfined.mod.client.screen;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.TestOnly;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import unconfined.core.interf.IGuiTextFieldExtension;
import unconfined.core.interf.PostRenderTextArgs;
import unconfined.core.mixins.misc.GuiChatAccessor;
import unconfined.mod.client.util.RenderUtils;
import unconfined.util.Assertions;
import unconfined.util.Utils;

import java.util.List;
import java.util.Objects;

@NullMarked
public class ChatScreen extends GuiChat {
    private static final boolean DEBUG = Boolean.getBoolean("unconfined.chatScreen.debug");

    protected static final FontRenderer FONT = RenderUtils.getFontRenderer();

    protected static final int MAX_VISIBLE_COUNT = 16;
    protected static int UNSELECTED_COLOR = 0xFFAAAAAA;
    protected static int SELECTED_COLOR = 0xFFFFFF00;
    protected static int BACKGROUND_COLOR = 0xD0000000;
    protected static int WHITE_COLOR = 0xFFFFFFFF;

    protected final SuggestWidget suggest = new SuggestWidget();
    /// `true` when the gui is opened with a slash, which means we need to immediately request the suggestions.
    protected final boolean guiOpenedWithSlash;

    public ChatScreen() {
        super();
        this.guiOpenedWithSlash = false;
    }

    public ChatScreen(String value) {
        super(value);
        this.guiOpenedWithSlash = value.equals("/");
    }

    public ChatScreen(GuiChat guiChat) {
        this(getInitialChatString(guiChat));
    }

    protected static String getInitialChatString(GuiChat guiChat) {
        return ((GuiChatAccessor) guiChat).getDefaultInputFieldText();
    }

    @Override
    public void initGui() {
        super.initGui();
        this.suggest.setTextField(this.inputField);
        if(this.guiOpenedWithSlash) {
            this.suggest.tryRequestSuggestions();
        }
    }

    @Override // invoked when receiving suggestions
    public void func_146406_a(String[] serverCompletions) {
        String[] clientCompletions = ClientCommandHandler.instance.latestAutoComplete;
        if (clientCompletions != null) {
            Utils.mapArrayInplace(clientCompletions, EnumChatFormatting::getTextWithoutFormattingCodes);
        }
        String[] completions = ArrayUtils.addAll(serverCompletions, clientCompletions);
        this.suggest.updateSuggestion(completions);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.suggest.render(mouseX, mouseY);
    }

    @Override
    public void handleMouseInput() {
        int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int mouseY = Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        int dWheel = Mouse.getDWheel();
        if (dWheel != 0) {
            if (!this.suggest.onScroll(mouseX, mouseY, dWheel)) {
                int scroll = dWheel / 120;
                if (scroll != 0) {
                    if (!GuiScreen.isShiftKeyDown()) scroll *= 7;
                    this.mc.ingameGUI.getChatGUI().scroll(scroll);
                }
            }
        }

        super.handleMouseInput();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (this.suggest.onMouseClick(mouseX, mouseY)) return;
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, @MagicConstant(valuesFromClass = Keyboard.class) int keyCode) {
        if (this.suggest.onKey(keyCode)) return;

        if (keyCode == Keyboard.KEY_ESCAPE) {
            // close the gui
            mc.displayGuiScreen(null);
        } else if (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER) {
            // send the message
            String s = inputField.getText().trim();
            if (!s.isEmpty()) this.func_146403_a(s);
            mc.displayGuiScreen(null);
        } else {
            switch (keyCode) {
                case Keyboard.KEY_UP -> this.getSentHistory(-1);
                case Keyboard.KEY_DOWN -> this.getSentHistory(1);
                case Keyboard.KEY_PRIOR ->
                    mc.ingameGUI.getChatGUI().scroll(mc.ingameGUI.getChatGUI().func_146232_i() - 1);
                case Keyboard.KEY_NEXT ->
                    mc.ingameGUI.getChatGUI().scroll(mc.ingameGUI.getChatGUI().func_146232_i() + 1);
                default -> {
                    inputField.textboxKeyTyped(typedChar, keyCode);
                    this.suggest.onTextUpdated();
                }
            }
        }
    }

    @Log4j2
    @RequiredArgsConstructor
    public static class SuggestWidget {
        protected @Nullable GuiTextField textField;
        /// the suggestion list
        ///
        /// - `null`: uninitialized, request sent but not received, or not sent.
        /// - otherwise: the suggestions; empty list is also valid (for no suggestion).
        protected @Nullable List<String> suggestion;
        /// the selection index of suggestions, or `-1` if not capable.
        protected int selection = -1;

        protected int lastRequestedHash;

        /// the candidate list widget, will be null when suggestion is missing, or empty
        ///
        /// Lifecycle:
        /// - _Create_ will be created when receiving a non-empty suggestion list, in [#updateSuggestion(List)].
        /// - _Active_ will be rendered along with this widget, in the text field callback [#callback(PostRenderTextArgs)].
        /// - _Remove_ will be removed when the player press ESC, or any candidate is applied, in [#closeCandidateList()].
        protected @Nullable CandidateListWidget candidateList;

        public void setTextField(@Nullable GuiTextField textField) {
            this.textField = textField;
            // add hook to render our suggested text
            if (this.textField instanceof IGuiTextFieldExtension ext) {
                ext.unconfined$addPostRenderTextCallback(this::callback);
            } else {
                throw new AssertionError("GuiTextField is not implementing IGuiTextFieldExtension, is mixin down?");
            }
        }

        protected GuiTextField getTextField() {
            return Assertions.checkNotNull(this.textField, "textField");
        }

        public boolean closeCandidateList() {
            this.candidateList = null;
            this.selection = -1;
            return true;
        }

        public void render(int mouseX, int mouseY) {
            if (DEBUG) renderScreenDebugInfo();
        }

        @TestOnly
        protected void renderScreenDebugInfo() {
            FONT.drawString("[ChatScreenDebug]", 0, 0, 0xFF000000);
            String selected = this.suggestion != null && this.selection > -1
                ? this.suggestion.get(this.selection) : "...";
            FONT.drawString(
                "selection = (" + selection + ") " + EnumChatFormatting.GOLD + EnumChatFormatting.BOLD + selected,
                0, 8, 0xFF000000
            );
            FONT.drawString("beforeCursor = " + getWordBeforeCursor(getTextField()), 0, 16, 0xFF000000);
            if (suggestion == null) {
                // if null (which means we haven't requested it yet, or not receive response from the server)
                FONT.drawString(EnumChatFormatting.ITALIC + "null", 0, 24, 0xFF000000);
            } else if (suggestion.isEmpty()) {
                // if empty, not null
                FONT.drawString(EnumChatFormatting.ITALIC + "empty", 0, 24, 0xFF000000);
            } else {
                // show first 15 candidates
                for (int i = 0; i < suggestion.size() && i < 16; i++) {
                    FONT.drawString(suggestion.get(i), 0, 24 + i * 8, 0xFF000000);
                }
                // show remaining count
                int i = suggestion.size() - 15;
                if (i > 0) {
                    FONT.drawString(
                        "" + EnumChatFormatting.GRAY + EnumChatFormatting.ITALIC + "<" + i + " remaining>",
                        0, 24 + 128, 0xFF000000
                    );
                }
            }
        }

        protected boolean tryRequestSuggestions() {
            /*
            Request the suggestions from both server and client.
            The response from server will be passed to the GuiChat, the obfuscated function (func_146406_a),
            which we delegate the values to this instance.
             */
            GuiTextField textField = this.getTextField();
            String leftOfCursor = getTextBeforeCursor(textField);
            if (leftOfCursor.isEmpty()) {
                // nothing can be suggested
                return false;
            }
            String textFieldValue = textField.getText();
            int hash = hashRequest(leftOfCursor, textFieldValue);
            if (this.suggestion == null || hash != this.lastRequestedHash) {
                // if (a) the suggestion is uninitialized or (b) the input has changed,
                // try to get the suggestion from the server.
                // but it will return false if we've already requested before.
                ClientCommandHandler.instance.autoComplete(leftOfCursor, textFieldValue);
                Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C14PacketTabComplete(leftOfCursor));
                if (DEBUG) log.info("Requesting suggestions for '{}' (hash {})", leftOfCursor, hash);
                this.lastRequestedHash = hash;
                return true;
            }
            return false;
        }

        public void updateSuggestion(List<String> suggestion) {
            if (DEBUG) log.info(
                "Received suggestions '{}' (hash {})",
                String.join(", ", suggestion), this.lastRequestedHash
            );
            if (this.suggestion == null || !this.suggestion.equals(suggestion)) {
                this.suggestion = suggestion;
                this.selection = -1;
                if (!this.suggestion.isEmpty()) {
                    this.candidateList = new CandidateListWidget(this, this.suggestion);
                } else {
                    this.candidateList = null;
                }
            }
        }

        public void updateSuggestion(String[] suggestion) { // in case of legacy suggestions
            this.updateSuggestion(Lists.newArrayList(suggestion));
        }

        protected static int hashRequest(String leftOfCursor, String full) {
            return Objects.hash(leftOfCursor, full);
        }

        public void clear() {
            this.suggestion = null;
            this.selection = -1;
            this.lastRequestedHash = 0;
        }

        public void select(int selection) {
            if (this.suggestion == null || this.suggestion.isEmpty()) return;
            // equivelant to selection % this.suggestion.size()
            // but always non-negative
            this.selection = Utils.mod(selection, this.suggestion.size());
        }

        /// Apply the selected suggestion and clear the state.
        protected void applySelection() {
            if (this.suggestion == null || this.suggestion.isEmpty()) return;
            if (this.selection == -1) {
                assert this.suggestion.size() == 1 : "You're supposed to select anything first.";
                this.selection = 0;
            }
            GuiTextField textField = Assertions.checkNotNull(this.textField, "textField");
            deleteLastWordBeforeCursor(textField);
            textField.writeText(this.suggestion.get(this.selection));
            this.clear();
        }

        public boolean onKey(@MagicConstant(valuesFromClass = Keyboard.class) int keyCode) {
            if (this.candidateList != null) {
                if (this.candidateList.onKey(keyCode)) return true;
                if (keyCode == Keyboard.KEY_ESCAPE) return closeCandidateList();
            }
            if (this.selection != -1 && keyCode == Keyboard.KEY_BACK) {
                this.selection = -1;
                return true;
            }
            if (keyCode == Keyboard.KEY_TAB) return onTabKey();

            return false;
        }

        protected void onTextUpdated() {
            tryRequestSuggestions();
        }

        protected boolean onTabKey() {
            if (tryRequestSuggestions()) return true;
            if (this.suggestion == null) return false;

            if (this.suggestion.size() == 1) {
                if (this.selection == -1) {
                    // didn't select anything, we propose the first candidate
                    select(0);
                } else { // selection == 0, apply it
                    applySelection();
                }
            } else {
                if (this.selection == -1) {
                    select(0);
                } else if (GuiScreen.isShiftKeyDown()) {
                    // invert when shift is down
                    select(this.selection - 1);
                } else {
                    select(this.selection + 1);
                }
            }
            return true;
        }

        protected boolean onMouseClick(int mouseX, int mouseY) {
            return candidateList != null && candidateList.mouseClicked(mouseX, mouseY);
        }

        protected boolean onScroll(int mouseX, int mouseY, int mouseDelta) {
            return candidateList != null && candidateList.mouseScrolled(mouseX, mouseY, mouseDelta);
        }

        /// callback of rendering the GuiTextField, we use this to render our candidate list for getting some _internal_ values.
        private void callback(PostRenderTextArgs args) {
            if (this.suggestion != null) {
                if (this.selection > -1) { // render the gray candidate right after the existing content
                    // get the selected candidate
                    String selected = this.suggestion.get(this.selection);
                    // drop the characters that's already typed.
                    String toRender = StringUtils.removeStart(selected, getWordBeforeCursor(args.self()));
                    // and draw
                    args.drawStringRightAfter(FONT, EnumChatFormatting.GRAY + toRender);
                }

                // suggestion widget
                if (this.candidateList != null) {
                    if (this.candidateList.x < 0) {
                        // correct positions for widgets that created elsewhere.
                        this.candidateList.setPos(
                            args.xPos() + FONT.getStringWidth(StringUtils.removeEnd(
                                args.text(),
                                getWordBeforeCursor(args.self())
                            )),
                            args.yPos()
                        );
                    }
                    this.candidateList.render(RenderUtils.getMouseVec());
                }
            }
        }
    }

    protected static int getStartIndexOfWordBeforeCursor(GuiTextField textField) {
        return textField.func_146197_a(-1, textField.getCursorPosition(), false);
    }

    protected static String getWordBeforeCursor(GuiTextField textField) {
        return textField.getText().substring(getStartIndexOfWordBeforeCursor(textField));
    }

    protected static String getTextBeforeCursor(GuiTextField textField) {
        return textField.getText().substring(0, textField.getCursorPosition());
    }

    protected static void deleteLastWordBeforeCursor(GuiTextField textField) {
        int startOfLastWordBeforeCursor = getStartIndexOfWordBeforeCursor(textField);
        textField.deleteFromCursor(startOfLastWordBeforeCursor - textField.getCursorPosition());
    }

}
