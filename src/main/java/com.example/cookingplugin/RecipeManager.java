package com.example.cookingplugin;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class RecipeManager {

    private final CookingPlugin plugin;
    private final Map<String, Recipe> recipes = new HashMap<>();
    private File configFile;
    private FileConfiguration config;

    public RecipeManager(CookingPlugin plugin) {
        this.plugin = plugin;
        loadRecipes();
    }

    public void loadRecipes() {
        configFile = new File(plugin.getDataFolder(), "recipes.yml");
        if (!configFile.exists()) {
            try {
                if (configFile.createNewFile()) {
                    plugin.getLogger().info("成功创建空的 recipes.yml 文件。");
                }
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "无法创建 recipes.yml 文件！", e);
            }
        }

        config = YamlConfiguration.loadConfiguration(configFile);
        ConfigurationSection recipesSection = config.getConfigurationSection("recipes");
        if (recipesSection != null) {
            System.out.println("[CookingDebug] 开始从 recipes.yml 加载配方...");
            for (String key : recipesSection.getKeys(false)) {
                String path = "recipes." + key;
                String name = config.getString(path + ".name");
                System.out.println("[CookingDebug] 正在加载配方: " + name);
                int hunger = config.getInt(path + ".hunger");
                double saturation = config.getDouble(path + ".saturation");
                PotionEffectType effectType = config.contains(path + ".effect.type") ? PotionEffectType.getByName(config.getString(path + ".effect.type")) : null;
                int effectLevel = config.getInt(path + ".effect.level", 1);
                int effectDuration = config.getInt(path + ".effect.duration", 0);

                List<?> rawIngredients = config.getList(path + ".ingredients");
                List<ItemStack> ingredients = new ArrayList<>();
                if (rawIngredients != null) {
                    for (Object obj : rawIngredients) {
                        if (obj instanceof ItemStack) {
                            ItemStack rawItem = (ItemStack) obj;
                            ItemStack cleanItem = new ItemStack(rawItem.getType(), rawItem.getAmount(), rawItem.getDurability());
                            ingredients.add(cleanItem);
                            System.out.println("[CookingDebug]   - 加载材料: " + cleanItem.getType() + ":" + cleanItem.getDurability() + " x" + cleanItem.getAmount());
                        }
                    }
                }

                Recipe recipe = new Recipe(name, hunger, saturation, effectType, effectLevel, effectDuration, ingredients);
                recipes.put(name.toLowerCase(), recipe);
            }
            System.out.println("[CookingDebug] 所有配方加载完毕。");
        }
    }

    public void saveRecipe(Recipe recipe) {
        String path = "recipes." + recipe.getName().toLowerCase();
        config.set(path + ".name", recipe.getName());
        config.set(path + ".hunger", recipe.getHunger());
        config.set(path + ".saturation", recipe.getSaturation());
        if (recipe.getEffectType() != null) {
            config.set(path + ".effect.type", recipe.getEffectType().getName());
            config.set(path + ".effect.level", recipe.getEffectLevel());
            config.set(path + ".effect.duration", recipe.getEffectDuration());
        }

        List<ItemStack> cleanIngredients = new ArrayList<>();
        for (ItemStack ingredient : recipe.getIngredients()) {
            cleanIngredients.add(new ItemStack(ingredient.getType(), ingredient.getAmount(), ingredient.getDurability()));
        }
        config.set(path + ".ingredients", cleanIngredients);

        try {
            config.save(configFile);
            recipes.put(recipe.getName().toLowerCase(), recipe);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "无法保存配方到 recipes.yml", e);
        }
    }

    public Recipe getRecipe(String name) {
        return recipes.get(name.toLowerCase());
    }
}