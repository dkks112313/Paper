package org.bukkit;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a song which may play in a Jukebox.
 */
@ApiStatus.Experimental
public interface JukeboxSong extends Keyed, Translatable {

    // Paper start - Generated/JukeboxSong
    JukeboxSong THIRTEEN = get("13");
    JukeboxSong CAT = get("cat");
    JukeboxSong BLOCKS = get("blocks");
    JukeboxSong CHIRP = get("chirp");
    JukeboxSong FAR = get("far");
    JukeboxSong MALL = get("mall");
    JukeboxSong MELLOHI = get("mellohi");
    JukeboxSong STAL = get("stal");
    JukeboxSong STRAD = get("strad");
    JukeboxSong WARD = get("ward");
    JukeboxSong ELEVEN = get("11");
    JukeboxSong WAIT = get("wait");
    JukeboxSong PIGSTEP = get("pigstep");
    JukeboxSong OTHERSIDE = get("otherside");
    JukeboxSong FIVE = get("5");
    JukeboxSong RELIC = get("relic");
    JukeboxSong PRECIPICE = get("precipice");
    JukeboxSong CREATOR = get("creator");
    JukeboxSong CREATOR_MUSIC_BOX = get("creator_music_box");
    // Paper end - Generated/JukeboxSong

    @NotNull
    private static JukeboxSong get(@NotNull String key) {
        return Registry.JUKEBOX_SONG.getOrThrow(NamespacedKey.minecraft(key));
    }

    // Paper start - adventure
    /**
     * @deprecated this method assumes that jukebox song description will
     * always be a translatable component which is not guaranteed.
     */
    @Override
    @Deprecated(forRemoval = true)
    @org.jetbrains.annotations.NotNull String getTranslationKey();
    // Paper end - adventure
}
