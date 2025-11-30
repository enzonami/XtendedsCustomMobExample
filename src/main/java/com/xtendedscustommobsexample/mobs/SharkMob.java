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
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.networkField.BooleanNetworkField;
import com.xtendedscustommobsexample.textures.SharkMobTextures;

import necesse.engine.sound.SoundSettings;

import necesse.entity.mobs.GameDamage;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;

import necesse.entity.mobs.Mob;
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
import necesse.gfx.gameTooltips.GameTooltips;
import java.awt.Color;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.engine.registries.TileRegistry;
import necesse.level.maps.TilePosition;

@SuppressWarnings("unchecked")
public class SharkMob extends HostileMob {
    public static LootTable lootTable = new LootTable(HostileMob.randomMapDrop);
    
    public static GameDamage baseDamage = new GameDamage(40.0f);
    public static GameDamage incursionDamage = new GameDamage(55.0f);
    
    public BooleanNetworkField isAttacking;
    private long lastAttackTime = 0L;
    private long lastStateChangeTime = 0L;

    public SharkMob() {
        super(250);
        this.isAttacking = this.registerNetworkField(new BooleanNetworkField(false));
        this.setSpeed(5.0f); // Very slow on land
        this.setSwimSpeed(25.0f); // Fast in water
        this.setFriction(2.0f);
        this.collision = new Rectangle(-12, -8, 24, 16);
        this.hitBox = new Rectangle(-14, -16, 28, 28);
        this.selectBox = new Rectangle(-18, -26, 36, 36);
        this.swimMaskMove = 12;
        this.swimMaskOffset = 26;
        this.swimSinkOffset = -4; // Sinks slightly underwater
    }

    @Override
    public void init() {
        super.init();
        GameDamage damage = this.getLevel() != null && this.getLevel().isIncursionLevel ? incursionDamage : baseDamage;
        this.ai = new BehaviourTreeAI<SharkMob>(this, new ConfusedCollisionPlayerChaserWandererAI(null, 200, damage, 100, 40000));
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        int i = 0;
        while (i < 4) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), SharkMobTextures.shark, i, 2, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
            ++i;
        }
        return;
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    public int getTileWanderPriority(necesse.level.maps.TilePosition pos, necesse.level.maps.biomes.Biome biome) {
        if (pos.tileID() == TileRegistry.waterID) {
            return 10000; // Very high priority for water tiles
        } else {
            int height = pos.level.liquidManager.getHeight(pos.tileX, pos.tileY);
            return height >= 0 && height <= 3 ? 10000 : -10000; // Strong preference for water, strong avoidance of land
        }
    }

    @Override
    public boolean isValidSpawnLocation(Server server, ServerClient client, int targetX, int targetY) {
        // Sharks can only spawn in water
        if (this.getLevel() != null) {
            int tileX = targetX / 32; // Convert world coordinates to tile coordinates
            int tileY = targetY / 32;
            int tileID = this.getLevel().getTileID(tileX, tileY);
            if (tileID == TileRegistry.waterID) {
                return true;
            }
            int height = this.getLevel().liquidManager.getHeight(tileX, tileY);
            return height >= 0 && height <= 3;
        }
        return false;
    }

    public MobSpawnLocation checkSpawnLocation(MobSpawnLocation location) {
        return location.checkNotLevelCollides().checkTile((tileX, tileY) -> {
            int tileID = this.getLevel().getTileID(tileX, tileY);
            if (tileID == TileRegistry.waterID) {
                return true;
            } else {
                int height = this.getLevel().liquidManager.getHeight(tileX, tileY);
                return height >= 0 && height <= 3;
            }
        });
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(getTileCoordinate(x), getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 16;
        int drawY = camera.getDrawY(y) - 26;

        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        
        final TextureDrawOptionsEnd options;
        
        if (isAttacking.get() && SharkMobTextures.sharkAttack != null) {
            options = SharkMobTextures.sharkAttack.initDraw().sprite(sprite.x, 0, 64).light(light).pos(drawX, drawY);
        } else {
            options = SharkMobTextures.shark.initDraw().sprite(sprite.x, dir, 64).light(light).pos(drawX, drawY);
        }
        
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }



    @Override
    public int getRockSpeed() {
        return 10;
    }

    @Override
    public void serverTick() {
        super.serverTick();
        // If shark is on land, try to move back to water
        if (!this.inLiquid() && this.getLevel() != null) {
            // Make movement on land extremely slow and try to find water
            this.dx *= 0.05f;
            this.dy *= 0.05f;
        }
        
        // Check if shark is attacking (has a target mob)
        boolean wasAttacking = isAttacking.get();
        
        // Try multiple ways to detect attack state
        boolean detectedAttack = false;
        
        // Method 1: Check AI target and only trigger attack when very close
        if (this.ai != null && this.ai.blackboard != null && this.ai.blackboard.mover != null) {
            Mob target = this.ai.blackboard.mover.getTargetMob();
            if (target != null && this.getDistance(target) < 50.0f) {
                detectedAttack = true;
            }
        }
        

        
        if (detectedAttack) {
            // Only set attack state if we haven't changed state recently (2.5 second minimum)
            if (this.getWorldEntity().getTime() - lastStateChangeTime > 2500L) {
                isAttacking.set(true);
                lastAttackTime = this.getWorldEntity().getTime();
                lastStateChangeTime = this.getWorldEntity().getTime();
            }
        }
        
        // Keep attack animation for 2.5 seconds after attack
        if (isAttacking.get()) {
            lastAttackTime = this.getWorldEntity().getTime();
        } else if (this.getWorldEntity().getTime() - lastAttackTime < 2500L) {
            // Only extend attack state if we haven't changed state recently
            if (this.getWorldEntity().getTime() - lastStateChangeTime > 2500L) {
                isAttacking.set(true);
                lastStateChangeTime = this.getWorldEntity().getTime();
            }
        } else {
            // Clear attack state after 2.5 second minimum duration
            if (isAttacking.get() && this.getWorldEntity().getTime() - lastStateChangeTime > 2500L) {
                isAttacking.set(false);
                lastStateChangeTime = this.getWorldEntity().getTime();
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Stream<ModifierValue<?>> getDefaultModifiers() {
        // Shark gets speed boost in water
        if (this.getLevel() != null && this.inLiquid()) {
            return Stream.<ModifierValue<?>>of(
                new ModifierValue<Float>(BuffModifiers.SPEED, Float.valueOf(1.5f)), 
                new ModifierValue<Float>(BuffModifiers.CHASER_RANGE, Float.valueOf(2.0f))
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
        return new LocalMessage("mob", "shark");
    }

    // Client-side tick - no need for attack detection since we have network sync
    @Override
    public void clientTick() {
        super.clientTick();
        // Attack state is now synchronized via NetworkField
    }
}