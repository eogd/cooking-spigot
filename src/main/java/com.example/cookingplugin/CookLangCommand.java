package com.example.cookingplugin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CookLangCommand implements CommandExecutor {

    private final CookingPlugin plugin;

    public CookLangCommand(CookingPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "只有玩家才能使用此命令。");
            return true;
        }

        if (args.length != 1 || (!args[0].equalsIgnoreCase("cn") && !args[0].equalsIgnoreCase("en"))) {
            sender.sendMessage(ChatColor.RED + "用法: /cooklang <cn|en>");
            return true;
        }

        Player player = (Player) sender;
        String lang = args[0].toLowerCase();
        plugin.getLanguageManager().setLanguage(player.getUniqueId(), lang);

        updatePlayerInventory(player, lang);

        String langName = "cn".equals(lang) ? "中文" : "English";
        player.sendMessage(ChatColor.GREEN + "语言已切换为: " + langName);
        return true;
    }

    private void updatePlayerInventory(Player player, String lang) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.PAPER && item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta.hasEnchant(Enchantment.LUCK) && meta.hasDisplayName() && meta.getDisplayName().startsWith(ChatColor.GOLD + "配方: ")) {
                    String recipeName = ChatColor.stripColor(meta.getDisplayName().substring("配方: ".length()));
                    Recipe recipe = plugin.getRecipeManager().getRecipe(recipeName);
                    if (recipe != null) {
                        RecipeListener.updateRecipePaper(item, recipe, plugin.getLanguageManager(), lang);
                    }
                }
            }
        }
        player.updateInventory();
    }
}