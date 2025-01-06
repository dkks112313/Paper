package org.bukkit.damage;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Translatable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Represent a type of damage that an entity can receive.
 * <p>
 * Constants in this class include the base types provided by the vanilla
 * server. Data packs are capable of registering more types of damage which may
 * be obtained through the {@link Registry#DAMAGE_TYPE}.
 *
 * @see <a href="https://minecraft.wiki/w/Damage_type">Minecraft Wiki</a>
 */
@ApiStatus.Experimental
public interface DamageType extends Keyed, Translatable {

    // Paper start - Generated/DamageType
    DamageType IN_FIRE = getDamageType("in_fire");
    DamageType CAMPFIRE = getDamageType("campfire");
    DamageType LIGHTNING_BOLT = getDamageType("lightning_bolt");
    DamageType ON_FIRE = getDamageType("on_fire");
    DamageType LAVA = getDamageType("lava");
    DamageType HOT_FLOOR = getDamageType("hot_floor");
    DamageType IN_WALL = getDamageType("in_wall");
    DamageType CRAMMING = getDamageType("cramming");
    DamageType DROWN = getDamageType("drown");
    DamageType STARVE = getDamageType("starve");
    DamageType CACTUS = getDamageType("cactus");
    DamageType FALL = getDamageType("fall");
    DamageType ENDER_PEARL = getDamageType("ender_pearl");
    DamageType FLY_INTO_WALL = getDamageType("fly_into_wall");
    DamageType OUT_OF_WORLD = getDamageType("out_of_world");
    DamageType GENERIC = getDamageType("generic");
    DamageType MAGIC = getDamageType("magic");
    DamageType WITHER = getDamageType("wither");
    DamageType DRAGON_BREATH = getDamageType("dragon_breath");
    DamageType DRY_OUT = getDamageType("dry_out");
    DamageType SWEET_BERRY_BUSH = getDamageType("sweet_berry_bush");
    DamageType FREEZE = getDamageType("freeze");
    DamageType STALAGMITE = getDamageType("stalagmite");
    DamageType FALLING_BLOCK = getDamageType("falling_block");
    DamageType FALLING_ANVIL = getDamageType("falling_anvil");
    DamageType FALLING_STALACTITE = getDamageType("falling_stalactite");
    DamageType STING = getDamageType("sting");
    DamageType MOB_ATTACK = getDamageType("mob_attack");
    DamageType MOB_ATTACK_NO_AGGRO = getDamageType("mob_attack_no_aggro");
    DamageType PLAYER_ATTACK = getDamageType("player_attack");
    DamageType ARROW = getDamageType("arrow");
    DamageType TRIDENT = getDamageType("trident");
    DamageType MOB_PROJECTILE = getDamageType("mob_projectile");
    DamageType SPIT = getDamageType("spit");
    DamageType FIREWORKS = getDamageType("fireworks");
    DamageType FIREBALL = getDamageType("fireball");
    DamageType UNATTRIBUTED_FIREBALL = getDamageType("unattributed_fireball");
    DamageType WITHER_SKULL = getDamageType("wither_skull");
    DamageType THROWN = getDamageType("thrown");
    DamageType INDIRECT_MAGIC = getDamageType("indirect_magic");
    DamageType THORNS = getDamageType("thorns");
    DamageType EXPLOSION = getDamageType("explosion");
    DamageType PLAYER_EXPLOSION = getDamageType("player_explosion");
    DamageType SONIC_BOOM = getDamageType("sonic_boom");
    DamageType BAD_RESPAWN_POINT = getDamageType("bad_respawn_point");
    DamageType OUTSIDE_BORDER = getDamageType("outside_border");
    DamageType GENERIC_KILL = getDamageType("generic_kill");
    DamageType WIND_CHARGE = getDamageType("wind_charge");
    DamageType MACE_SMASH = getDamageType("mace_smash");
    // Paper end - Generated/DamageType

    @NotNull
    private static DamageType getDamageType(@NotNull String key) {
        return Registry.DAMAGE_TYPE.getOrThrow(NamespacedKey.minecraft(key));
    }

    /**
     * {@inheritDoc}
     * <p>
     * The returned key is that of the death message sent when this damage type
     * is responsible for the death of an entity.
     * <p>
     * <strong>Note</strong> This translation key is only used if
     * {@link #getDeathMessageType()} is {@link DeathMessageType#DEFAULT}
     */
    @NotNull
    @Override
    public String getTranslationKey();

    /**
     * Get the {@link DamageScaling} for this damage type.
     *
     * @return the damage scaling
     */
    @NotNull
    public DamageScaling getDamageScaling();

    /**
     * Get the {@link DamageEffect} for this damage type.
     *
     * @return the damage effect
     */
    @NotNull
    public DamageEffect getDamageEffect();

    /**
     * Get the {@link DeathMessageType} for this damage type.
     *
     * @return the death message type
     */
    @NotNull
    public DeathMessageType getDeathMessageType();

    /**
     * Get the amount of hunger exhaustion caused by this damage type.
     *
     * @return the exhaustion
     */
    public float getExhaustion();
}
