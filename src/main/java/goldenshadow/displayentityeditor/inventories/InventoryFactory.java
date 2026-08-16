package goldenshadow.displayentityeditor.inventories;

import goldenshadow.displayentityeditor.DisplayEntityEditor;
import goldenshadow.displayentityeditor.Utilities;
import goldenshadow.displayentityeditor.items.GUIItems;
import goldenshadow.displayentityeditor.items.InventoryItems;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public record InventoryFactory(GUIItems guiItems, InventoryItems inventoryItems) {

    /**
    /**
     * Getter for the gui items
     * @return The class containing all gui items
     */
    public GUIItems getGuiItems() {
        return this.guiItems;
    }

    public InventoryItems getInventoryItems() {
        return this.inventoryItems;
    }

    /**
     * Used to create the gui for item displays
     * @param entity The item display entity being edited
     * @return The gui
     */
    public Inventory createItemDisplayGUI(ItemDisplay entity) {
        Inventory inventory = Bukkit.createInventory(null, 27, ChatColor.translateAlternateColorCodes('&', DisplayEntityEditor.messageManager.getString("item_display_gui_name")));
        for (int i = 0; i < inventory.getSize(); i++) {
            switch (i) {
                case 4 -> inventory.setItem(i, this.guiItems.name(entity.getCustomName()));
                case 5 -> inventory.setItem(i, this.guiItems.rightRotNormalize(Utilities.getData(entity, "GUIRRNormalize")));
                case 6 -> inventory.setItem(i, this.guiItems.height(entity.getDisplayHeight()));
                case 7 -> inventory.setItem(i, this.guiItems.shadowRadius(entity.getShadowRadius()));
                case 8 -> inventory.setItem(i, this.guiItems.skyLight(entity.getBrightness() != null ? entity.getBrightness().getSkyLight() : -1));

                case 10 -> inventory.setItem(i, entity.getItemStack());
                case 12 -> inventory.setItem(i, this.guiItems.itemDisplayTransform(entity.getItemDisplayTransform()));
                case 13 -> inventory.setItem(i, this.guiItems.glowing(entity.isGlowing()));
                case 14 -> inventory.setItem(i, this.guiItems.leftRotNormalize(Utilities.getData(entity, "GUILRNormalize")));
                case 15 -> inventory.setItem(i, this.guiItems.width(entity.getDisplayWidth()));
                case 16 -> inventory.setItem(i, this.guiItems.shadowStrength(entity.getShadowStrength()));
                case 17 -> inventory.setItem(i, this.guiItems.blockLight(entity.getBrightness() != null ? entity.getBrightness().getBlockLight() : -1));

                case 22 -> inventory.setItem(i, this.guiItems.glowColor(entity.getGlowColorOverride()));
                case 23 -> inventory.setItem(i, this.guiItems.viewRange(entity.getViewRange()));
                case 24 -> inventory.setItem(i, this.guiItems.billboard(entity.getBillboard()));
                case 25 -> inventory.setItem(i, this.guiItems.lock());
                case 26 -> inventory.setItem(i, this.guiItems.delete());
                default -> inventory.setItem(i, this.guiItems.filler());
            }
        }
        return inventory;
    }

    /**
     * Used to create the gui for block displays
     * @param entity The block display entity being edited
     * @return The gui
     */
    public Inventory createBlockDisplayGUI(BlockDisplay entity) {
        Inventory inventory = Bukkit.createInventory(null, 27, ChatColor.translateAlternateColorCodes('&', DisplayEntityEditor.messageManager.getString("block_display_gui_name")));
        for (int i = 0; i < inventory.getSize(); i++) {
            switch (i) {
                case 4 -> inventory.setItem(i, this.guiItems.name(entity.getCustomName()));
                case 5 -> inventory.setItem(i, this.guiItems.rightRotNormalize(Utilities.getData(entity, "GUIRRNormalize")));
                case 6 -> inventory.setItem(i, this.guiItems.height(entity.getDisplayHeight()));
                case 7 -> inventory.setItem(i, this.guiItems.shadowRadius(entity.getShadowRadius()));
                case 8 -> inventory.setItem(i, this.guiItems.skyLight(entity.getBrightness() != null ? entity.getBrightness().getSkyLight() : -1));

                case 10 -> inventory.setItem(i, new ItemStack(entity.getBlock().getMaterial()));
                case 11 -> inventory.setItem(i, this.guiItems.blockState(entity.getBlock().getAsString(true)));

                case 13 -> inventory.setItem(i, this.guiItems.glowing(entity.isGlowing()));
                case 14 -> inventory.setItem(i, this.guiItems.leftRotNormalize(Utilities.getData(entity, "GUILRNormalize")));
                case 15 -> inventory.setItem(i, this.guiItems.width(entity.getDisplayWidth()));
                case 16 -> inventory.setItem(i, this.guiItems.shadowStrength(entity.getShadowStrength()));
                case 17 -> inventory.setItem(i, this.guiItems.blockLight(entity.getBrightness() != null ? entity.getBrightness().getBlockLight() : -1));

                case 22 -> inventory.setItem(i, this.guiItems.glowColor(entity.getGlowColorOverride()));
                case 23 -> inventory.setItem(i, this.guiItems.viewRange(entity.getViewRange()));
                case 24 -> inventory.setItem(i, this.guiItems.billboard(entity.getBillboard()));
                case 25 -> inventory.setItem(i, this.guiItems.lock());
                case 26 -> inventory.setItem(i, this.guiItems.delete());
                default -> inventory.setItem(i, this.guiItems.filler());
            }
        }
        return inventory;
    }

    /**
     * Used to create the gui for text displays
     * @param entity The text display entity being edited
     * @return The gui
     */
    @SuppressWarnings("deprecation")
    public Inventory createTextDisplayGUI(TextDisplay entity) {
        Inventory inventory = Bukkit.createInventory(null, 27, ChatColor.translateAlternateColorCodes('&', DisplayEntityEditor.messageManager.getString("text_display_gui_name")));
        for (int i = 0; i < inventory.getSize(); i++) {
            switch (i) {
                case 2 -> inventory.setItem(i, this.guiItems.textBackgroundColor(entity.getBackgroundColor()));
                case 3 -> inventory.setItem(i, this.guiItems.textDefaultBackground(entity.isDefaultBackground()));
                case 4 -> inventory.setItem(i, this.guiItems.name(entity.getCustomName()));
                case 5 -> inventory.setItem(i, this.guiItems.rightRotNormalize(Utilities.getData(entity, "GUIRRNormalize")));
                case 6 -> inventory.setItem(i, this.guiItems.height(entity.getDisplayHeight()));
                case 7 -> inventory.setItem(i, this.guiItems.shadowRadius(entity.getShadowRadius()));
                case 8 -> inventory.setItem(i, this.guiItems.skyLight(entity.getBrightness() != null ? entity.getBrightness().getSkyLight() : -1));

                case 10 -> inventory.setItem(i, this.guiItems.text());
                case 11 -> inventory.setItem(i, this.guiItems.textBackgroundOpacity(entity.getBackgroundColor()));
                case 12 -> inventory.setItem(i, this.guiItems.textSeeThrough(entity.isSeeThrough()));
                case 13 -> inventory.setItem(i, this.guiItems.textOpacity(entity.getTextOpacity()));
                case 14 -> inventory.setItem(i, this.guiItems.leftRotNormalize(Utilities.getData(entity, "GUILRNormalize")));
                case 15 -> inventory.setItem(i, this.guiItems.width(entity.getDisplayWidth()));
                case 16 -> inventory.setItem(i, this.guiItems.shadowStrength(entity.getShadowStrength()));
                case 17 -> inventory.setItem(i, this.guiItems.blockLight(entity.getBrightness() != null ? entity.getBrightness().getBlockLight() : -1));

                case 20 -> inventory.setItem(i, this.guiItems.textAlignment(entity.getAlignment()));
                case 21 -> inventory.setItem(i, this.guiItems.textShadow(entity.isShadowed()));
                case 22 -> inventory.setItem(i, this.guiItems.textLineWidth(entity.getLineWidth()));
                case 23 -> inventory.setItem(i, this.guiItems.viewRange(entity.getViewRange()));
                case 24 -> inventory.setItem(i, this.guiItems.billboard(entity.getBillboard()));
                case 25 -> inventory.setItem(i, this.guiItems.lock());
                case 26 -> inventory.setItem(i, this.guiItems.delete());
                default -> inventory.setItem(i, this.guiItems.filler());
            }
        }
        return inventory;
    }

    /**
     * Used to generate an array of tools to be easily added to a players inventory
     * @return An array of tools
     */
    public ItemStack[] getInventoryArray(Player p) {
        ItemStack[] array = new ItemStack[36];

        array[0] = this.inventoryItems.gui();
        array[1] = this.inventoryItems.cloneTool();
        array[2] = this.inventoryItems.groupSelectTool(p);
        array[3] = this.inventoryItems.toolSearchMode();
        array[4] = this.inventoryItems.toolSelectionMode(p);
        array[6] = this.inventoryItems.toolSelectionMultiple();
        array[5] = this.inventoryItems.toolSelectionRange(p);
        array[7] = this.inventoryItems.toolPrecision(p);

        array[27] = this.inventoryItems.spawnItemDisplay();
        array[28] = this.inventoryItems.spawnBlockDisplay();
        array[29] = this.inventoryItems.spawnTextDisplay();
        array[30] = this.inventoryItems.moveX(p);
        array[31] = this.inventoryItems.moveY(p);
        array[32] = this.inventoryItems.moveZ(p);
        array[33] = this.inventoryItems.rotateYaw(p);
        array[34] = this.inventoryItems.rotatePitch(p);

        array[18] = this.inventoryItems.translationX(p);
        array[19] = this.inventoryItems.translationY(p);
        array[20] = this.inventoryItems.translationZ(p);
        array[21] = this.inventoryItems.scaleX(p);
        array[22] = this.inventoryItems.scaleY(p);
        array[23] = this.inventoryItems.scaleZ(p);
        array[24] = this.inventoryItems.highlightTarget();
        array[25] = this.inventoryItems.unlock();

        array[9] = this.inventoryItems.leftRotationX(p);
        array[10] = this.inventoryItems.leftRotationY(p);
        array[11] = this.inventoryItems.leftRotationZ(p);
        array[12] = this.inventoryItems.rightRotationX(p);
        array[13] = this.inventoryItems.rightRotationY(p);
        array[14] = this.inventoryItems.rightRotationZ(p);
        array[15] = this.inventoryItems.centerPivot();
        array[16] = this.inventoryItems.centerOnBlock();

        return array;
    }
}
