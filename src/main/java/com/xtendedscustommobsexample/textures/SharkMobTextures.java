package com.xtendedscustommobsexample.textures;

import necesse.gfx.gameTexture.GameTexture;
import necesse.engine.GameLog;

public class SharkMobTextures {
    public static GameTexture shark;
    public static GameTexture sharkAttack;
    
    public static void load() {
        try {
            shark = GameTexture.fromFile("mobs/sharkcustom");
            sharkAttack = GameTexture.fromFile("mobs/sharkattackcustom");
        } catch (Exception e) {
            GameLog.err.println("Error loading shark textures: " + e.getMessage());
        }
    }
}