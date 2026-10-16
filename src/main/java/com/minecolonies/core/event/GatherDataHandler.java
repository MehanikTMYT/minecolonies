package com.minecolonies.core.event;

import com.minecolonies.api.util.Log;
import com.minecolonies.api.util.constant.Constants;
import com.minecolonies.core.generation.DatagenLootTableManager;
import com.minecolonies.core.generation.ItemNbtCalculator;
import com.minecolonies.core.generation.defaults.*;
import com.minecolonies.core.generation.defaults.workers.*;
import com.minecolonies.core.util.SchemFixerUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.component.DataComponentInitializers;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.ValidationContextSource;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.internal.DataComponentModifiers;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class GatherDataHandler
{
    /**
     * This method is for adding datagenerators. this does not run during normal client operations, only during building.
     *
     * @param event event sent when you run the "runData" gradle task
     */
    public static void dataGeneratorSetup(final GatherDataEvent.Server event)
    {
        final DataGenerator generator = event.getGenerator();
        final PackOutput output = generator.getPackOutput();
        final CompletableFuture<HolderLookup.Provider> provider = createLootTableAwareProvider(event);

        final BlockTagsProvider blockTagsProvider = new DefaultBlockTagsProvider(output, provider);

        event.addProvider(new DefaultAdvancementsProvider(output, provider));
        event.addProvider(blockTagsProvider);
        event.addProvider(new DefaultItemTagsProvider(output, provider, blockTagsProvider));
        event.addProvider(new DefaultEntityTypeTagsProvider(output, provider));
        event.addProvider(new DefaultDamageTagsProvider(output, provider));
        event.addProvider(new DefaultResearchProvider(output, provider));
        event.addProvider(new DefaultRecipeProvider.Runner(output, provider));
        event.addProvider(new DefaultBiomeTagsProvider(output, provider));
        event.addProvider(new DefaultLootModifiersProvider(output, provider));
        event.addProvider(new DefaultDataMapsProvider(output, provider));
        event.addProvider(new DefaultRecruitmentItemsProvider(output));

        // workers
        event.addProvider(new DefaultAlchemistCraftingProvider(output, provider));
        event.addProvider(new DefaultBakerCraftingProvider(output, provider));
        event.addProvider(new DefaultBlacksmithCraftingProvider(output, provider));
        event.addProvider(new DefaultConcreteMixerCraftingProvider(output, provider));
        event.addProvider(new DefaultChefCraftingProvider(output, provider));
        event.addProvider(new DefaultCrusherCraftingProvider(output, provider));
        event.addProvider(new DefaultDyerCraftingProvider(output, provider));
        event.addProvider(new DefaultEnchanterCraftingProvider(output, provider));
        event.addProvider(new DefaultFarmerCraftingProvider(output, provider));
        event.addProvider(new LootTableProviders(output, provider));
        event.addProvider(new DefaultFletcherCraftingProvider(output, provider));
        event.addProvider(new DefaultGlassblowerCraftingProvider(output, provider));
        event.addProvider(new DefaultLumberjackCraftingProvider(output, provider));
        event.addProvider(new DefaultMechanicCraftingProvider(output, provider));
        event.addProvider(new DefaultNetherWorkerLootProvider(output, provider));
        event.addProvider(new DefaultPlanterCraftingProvider(output, provider));
        event.addProvider(new DefaultSawmillCraftingProvider(output, provider));
        event.addProvider(new DefaultSifterCraftingProvider(output, provider));
        event.addProvider(new DefaultStonemasonCraftingProvider(output, provider));
        event.addProvider(new DefaultStoneSmelteryCraftingProvider(output, provider));

        event.addProvider(new ItemNbtCalculator(output, provider));

        SchemFixerUtil.fixSchematics(provider);
    }

    /**
     * Client side counterpart of {@link #dataGeneratorSetup(GatherDataEvent.Server)}.
     * <p>
     * Client and server data are gathered through separate events since 26.1, the former {@code includeClient()} and
     * {@code includeServer()} flags no longer exist.
     *
     * @param event event sent when you run the "runData" gradle task
     */
    public static void clientDataGeneratorSetup(final GatherDataEvent.Client event)
    {
        final DataGenerator generator = event.getGenerator();
        final PackOutput output = generator.getPackOutput();

        event.addProvider(new DefaultSoundProvider(output));
        event.addProvider(new DefaultItemModelProvider(output));
        event.addProvider(new DefaultEntityIconProvider(generator));
        event.addProvider(new DefaultStoriesProvider(output));
        event.addProvider(new QuestTranslationProvider(output));
    }

    /**
     * Register the modded enchantment registry and wrap the resulting lookup provider into a
     * {@link DatagenLootTableManager}, so that loot tables can be resolved on demand while generating.
     *
     * @param event the datagen event to take the generator and resources from.
     * @return the wrapped lookup provider.
     */
    private static CompletableFuture<HolderLookup.Provider> createLootTableAwareProvider(final GatherDataEvent event)
    {
        final RegistrySetBuilder enchRegBuilder = new RegistrySetBuilder()
          .add(Registries.ENCHANTMENT, DefaultEnchantmentProvider::bootstrap)
          .add(Registries.DAMAGE_TYPE, DefaultDamageTypeProvider::bootstrap);
        final DatapackBuiltinEntriesProvider enchRegProvider =
          new DatapackBuiltinEntriesProvider(event.getGenerator().getPackOutput(), event.getLookupProvider(), enchRegBuilder, Set.of(Constants.MOD_ID, "minecraft"));
        event.addProvider(enchRegProvider);

        final ResourceManager resourceManager = event.getResourceManager(PackType.SERVER_DATA);
        return enchRegProvider.getRegistryProvider().thenApply(p -> {
            bindResolvableItemComponents(p);
            return new DatagenLootTableManager(p, resourceManager);
        });
    }

    /**
     * Binds default data components for every item whose components can actually be resolved right now,
     * mirroring what {@code DefaultDataComponentsBoundEvent} does during a normal server/client session. Datagen
     * never fires that event (there's no real world/connection for it to react to), so without this, constructing
     * an {@code ItemStack} or reading default components for ANY modded item anywhere in datagen throws
     * {@code NullPointerException("Components not bound yet")}.
     * <p>
     * A handful of vanilla items (banner patterns, at last check) have components that reference a tag and only
     * resolve once a real server resource reload has fully loaded it; eagerly evaluating those here yields
     * {@link HolderSet#emptyNamed} - the same "not available during construction" placeholder Mojang's own code
     * uses - which then fails a NeoForge dev-only sanity check ({@code CommonHooks.validateComponent}, gated
     * behind {@code SharedConstants.IS_RUNNING_IN_IDE}) because that placeholder doesn't implement equals/hashCode.
     * For exactly that recognized situation we leave the item's components unbound - nothing is substituted or
     * hidden, and its real runtime binding via {@code DefaultDataComponentsBoundEvent} is untouched. Which items
     * fall into this bucket is discovered dynamically every run (whichever ones fail to resolve right now, purely
     * by their resulting placeholder class, never by name), not a fixed list. Any other failure is a real bug and
     * is rethrown immediately, aborting datagen rather than silently doing the wrong thing.
     *
     * @param registries the fully merged registry provider (including this mod's own datapack registries).
     */
    private static void bindResolvableItemComponents(final HolderLookup.Provider registries)
    {
        final Class<?> unresolvedTagPlaceholderClass = unresolvedTagPlaceholderClass();

        // Reflection bridge: BuiltInRegistries.DATA_COMPONENT_INITIALIZERS has no public per-item API, only a
        // private `initializers` list and a `build(Provider)` that runs ALL of them unconditionally, aborting
        // entirely on the first failure - which is exactly the problem this method works around. This reads the
        // same private field vanilla's own datagen (net.minecraft.data.info.RegistryComponentsReport) reads,
        // just one item at a time instead of all-or-nothing. It fails fast if that field's name/type ever
        // changes upstream, and never mutates the list or any vanilla/NeoForge object.
        final List<?> entries;
        final Method keyMethod;
        final Method runMethod;
        try
        {
            final Field listField = DataComponentInitializers.class.getDeclaredField("initializers");
            listField.setAccessible(true);
            entries = (List<?>) listField.get(BuiltInRegistries.DATA_COMPONENT_INITIALIZERS);

            final Class<?> entryClass = Class.forName("net.minecraft.core.component.DataComponentInitializers$InitializerEntry");
            keyMethod = entryClass.getDeclaredMethod("key");
            keyMethod.setAccessible(true);
            runMethod = entryClass.getDeclaredMethod("run", DataComponentMap.Builder.class, HolderLookup.Provider.class);
            runMethod.setAccessible(true);
        }
        catch (final ReflectiveOperationException e)
        {
            throw new IllegalStateException(
              "DataComponentInitializers' internal shape changed; the datagen component-binding bridge in GatherDataHandler needs updating to match", e);
        }

        final HolderLookup.RegistryLookup<Item> items = registries.lookupOrThrow(Registries.ITEM);
        int bound = 0;
        final List<ResourceKey<Item>> skipped = new ArrayList<>();

        for (final Object entry : entries)
        {
            final ResourceKey<Item> key;
            try
            {
                //noinspection unchecked
                key = (ResourceKey<Item>) keyMethod.invoke(entry);
            }
            catch (final ReflectiveOperationException e)
            {
                throw new IllegalStateException("Could not read a DataComponentInitializers entry's key", e);
            }

            final DataComponentMap.Builder builder = DataComponentMap.builder();
            try
            {
                runMethod.invoke(entry, builder, registries);
            }
            catch (final InvocationTargetException e)
            {
                if (isUnresolvedTagPlaceholderFailure(e.getCause(), unresolvedTagPlaceholderClass))
                {
                    skipped.add(key);
                    continue;
                }
                throw new IllegalStateException(
                  "Default component initializer for item " + key.identifier() + " failed for a reason other than an "
                    + "unresolved tag reference (see cause); refusing to silently skip it.", e.getCause());
            }
            catch (final ReflectiveOperationException e)
            {
                throw new IllegalStateException("Could not run the default component initializer for item " + key.identifier(), e);
            }

            final Holder.Reference<Item> ref = items.getOrThrow(key);
            DataComponentModifiers.apply(registries, ref.value(), builder);
            ref.bindComponents(builder.build());
            bound++;
        }

        Log.getLogger().info("Datagen default-component binding: {} total, {} bound, {} left for a real resource reload to bind: {}",
          entries.size(), bound, skipped.size(), skipped);
    }

    /**
     * Constructs a {@link HolderSet#emptyNamed} the same way Mojang's own code does when a tag can't be
     * dereferenced yet, purely to learn its class and confirm - empirically, via {@link HolderSet#isBound()},
     * not by assumption - that it really is the unbound placeholder {@link #bindResolvableItemComponents} needs
     * to recognize. The type parameter and tag/owner are irrelevant to the check: this class is a single
     * compile-time anonymous class regardless of {@code T}, so any instance of it, for any registry, is enough.
     */
    private static Class<?> unresolvedTagPlaceholderClass()
    {
        final HolderSet<Item> placeholder = HolderSet.emptyNamed(
          new HolderOwner<Item>()
          {
          },
          TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "__datagen_unresolved_tag_probe__")));
        if (placeholder.isBound())
        {
            throw new IllegalStateException(
              "HolderSet.emptyNamed() no longer produces an unbound placeholder; the datagen component-binding bridge in GatherDataHandler needs updating to match");
        }
        return placeholder.getClass();
    }

    /**
     * Recognizes exactly one situation as safe to skip: {@code cause} is the NeoForge dev-only
     * {@code CommonHooks.validateComponent} check rejecting an instance of the unresolved-tag placeholder class
     * from {@link #unresolvedTagPlaceholderClass()} - confirmed both by where the exception was thrown (so an
     * unrelated equals/hashCode bug elsewhere isn't mistaken for this) and by which class it names (so an
     * unrelated broken component isn't mistaken for this either). Anything else - a codec failure, a registry
     * lookup failure, a genuinely broken MineColonies or third-party component, or any other error - returns
     * false so the caller rethrows and stops datagen instead of silently continuing.
     */
    private static boolean isUnresolvedTagPlaceholderFailure(@Nullable final Throwable cause, final Class<?> unresolvedTagPlaceholderClass)
    {
        if (!(cause instanceof final IllegalArgumentException iae))
        {
            return false;
        }

        final boolean fromDevOnlyComponentValidation = Arrays.stream(iae.getStackTrace())
          .anyMatch(frame -> frame.getClassName().equals("net.neoforged.neoforge.common.CommonHooks") && frame.getMethodName().equals("validateComponent"));
        if (!fromDevOnlyComponentValidation)
        {
            return false;
        }

        final String problematicClassName = extractProblematicClassName(iae.getMessage());
        return unresolvedTagPlaceholderClass.getName().equals(problematicClassName);
    }

    /**
     * Parses the class name out of {@code CommonHooks.validateComponent}'s
     * {@code "...Problematic class: " + clazz} error message; that method doesn't expose the failing value or its
     * class any other way.
     */
    private static String extractProblematicClassName(@Nullable final String message)
    {
        if (message == null)
        {
            return "";
        }
        final String marker = "Problematic class: class ";
        final int idx = message.indexOf(marker);
        return idx < 0 ? "" : message.substring(idx + marker.length()).trim();
    }

    // todo: move this back to SimpleLootTableProvider?
    private static final class LootTableProviders extends LootTableProvider
    {
        public LootTableProviders(final PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider)
        {
            super(packOutput, Set.of(), List.of(
                new SubProviderEntry(DefaultFishermanLootProvider::new, LootContextParamSets.FISHING),
                new SubProviderEntry(DefaultRecipeLootProvider::new, LootContextParamSets.ALL_PARAMS),
                new SubProviderEntry(DefaultSupplyLootProvider::new, LootContextParamSets.CHEST),
                new SubProviderEntry(DefaultCropsLootProvider::new, LootContextParamSets.BLOCK),
                new SubProviderEntry(DefaultEntityLootProvider::new, LootContextParamSets.ENTITY),
                new SubProviderEntry(DefaultBlockLootTableProvider::new, LootContextParamSets.BLOCK),
                new SubProviderEntry(DefaultLuckyOreLootProvider::new, LootContextParamSets.BLOCK)
            ), provider);
        }

        @Override
        protected void validate(
          final WritableRegistry<LootTable> writableregistry,
          final ValidationContextSource validationcontext,
          final ProblemReporter.Collector problemreporter$collector)
        {
            // todo this might be a bit aggressive, someone should adjust this.
        }

        // NOTE: I'm reasonably sure this code will not be required due to usage of the DatagenLootTableManager, but keeping for now just in case
//        @Override
//        protected void validate(WritableRegistry<LootTable> writableregistry, ValidationContext validationcontext, ProblemReporter.Collector collector)
//        {
//            {
//                final ValidationContext newTracker =
//                  new ValidationContext(validationcontext.reporter, validationcontext.params, new LootDataResolver()
//                  {
//                      public <T> T getElement(final LootDataId<T> id)
//                      {
//                          if (id.location().equals(BuiltInLootTables.FISHING_FISH) ||
//                                id.location().equals(BuiltInLootTables.FISHING_JUNK) ||
//                                id.location().equals(BuiltInLootTables.FISHING_TREASURE))
//                          {
//                              return id.type() == LootDataType.TABLE ? (T) map.getOrDefault(id.location(), LootTable.EMPTY) : null;
//                          }
//                          return validationcontext.resolver.getElement(id);
//                      }
//                  }, validationcontext.visitedElements);
//
//                super.validate(map, newTracker);
//            }
//        }
    }
}
