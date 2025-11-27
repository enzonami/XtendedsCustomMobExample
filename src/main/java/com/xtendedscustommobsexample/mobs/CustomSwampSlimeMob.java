package com.xtendedscustommobsexample.mobs;

import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import com.xtendedscustommobsexample.textures.SwampSlimeTextures;

import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.hostile.SwampSlimeMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;

import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class CustomSwampSlimeMob extends SwampSlimeMob {
    
    public CustomSwampSlimeMob() {
        super();
    }
    
    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(getTileCoordinate(x), getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 16;
        int drawY = camera.getDrawY(y) - 26;
        
        int spriteX = 0;
        
        final TextureDrawOptionsEnd options = SwampSlimeTextures.swampslime.initDraw().sprite(spriteX, 0, 32).light(light).pos(drawX, drawY);
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }
    
    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        int i = 0;
        while (i < 4) {
            this.getLevel().entityManager.addParticle(new necesse.entity.particle.FleshParticle(this.getLevel(), SwampSlimeTextures.swampslime, i, 2, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), necesse.entity.particle.Particle.GType.IMPORTANT_COSMETIC);
            ++i;
        }
    }
}