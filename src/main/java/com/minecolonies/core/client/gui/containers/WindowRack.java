package com.minecolonies.core.client.gui.containers;

import com.minecolonies.api.blocks.AbstractBlockMinecoloniesRack;
import com.minecolonies.api.blocks.types.RackType;
import com.minecolonies.api.inventory.container.ContainerRack;
import com.minecolonies.api.util.constant.Constants;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.Identifier;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;

import static com.minecolonies.api.util.constant.InventoryConstants.*;

public class WindowRack extends AbstractContainerScreen<ContainerRack>
{
    /**
     * The resource LOCATION of the texture.
     */
    private static final Identifier CHEST_GUI_TEXTURE = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/generic_108.png");

    /**
     * The LOCATION of the additional styles.
     */
    private static final String LOCATION = "textures/gui/gui%s.png";

    /**
     * Size of the custom texture.
     */
    private static final int TEXTURE_SIZE = 350;

    /**
     * Size at which the normal GUI texture still works.
     */
    private static final int GOOD_SIZE = 8;

    /**
     * General y offset.
     */
    private static final int Y_OFFSET = 114;

    /**
     * Offet of the screen for the texture.
     */
    private static final int TEXTURE_HEIGHT = 96;

    /**
     * Offset inside the texture to use.
     */
    private static final int TEXTURE_OFFSET = 126 * 2 - 17;

    /**
     * The upper chest inventory.
     */
    private final IItemHandler jointChestInventory;

    /**
     * Used to calculate the window height.
     */
    private final int inventoryRows;

    public WindowRack(final ContainerRack container, final Inventory playerInventory, final Component iTextComponent)
    {
        super(container, playerInventory, iTextComponent,
            176 + Math.max(0, ((container.rack.getInventory().getSlots() + (container.neighborRack == null ? 0 : container.neighborRack.getInventory().getSlots())) / INVENTORY_BAR_SIZE + 1) - INVENTORY_COLUMNS) * PLAYER_INVENTORY_OFFSET_EACH,
            Y_OFFSET + Math.min((container.rack.getInventory().getSlots() + (container.neighborRack == null ? 0 : container.neighborRack.getInventory().getSlots())) / INVENTORY_COLUMNS, INVENTORY_BAR_SIZE) * PLAYER_INVENTORY_OFFSET_EACH);
        if (container.neighborRack != null)
        {
            if (container.rack.getBlockState().getValue(AbstractBlockMinecoloniesRack.VARIANT) != RackType.NO_RENDER)
            {
                this.jointChestInventory = new CombinedInvWrapper(container.rack.getInventory(), container.neighborRack.getInventory());
            }
            else
            {
                this.jointChestInventory = new CombinedInvWrapper(container.neighborRack.getInventory(), container.rack.getInventory());
            }
        }
        else
        {
            this.jointChestInventory = container.rack.getInventory();
        }

        final int size = jointChestInventory.getSlots();
        this.inventoryRows = size / INVENTORY_COLUMNS;
        final int rows = Math.min(this.inventoryRows, INVENTORY_BAR_SIZE);
        final int columns = this.inventoryRows <= INVENTORY_BAR_SIZE ? INVENTORY_COLUMNS : ((size / INVENTORY_BAR_SIZE) + 1);

    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
    protected void extractLabels(@NotNull final GuiGraphicsExtractor stack, final int mouseX, final int mouseY)
    {
        stack.text(this.font, this.title, 8, 6, 4210752, false);
        stack.text(this.font, this.playerInventoryTitle, 8, (this.imageHeight - 94), 4210752, false);
    }

    /**
     * Draws the background layer of this container (behind the items).
     */
    @Override
    public void extractBackground(@NotNull final GuiGraphicsExtractor stack, final int mouseX, final int mouseY, final float partialTicks)
    {
        final Identifier loc = getCorrectTextureForSlots(inventoryRows);

        if (inventoryRows <= GOOD_SIZE)
        {
            final int rowsHeight = this.inventoryRows * PLAYER_INVENTORY_OFFSET_EACH + PLAYER_INVENTORY_OFFSET_EACH - 1;
            stack.blit(loc, this.leftPos, this.topPos, 0, 0, this.imageWidth, rowsHeight, TEXTURE_SIZE, TEXTURE_SIZE);
            stack.blit(loc, this.leftPos, this.topPos + rowsHeight, 0,
              TEXTURE_OFFSET, this.imageWidth, TEXTURE_HEIGHT, TEXTURE_SIZE, TEXTURE_SIZE);
        }
        else
        {
            stack.blit(loc, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, TEXTURE_SIZE, TEXTURE_SIZE);
        }
    }

    /**
     * Get the correct resource LOCATION for this amount of rows.
     *
     * @param inventoryRows the amount of rows.
     * @return the correct LOCATION.
     */
    private static Identifier getCorrectTextureForSlots(final int inventoryRows)
    {
        if (inventoryRows <= GOOD_SIZE)
        {
            return CHEST_GUI_TEXTURE;
        }
        else
        {
            return Identifier.fromNamespaceAndPath(Constants.MOD_ID, String.format(LOCATION, inventoryRows * INVENTORY_COLUMNS));
        }
    }

    @Override
    public void extractRenderState(@NotNull final GuiGraphicsExtractor stack, final int x, final int y, final float z)
    {
        super.extractRenderState(stack, x, y, z);
    }
}
