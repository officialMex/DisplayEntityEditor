package goldenshadow.displayentityeditor.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import goldenshadow.displayentityeditor.DisplayEntityEditor;
import goldenshadow.displayentityeditor.Utilities;
import goldenshadow.displayentityeditor.conversation.InputData;
import goldenshadow.displayentityeditor.conversation.InputManager;
import goldenshadow.displayentityeditor.enums.InputType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

/**
 * Brigadier command handler for the DisplayEntityEditor plugin.
 * Manages all command registration and execution for editing display entities.
 * Handles inventory management and command processing for players.
 */
@SuppressWarnings("UnstableApiUsage")
public class DisplayEntityEditorBrigadierCommand {

    /** Stores saved player inventories mapped by player UUID for later restoration */
    private final HashMap<UUID, ItemStack[]> savedInventories = new HashMap<>();

    /**
     * Creates and registers the main displayentityeditor command with all subcommands.
     *
     * @return the root LiteralCommandNode for the displayentityeditor command
     */
    public LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("displayentityeditor")
                .executes(ctx -> {
                    Player p = getPlayerOrFail(ctx);
                    if (p == null) return 0;

                    if (savedInventories.containsKey(p.getUniqueId())) {
                        returnInventory(p);
                        p.sendMessage(Utilities.getInfoMessageFormat(DisplayEntityEditor.messageManager.getString("inventory_returned")));
                        return Command.SINGLE_SUCCESS;
                    }

                    saveInventory(p);
                    ItemStack[] array = DisplayEntityEditor.inventoryFactory.getInventoryArray(p);
                    for (int i = 0; i < array.length; i++) {
                        p.getInventory().setItem(i, array[i]);
                    }
                    if (!p.getPersistentDataContainer().has(DisplayEntityEditor.toolPrecisionKey, PersistentDataType.DOUBLE)) {
                        p.getPersistentDataContainer().set(DisplayEntityEditor.toolPrecisionKey, PersistentDataType.DOUBLE, 1d);
                    }
                    p.sendMessage(Utilities.getInfoMessageFormat(DisplayEntityEditor.messageManager.getString("tools_received_1")));
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', DisplayEntityEditor.messageManager.getString("tools_received_2")));
                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.literal("reload")
                        .executes(ctx -> {
                            Player p = getPlayerOrFail(ctx);
                            if (p == null) return 0;

                            try {
                                DisplayEntityEditor.getPlugin().reloadConfig();
                                DisplayEntityEditor.alternateTextInput = DisplayEntityEditor.getPlugin().getConfig().getBoolean("alternate-text-input");
                                DisplayEntityEditor.useMiniMessageFormat = DisplayEntityEditor.getPlugin().getConfig().getBoolean("use-minimessage-format");
                                DisplayEntityEditor.checkForMessageFile();
                                p.sendMessage(Utilities.getInfoMessageFormat(DisplayEntityEditor.messageManager.getString("config_reload")));
                            } catch (IOException e) {
                                p.sendMessage(Utilities.getErrorMessageFormat(DisplayEntityEditor.messageManager.getString("messages_reload_fail")));
                            }
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(Commands.literal("edit")
                        .then(editCommand("name", InputType.NAME, false))
                        .then(editCommand("text", InputType.TEXT, true))
                        .then(editCommand("text_append", InputType.TEXT_APPEND, true))
                        .then(editCommand("background_color", InputType.BACKGROUND_COLOR, true))
                        .then(editCommand("glow_color", InputType.GLOW_COLOR, false))
                        .then(editCommand("block_state", InputType.BLOCK_STATE, false))
                        .then(floatCommand("view_range", InputType.VIEW_RANGE, null, null))
                        .then(floatCommand("display_height", InputType.DISPLAY_HEIGHT, null, null))
                        .then(floatCommand("display_width", InputType.DISPLAY_WIDTH, null, null))
                        .then(floatCommand("shadow_radius", InputType.SHADOW_RADIUS, null, null))
                        .then(floatCommand("shadow_strength", InputType.SHADOW_STRENGTH, 0f, 1f))
                        .then(byteCommand("text_opacity", InputType.TEXT_OPACITY, true))
                        .then(byteCommand("background_opacity", InputType.BACKGROUND_OPACITY, true))
                        .then(integerCommand("line_width", InputType.LINE_WIDTH, true)))
                .build();
    }

    /**
     * Creates a text input command node for editing string-based display properties.
     *
     * @param name the command literal name
     * @param type the input type enum specifying which property is being edited
     * @param textDisplayOnly if true, the command only works with TextDisplay entities
     * @return the configured LiteralCommandNode
     */
    private LiteralCommandNode<CommandSourceStack> editCommand(String name, InputType type, boolean textDisplayOnly) {
        return Commands.literal(name)
                .then(Commands.argument("value", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            // Validate player is online and alternate text input is enabled
                            Player p = getPlayerOrFail(ctx);
                            if (p == null || !DisplayEntityEditor.alternateTextInput) return 0;

                            // Get the input string and nearest display entity
                            String input = StringArgumentType.getString(ctx, "value");
                            Display display = Utilities.getNearestDisplayEntity(p.getLocation(), true);

                            // Validate display entity type requirements
                            if (display == null || (textDisplayOnly && !(display instanceof TextDisplay))) {
                                p.sendMessage(Utilities.getErrorMessageFormat(DisplayEntityEditor.messageManager.getString("generic_fail")));
                                return 0;
                            }

                            // Extract additional data if needed (e.g., block material for block states)
                            Material extra = (type == InputType.BLOCK_STATE && display instanceof BlockDisplay bd) ? bd.getBlock().getMaterial() : Material.AIR;
                            InputManager.successfulTextInput(new InputData(display, type, extra), input, p);
                            return Command.SINGLE_SUCCESS;
                        }))
                .build();
    }

    /**
     * Creates a float input command node for editing float-based display properties.
     * Optionally enforces minimum and maximum value constraints.
     *
     * @param name the command literal name
     * @param type the input type enum specifying which property is being edited
     * @param min the minimum allowed value, or null for no minimum
     * @param max the maximum allowed value, or null for no maximum
     * @return the configured LiteralCommandNode
     */
    private LiteralCommandNode<CommandSourceStack> floatCommand(String name, InputType type, Float min, Float max) {
        // Build argument type with optional min/max constraints
        FloatArgumentType argType = (min != null && max != null) ? FloatArgumentType.floatArg(min, max) : FloatArgumentType.floatArg();
        return Commands.literal(name)
                .then(Commands.argument("value", argType)
                        .executes(ctx -> {
                            // Validate player is online and alternate text input is enabled
                            Player p = getPlayerOrFail(ctx);
                            if (p == null || !DisplayEntityEditor.alternateTextInput) return 0;

                            // Get float value and nearest display entity
                            float value = FloatArgumentType.getFloat(ctx, "value");
                            Display display = Utilities.getNearestDisplayEntity(p.getLocation(), true);

                            // Ensure a valid display entity was found
                            if (display == null) {
                                p.sendMessage(Utilities.getErrorMessageFormat(DisplayEntityEditor.messageManager.getString("generic_fail")));
                                return 0;
                            }

                            // Process the float input through the input manager
                            InputManager.successfulFloatInput(new InputData(display, type, null), value, p);
                            return Command.SINGLE_SUCCESS;
                        }))
                .build();
    }

    /**
     * Creates a byte input command node for editing byte-based display properties.
     * Accepts integer values between Byte.MIN_VALUE and Byte.MAX_VALUE.
     *
     * @param name the command literal name
     * @param type the input type enum specifying which property is being edited
     * @param textDisplayOnly if true, the command only works with TextDisplay entities
     * @return the configured LiteralCommandNode
     */
    private LiteralCommandNode<CommandSourceStack> byteCommand(String name, InputType type, boolean textDisplayOnly) {
        return Commands.literal(name)
                .then(Commands.argument("value", IntegerArgumentType.integer(Byte.MIN_VALUE, Byte.MAX_VALUE))
                        .executes(ctx -> {
                            // Validate player is online and alternate text input is enabled
                            Player p = getPlayerOrFail(ctx);
                            if (p == null || !DisplayEntityEditor.alternateTextInput) return 0;

                            // Get byte value and nearest display entity
                            int value = IntegerArgumentType.getInteger(ctx, "value");
                            Display display = Utilities.getNearestDisplayEntity(p.getLocation(), true);

                            // Validate display entity type requirements
                            if (display == null || (textDisplayOnly && !(display instanceof TextDisplay))) {
                                p.sendMessage(Utilities.getErrorMessageFormat(DisplayEntityEditor.messageManager.getString("generic_fail")));
                                return 0;
                            }

                            // Process the byte input through the input manager
                            InputManager.successfulByteInput(new InputData(display, type, null), value, p);
                            return Command.SINGLE_SUCCESS;
                        }))
                .build();
    }

    /**
     * Creates an integer input command node for editing integer-based display properties.
     *
     * @param name the command literal name
     * @param type the input type enum specifying which property is being edited
     * @param textDisplayOnly if true, the command only works with TextDisplay entities
     * @return the configured LiteralCommandNode
     */
    private LiteralCommandNode<CommandSourceStack> integerCommand(String name, InputType type, boolean textDisplayOnly) {
        return Commands.literal(name)
                .then(Commands.argument("value", IntegerArgumentType.integer())
                        .executes(ctx -> {
                            // Validate player is online and alternate text input is enabled
                            Player p = getPlayerOrFail(ctx);
                            if (p == null || !DisplayEntityEditor.alternateTextInput) return 0;

                            // Get integer value and nearest display entity
                            int value = IntegerArgumentType.getInteger(ctx, "value");
                            Display display = Utilities.getNearestDisplayEntity(p.getLocation(), true);

                            // Validate display entity type requirements
                            if (display == null || (textDisplayOnly && !(display instanceof TextDisplay))) {
                                p.sendMessage(Utilities.getErrorMessageFormat(DisplayEntityEditor.messageManager.getString("generic_fail")));
                                return 0;
                            }

                            // Process the integer input through the input manager
                            InputManager.successfulIntegerInput(new InputData(display, type, null), value, p);
                            return Command.SINGLE_SUCCESS;
                        }))
                .build();
    }

    /**
     * Extracts the Player from the command context and validates it.
     * Sends an error message if the command sender is not a player.
     *
     * @param ctx the brigadier command context
     * @return the Player if the sender is a player, or null if validation fails
     */
    private Player getPlayerOrFail(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx) {
        // Validate the command sender is an online player
        if (!(ctx.getSource().getSender() instanceof Player p)) {
            ctx.getSource().getSender().sendMessage(DisplayEntityEditor.messageManager.getString("none_player_fail"));
            return null;
        }
        return p;
    }

    /**
     * Saves a player's current inventory to storage for later restoration.
     * Clears the player's inventory after saving.
     *
     * @param player the player whose inventory should be saved
     */
    private void saveInventory(Player player) {
        // Store a clone of the current inventory contents
        savedInventories.put(player.getUniqueId(), player.getInventory().getContents().clone());
        player.getInventory().clear();
    }

    /**
     * Restores a player's previously saved inventory.
     * Does nothing if no saved inventory exists for the player.
     *
     * @param player the player whose inventory should be restored
     */
    public void returnInventory(Player player) {
        // Check if an inventory is saved for this player
        if (!savedInventories.containsKey(player.getUniqueId())) return;

        // Retrieve the saved inventory
        ItemStack[] saved = savedInventories.get(player.getUniqueId());
        player.getInventory().clear();

        // Restore all items from the saved inventory
        for (int i = 0; i < saved.length; i++) {
            player.getInventory().setItem(i, saved[i]);
        }

        // Remove the saved inventory from storage
        savedInventories.remove(player.getUniqueId());
    }
}