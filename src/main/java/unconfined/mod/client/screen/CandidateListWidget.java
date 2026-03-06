package unconfined.mod.client.screen;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import org.intellij.lang.annotations.MagicConstant;
import org.joml.Vector2i;
import org.jspecify.annotations.Nullable;
import org.lwjgl.input.Keyboard;
import unconfined.mod.client.util.data.Rect2i;
import unconfined.util.Assertions;
import unconfined.util.Utils;

import java.util.List;

/// The widget that renders the candidate list on top of the chat input field.
public class CandidateListWidget {
    protected final ChatScreen.SuggestWidget widget;

    protected int x;
    protected int y;

    /// the width of the widget, calculated by suggestions.
    protected final int width;
    /// the list of candidates, non-empty.
    protected final List<String> suggestions;

    /// the size of [#suggestions]
    protected final int totalCount;
    /// the count of visible (rendered) candidates
    protected final int visibleCount;
    /// the rect area of this widget
    ///
    /// NOTE: when there's border needed, the real value will expand by 1 on Y-axis.
    protected final Rect2i rect;
    /// the index that the candidate list shows as the first
    protected int offset;
    /// the index of the candidate in [#suggestions]
    protected int current;
    /// the coordinates of the mouse at the last rendering (used to check if the mouse is moved)
    protected @Nullable Vector2i lastMouse = null;
    /// Tab Cycling Mode:
    /// enabled by default; disabled when ARROW_UP or ARROW_DOWN is used to select the candidates.
    ///
    /// `true`:
    /// pressing TAB will shift the candidate by 1 (up or down depends on whether or not SHIFT is pressed).
    ///
    /// `false`:
    /// pressing TAB will select the candidate, and close this widget.
    protected boolean tabCylce = true;

    public CandidateListWidget(ChatScreen.SuggestWidget widget, int x, int y, int width, List<String> suggestions) {
        this.widget = widget;
        this.x = x;
        this.y = y;
        this.width = width;
        this.suggestions = suggestions;
        this.totalCount = suggestions.size();
        Assertions.require(this.totalCount > 0, "Empty suggestion list can't be used to create SuggestListWidget.");
        this.visibleCount = Math.min(totalCount, ChatScreen.MAX_VISIBLE_COUNT);
        int listHeight = visibleCount * 12;
        int listY = y - 3 - listHeight;
        this.rect = Rect2i.ofMutable(x, listY, width + 1, listHeight);
    }

    public CandidateListWidget(ChatScreen.SuggestWidget widget, int x, int y, List<String> suggestions) {
        this(widget, x, y, ChatScreen.FONT.getStringWidth(Utils.getLongestString(suggestions)), suggestions);
    }

    public CandidateListWidget(ChatScreen.SuggestWidget widget, List<String> suggestions) {
        this(widget, -1, -1, suggestions);
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
        int listY = y - 3 - visibleCount * 12;
        this.rect.x(x);
        this.rect.y(listY);
    }

    public void select(int index) {
        this.current = Utils.mod(index, totalCount);
        this.widget.select(this.current);
    }

    public void selectAndClose() {
        this.widget.select(this.current);
        this.widget.applySelection();
        this.widget.closeCandidateList();
    }

    public boolean onKey(@MagicConstant(valuesFromClass = Keyboard.class) int keyCode) {
        if (keyCode == Keyboard.KEY_UP) {
            offsetSelect(-1);
            tabCylce = false;
            return true;
        }
        if (keyCode == Keyboard.KEY_DOWN) {
            offsetSelect(1);
            tabCylce = false;
            return true;
        }
        if (keyCode == Keyboard.KEY_TAB) {
            if (tabCylce) {
                if (this.suggestions.size() == 1) {
                    // directly select if there's only one element
                    selectAndClose();
                    return true;
                }
                offsetSelect(GuiScreen.isShiftKeyDown() ? -1 : 1);
            } else {
                selectAndClose();
            }
            return true;
        }
        if (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER) {
            selectAndClose();
            return true;
        }
        return false;
    }

