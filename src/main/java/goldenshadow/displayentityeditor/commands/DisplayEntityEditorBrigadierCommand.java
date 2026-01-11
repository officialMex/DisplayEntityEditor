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
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class DisplayEntityEditorBrigadierCommand {

    private static final HashMap<UUID, ItemStack[]> savedInventories = new HashMap<>();

    public static LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("displayentityeditor")
                .executes(context -> {
                    if (!(context.getSource().getSender() instanceof Player p)) {
                        context.getSource().getSender().sendMessage(DisplayEntityEditor.messageManager.getString("none_player_fail"));
                        return 0;
                    }

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
                        .executes(context -> {
                            if (!(context.getSource().getSender() instanceof Player p)) {
                                context.getSource().getSender().sendMessage(DisplayEntityEditor.messageManager.getString("none_player_fail"));
                                return 0;
                            }

                            DisplayEntityEditor.getPlugin().reloadConfig();
                            DisplayEntityEditor.alternateTextInput = DisplayEntityEditor.getPlugin().getConfig().getBoolean("alternate-text-input");
                            DisplayEntityEditor.useMiniMessageFormat = DisplayEntityEditor.getPlugin().getConfig().getBoolean("use-minimessage-format");
                            try {
                                DisplayEntityEditor.checkForMessageFile();
                            } catch (IOException e) {
                                p.sendMessage(Utilities.getErrorMessageFormat(DisplayEntityEditor.messageManager.getString("messages_reload_fail")));
                            }
                            p.sendMessage(Utilities.getInfoMessageFormat(DisplayEntityEditor.messageManager.getString("config_reload")));
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(Commands.literal("edit")
                        .then(Commands.literal("name")
                                .then(Commands.argument("value", StringArgumentType.greedyString())
                                        .executes(context -> {
                                            if (!(context.getSource().getSender() instanceof Player p)) {
                                                return 0;
                                            }
                                            if (!DisplayEntityEditor.alternateTextInput) {
                                                return 0;
                                            }
                                            String input = StringArgumentType.getString(context, "value");
                                            Display display = Utilities.getNearestDisplayEntity(p.getLocation(), true);
                                            if (display == null) {
                                                p.sendMessage(Utilities.getErrorMessageFormat(DisplayEntityEditor.messageManager.getString("generic_fail")));
                                                return 0;
                                            }
                                            InputManager.successfulTextInput(new InputData(display, InputType.NAME, null), input, p);
                                            return Command.SINGLE_SUCCESS;
                                        })))
                        .then(Commands.literal("text")
                                .then(Commands.argument("value", StringArgumentType.greedyString())
                                        .executes(context -> {
                                            if (!(context.getSource().getSender() instanceof Player p)) {
                                                return 0;
                                            }
                                            if (!DisplayEntityEditor.alternateTextInput) {
                                                return 0;
                                            }
                                            String input = StringArgumentType.getString(context, "value");
                                            Display display = Utilities.getNearestDisplayEntity(p.getLocation(), true);
                                            if (display == null || !(display instanceof TextDisplay)) {
                                                p.sendMessage(Utilities.getErrorMessageFormat(DisplayEntityEditor.messageManager.getString("generic_fail")));
                                                return 0;
                                            }
                                            InputManager.successfulTextInput(new InputData(display, InputType.TEXT, null), input, p);
                                            return Command.SINGLE_SUCCESS;
                                        })))
                        .then(Commands.literal("text_append")
                                .then(Commands.argument("value", StringArgumentType.greedyString())
                                        .executes(context -> {
                                            if (!(context.getSource().getSender() instanceof Player p)) {
                                                return 0;
                                            }
                                            if (!DisplayEntityEditor.alternateTextInput) {
                                                return 0;
                                            }
                                            String input = StringArgumentType.getString(context, "value");
                                            Display display = Utilities.getNearestDisplayEntity(p.getLocation(), true);
                                            if (display == null || !(display instanceof TextDisplay)) {
                                                p.sendMessage(Utilities.getErrorMessageFormat(DisplayEntityEditor.messageManager.getString("generic_fail")));
                                                return 0;
                                            }
                                            InputManager.successfulTextInput(new InputData(display, InputType.TEXT_APPEND, null), input, p);
                                            return Command.SINGLE_SUCCESS;
                                        })))
                        .then(Commands.literal("background_color")
                                .then(Commands.argument("value", StringArgumentType.greedyString())
                                        .executes(context -> {
                                            if (!(context.getSource().getSender() instanceof Player p)) {
                                                return 0;
                                            }
                                            if (!DisplayEntityEditor.alternateTextInput) {
                                                return 0;
                                            }
                                            String input = StringArgumentType.getString(context, "value");
                                            Display display = Utilities.getNearestDisplayEntity(p.getLocation(), true);
                                            if (display == null || !(display instanceof TextDisplay)) {
                                                p.sendMessage(Utilities.getErrorMessageFormat(DisplayEntityEditor.messageManager.getString("generic_fail")));
                                                return 0;
                                            }
                                            InputManager.successfulTextInput(new InputData(display, InputType.BACKGROUND_COLOR, null), input, p);
                                            return Command.SINGLE_SUCCESS;
                                        })))
                        .then(Commands.literal("glow_color")
                                .then(Commands.argument("value", StringArgumentType.greedyString())
                                        .executes(context -> {
                                            if (!(context.getSource().getSender() instanceof Player p)) {
                                                return 0;
                                            }
                                            if (!DisplayEntityEditor.alternateTextInput) {
                                                return 0;
                                            }
                                            String input = StringArgumentType.getString(context, "value");
                                            Display display = Utilities.getNearestDisplayEntity(p.getLocation(), true);
                                            if (display == null || display instanceof TextDisplay) {
                                                p.sendMessage(Utilities.getErrorMessageFormat(DisplayEntityEditor.messageManager.getString("generic_fail")));
                                                return 0;
                                            }
                                            InputManager.successfulTextInput(new InputData(display, InputType.GLOW_COLOR, null), input, p);
                                            return Command.SINGLE_SUCCESS;
                                        })))
                        .then(Commands.literal("block_state")
                                .then(Commands.argument("value", StringArgumentType.greedyString())
                                        .executes(context -> {
                                            if (!(context.getSource().getSender() instanceof Player p)) {
                                                return 0;
                                            }
                                            if (!DisplayEntityEditor.alternateTextInput) {
                                                return 0;
                                            }
                                            String input = StringArgumentType.getString(context, "value");
                                            Display display = Utilities.getNearestDisplayEntity(p.getLocation(), true);
                                            if (display == null || !(display instanceof BlockDisplay)) {
                                                p.sendMessage(Utilities.getErrorMessageFormat(DisplayEntityEditor.messageManager.getString("generic_fail")));
                                                return 0;
                                            }
                                            InputManager.successfulTextInput(new InputData(display, InputType.BLOCK_STATE, ((BlockDisplay) display).getBlock().getMaterial()), input, p);
                                            return Command.SINGLE_SUCCESS;
                                        })))
                        .then(Commands.literal("view_range")
                                .then(Commands.argument("value", FloatArgumentType.floatArg())
                                        .executes(context -> {
                                            if (!(context.getSource().getSender() instanceof Player p)) {
                                                return 0;
                                            }
                                            if (!DisplayEntityEditor.alternateTextInput) {
                                                return 0;
                                            }
                                            float value = FloatArgumentType.getFloat(context, "value");
                                            Display display = Utilities.getNearestDisplayEntity(p.getLocation(), true);
                                            if (display == null) {
                                                p.sendMessage(Utilities.getErrorMessageFormat(DisplayEntityEditor.messageManager.getString("generic_fail")));
                                                return 0;
                                            }
                                            InputManager.successfulFloatInput(new InputData(display, InputType.VIEW_RANGE, null), value, p);
                                            return Command.SINGLE_SUCCESS;
                                        })))
                        .then(Commands.literal("display_height")
                                .then(Commands.argument("value", FloatArgumentType.floatArg())
                                        .executes(context -> {
                                            if (!(context.getSource().getSender() instanceof Player p)) {
                                                return 0;
                                            }
                                            if (!DisplayEntityEditor.alternateTextInput) {
                                                return 0;
                                            }
                                            float value = FloatArgumentType.getFloat(context, "value");
                                            Display display = Utilities.getNearestDisplayEntity(p.getLocation(), true);
                                            if (display == null) {
                                                p.sendMessage(Utilities.getErrorMessageFormat(DisplayEntityEditor.messageManager.getString("generic_fail")));
                                                return 0;
                                            }
                                            InputManager.successfulFloatInput(new InputData(display, InputType.DISPLAY_HEIGHT, null), value, p);
                                            return Command.SINGLE_SUCCESS;
                                        })))
                        .then(Commands.literal("display_width")
                                .then(Commands.argument("value", FloatArgumentType.floatArg())
                                        .executes(context -> {
                                            if (!(context.getSource().getSender() instanceof Player p)) {
                                                return 0;
                                            }
                                            if (!DisplayEntityEditor.alternateTextInput) {
                                                return 0;
                                            }
                                            float value = FloatArgumentType.getFloat(context, "value");
                                            Display display = Utilities.getNearestDisplayEntity(p.getLocation(), true);
                                            if (display == null) {
                                                p.sendMessage(Utilities.getErrorMessageFormat(DisplayEntityEditor.messageManager.getString("generic_fail")));
                                                return 0;
                                            }
                                            InputManager.successfulFloatInput(new InputData(display, InputType.DISPLAY_WIDTH, null), value, p);
                                            return Command.SINGLE_SUCCESS;
                                        })))
                        .then(Commands.literal("shadow_radius")
                                .then(Commands.argument("value", FloatArgumentType.floatArg())
                                        .executes(context -> {
                                            if (!(context.getSource().getSender() instanceof Player p)) {
                                                return 0;
                                            }
                                            if (!DisplayEntityEditor.alternateTextInput) {
                                                return 0;
                                            }
                                            float value = FloatArgumentType.getFloat(context, "value");
                                            Display display = Utilities.getNearestDisplayEntity(p.getLocation(), true);
                                            if (display == null) {
                                                p.sendMessage(Utilities.getErrorMessageFormat(DisplayEntityEditor.messageManager.getString("generic_fail")));
                                                return 0;
                                            }
                                            InputManager.successfulFloatInput(new InputData(display, InputType.SHADOW_RADIUS, null), value, p);
                                            return Command.SINGLE_SUCCESS;
                                        })))
                        .then(Commands.literal("shadow_strength")
                                .then(Commands.argument("value", FloatArgumentType.floatArg(0f, 1f))
                                        .executes(context -> {
                                            if (!(context.getSource().getSender() instanceof Player p)) {
                                                return 0;
                                            }
                                            if (!DisplayEntityEditor.alternateTextInput) {
                                                return 0;
                                            }
                                            float value = FloatArgumentType.getFloat(context, "value");
                                            Display display = Utilities.getNearestDisplayEntity(p.getLocation(), true);
                                            if (display == null) {
                                                p.sendMessage(Utilities.getErrorMessageFormat(DisplayEntityEditor.messageManager.getString("generic_fail")));
                                                return 0;
                                            }
                                            InputManager.successfulFloatInput(new InputData(display, InputType.SHADOW_STRENGTH, null), value, p);
                                            return Command.SINGLE_SUCCESS;
                                        })))
                        .then(Commands.literal("text_opacity")
                                .then(Commands.argument("value", IntegerArgumentType.integer(Byte.MIN_VALUE, Byte.MAX_VALUE))
                                        .executes(context -> {
                                            if (!(context.getSource().getSender() instanceof Player p)) {
                                                return 0;
                                            }
                                            if (!DisplayEntityEditor.alternateTextInput) {
                                                return 0;
                                            }
                                            int value = IntegerArgumentType.getInteger(context, "value");
                                            Display display = Utilities.getNearestDisplayEntity(p.getLocation(), true);
                                            if (display == null || !(display instanceof TextDisplay)) {
                                                p.sendMessage(Utilities.getErrorMessageFormat(DisplayEntityEditor.messageManager.getString("generic_fail")));
                                                return 0;
                                            }
                                            InputManager.successfulByteInput(new InputData(display, InputType.TEXT_OPACITY, null), value, p);
                                            return Command.SINGLE_SUCCESS;
                                        })))
                        .then(Commands.literal("background_opacity")
                                .then(Commands.argument("value", IntegerArgumentType.integer(Byte.MIN_VALUE, Byte.MAX_VALUE))
                                        .executes(context -> {
                                            if (!(context.getSource().getSender() instanceof Player p)) {
                                                return 0;
                                            }
                                            if (!DisplayEntityEditor.alternateTextInput) {
                                                return 0;
                                            }
                                            int value = IntegerArgumentType.getInteger(context, "value");
                                            Display display = Utilities.getNearestDisplayEntity(p.getLocation(), true);
                                            if (display == null || !(display instanceof TextDisplay)) {
                                                p.sendMessage(Utilities.getErrorMessageFormat(DisplayEntityEditor.messageManager.getString("generic_fail")));
                                                return 0;
                                            }
                                            InputManager.successfulByteInput(new InputData(display, InputType.BACKGROUND_OPACITY, null), value, p);
                                            return Command.SINGLE_SUCCESS;
                                        })))
                        .then(Commands.literal("line_width")
                                .then(Commands.argument("value", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            if (!(context.getSource().getSender() instanceof Player p)) {
                                                return 0;
                                            }
                                            if (!DisplayEntityEditor.alternateTextInput) {
                                                return 0;
                                            }
                                            int value = IntegerArgumentType.getInteger(context, "value");
                                            Display display = Utilities.getNearestDisplayEntity(p.getLocation(), true);
                                            if (display == null || !(display instanceof TextDisplay)) {
                                                p.sendMessage(Utilities.getErrorMessageFormat(DisplayEntityEditor.messageManager.getString("generic_fail")));
                                                return 0;
                                            }
                                            InputManager.successfulIntegerInput(new InputData(display, InputType.LINE_WIDTH, null), value, p);
                                            return Command.SINGLE_SUCCESS;
                                        }))))
                .build();
    }

    /**
     * A utility method used to save a players inventory in order to be able to return it later
     * @param player The player whose inventory should be saved
     */
    private static void saveInventory(Player player) {
        savedInventories.put(player.getUniqueId(), player.getInventory().getContents().clone());
        player.getInventory().clear();
    }

    /**
     * A utility method used to return a players inventory
     * @param player The player whose inventory should be returned
     */
    public static void returnInventory(Player player) {
        if (!savedInventories.containsKey(player.getUniqueId())) return;
        player.getInventory().clear();
        ItemStack[] saved = savedInventories.get(player.getUniqueId());
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            player.getInventory().setItem(i, saved[i]);
        }
        savedInventories.remove(player.getUniqueId());
    }
}
