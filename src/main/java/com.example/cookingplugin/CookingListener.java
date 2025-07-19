package com.example.cookingplugin;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CookingListener implements Listener {

    private final CookingPlugin plugin;

    public CookingListener(CookingPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(CookingGUI.COOKING_GUI_TITLE)) {
            return;
        }

        int clickedSlot = event.getRawSlot();

        if (clickedSlot >= 0 && clickedSlot < event.getInventory().getSize()) {
            event.setCancelled(true);
        }

        if (isClickableSlot(clickedSlot)) {
            event.setCancelled(false);
        }

        if (clickedSlot == CookingGUI.START_BUTTON_SLOT) {
            System.out.println("[CookingDebug] 检测到开始烹饪按钮点击。");
            plugin.getCookingManager().startCooking(event.getInventory(), plugin);
        }

        if (clickedSlot == CookingGUI.OUTPUT_SLOT) {
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
                event.setCancelled(false);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getView().getTitle().equals(CookingGUI.COOKING_GUI_TITLE)) {
            return;
        }

        Inventory inventory = event.getInventory();
        if (plugin.getCookingManager().isCooking(inventory)) {
            return;
        }

        Player player = (Player) event.getPlayer();

        for (int slot : CookingGUI.INGREDIENT_SLOTS) {
            returnItem(player, inventory.getItem(slot));
        }
        returnItem(player, inventory.getItem(CookingGUI.FUEL_SLOT));
        returnItem(player, inventory.getItem(CookingGUI.RECIPE_SLOT));

        inventory.clear();
    }

    private void returnItem(Player player, ItemStack item) {
        if (item != null && item.getType() != Material.AIR) {
            player.getInventory().addItem(item).values().forEach(i -> player.getWorld().dropItem(player.getLocation(), i));
        }
    }

    private boolean isClickableSlot(int slot) {
        for (int s : CookingGUI.INGREDIENT_SLOTS) {
            if (s == slot) return true;
        }
        return slot == CookingGUI.FUEL_SLOT || slot == CookingGUI.RECIPE_SLOT;
    }
}