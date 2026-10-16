package com.minecolonies.core.client.render.worldevent;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

import java.util.function.Function;

public final class RenderTypes
{
    private RenderTypes()
    {
    }

    private static final RenderPipeline WORLD_ENTITY_ICON_PIPELINE = RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET)
        // PORT(26.1): the String overload prepends "minecraft:", so "minecolonies:..." became an invalid id and crashed
        // the first citizen render (ExceptionInInitializerError).
        .withLocation(Identifier.fromNamespaceAndPath("minecolonies", "world_entity_icon"))
        .withVertexShader("core/position_tex")
        .withFragmentShader("core/position_tex")
        .withSampler("Sampler0")
        .withCull(false)
        .withVertexFormat(DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS)
        .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
        .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
        .build();

    private static final Function<Identifier, RenderType> WORLD_ENTITY_ICON = Util.memoize(texture -> RenderType.create(
        "minecolonies_entity_icon",
        RenderSetup.builder(WORLD_ENTITY_ICON_PIPELINE)
            .withTexture("Sampler0", texture)
            .bufferSize(1024)
            .createRenderSetup()));

    /**
     * Unlit, camera-facing icon which deliberately remains visible through terrain.
     */
    public static RenderType worldEntityIcon(final Identifier texture)
    {
        return WORLD_ENTITY_ICON.apply(texture);
    }
}
