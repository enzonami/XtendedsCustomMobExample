package com.xtendedscustommobsexample.config;

import necesse.level.maps.biomes.Biome;

public class SharkSpawnConfig {
    
    public static void configureSharkSpawnRates() {
        // Sharks spawn everywhere on surface like LaserDuck, but only on water tiles
        Biome.defaultSurfaceMobs.add(50, "shark");
    }
}