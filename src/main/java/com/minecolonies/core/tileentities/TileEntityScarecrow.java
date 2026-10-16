package com.minecolonies.core.tileentities;

import com.minecolonies.api.util.ValueIOUtils;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.tileentities.AbstractTileEntityScarecrow;
import com.minecolonies.api.tileentities.ScareCrowType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

import static com.minecolonies.core.colony.buildingextensions.FarmField.*;

/**
 * The scarecrow tile entity to store extra data.
 */
@SuppressWarnings("PMD.ExcessiveImports")
public class TileEntityScarecrow extends AbstractTileEntityScarecrow
{
    /**
     * Random generator.
     */
    private final Random random = new Random();

    /**
     * The colony this field is located in.
     */
    private IColony currentColony;

    /**
     * The type of the scarecrow.
     */
    private ScareCrowType type;

    /**
     * The size of the field in all four directions
     * in the same order as {@link Direction}:
     * S, W, N, E
     */
    private int[] fieldSize = {DEFAULT_RANGE, DEFAULT_RANGE, DEFAULT_RANGE, DEFAULT_RANGE};

    /**
     * Creates an instance of the tileEntity.
     */
    public TileEntityScarecrow(final BlockPos pos, final BlockState state)
    {
        super(pos, state);
    }

    @Override
    public ScareCrowType getScarecrowType()
    {
        if (this.type == null)
        {
            final ScareCrowType[] values = ScareCrowType.values();
            this.type = values[this.random.nextInt(values.length)];
        }
        return this.type;
    }

    @Override
    public IColony getCurrentColony()
    {
        if (currentColony == null && level != null)
        {
            this.currentColony = IColonyManager.getInstance().getIColony(level, worldPosition);
        }
        return currentColony;
    }

    @Override
    protected void saveAdditional(@NotNull final ValueOutput output)
    {
        super.saveAdditional(output);
        final CompoundTag saveTag = new CompoundTag();
        saveAdditionalTag(saveTag, ValueIOUtils.registries(this.level));
        ValueIOUtils.writeAsTag(output, saveTag);
    }

    /**
     * Writes the colony save data, which keeps using the tag layout existing worlds are written in.
     */
    protected void saveAdditionalTag(final CompoundTag compoundTag, @NotNull final HolderLookup.Provider provider)
    {
        compoundTag.putIntArray(TAG_RADIUS, fieldSize);
    }

    @Override
    protected void loadAdditional(@NotNull final ValueInput input)
    {
        super.loadAdditional(input);
        loadAdditionalTag(ValueIOUtils.readAsTag(input), input.lookup());
    }

    /**
     * Reads the colony save data, which keeps using the tag layout existing worlds are written in.
     */
    protected void loadAdditionalTag(final CompoundTag compoundTag, @NotNull final HolderLookup.Provider provider)
    {
        if (compoundTag.contains(TAG_RADIUS))
        {
            fieldSize = compoundTag.getIntArray(TAG_RADIUS).orElseGet(() -> new int[0]);
        }
    }

    /**
     * @param direction the direction for the radius
     * @param radius    the number of blocks from the scarecrow that the farmer will work with
     */
    public void setFieldSize(Direction direction, int radius)
    {
        this.fieldSize[direction.get2DDataValue()] = Math.min(radius, MAX_RANGE);
        setChanged();
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag(@NotNull final HolderLookup.Provider provider)
    {
        return saveWithoutMetadata(provider);
    }

    /**
     * Field size.
     * @return the field size.
     */
    public int[] getFieldSize()
    {
        return fieldSize;
    }
}
