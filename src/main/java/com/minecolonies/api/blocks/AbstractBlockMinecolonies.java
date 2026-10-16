package com.minecolonies.api.blocks;

import com.minecolonies.api.blocks.interfaces.IBlockMinecolonies;
import net.minecraft.core.Registry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import static com.minecolonies.api.util.RegistryConstructionContext.applyBlockKey;

public abstract class AbstractBlockMinecolonies<B extends AbstractBlockMinecolonies<B>> extends Block implements IBlockMinecolonies<B>
{
    public AbstractBlockMinecolonies(final Properties properties)
    {
        super(applyBlockKey(properties));
    }

    @Override
    public void registerBlockItem(final Registry<Item> registry, final Item.Properties properties)
    {
        Registry.register(registry, getRegistryName(), new BlockItem(this, properties));
    }

    @Override
    public B registerBlock(final Registry<Block> registry)
    {
        Registry.register(registry, getRegistryName(), this);
        return (B) this;
    }
}
