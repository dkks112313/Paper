package org.bukkit.entity.memory;

import static org.junit.jupiter.api.Assertions.*;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.memory.CraftMemoryKey;
import org.bukkit.support.environment.AllFeatures;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@AllFeatures
public class CraftMemoryKeyTest {

    @Test
    public void shouldConvertBukkitHomeKeyToNMSRepresentation() {
        MemoryModuleType<GlobalPos> nmsHomeKey = CraftMemoryKey.bukkitToMinecraft(MemoryKey.HOME);
        assertEquals(MemoryModuleType.HOME, nmsHomeKey, "MemoryModuleType should be HOME");
    }

    @Test
    public void shouldConvertBukkitJobSiteKeyToNMSRepresentation() {
        MemoryModuleType<GlobalPos> nmsHomeKey = CraftMemoryKey.bukkitToMinecraft(MemoryKey.JOB_SITE);
        assertEquals(MemoryModuleType.JOB_SITE, nmsHomeKey, "MemoryModuleType should be JOB_SITE");
    }

    @Test
    public void shouldConvertBukkitMeetingPointKeyToNMSRepresentation() {
        MemoryModuleType<GlobalPos> nmsHomeKey = CraftMemoryKey.bukkitToMinecraft(MemoryKey.MEETING_POINT);
        assertEquals(MemoryModuleType.MEETING_POINT, nmsHomeKey, "MemoryModuleType should be MEETING_POINT");
    }

    @Test
    public void shouldConvertNMSHomeKeyToBukkitRepresentation() {
        MemoryKey<Location> bukkitHomeKey = CraftMemoryKey.minecraftToBukkit(MemoryModuleType.HOME);
        assertEquals(MemoryKey.HOME, bukkitHomeKey, "MemoryModuleType should be HOME");
    }

    @Test
    public void shouldConvertNMSJobSiteKeyToBukkitRepresentation() {
        MemoryKey<Location> bukkitJobSiteKey = CraftMemoryKey.minecraftToBukkit(MemoryModuleType.JOB_SITE);
        assertEquals(MemoryKey.JOB_SITE, bukkitJobSiteKey, "MemoryKey should be JOB_SITE");
    }

    @Test
    public void shouldConvertNMSMeetingPointKeyToBukkitRepresentation() {
        MemoryKey<Location> bukkitHomeKey = CraftMemoryKey.minecraftToBukkit(MemoryModuleType.MEETING_POINT);
        assertEquals(MemoryKey.MEETING_POINT, bukkitHomeKey, "MemoryKey should be MEETING_POINT");
    }
}
