package com.minecolonies.core.client.gui.containers;

import com.minecolonies.api.colony.ICitizen;
import com.minecolonies.api.inventory.container.ContainerCitizenInventory;
import com.minecolonies.api.util.constant.Constants;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * ------------ Class not Documented ------------
 */
public class WindowCitizenInventory extends AbstractContainerScreen<ContainerCitizenInventory>
{
    /**
     * Texture res loc.
     */
    private static final Identifier TEXT = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/citizen_container.png");

    /**
     * Offset inside the texture to use.
     */
    private static final int TEXTURE_OFFSET = 130;

    /**
     * Offset of each slot.
     */
    private static final int SLOT_OFFSET = 18;

    /**
     * Size of the custom texture.
     */
    private static final int TEXTURE_SIZE = 350;

    /**
     * Offet of the screen for the texture.
     */
    private static final int TEXTURE_HEIGHT = 96;

    /**
     * General y offset.
     */
    private static final int Y_OFFSET = 114;

    /**
     * Amount of slots each row.
     */
    private static final int SLOTS_EACH_ROW = 9;

    /**
     * Current active citizen inventory window
     */
    public static WindowCitizenInventory activeCitizenInventory = null;

    /**
     * Citizen of this UI
     */
    private ICitizen citizenData;

    /**
     * window height is calculated with these values; the more rows, the heigher
     */
    private final int inventoryRows;

    public WindowCitizenInventory(final ContainerCitizenInventory container, final Inventory playerInventory, final Component iTextComponent)
    {
        super(container, playerInventory, iTextComponent, 245,
          Y_OFFSET + Math.min(SLOTS_EACH_ROW, (container.getItems().size() - 36) / 9) * SLOT_OFFSET);
        this.inventoryRows = (container.getItems().size() - 36) / 9;
        activeCitizenInventory = this;
        citizenData = container.getCitizenData();
    }

    @Override
    public void extractRenderState(@NotNull final GuiGraphicsExtractor stack, final int x, final int y, final float z)
    {
        super.extractRenderState(stack, x, y, z);
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
    protected void extractLabels(@NotNull final GuiGraphicsExtractor stack, final int mouseX, final int mouseY)
    {
        stack.text(this.font, this.menu.getDisplayName(), 80, 9, 4210752, false);
        stack.text(this.font, this.playerInventoryTitle, 8, 25 + this.inventoryRows * SLOT_OFFSET, 4210752, false);
    }

    /**
     * Draws the background layer of this container (behind the items).
     */
    @Override
    public void extractBackground(@NotNull final GuiGraphicsExtractor stack, final int mouseX, final int mouseY, final float partialTicks)
    {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;


        stack.blit(TEXT, i, j, 0, 0, this.imageWidth,  10 + this.inventoryRows * SLOT_OFFSET + 12, TEXTURE_SIZE, TEXTURE_SIZE);


        stack.blit(TEXT, i, j + 10 + this.inventoryRows * SLOT_OFFSET + 12, 0, TEXTURE_OFFSET, this.imageWidth, TEXTURE_HEIGHT, TEXTURE_SIZE, TEXTURE_SIZE);


        //stack.blit(TEXT, i, j, 0, 0, this.imageWidth,  this.inventoryRows * SLOT_OFFSET + 12, TEXTURE_SIZE, TEXTURE_SIZE);


        stack.blit(TEXT, i + 172, j + 22, 0, 227, 49, 72, TEXTURE_SIZE, TEXTURE_SIZE);

        for (int index = 0; index < 4; index++)
        {
            stack.blit(TEXT, i + 222, j + 22 + index * 18, 0, 300, 18, 18, TEXTURE_SIZE, TEXTURE_SIZE);
        }

        this.menu.getEntity().filter(LivingEntity.class::isInstance).map(LivingEntity.class::cast).ifPresent(entity ->
          InventoryScreen.extractEntityInInventoryFollowsMouse(
            stack, i + 172, j + 22, i + 221, j + 94, 30, 0.0625F, mouseX, mouseY, entity));
    }

    @Override
    public void onClose()
    {
        activeCitizenInventory = null;
        super.onClose();
    }

    /**
     * Get the citizen for this UI
     *
     * @return
     */
    public ICitizen getCitizenData()
    {
        return citizenData;
    }
}
