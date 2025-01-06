package io.papermc.generator.rewriter.types.simple;

import io.papermc.typewriter.replace.SearchMetadata;
import io.papermc.typewriter.replace.SearchReplaceRewriter;
import java.awt.Color;
import net.minecraft.world.level.material.MapColor;
import org.jspecify.annotations.Nullable;

public class MapPaletteRewriter extends SearchReplaceRewriter {

    @Override
    protected void insert(SearchMetadata metadata, StringBuilder builder) {
        for (@Nullable MapColor mapColor : MapColor.MATERIAL_COLORS) {
            if (mapColor == null) {
                continue;
            }

            for (MapColor.Brightness brightness : MapColor.Brightness.values()) {
                builder.append(metadata.indent());
                Color color = new Color(mapColor.calculateARGBColor(brightness), true);
                if (color.getAlpha() != 0xFF) {
                    builder.append("new %s(0x%08X, true),".formatted(color.getClass().getSimpleName(), color.getRGB()));
                } else {
                    builder.append("new %s(0x%06X),".formatted(color.getClass().getSimpleName(), color.getRGB() & 0x00FFFFFF));
                }
                builder.append('\n');
            }
        }
    }
}
