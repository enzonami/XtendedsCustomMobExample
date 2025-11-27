package com.xtendedscustommobsexample.registry;

import com.xtendedscustommobsexample.mobs.DarknessMob;
import com.xtendedscustommobsexample.mobs.CustomSwampSlimeMob;
import necesse.engine.registries.MobRegistry;

public class MobRegistryHandler {
    
    public static void registerMobs() {
        MobRegistry.registerMob("darkness", DarknessMob.class, true);
    }
    
    public static void replaceSwampSlime() {
        try {
            java.lang.reflect.Method replaceMethod = MobRegistry.instance.getClass().getSuperclass().getDeclaredMethod("replace", String.class, Object.class);
            replaceMethod.setAccessible(true);
            
            Class<?> mobRegistryElementClass = Class.forName("necesse.engine.registries.MobRegistry$MobRegistryElement");
            java.lang.reflect.Constructor<?> constructor = mobRegistryElementClass.getDeclaredConstructor(
                Class.class, boolean.class, boolean.class, boolean.class, 
                necesse.engine.localization.message.GameMessage.class, necesse.engine.localization.message.GameMessage.class
            );
            constructor.setAccessible(true);
            
            Object customElement = constructor.newInstance(
                CustomSwampSlimeMob.class,
                true,
                false,
                true,
                new necesse.engine.localization.message.LocalMessage("mob", "swampslime"),
                null
            );
            
            replaceMethod.invoke(MobRegistry.instance, "swampslime", customElement);
        } catch (Exception e) {
        }
    }
}