package com.example.cookingplugin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RecipeListener implements Listener {

    private final CookingPlugin plugin;

    public RecipeListener(CookingPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getView().getTitle().equals(CookCommand.RECIPE_CREATION_GUI_TITLE)) {
            return;
        }

        Player player = (Player) event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (!plugin.getPendingRecipes().containsKey(playerUUID)) {
            return;
        }

        PendingRecipe pendingRecipe = plugin.getPendingRecipes().remove(playerUUID);
        Inventory inventory = event.getInventory();
        List<ItemStack> ingredients = Arrays.stream(inventory.getContents())
                .filter(item -> item != null && item.getType() != Material.AIR)
                .collect(Collectors.toList());

        if (ingredients.isEmpty()) {
            player.sendMessage(ChatColor.RED + "配方创建已取消，因为没有添加任何材料。");
            return;
        }

        Recipe recipe = new Recipe(
                pendingRecipe.getName(),
                pendingRecipe.getHunger(),
                pendingRecipe.getSaturation(),
                pendingRecipe.getEffectType(),
                pendingRecipe.getEffectLevel(),
                pendingRecipe.getEffectDuration(),
                ingredients
        );

        plugin.getRecipeManager().saveRecipe(recipe);

        ItemStack recipePaper = new ItemStack(Material.PAPER, 1);
        String lang = plugin.getLanguageManager().getLanguage(playerUUID);
        updateRecipePaper(recipePaper, recipe, plugin.getLanguageManager(), lang);

        player.getInventory().addItem(recipePaper);
        player.sendMessage(ChatColor.GREEN + "配方 " + ChatColor.YELLOW + recipe.getName() + ChatColor.GREEN + " 创建成功！");
    }

    public static void updateRecipePaper(ItemStack recipePaper, Recipe recipe, LanguageManager langManager, String lang) {
        ItemMeta meta = recipePaper.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "配方:" + recipe.getName());
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "所需材料:");
        for (ItemStack ingredient : recipe.getIngredients()) {
            String materialName = langManager.getTranslatedName(ingredient, lang);
            lore.add(ChatColor.AQUA + "- " + materialName + ChatColor.GRAY + " x" + ingredient.getAmount());
        }
        meta.setLore(lore);
        meta.addEnchant(Enchantment.LUCK, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        recipePaper.setItemMeta(meta);
    }
}
