package com.xtendedscustommobsexample.textures;

import necesse.gfx.gameTexture.GameTexture;

public class DarknessMobTextures {
    public static GameTexture darkness;
    
    public static void load() {
        try {
            darkness = GameTexture.fromFile("mobs/darknesscustom");
        } catch (Exception e) {
        }
    }
}