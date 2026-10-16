package com.minecolonies.api.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;

/**
 * Contract for objects that serialize themselves into a vanilla {@link Tag}.
 * <p>
 * NeoForge dropped {@code net.neoforged.neoforge.common.util.INBTSerializable} in 26.1 in favour of
 * {@code ValueIOSerializable}, which speaks {@code ValueInput}/{@code ValueOutput} instead of raw tags. The whole
 * colony save format (colony managers, buildings, jobs, quests, the request system) is built on {@link
 * net.minecraft.nbt.CompoundTag} and is persisted verbatim into existing worlds, so it keeps using tags directly and
 * only bridges to {@code ValueInput}/{@code ValueOutput} at the vanilla boundaries (block entities, entities,
 * {@code ItemStackHandler}) via {@code TagValueInput}/{@code TagValueOutput}.
 *
 * @param <T> the tag type this object serializes to.
 */
public interface INBTSerializable<T extends Tag>
{
    /**
     * Serialize this object into a tag.
     *
     * @param provider the registry lookup provider used for registry-aware (de)serialization.
     * @return the serialized tag.
     */
    T serializeNBT(final HolderLookup.Provider provider);

    /**
     * Restore this object from a previously serialized tag.
     *
     * @param provider the registry lookup provider used for registry-aware (de)serialization.
     * @param nbt      the tag to read from.
     */
    void deserializeNBT(final HolderLookup.Provider provider, final T nbt);
}
