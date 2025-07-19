package com.example.cookingplugin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public final class CookingPlugin extends JavaPlugin {

    private RecipeManager recipeManager;
    private CookingManager cookingManager;
    private LanguageManager languageManager;
    private final Map<UUID, PendingRecipe> pendingRecipes = new HashMap<>();

    private File playersFile;
    private FileConfiguration playersConfig;

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            if (!getDataFolder().mkdirs()) {
                getLogger().log(Level.SEVERE, "无法创建插件数据文件夹！");
            }
        }

        saveDefaultConfig();
        createPlayersConfig();

        recipeManager = new RecipeManager(this);
        cookingManager = new CookingManager();
        languageManager = new LanguageManager(this);

        this.getCommand("cook").setExecutor(new CookCommand(this));
        this.getCommand("cooklang").setExecutor(new CookLangCommand(this));

        this.getServer().getPluginManager().registerEvents(new RecipeListener(this), this);
        this.getServer().getPluginManager().registerEvents(new CookingListener(this), this);
        this.getServer().getPluginManager().registerEvents(new FoodListener(), this);
        this.getServer().getPluginManager().registerEvents(new BlockListener(), this);

        getLogger().info("CookingPlugin has been enabled!");
    }

    @Override
    public void onDisable() {
        languageManager.savePlayerLanguages();
        getLogger().info("CookingPlugin has been disabled!");
    }

    public RecipeManager getRecipeManager() {
        return recipeManager;
    }

    public CookingManager getCookingManager() {
        return cookingManager;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public Map<UUID, PendingRecipe> getPendingRecipes() {
        return pendingRecipes;
    }

    private void createPlayersConfig() {
        playersFile = new File(getDataFolder(), "players.yml");
        if (!playersFile.exists()) {
            try {
                if (!playersFile.createNewFile()) {
                    getLogger().log(Level.SEVERE, "无法创建 players.yml 文件！");
                }
            } catch (IOException e) {
                getLogger().log(Level.SEVERE, "创建 players.yml 时出错！", e);
            }
        }
        playersConfig = YamlConfiguration.loadConfiguration(playersFile);
    }

    public FileConfiguration getPlayersConfig() {
        return playersConfig;
    }

    public void savePlayersConfig() {
        try {
            playersConfig.save(playersFile);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "无法保存 players.yml 文件！", e);
        }
    }
}