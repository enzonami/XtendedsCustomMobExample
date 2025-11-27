package com.xtendedscustommobsexample.mobs;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.GameLog;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import com.xtendedscustommobsexample.textures.DarknessMobTextures;

import necesse.engine.sound.SoundSettings;

import necesse.entity.mobs.GameDamage;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;

import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobSpawnLocation;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.ConfusedCollisionPlayerChaserWandererAI;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

@SuppressWarnings("unchecked")
public class DarknessMob extends HostileMob {
    public static LootTable lootTable = new LootTable(HostileMob.randomMapDrop);
    
    public static GameDamage baseDamage = new GameDamage(65.0f);
    public static GameDamage incursionDamage = new GameDamage(85.0f);

    public DarknessMob() {
        super(300);
        this.setSpeed(30.0f);
        this.setFriction(2.0f);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-12, -14, 24, 24);
        this.selectBox = new Rectangle(-16, -24, 32, 32);
        this.swimMaskMove = 8;
        this.swimMaskOffset = 28;
        this.swimSinkOffset = 0;
    }

    @Override
    public void init() {
        super.init();
        GameDamage damage = this.getLevel() != null && this.getLevel().isIncursionLevel ? incursionDamage : baseDamage;
        this.ai = new BehaviourTreeAI<DarknessMob>(this, new ConfusedCollisionPlayerChaserWandererAI(null, 200, damage, 100, 40000));
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        int i = 0;
        while (i < 4) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), DarknessMobTextures.darkness, i, 2, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
            ++i;
        }
        return;
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public boolean isValidSpawnLocation(Server server, ServerClient client, int targetX, int targetY) {
        MobSpawnLocation location = new MobSpawnLocation(this, targetX, targetY).checkMobSpawnLocation();
        location = this.getLevel().isCave ? location.checkLightThreshold(client) : location.checkMaxStaticLightThreshold(5);
        return location.validAndApply();
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (DarknessMobTextures.darkness == null) {
            return;
        }
        
        GameLight light = level.getLightLevel(getTileCoordinate(x), getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 16;
        int drawY = camera.getDrawY(y) - 26;

        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        
        final TextureDrawOptionsEnd options = DarknessMobTextures.darkness.initDraw().sprite(sprite.x, dir, 64).light(light).pos(drawX, drawY);
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }



    @Override
    public int getRockSpeed() {
        return 10;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Stream<ModifierValue<?>> getDefaultModifiers() {
        if (this.getLevel() != null && this.getLevel().isCave) {
            return Stream.<ModifierValue<?>>of(
                new ModifierValue<Float>(BuffModifiers.SPEED, Float.valueOf(1.2f)), 
                new ModifierValue<Float>(BuffModifiers.CHASER_RANGE, Float.valueOf(2.5f))
            );
        }
        return super.getDefaultModifiers();
    }

    @Override
    protected SoundSettings getHurtSound() {
        return new SoundSettings(GameResources.hurt);
    }

    @Override
    public Point getAnimSprite(int x, int y, int dir) {
        Point p = new Point(0, 0);
        p.x = this.inLiquid(x, y) ? 5 : (Math.abs(this.dx) <= 0.01f && Math.abs(this.dy) <= 0.01f ? 0 : (int)(this.getDistanceRan() / (double)this.getRockSpeed()) % 7);
        return p;
    }

    @Override
    public GameMessage getLocalization() {
        return new LocalMessage("mob", "darkness");
    }
}