    public void offsetSelect(int direction) {
        this.select(this.current + direction);
        int first = this.offset;
        int last = this.offset + ChatScreen.MAX_VISIBLE_COUNT - 1;
        if (current < first) {
            this.offset = Math.clamp(this.current, 0, Math.max(0, suggestions.size() - ChatScreen.MAX_VISIBLE_COUNT));
        } else if (current > last) {
            this.offset = Math.clamp(
                this.current + 1 - visibleCount,
                0, Math.max(0, suggestions.size() - visibleCount)
            );
        }
    }

    public boolean mouseClicked(int mouseX, int mouseY) {
        if (this.rect.contains(mouseX, mouseY)) {
            int index = ((mouseY - this.rect.y()) / 12) + this.offset;
            if (index >= 0 && index < this.suggestions.size()) {
                select(index);
                selectAndClose();
            }
            return true;
        }
        return false;
    }

    public boolean mouseScrolled(int mouseX, int mouseY, int scroll) {
        if (this.rect.contains(mouseX, mouseY)) {
            this.offset = Math.clamp(this.offset - scroll, 0, Math.max(0, this.suggestions.size() - visibleCount));
        }
        return false;
    }

    public void render(Vector2i mouseVec) {
        boolean prev = this.offset > 0;
        boolean next = this.suggestions.size() > this.offset + this.visibleCount;
        boolean hidden = prev || next;

        if (hidden) {
            // top line
            int topLineY1 = rect.y() - 1;
            int topLineY2 = rect.y();
            Gui.drawRect(rect.x(), topLineY1, rect.x() + rect.width(), topLineY2, ChatScreen.BACKGROUND_COLOR);
            // bottom line
            int bottomLineY1 = rect.y() + rect.height();
            int bottomLineY2 = rect.y() + rect.height() + 1;
            Gui.drawRect(
                rect.x(),
                bottomLineY1,
                rect.x() + rect.width(),
                bottomLineY2,
                ChatScreen.BACKGROUND_COLOR
            );
            // top line dots
            if (prev) for (int i = 0; i < rect.width(); i++) {
                if (i % 2 == 0)
                    Gui.drawRect(rect.x() + i, topLineY1, rect.x() + i + 1, topLineY2, ChatScreen.WHITE_COLOR);
            }
            // bottom line dots
            if (next) for (int i = 0; i < rect.width(); i++) {
                if (i % 2 == 0)
                    Gui.drawRect(rect.x() + i, bottomLineY1, rect.x() + i + 1, bottomLineY2, ChatScreen.WHITE_COLOR);
            }
        }

        // check if mouse moved comparing to the last coordinates,
        // if so, we need to update the selected.
        boolean mouseMoved;
        if (lastMouse == null) {
            lastMouse = mouseVec;
            mouseMoved = false;
        } else {
            if (lastMouse.equals(mouseVec)) {
                mouseMoved = false;
            } else {
                lastMouse = mouseVec;
                mouseMoved = true;
            }
        }

        for (int i = 0; i < this.visibleCount; i++) {
            int thisIndex = i + this.offset;
            if (mouseMoved) { // mouse moved, so select what it points
                if (mouseVec.x() >= rect.x()
                    && mouseVec.x() <= rect.x() + rect.width()
                    && mouseVec.y() >= rect.y() + 12 * i
                    && mouseVec.y() < rect.y() + 12 * (i + 1) // not equal, to prevent from selecting both
                ) {
                    select(thisIndex);
                }
            }

            String s = this.suggestions.get(thisIndex);
            // background color
            Gui.drawRect(
                rect.x(),
                rect.y() + 12 * i,
                rect.x() + rect.width(),
                rect.y() + 12 * (i + 1),
                ChatScreen.BACKGROUND_COLOR
            );
            // foreground text
            ChatScreen.FONT.drawStringWithShadow(
                s,
                rect.x() + 1,
                rect.y() + 2 + 12 * i,
                thisIndex == this.current ? ChatScreen.SELECTED_COLOR : ChatScreen.UNSELECTED_COLOR
            );
        }
    }
}
