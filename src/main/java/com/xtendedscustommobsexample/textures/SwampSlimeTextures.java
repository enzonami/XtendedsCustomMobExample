package com.xtendedscustommobsexample.textures;

import necesse.engine.registries.MobRegistry;
import necesse.gfx.gameTexture.GameTexture;

public class SwampSlimeTextures {
    public static GameTexture swampslime;
    public static GameTexture swampslime_shadow;
    
    public static void load() {
        try {
            swampslime = MobRegistry.Textures.fromFile("swampslime");
            swampslime_shadow = MobRegistry.Textures.fromFile("swampslime_shadow");
            
            if (swampslime == null || swampslime_shadow == null) {
                swampslime = GameTexture.fromFile("mobs/swampslime");
                swampslime_shadow = GameTexture.fromFile("mobs/swampslime_shadow");
            }
            
            if (swampslime == null || swampslime_shadow == null) {
                swampslime = GameTexture.fromFile("swampslime");
                swampslime_shadow = GameTexture.fromFile("swampslime_shadow");
            }
        } catch (Exception e) {
        }
    }
}