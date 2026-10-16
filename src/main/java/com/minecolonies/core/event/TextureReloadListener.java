package com.minecolonies.core.event;

import com.ldtteam.blockui.mod.BlockUI;
import com.minecolonies.api.util.constant.Constants;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterTextureAtlasesEvent;
import net.minecraft.client.resources.metadata.gui.GuiMetadataSection;
import net.minecraft.client.resources.model.sprite.AtlasManager.AtlasConfig;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Specific texture reload listener.
 */
@EventBusSubscriber(value= Dist.CLIENT, modid= Constants.MOD_ID)
public class TextureReloadListener extends SimplePreparableReloadListener<TextureReloadListener.TexturePacks>
{
    /**
     * List of all texture packs available.
     */
    public static final List<String> TEXTURE_PACKS = new ArrayList<>();

    @NotNull
    @Override
    protected TexturePacks prepare(@NotNull final ResourceManager manager, @NotNull final ProfilerFiller profiler)
    {
        final Set<String> set = new HashSet<>();
        final List<Identifier> resLocs = new ArrayList<>(manager.listResources("textures/entity/citizen", f -> true).keySet());
        for (final Identifier res : resLocs)
        {
            if (res.getPath().contains("png") && res.getPath().contains("textures/entity/citizen"))
            {
                final String folder = res.getPath().split("/")[3];
                if (!folder.isEmpty())
                {
                    set.add(folder);
                }
            }
        }

        final TexturePacks packs = new TexturePacks();
        packs.packs = new ArrayList<>(set);
        return packs;
    }

    @Override
    protected void apply(@NotNull final TexturePacks packs, @NotNull final ResourceManager manager, @NotNull final ProfilerFiller profiler)
    {
       TextureReloadListener.TEXTURE_PACKS.clear();
       TextureReloadListener.TEXTURE_PACKS.addAll(packs.packs);
    }

    /**
     * Storage class to hand the texture packs from off-thread to the main thread.
     */
    public static class TexturePacks
    {
        public List<String> packs = new ArrayList<>();
    }

    @SubscribeEvent
    public static void modInitClient(final AddClientReloadListenersEvent event)
    {
        event.addListener(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "citizen_textures"), new TextureReloadListener());
    }

    @SubscribeEvent
    public static void registerTextureAtlas(final RegisterTextureAtlasesEvent event)
    {
        final Identifier atlas = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "minecolonies_gui");
        BlockUI.NAMESPACE_TO_ATLAS_MAP.put(Constants.MOD_ID, atlas);
        event.register(new AtlasConfig(
          Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/atlas/minecolonies_gui.png"),
          atlas, false, Set.of(GuiMetadataSection.TYPE)));
    }
}
