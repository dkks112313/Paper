package org.bukkit.inventory.meta.trim;

import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Translatable;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a pattern that may be used in an {@link ArmorTrim}.
 */
public interface TrimPattern extends Keyed, Translatable {

    // Paper start - Generated/TrimPattern
    /**
     * {@link Material#SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE}.
     */
    TrimPattern SENTRY = getTrimPattern("sentry");
    /**
     * {@link Material#DUNE_ARMOR_TRIM_SMITHING_TEMPLATE}.
     */
    TrimPattern DUNE = getTrimPattern("dune");
    /**
     * {@link Material#COAST_ARMOR_TRIM_SMITHING_TEMPLATE}.
     */
    TrimPattern COAST = getTrimPattern("coast");
    /**
     * {@link Material#WILD_ARMOR_TRIM_SMITHING_TEMPLATE}.
     */
    TrimPattern WILD = getTrimPattern("wild");
    /**
     * {@link Material#WARD_ARMOR_TRIM_SMITHING_TEMPLATE}.
     */
    TrimPattern WARD = getTrimPattern("ward");
    /**
     * {@link Material#EYE_ARMOR_TRIM_SMITHING_TEMPLATE}.
     */
    TrimPattern EYE = getTrimPattern("eye");
    /**
     * {@link Material#VEX_ARMOR_TRIM_SMITHING_TEMPLATE}.
     */
    TrimPattern VEX = getTrimPattern("vex");
    /**
     * {@link Material#TIDE_ARMOR_TRIM_SMITHING_TEMPLATE}.
     */
    TrimPattern TIDE = getTrimPattern("tide");
    /**
     * {@link Material#SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE}.
     */
    TrimPattern SNOUT = getTrimPattern("snout");
    /**
     * {@link Material#RIB_ARMOR_TRIM_SMITHING_TEMPLATE}.
     */
    TrimPattern RIB = getTrimPattern("rib");
    /**
     * {@link Material#SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE}.
     */
    TrimPattern SPIRE = getTrimPattern("spire");
    /**
     * {@link Material#WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE}.
     */
    TrimPattern WAYFINDER = getTrimPattern("wayfinder");
    /**
     * {@link Material#SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE}.
     */
    TrimPattern SHAPER = getTrimPattern("shaper");
    /**
     * {@link Material#SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE}.
     */
    TrimPattern SILENCE = getTrimPattern("silence");
    /**
     * {@link Material#RAISER_ARMOR_TRIM_SMITHING_TEMPLATE}.
     */
    TrimPattern RAISER = getTrimPattern("raiser");
    /**
     * {@link Material#HOST_ARMOR_TRIM_SMITHING_TEMPLATE}.
     */
    TrimPattern HOST = getTrimPattern("host");
    /**
     * {@link Material#FLOW_ARMOR_TRIM_SMITHING_TEMPLATE}.
     */
    TrimPattern FLOW = getTrimPattern("flow");
    /**
     * {@link Material#BOLT_ARMOR_TRIM_SMITHING_TEMPLATE}.
     */
    TrimPattern BOLT = getTrimPattern("bolt");
    // Paper end - Generated/TrimPattern

    @NotNull
    private static TrimPattern getTrimPattern(@NotNull String key) {
        return Registry.TRIM_PATTERN.getOrThrow(NamespacedKey.minecraft(key));
    }

    // Paper start - adventure
    /**
     * Get the description of this {@link TrimPattern}.
     *
     * @return the description
     */
    net.kyori.adventure.text.@org.jetbrains.annotations.NotNull Component description();

    /**
     * @deprecated this method assumes that {@link #description()} will
     * always be a translatable component which is not guaranteed.
     */
    @Override
    @Deprecated(forRemoval = true)
    @org.jetbrains.annotations.NotNull String getTranslationKey();
    // Paper end - adventure

    // Paper start - Registry#getKey
    /**
     * @deprecated use {@link Registry#getKey(Keyed)}, {@link io.papermc.paper.registry.RegistryAccess#getRegistry(io.papermc.paper.registry.RegistryKey)},
     * and {@link io.papermc.paper.registry.RegistryKey#TRIM_PATTERN}. TrimPatterns can exist without a key.
     */
    @Deprecated(forRemoval = true, since = "1.20.4")
    @Override
    org.bukkit.@org.jetbrains.annotations.NotNull NamespacedKey getKey();

    /**
     * @deprecated use {@link Registry#getKey(Keyed)}, {@link io.papermc.paper.registry.RegistryAccess#getRegistry(io.papermc.paper.registry.RegistryKey)},
     * and {@link io.papermc.paper.registry.RegistryKey#TRIM_PATTERN}. TrimPatterns can exist without a key.
     */
    @Deprecated(forRemoval = true, since = "1.20.4")
    @Override
    default net.kyori.adventure.key.@org.jetbrains.annotations.NotNull Key key() {
        return org.bukkit.Keyed.super.key();
    }
    // Paper end - Registry#getKey
}
