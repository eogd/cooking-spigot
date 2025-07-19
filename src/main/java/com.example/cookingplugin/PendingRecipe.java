package com.example.cookingplugin;

import org.bukkit.potion.PotionEffectType;

public class PendingRecipe {

    private final String name;
    private final int hunger;
    private final double saturation;
    private final PotionEffectType effectType;
    private final int effectLevel;
    private final int effectDuration;

    public PendingRecipe(String name, int hunger, double saturation, PotionEffectType effectType, int effectLevel, int effectDuration) {
        this.name = name;
        this.hunger = hunger;
        this.saturation = saturation;
        this.effectType = effectType;
        this.effectLevel = effectLevel;
        this.effectDuration = effectDuration;
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
}