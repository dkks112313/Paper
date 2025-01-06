package org.bukkit.inventory;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

/**
 * A item's rarity determines the default color of its name. This enum is
 * ordered from least rare to most rare.
 */
public enum ItemRarity {

    // Paper start - Generated/ItemRarity
    /**
     * White item name.
     */
    COMMON(NamedTextColor.WHITE),
    /**
     * Yellow item name.
     */
    UNCOMMON(NamedTextColor.YELLOW),
    /**
     * Aqua item name.
     */
    RARE(NamedTextColor.AQUA),
    /**
     * Light purple item name.
     */
    EPIC(NamedTextColor.LIGHT_PURPLE);
    // Paper end - Generated/ItemRarity

    // Paper start - improve ItemRarity
    private final NamedTextColor color;

    ItemRarity(final NamedTextColor color) {
        this.color = color;
    }

    /**
     * Gets the color formatting associated with this rarity.
     *
     * @return the color
     */
    public @org.jetbrains.annotations.NotNull TextColor color() {
        return this.color;
    }
    // Paper end
}
