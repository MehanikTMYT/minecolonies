package com.minecolonies.api.client.render.modeltype;

import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;

import java.util.UUID;
import org.jetbrains.annotations.Nullable;

/**
 * Render state of the MineColonies citizens.
 * <p>
 * Renderers and models no longer see the entity while drawing since 26.1, so everything the citizen models used to read
 * off {@code AbstractEntityCitizen} has to be captured during extraction. This state deliberately holds only plain
 * values - never a reference to the citizen entity itself - so that nothing keeps a client entity alive between frames.
 */
public class CitizenRenderState extends HumanoidRenderState
{
    /**
     * The texture to draw this citizen with, already resolved from the model type or the custom texture.
     */
    @Nullable
    public Identifier texture;

    /**
     * The id of the model type this citizen uses. The concrete model is resolved from it at submit time.
     * <p>
     * The renderer must NOT remember the picked model in a field: the level renderer extracts the state of every
     * visible entity first and only submits them afterwards, so a field would always hold the last extracted
     * citizen's model by the time anything is drawn.
     */
    @Nullable
    public Identifier modelTypeId;

    /**
     * Whether the female variant of the model type has to be used.
     */
    public boolean female;

    /**
     * Whether this citizen renders with a player-skin based custom texture; the model head and hat are hidden then,
     * because the custom skin already draws them.
     */
    public boolean hasCustomSkin;

    /**
     * The render metadata markers of the citizen, e.g. "working" or "backpack". Job models key their extra parts off it.
     */
    public String renderMetadata = "";

    /**
     * Whether a job hat may be shown, i.e. the citizen wears no head armour and has no custom skin.
     */
    public boolean displayHat;

    /**
     * Stable per-citizen id, used by the April 1st joke to always hide the same body part for the same citizen.
     */
    public int civilianId;

    /**
     * Whether the citizen's main hand is empty; some job models only show a tool when the hands are free.
     */
    public boolean mainHandEmpty;

    /**
     * Whether the citizen wears anything in the chest slot, regardless of whether that piece is rendered.
     */
    public boolean hasChestItem;

    /**
     * Whether the citizen is being rendered as a ghost.
     */
    public boolean isGhost;

    /**
     * The player skin uuid a citizen was given, or null when it uses a normal MineColonies texture.
     */
    @Nullable
    public UUID customTextureUUID;

    /**
     * Whether the citizen has the colony data needed to draw its armour at all.
     */
    public boolean renderArmor;

    /**
     * The status icon to draw above the name tag, or null when the citizen has no visible status.
     */
    @Nullable
    public Identifier statusIcon;
}
