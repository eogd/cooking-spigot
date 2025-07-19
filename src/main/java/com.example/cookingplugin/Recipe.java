package com.example.cookingplugin;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Recipe {

    private final String name;
    private final int hunger;
    private final double saturation;
    private final PotionEffectType effectType;
    private final int effectLevel;
    private final int effectDuration;
    private final List<ItemStack> ingredients;

    public Recipe(String name, int hunger, double saturation, PotionEffectType effectType, int effectLevel, int effectDuration, List<ItemStack> ingredients) {
        this.name = name;
        this.hunger = hunger;
        this.saturation = saturation;
        this.effectType = effectType;
        this.effectLevel = effectLevel;
        this.effectDuration = effectDuration;
        this.ingredients = ingredients;
    }

    public String getName() {
        return name;
    }

    public int getHunger() {
        return hunger;
    }

    public double getSaturation() {
        return saturation;
    }

    public PotionEffectType getEffectType() {
        return effectType;
    }

    public int getEffectLevel() {
        return effectLevel;
    }

    public int getEffectDuration() {
        return effectDuration;
    }

    public List<ItemStack> getIngredients() {
        return ingredients;
    }
}