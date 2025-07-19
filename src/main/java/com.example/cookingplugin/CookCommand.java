package com.example.cookingplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffectType;

public class CookCommand implements CommandExecutor {

    private final CookingPlugin plugin;
    public static final String RECIPE_CREATION_GUI_TITLE = "配方创建台";

    public CookCommand(CookingPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "只有玩家才能使用此命令。");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "用法: /cook <名称> <饥饿值> <饱和度> [效果] [等级] [时长]");
            return true;
        }

        Player player = (Player) sender;
        String name = args[0];
        int hunger;
        double saturation;
        PotionEffectType effectType = null;
        int effectLevel = 1;
        int effectDuration = 0;

        try {
            hunger = Integer.parseInt(args[1]);
            saturation = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "饥饿值、饱和度必须是有效的数字！");
            return true;
        }

        if (args.length >= 4) {
            effectType = PotionEffectType.getByName(args[3].toUpperCase());
            if (effectType == null) {
                player.sendMessage(ChatColor.RED + "无效的药水效果！");
                return true;
            }
        }
        if (args.length >= 5) {
            try {
                effectLevel = Integer.parseInt(args[4]);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "效果等级必须是有效的数字！");
                return true;
            }
        }
        if (args.length >= 6) {
            try {
                effectDuration = Integer.parseInt(args[5]) * 20;
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "效果时长必须是有效的数字！");
                return true;
            }
        }

        PendingRecipe pendingRecipe = new PendingRecipe(name, hunger, saturation, effectType, effectLevel, effectDuration);
        plugin.getPendingRecipes().put(player.getUniqueId(), pendingRecipe);

        Inventory recipeGUI = Bukkit.createInventory(null, 27, RECIPE_CREATION_GUI_TITLE);
        player.openInventory(recipeGUI);
        player.sendMessage(ChatColor.GREEN + "请在GUI中放入配方材料，关闭后将自动保存。");

        return true;
    }
}