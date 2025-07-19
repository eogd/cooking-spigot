package com.example.cookingplugin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class FoodListener implements Listener {

    private static final String LORE_PREFIX = ChatColor.BLACK.toString() + ChatColor.DARK_PURPLE;
    private static final String HUNGER_KEY = "H:";
    private static final String SATURATION_KEY = "S:";
    private static final String EFFECT_KEY = "E:";

    public static void setFoodProperties(ItemStack item, String name, int hunger, double saturation, PotionEffectType effectType, int effectLevel, int effectDuration) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + name);

        List<String> lore = new ArrayList<>();
        lore.add(LORE_PREFIX + HUNGER_KEY + hunger);
        lore.add(LORE_PREFIX + SATURATION_KEY + saturation);
        if (effectType != null) {
            String effectData = effectType.getName() + ";" + effectLevel + ";" + effectDuration;
            lore.add(LORE_PREFIX + EFFECT_KEY + effectData);
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasLore()) {
            return;
        }

        List<String> lore = item.getItemMeta().getLore();
        int hunger = 0;
        float saturation = 0;
        PotionEffect effect = null;
        boolean isCustomFood = false;

        for (String line : lore) {
            if (line.startsWith(LORE_PREFIX)) {
                isCustomFood = true;
                String data = ChatColor.stripColor(line.substring(LORE_PREFIX.length()));

                try {
                    if (data.startsWith(HUNGER_KEY)) {
                        hunger = Integer.parseInt(data.substring(HUNGER_KEY.length()));
                    } else if (data.startsWith(SATURATION_KEY)) {
                        saturation = Float.parseFloat(data.substring(SATURATION_KEY.length()));
                    } else if (data.startsWith(EFFECT_KEY)) {
                        String[] parts = data.substring(EFFECT_KEY.length()).split(";");
                        if (parts.length == 3) {
                            PotionEffectType type = PotionEffectType.getByName(parts[0]);
                            int level = Integer.parseInt(parts[1]);
                            int duration = Integer.parseInt(parts[2]);
                            if (type != null) {
                                effect = new PotionEffect(type, duration, level - 1, true, true);
                            }
                        }
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }

        if (isCustomFood) {
            Player player = event.getPlayer();
            player.setFoodLevel(Math.min(20, player.getFoodLevel() + hunger));
            player.setSaturation(Math.min(player.getFoodLevel(), player.getSaturation() + saturation));

            if (effect != null) {
                player.addPotionEffect(effect, true);
            }
        }
    }
}