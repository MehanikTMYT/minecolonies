package com.minecolonies.api.tileentities;

import net.minecraft.core.Direction;
import com.minecolonies.api.util.ValueIOUtils;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import com.minecolonies.api.util.WorldUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.minecolonies.api.util.constant.Constants.TAG_STRING;
import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_CONTENT;

public class AbstractTileEntityNamedGrave extends BlockEntity
{
    /**
     * The position it faces.
     */
    public static final EnumProperty<Direction> FACING       = HorizontalDirectionalBlock.FACING;

    /**
     * The text displayed on the name plate
     */
    private ArrayList<String> textLines = new ArrayList<>();

    public AbstractTileEntityNamedGrave(BlockEntityType<?> tileEntityTypeIn, final BlockPos pos, final BlockState state)
    {
        super(tileEntityTypeIn, pos, state);
        textLines.add("Unknown Citizen");
    }

    public ArrayList<String> getTextLines()
    {
        return textLines;
    }

    public void setTextLines(final ArrayList<String> content)
    {
        this.textLines = content;
        setChanged();
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
    protected void loadAdditionalTag(final CompoundTag compound, @NotNull final HolderLookup.Provider provider)
    {

        textLines.clear();
        if (compound.contains(TAG_CONTENT))
        {
            final ListTag lines = compound.getListOrEmpty(TAG_CONTENT);
            for (int i = 0; i < lines.size(); i++)
            {
                final String line = lines.getStringOr(i, "");
                textLines.add(line);
            }
        }
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
    protected void saveAdditionalTag(final CompoundTag compound, @NotNull final HolderLookup.Provider provider)
    {

        @NotNull final ListTag lines = new ListTag();
        for (@NotNull final String line : textLines)
        {
            lines.add(StringTag.valueOf(line));
        }
        compound.put(TAG_CONTENT, lines);
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
        return this.saveWithoutMetadata(provider);
    }

    @Override
    public void onDataPacket(final Connection net, @NotNull final ValueInput input)
    {
        this.loadAdditional(input);
    }

    @Override
    public void handleUpdateTag(@NotNull final ValueInput input)
    {
        this.loadAdditional(input);
    }

    @Override
    public void setChanged()
    {
        if (level != null)
        {
            WorldUtil.markChunkDirty(level, worldPosition);
        }
    }
}
