package org.bukkit.inventory.meta.trim;

import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Translatable;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a material that may be used in an {@link ArmorTrim}.
 */
public interface TrimMaterial extends Keyed, Translatable {

    // Paper start - Generated/TrimMaterial
    /**
     * {@link Material#QUARTZ}.
     */
    TrimMaterial QUARTZ = getTrimMaterial("quartz");
    /**
     * {@link Material#IRON_INGOT}.
     */
    TrimMaterial IRON = getTrimMaterial("iron");
    /**
     * {@link Material#NETHERITE_INGOT}.
     */
    TrimMaterial NETHERITE = getTrimMaterial("netherite");
    /**
     * {@link Material#REDSTONE}.
     */
    TrimMaterial REDSTONE = getTrimMaterial("redstone");
    /**
     * {@link Material#COPPER_INGOT}.
     */
    TrimMaterial COPPER = getTrimMaterial("copper");
    /**
     * {@link Material#GOLD_INGOT}.
     */
    TrimMaterial GOLD = getTrimMaterial("gold");
    /**
     * {@link Material#EMERALD}.
     */
    TrimMaterial EMERALD = getTrimMaterial("emerald");
    /**
     * {@link Material#DIAMOND}.
     */
    TrimMaterial DIAMOND = getTrimMaterial("diamond");
    /**
     * {@link Material#LAPIS_LAZULI}.
     */
    TrimMaterial LAPIS = getTrimMaterial("lapis");
    /**
     * {@link Material#AMETHYST_SHARD}.
     */
    TrimMaterial AMETHYST = getTrimMaterial("amethyst");
    /**
     * {@link Material#RESIN_BRICK}.
     */
    TrimMaterial RESIN = getTrimMaterial("resin");
    // Paper end - Generated/TrimMaterial

    @NotNull
    private static TrimMaterial getTrimMaterial(@NotNull String key) {
        return Registry.TRIM_MATERIAL.getOrThrow(NamespacedKey.minecraft(key));
    }

    // Paper start - adventure
    /**
     * Get the description of this {@link TrimMaterial}.
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
     * and {@link io.papermc.paper.registry.RegistryKey#TRIM_MATERIAL}. TrimMaterials can exist without a key.
     */
    @Deprecated(forRemoval = true, since = "1.20.4")
    @Override
    org.bukkit.@org.jetbrains.annotations.NotNull NamespacedKey getKey();

    /**
     * @deprecated use {@link Registry#getKey(Keyed)}, {@link io.papermc.paper.registry.RegistryAccess#getRegistry(io.papermc.paper.registry.RegistryKey)},
     * and {@link io.papermc.paper.registry.RegistryKey#TRIM_MATERIAL}. TrimMaterials can exist without a key.
     */
    @Deprecated(forRemoval = true, since = "1.20.4")
    @Override
    default net.kyori.adventure.key.@org.jetbrains.annotations.NotNull Key key() {
        return org.bukkit.Keyed.super.key();
    }
    // Paper end - Registry#getKey
}
