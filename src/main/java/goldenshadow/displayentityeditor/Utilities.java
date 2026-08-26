package goldenshadow.displayentityeditor;

import goldenshadow.displayentityeditor.enums.LockSearchMode;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public class Utilities {

    /**
     * Used to easily set an items meta
     * @param item The item
     * @param name The name it should get
     * @param lore The lore it should get
     * @param data The data it should get
     */
    public static void setMeta(ItemStack item, String name, List<String> lore, String data) {
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        lore.replaceAll(textToTranslate -> ChatColor.translateAlternateColorCodes('&', textToTranslate));
        meta.setLore(lore);
        meta.getPersistentDataContainer().set(DisplayEntityEditor.toolKey, PersistentDataType.STRING, data);

        meta.addItemFlags(ItemFlag.values());
        meta.setUnbreakable(true);
        item.setItemMeta(meta);
    }

    /**
     * Used to easily set an items meta
     * @param item The item
     * @param name The name it should get
     * @param lore The lore it should get
     * @param data The data it should get
     * @param formatData Data that should be used to format a string
     */
    public static void setMeta(ItemStack item, String name, List<String> lore, String data, Object... formatData) {
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        lore.replaceAll(textToTranslate -> ChatColor.translateAlternateColorCodes('&', textToTranslate).formatted(formatData));
        meta.setLore(lore);
        meta.getPersistentDataContainer().set(DisplayEntityEditor.toolKey, PersistentDataType.STRING, data);

        meta.addItemFlags(ItemFlag.values());
        meta.setUnbreakable(true);
        item.setItemMeta(meta);
    }

    /**
     * Used to check if an item has a specific NamespacedKey
     * @param item The item
     * @return True if it does, otherwise false
     */
    public static boolean hasDataKey(ItemStack item) {
        if (item.getItemMeta() != null) {
            return item.getItemMeta().getPersistentDataContainer().has(DisplayEntityEditor.toolKey, PersistentDataType.STRING);
        }
        return false;
    }

    /**
     * Used to get the specific tools type
     * @param item The item
     * @return The tool type
     */
    public static String getToolValue(ItemStack item) {
        if (item.getItemMeta() != null) {
            return item.getItemMeta().getPersistentDataContainer().get(DisplayEntityEditor.toolKey, PersistentDataType.STRING);
        }
        return null;
    }

    /**
     * Used to add a new namespacedKey to an entity
     * @param entity The entity
     * @param dataKey The key
     * @param dataValue The value
     * @implNote Yes, I am aware that PersistentDataType.BOOLEAN exists, but I was getting NoSuchField exceptions, so I chose the path of least resistance
     */
    public static void setData(Display entity, String dataKey, boolean dataValue) {
        entity.getPersistentDataContainer().set(new NamespacedKey(DisplayEntityEditor.getPlugin(), dataKey), PersistentDataType.STRING, Boolean.toString(dataValue));
    }

    /**
     * Used to get data stored in an entity
     * @param entity The entity
     * @param dataKey The key
     * @implNote Yes, I am aware that PersistentDataType.BOOLEAN exists, but I was getting NoSuchField exceptions, so I chose the path of least resistance
     */
    public static boolean getData(Display entity, String dataKey) {
        String b = entity.getPersistentDataContainer().get(new NamespacedKey(DisplayEntityEditor.getPlugin(), dataKey), PersistentDataType.STRING);
        return b == null || b.equals("true");
    }

    /**
     * Used to get a string representation of an RGB color
     * @param color The color
     * @return The string representation
     */
    public static String getColor(Color color) {
        if (color == null) {
            return DisplayEntityEditor.messageManager.getString("none");
        }
        return DisplayEntityEditor.messageManager.getString("rgb").formatted(color.getRed(), color.getBlue(), color.getGreen());
    }

    /**
     * Used to format an info message for chat
     * @param message The raw message
     * @return The formatted message
     */
    public static String getInfoMessageFormat(String message) {
        return ChatColor.translateAlternateColorCodes('&', DisplayEntityEditor.messageManager.getString("info_message_format").formatted(message));
    }

    /**
     * Used to format an error message for chat
     * @param message The raw message
     * @return The formatted message
     */
    public static String getErrorMessageFormat(String message) {
        return ChatColor.translateAlternateColorCodes('&', DisplayEntityEditor.messageManager.getString("error_message_format").formatted(message));
    }

    /**
     * Used to get the nearest display entity
     * @param location The location from where the nearest display entity should be gotten
     * @param lockSearchToggle If this method should look for locked or unlocked entities. If true, it will only look for unlocked entities, and if false it will only look for locked ones
     * @return The nearest display entity or null if none were found
     */
    @Nullable
    public static Display getNearestDisplayEntity(Location location, boolean lockSearchToggle) {
        Display entity = null;
        double distance = 5;
        assert location.getWorld() != null;
        for (Entity e : location.getWorld().getNearbyEntities(location, 5, 5, 5)) {
            if (e instanceof Display d) {
                if (lockSearchToggle) {
                    if (!d.getScoreboardTags().contains("dee:locked")) {
                        double dis = d.getLocation().distance(location);
                        if (dis < distance) {
                            entity = d;
                            distance = dis;
                        }
                    }
                } else {
                    if (d.getScoreboardTags().contains("dee:locked")) {
                        double dis = d.getLocation().distance(location);
                        if (dis < distance) {
                            entity = d;
                            distance = dis;
                        }
                    }
                }
            }
        }
        return entity;
    }

    public static BaseComponent[] getCommandMessage(String commandMessage, String hint) {
        TextComponent click = new TextComponent(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', DisplayEntityEditor.messageManager.getString("command_message").formatted(commandMessage, hint)));
        click.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/deeditor edit " + commandMessage));

        return new ComponentBuilder(click).create();
    }

    public static void sendActionbarMessage(Player p, String message) {
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(net.md_5.bungee.api.ChatColor.DARK_AQUA + message));
    }

    public static BaseComponent[] getClipboardMessage(String messageKey, String clipboardContent) {
        TextComponent text = new TextComponent(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', DisplayEntityEditor.messageManager.getString(messageKey)));

        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(clipboardContent)));
        text.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, clipboardContent));
        return new ComponentBuilder(text).create();
    }

    public static String getObjectNameMessage(Object object) {
        return switch (object) {
            case Boolean b -> DisplayEntityEditor.messageManager.getList("boolean").get(b ? 0 : 1);
            case Display.Billboard b -> DisplayEntityEditor.messageManager.getList("billboard").get(b.ordinal());
            case TextDisplay.TextAlignment t -> DisplayEntityEditor.messageManager.getList("text_alignment").get(t.ordinal());
            case ItemDisplay.ItemDisplayTransform t -> DisplayEntityEditor.messageManager.getList("item_display_transform").get(t.ordinal());
            case LockSearchMode m -> DisplayEntityEditor.messageManager.getList("lock_search_mode").get(m.ordinal());
            case SelectionMode m -> DisplayEntityEditor.messageManager.getList("selection_mode").get(m.index());
            case null, default -> "";
        };
    }

    public static SelectionMode getToolSelectMode(Player p) {
        return SelectionMode.get(p.getPersistentDataContainer().getOrDefault(DisplayEntityEditor.toolSelectionModeKey, PersistentDataType.STRING, "nearby"));
    }

    public static LockSearchMode getToolSearchMode(Player p) {
        return LockSearchMode.valueOf(p.getPersistentDataContainer().getOrDefault(DisplayEntityEditor.toolSelectionSearchModeKey, PersistentDataType.STRING, "UNLOCKED"));
    }

    public static boolean getToolSelectMultiple(Player p) {
        return p.getPersistentDataContainer().getOrDefault(DisplayEntityEditor.toolSelectionMultipleKey, PersistentDataType.BOOLEAN, false);
    }

    public static float getToolSelectRange(Player p) {
        return p.getPersistentDataContainer().getOrDefault(DisplayEntityEditor.toolSelectionRangeKey, PersistentDataType.DOUBLE, 5d).floatValue();
    }

    public static float getToolPrecision(Player p) {
        Double i = p.getPersistentDataContainer().get(DisplayEntityEditor.toolPrecisionKey, PersistentDataType.DOUBLE);
        return i != null ? i.floatValue() : 1;
    }

    public static Location getAverageLocation(Collection<Display> displays) {
        if (displays == null || displays.isEmpty()) {
            return null;
        }
        double x = 0, y = 0, z = 0;
        for (Display d : displays) {
            x += d.getLocation().getX();
            y += d.getLocation().getY();
            z += d.getLocation().getZ();
        }
        return new Location(displays.iterator().next().getWorld(), x / displays.size(), y / displays.size(), z / displays.size());
    }

    public static void rotateGroup(Collection<Display> displays, Location pivot, float angle, boolean horizontal, Player player) {
        if (displays == null || displays.isEmpty() || pivot == null) {
            return;
        }
        double rad = Math.toRadians(angle);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        float playerYaw = player.getLocation().getYaw();
        double axisX = Math.cos(Math.toRadians(playerYaw));
        double axisZ = Math.sin(Math.toRadians(playerYaw));

        for (Display d : displays) {
            Location loc = d.getLocation();
            double x = loc.getX() - pivot.getX();
            double y = loc.getY() - pivot.getY();
            double z = loc.getZ() - pivot.getZ();

            if (horizontal) {
                // Rotate around global Y axis
                double newX = x * cos - z * sin;
                double newZ = x * sin + z * cos;
                loc.setX(pivot.getX() + newX);
                loc.setZ(pivot.getZ() + newZ);
                loc.setYaw(loc.getYaw() + angle);
            } else {
                // Rotate around an axis perpendicular to player's view (Rodriguez rotation formula)
                double dot = x * axisX + z * axisZ;
                double crossX = -y * axisZ;
                double crossY = x * axisZ - z * axisX;
                double crossZ = y * axisX;

                double newX = x * cos + crossX * sin + axisX * dot * (1 - cos);
                double newY = y * cos + crossY * sin;
                double newZ = z * cos + crossZ * sin + axisZ * dot * (1 - cos);

                loc.setX(pivot.getX() + newX);
                loc.setY(pivot.getY() + newY);
                loc.setZ(pivot.getZ() + newZ);

                // Apply the configured pitch step directly. Rotating the direction vector around
                // the player's view axis makes the result depend on the display's current yaw
                // and can also change its yaw unexpectedly. Minecraft limits pitch to +/-90°.
                loc.setPitch(Location.normalizePitch(loc.getPitch() + angle));
            }
            d.teleport(loc);
        }
    }

    public static String reduceFloatLength(String s) {
        return s.substring(0, Math.min(s.length(), 4));
    }
}
