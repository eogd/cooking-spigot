package com.example.cookingplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CookingGUI {

    public static final String COOKING_GUI_TITLE = "烹饪台";
    public static final int RECIPE_SLOT = 7;
    public static final int FUEL_SLOT = 37;
    public static final int START_BUTTON_SLOT = 23;
    public static final int OUTPUT_SLOT = 25;
    public static final int[] INGREDIENT_SLOTS = {1, 2, 3, 10, 11, 12, 19, 20, 21};
    public static final int[] PROGRESS_BAR_SLOTS = {45, 46, 47, 48, 49, 50, 51, 52, 53};

    public static Inventory createCookingGUI() {
        Inventory gui = Bukkit.createInventory(null, 54, COOKING_GUI_TITLE);

        ItemStack grayPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
        ItemMeta grayMeta = grayPane.getItemMeta();
        grayMeta.setDisplayName(" ");
        grayPane.setItemMeta(grayMeta);

        ItemStack orangePane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 1);
        ItemMeta orangeMeta = orangePane.getItemMeta();
        orangeMeta.setDisplayName(ChatColor.GOLD + "开始烹饪");
        orangePane.setItemMeta(orangeMeta);

        ItemStack greenPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 13);
        ItemMeta greenMeta = greenPane.getItemMeta();
        greenMeta.setDisplayName(ChatColor.GREEN + "进度");
        greenPane.setItemMeta(greenMeta);

        for (int i = 0; i < gui.getSize(); i++) {
            gui.setItem(i, grayPane);
        }

        for (int slot : INGREDIENT_SLOTS) {
            gui.setItem(slot, null);
        }

        gui.setItem(RECIPE_SLOT, null);
        gui.setItem(FUEL_SLOT, null);
        gui.setItem(OUTPUT_SLOT, null);
        gui.setItem(START_BUTTON_SLOT, orangePane);

        for (int slot : PROGRESS_BAR_SLOTS) {
            gui.setItem(slot, greenPane);
        }
        return gui;
    }
}