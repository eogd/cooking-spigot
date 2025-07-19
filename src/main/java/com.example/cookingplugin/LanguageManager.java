package com.example.cookingplugin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LanguageManager {

    private final CookingPlugin plugin;
    private final Map<UUID, String> playerLanguages = new HashMap<>();
    private final Map<String, String> translations = new HashMap<>();
    private FileConfiguration langConfig;

    public LanguageManager(CookingPlugin plugin) {
        this.plugin = plugin;
        loadTranslations();
        loadPlayerLanguages();
    }

    private void loadTranslations() {
        File langFile = new File(plugin.getDataFolder(), "language.yml");
        if (!langFile.exists()) {
            plugin.saveResource("language.yml", false);
        }
        langConfig = YamlConfiguration.loadConfiguration(langFile);

        InputStream defStream = plugin.getResource("language.yml");
        if (defStream != null) {
            langConfig.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defStream, StandardCharsets.UTF_8)));
        }

        if (langConfig.isConfigurationSection("translations")) {
            for (String key : langConfig.getConfigurationSection("translations").getKeys(false)) {
                translations.put(key.toUpperCase(), langConfig.getString("translations." + key));
            }
        }
    }

    public void loadPlayerLanguages() {
        FileConfiguration playersConfig = plugin.getPlayersConfig();
        if (playersConfig.isConfigurationSection("players")) {
            for (String uuid : playersConfig.getConfigurationSection("players").getKeys(false)) {
                playerLanguages.put(UUID.fromString(uuid), playersConfig.getString("players." + uuid + ".language", "en"));
            }
        }
    }

    public void savePlayerLanguages() {
        FileConfiguration playersConfig = plugin.getPlayersConfig();
        for (Map.Entry<UUID, String> entry : playerLanguages.entrySet()) {
            playersConfig.set("players." + entry.getKey().toString() + ".language", entry.getValue());
        }
        plugin.savePlayersConfig();
    }

    public String getLanguage(UUID playerUUID) {
        return playerLanguages.getOrDefault(playerUUID, "en");
    }

    public void setLanguage(UUID playerUUID, String lang) {
        playerLanguages.put(playerUUID, lang);
    }

    public String getTranslatedName(ItemStack item, String lang) {
        if ("cn".equalsIgnoreCase(lang)) {
            String key = item.getType().toString();
            if (item.getDurability() != 0) {
                key += ":" + item.getDurability();
            }
            String translated = translations.get(key.toUpperCase());
            if (translated != null) {
                return translated;
            }
            translated = translations.get(item.getType().toString().toUpperCase());
            if (translated != null) {
                return translated;
            }
        }
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            return item.getItemMeta().getDisplayName();
        }
        String name = item.getType().toString().replace("_", " ").toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}