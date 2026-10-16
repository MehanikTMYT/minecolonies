package com.minecolonies.api.util;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Bridges between the tag based colony save format and the {@code ValueInput}/{@code ValueOutput} API that vanilla and
 * NeoForge switched to in 26.1.
 * <p>
 * The colony data itself stays on {@link CompoundTag} (see {@link INBTSerializable}); these helpers are used wherever
 * colony data has to be handed to, or read back from, a vanilla/NeoForge component that only speaks value IO.
 */
public final class ValueIOUtils
{
    private ValueIOUtils()
    {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Resolve the registry lookup provider for a level, falling back to the empty registry access for unattached
     * block entities exactly like vanilla does while saving.
     *
     * @param level the level, may be null.
     * @return the registry lookup provider.
     */
    public static HolderLookup.Provider registries(@Nullable final Level level)
    {
        return level != null ? level.registryAccess() : RegistryAccess.EMPTY;
    }

    /**
     * Read the whole contents of a {@link ValueInput} back as a {@link CompoundTag}.
     * <p>
     * This is the same round trip NeoForge itself uses for {@code ValueInput#keySet()} and lets the block entity and
     * entity save code keep working on the tag layout that existing worlds are written in.
     *
     * @param input the value input to read.
     * @return the contents as a tag; empty when the input holds no readable compound.
     */
    @SuppressWarnings("deprecation")
    public static CompoundTag readAsTag(@NotNull final ValueInput input)
    {
        return input.read(MapCodec.assumeMapUnsafe(CompoundTag.CODEC)).orElseGet(CompoundTag::new);
    }

    /**
     * Write every entry of a {@link CompoundTag} into the root of a {@link ValueOutput}.
     *
     * @param output the value output to write to.
     * @param tag    the tag whose entries should be stored.
     */
    public static void writeAsTag(@NotNull final ValueOutput output, @NotNull final CompoundTag tag)
    {
        output.store(tag);
    }

    /**
     * Serialize a {@link ValueIOSerializable} into a {@link CompoundTag}.
     *
     * @param serializable the object to serialize.
     * @param provider     the registry lookup provider.
     * @return the resulting tag.
     */
    public static CompoundTag serializeToTag(@NotNull final ValueIOSerializable serializable, @NotNull final HolderLookup.Provider provider)
    {
        final TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, provider);
        serializable.serialize(output);
        return output.buildResult();
    }

    /**
     * Restore a {@link ValueIOSerializable} from a {@link CompoundTag}.
     *
     * @param serializable the object to restore.
     * @param provider     the registry lookup provider.
     * @param tag          the tag previously produced by {@link #serializeToTag}.
     */
    public static void deserializeFromTag(
      @NotNull final ValueIOSerializable serializable,
      @NotNull final HolderLookup.Provider provider,
      @NotNull final CompoundTag tag)
    {
        serializable.deserialize(TagValueInput.create(ProblemReporter.DISCARDING, provider, tag));
    }
}
