package com.minecolonies.core.generation.defaults;

import com.minecolonies.api.entity.ModEntities;
import com.minecolonies.api.items.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static com.minecolonies.api.util.constant.Constants.MOD_ID;

public class DefaultEntityTypeTagsProvider extends EntityTypeTagsProvider
{
    public DefaultEntityTypeTagsProvider(final PackOutput output,
      final CompletableFuture<HolderLookup.Provider> lookupProvider)
    {
        super(output, lookupProvider, MOD_ID);
    }

    @Override
    protected void addTags(final HolderLookup.Provider holder)
    {
        tag(ModTags.hostile).add(EntityType.SLIME);
        tag(ModTags.mobAttackBlacklist).add(EntityType.ENDERMAN, EntityType.LLAMA);
        tag(ModTags.freeToInteractWith).add(TagEntry.optionalElement(Identifier.fromNamespaceAndPath("corpse", "corpse")));

        final TagAppender<EntityType<?>, EntityType<?>> raiderTagAppender = tag(ModTags.raiders);
        ModEntities.getRaiders().forEach(raiderType -> raiderTagAppender.add(TagEntry.element(EntityType.getKey(raiderType))));

        tag(TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("dynamictrees", "falling_tree_damage_immune")))
                .add(ModEntities.CITIZEN)
                .add(ModEntities.VISITOR);
    }
}