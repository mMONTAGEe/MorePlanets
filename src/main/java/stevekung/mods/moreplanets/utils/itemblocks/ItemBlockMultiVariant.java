package stevekung.mods.moreplanets.utils.itemblocks;

import net.minecraft.block.Block;
import stevekung.mods.moreplanets.utils.blocks.IBlockVariants;

@Deprecated //TODO Remove 1.13
public class ItemBlockMultiVariant extends ItemBlockBaseMP
{
    public ItemBlockMultiVariant(Block block)
    {
        super(block);
    }

    @Override
    protected String[] getBlockVariantsName()
    {
        if (this.block instanceof IBlockVariants)
        {
            return ((IBlockVariants)this.block).getVariantsName().getNameList();
        }
        return new String[] {};
    }
}