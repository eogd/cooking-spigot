package com.example.cookingplugin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class CookingManager {

    private final Set<Inventory> cookingInventories = new HashSet<>();

    public boolean isCooking(Inventory inventory) {
        return cookingInventories.contains(inventory);
    }

    public void startCooking(Inventory inventory, CookingPlugin plugin) {
        System.out.println("[CookingDebug] 调用 startCooking 方法。");

        if (isCooking(inventory)) {
            System.out.println("[CookingDebug] 失败：已在烹饪中。");
            return;
        }

        ItemStack recipePaper = inventory.getItem(CookingGUI.RECIPE_SLOT);
        String prefixWithColor = ChatColor.GOLD + "配方:";
        if (recipePaper == null || recipePaper.getType() != Material.PAPER || !recipePaper.hasItemMeta() || !recipePaper.getItemMeta().getDisplayName().startsWith(prefixWithColor)) {
            System.out.println("[CookingDebug] 失败：配方纸不存在或无效。");
            return;
        }
        System.out.println("[CookingDebug] 成功：找到配方纸。");

        String recipeName = ChatColor.stripColor(recipePaper.getItemMeta().getDisplayName().substring(prefixWithColor.length())).trim();

        System.out.println("[CookingDebug] 正在查找配方: " + recipeName);
        Recipe recipe = plugin.getRecipeManager().getRecipe(recipeName);
        if (recipe == null) {
            System.out.println("[CookingDebug] 失败：在 RecipeManager 中找不到配方 '" + recipeName + "'。");
            return;
        }
        System.out.println("[CookingDebug] 成功：找到配方 '" + recipe.getName() + "'。");
        System.out.println("[CookingDebug] 配方所需材料:");
        for (ItemStack item : recipe.getIngredients()) {
            System.out.println("[CookingDebug]   - " + item.getType() + ":" + item.getDurability() + " x" + item.getAmount());
        }

        List<ItemStack> providedIngredients = new ArrayList<>();
        for (int slot : CookingGUI.INGREDIENT_SLOTS) {
            if (inventory.getItem(slot) != null && inventory.getItem(slot).getType() != Material.AIR) {
                providedIngredients.add(inventory.getItem(slot));
            }
        }
        System.out.println("[CookingDebug] GUI中提供的材料:");
        for (ItemStack item : providedIngredients) {
            System.out.println("[CookingDebug]   - " + item.getType() + ":" + item.getDurability() + " x" + item.getAmount());
        }

        boolean hasEnough = hasEnoughIngredients(recipe.getIngredients(), providedIngredients);
        System.out.println("[CookingDebug] hasEnoughIngredients 的结果: " + hasEnough);
        if (!hasEnough) {
            System.out.println("[CookingDebug] 失败：材料不足。");
            return;
        }

        ItemStack fuel = inventory.getItem(CookingGUI.FUEL_SLOT);
        if (fuel == null || fuel.getType() != Material.COAL) {
            System.out.println("[CookingDebug] 失败：找不到燃料（煤炭）或燃料不正确。");
            return;
        }
        System.out.println("[CookingDebug] 成功：找到燃料。");

        ItemStack outputSlotItem = inventory.getItem(CookingGUI.OUTPUT_SLOT);
        if (outputSlotItem != null && outputSlotItem.getType() != Material.AIR) {
            System.out.println("[CookingDebug] 失败：产出槽不为空。");
            return;
        }
        System.out.println("[CookingDebug] 成功：产出槽为空。");
        System.out.println("[CookingDebug] 所有检查通过，开始烹饪流程。");

        consumeIngredients(inventory, recipe.getIngredients());
        fuel.setAmount(fuel.getAmount() - 1);
        inventory.setItem(CookingGUI.FUEL_SLOT, fuel.getAmount() > 0 ? fuel : null);
        cookingInventories.add(inventory);

        new BukkitRunnable() {
            int progress = 0;
            final int totalTime = 10;
            @Override
            public void run() {
                if (progress >= totalTime) {
                    ItemStack result = new ItemStack(Material.MUSHROOM_SOUP);
                    FoodListener.setFoodProperties(result, recipe.getName(), recipe.getHunger(), recipe.getSaturation(), recipe.getEffectType(), recipe.getEffectLevel(), recipe.getEffectDuration());
                    inventory.setItem(CookingGUI.OUTPUT_SLOT, result);
                    cookingInventories.remove(inventory);
                    if (plugin.getConfig().getBoolean("settings.consume-recipe-on-use", false)) {
                        inventory.setItem(CookingGUI.RECIPE_SLOT, null);
                    }
                    for (int slot : CookingGUI.PROGRESS_BAR_SLOTS) {
                        inventory.setItem(slot, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 13));
                    }
                    cancel();
                    return;
                }
                int filledSlots = (int) Math.ceil(((double) (progress + 1) / totalTime) * CookingGUI.PROGRESS_BAR_SLOTS.length);
                for (int i = 0; i < CookingGUI.PROGRESS_BAR_SLOTS.length; i++) {
                    if (i < filledSlots) {
                        inventory.setItem(CookingGUI.PROGRESS_BAR_SLOTS[i], new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5));
                    } else {
                        inventory.setItem(CookingGUI.PROGRESS_BAR_SLOTS[i], new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 13));
                    }
                }
                progress++;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private boolean hasEnoughIngredients(List<ItemStack> requiredList, List<ItemStack> providedList) {
        System.out.println("[CookingDebug] 调用 hasEnoughIngredients 方法。");
        Map<String, Integer> requiredMap = new HashMap<>();
        for (ItemStack item : requiredList) {
            String key = item.getType().name() + ":" + item.getDurability();
            requiredMap.put(key, requiredMap.getOrDefault(key, 0) + item.getAmount());
        }
        System.out.println("[CookingDebug] Required Map: " + requiredMap);

        Map<String, Integer> providedMap = new HashMap<>();
        for (ItemStack item : providedList) {
            String key = item.getType().name() + ":" + item.getDurability();
            providedMap.put(key, providedMap.getOrDefault(key, 0) + item.getAmount());
        }
        System.out.println("[CookingDebug] Provided Map: " + providedMap);

        for (Map.Entry<String, Integer> entry : requiredMap.entrySet()) {
            if (providedMap.getOrDefault(entry.getKey(), 0) < entry.getValue()) {
                System.out.println("[CookingDebug] Map 比较失败: 需要 " + entry.getKey() + " x" + entry.getValue() + ", 但只提供了 " + providedMap.getOrDefault(entry.getKey(), 0));
                return false;
            }
        }
        return true;
    }

    private void consumeIngredients(Inventory inventory, List<ItemStack> requiredList) {
        for (ItemStack requiredItem : requiredList) {
            int amountToConsume = requiredItem.getAmount();
            for (int slot : CookingGUI.INGREDIENT_SLOTS) {
                if (amountToConsume <= 0) break;
                ItemStack inventoryItem = inventory.getItem(slot);
                if (inventoryItem == null || inventoryItem.getType() == Material.AIR) continue;
                if (inventoryItem.getType() == requiredItem.getType() && inventoryItem.getDurability() == requiredItem.getDurability()) {
                    int amountInSlot = inventoryItem.getAmount();
                    int consumeFromThisSlot = Math.min(amountToConsume, amountInSlot);
                    amountToConsume -= consumeFromThisSlot;
                    inventoryItem.setAmount(amountInSlot - consumeFromThisSlot);
                    if (inventoryItem.getAmount() <= 0) {
                        inventory.setItem(slot, null);
                    }
                }
            }
        }
    }
}
