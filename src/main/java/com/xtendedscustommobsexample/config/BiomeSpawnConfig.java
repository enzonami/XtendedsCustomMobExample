package com.xtendedscustommobsexample.config;

import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.swamp.SwampBiome;
import necesse.level.maps.biomes.snow.SnowBiome;
import necesse.level.maps.biomes.desert.DesertBiome;

public class BiomeSpawnConfig {
    
    public static void configureDarknessSpawnRates() {
        Biome.defaultSurfaceMobs.add(30, "darkness");
        Biome.defaultCaveMobs.add(50, "darkness");
        Biome.defaultDeepCaveMobs.add(75, "darkness");
        
        SwampBiome.surfaceMobs.add(35, "darkness");
        SnowBiome.surfaceMobs.add(15, "darkness");
        DesertBiome.surfaceMobs.add(10, "darkness");
        
        SwampBiome.caveMobs.add(45, "darkness");
        SnowBiome.caveMobs.add(30, "darkness");
        DesertBiome.caveMobs.add(25, "darkness");
        
        SwampBiome.deepSwampCaveMobs.add(60, "darkness");
        SnowBiome.deepSnowCaveMobs.add(40, "darkness");
        DesertBiome.deepDesertCaveMobs.add(35, "darkness");
    }
}