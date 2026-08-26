package goldenshadow.displayentityeditor.enums;

import lombok.Getter;
import org.bukkit.entity.Display;

import java.util.function.Predicate;

@Getter
public enum LockSearchMode {

    ALL(display -> true),
    LOCKED(display -> display.getScoreboardTags().contains("dee:locked")),
    UNLOCKED(display -> !display.getScoreboardTags().contains("dee:locked"));

    private static final LockSearchMode[] MODES = LockSearchMode.values();

    private final Predicate<Display> predicate;

    LockSearchMode(final Predicate<Display> predicate) {
        this.predicate = predicate;
    }

    public LockSearchMode previousMode() {
        int i = ordinal();
        return i == 0 ? MODES[MODES.length - 1] : MODES[i - 1];
    }

    public LockSearchMode nextMode() {
        int i = ordinal();
        return i + 1 == MODES.length ? MODES[0] : MODES[i + 1];
    }
}
