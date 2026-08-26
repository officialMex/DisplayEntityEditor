package goldenshadow.displayentityeditor;

import goldenshadow.displayentityeditor.enums.LockSearchMode;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditingHandler {

    /**
     * The map of player UUIDs to the displays they are currently editing.
     */
    private final Map<UUID, Collection<Display>> editingDisplaysMap = new HashMap<>();

    /**
     * @param player The player that should be editing the displays.
     * @param displays The collection of displays the player should be editing.
     */
    public void setEditingDisplays(Player player, Collection<Display> displays) {
        this.editingDisplaysMap.put(player.getUniqueId(), displays);
    }

    /**
     * @param player The player that should no longer be editing any displays.
     */
    public void removeEditingDisplays(Player player) {
        this.editingDisplaysMap.remove(player.getUniqueId());
    }

    /**
     * @param player The player that is editing display(s).
     * @return The collection of displays the player is currently editing.
     * If the player is not editing any displays, an display search is being started according to the players' @{link SelectionMode}.
     * <p>
     * Please note that this method will use the players' currently selected {@link LockSearchMode}.
     */
    @Nullable
    public Collection<Display> getEditingDisplays(Player player) {
        return getEditingDisplays(player, Utilities.getToolSearchMode(player));
    }

    /**
     * @param player The player that is editing display(s).
     * @param lockSearchMode The lock search mode to check if an entity should be included in the selection or not.
     * @return The collection of displays the player is currently editing.
     * If the player is not editing any displays, an display search is being started according to the players' @{link SelectionMode}.
     * @see SelectionMode#select(Player, LockSearchMode)
     */
    @Nullable
    public Collection<Display> getEditingDisplays(Player player, LockSearchMode lockSearchMode) {
        Collection<Display> displays = this.editingDisplaysMap.get(player.getUniqueId());
        if (displays != null) {
            return displays;
        }
        return Utilities.getToolSelectMode(player).select(player, lockSearchMode);
    }
}
