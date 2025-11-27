package com.xtendedscustommobsexample;

import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.GameLog;
import com.xtendedscustommobsexample.registry.MobRegistryHandler;
import com.xtendedscustommobsexample.config.BiomeSpawnConfig;
import com.xtendedscustommobsexample.textures.DarknessMobTextures;
import com.xtendedscustommobsexample.textures.SwampSlimeTextures;

@ModEntry
public class XtendedsCustomMobsExampleMod {
    
    public void init() {
        MobRegistryHandler.registerMobs();
        MobRegistryHandler.replaceSwampSlime();
    }
    
    public void initResources() {
        DarknessMobTextures.load();
        SwampSlimeTextures.load();
    }
    
    public void postInit() {
        BiomeSpawnConfig.configureDarknessSpawnRates();
    }
}