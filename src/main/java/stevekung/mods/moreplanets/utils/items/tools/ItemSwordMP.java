package stevekung.mods.moreplanets.utils.items.tools;

import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevekung.mods.moreplanets.core.MorePlanetsMod;
import stevekung.mods.moreplanets.integration.jei.MPJEIRecipes;
import stevekung.mods.moreplanets.utils.client.renderer.IItemModelRender;
import stevekung.mods.moreplanets.utils.items.EnumSortCategoryItem;
import stevekung.mods.moreplanets.utils.items.ISortableItem;

public class ItemSwordMP extends ItemSword implements ISortableItem, IItemModelRender
{
    private Item repairItem;
    private String name;

    public ItemSwordMP(String name, ToolMaterial material, Block block)
    {
        this(name, material, Item.getItemFromBlock(block));
    }

    public ItemSwordMP(String name, ToolMaterial material, Item item)
    {
        super(material);
        this.repairItem = item;
        this.setUnlocalizedName(name);
        MPJEIRecipes.collectAnvilList(name, this, item);
    }

    public ItemSwordMP(ToolMaterial material)
    {
        super(material);
    }

    @Override
    public Item setUnlocalizedName(String name)
    {
        this.name = name;
        return super.setUnlocalizedName(name);
    }

    @Override
    public CreativeTabs getCreativeTab()
    {
        return MorePlanetsMod.ITEM_TAB;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair)
    {
        if (repair.getItem() == this.repairItem)
        {
            return true;
        }
        return false;
    }

    @Override
    public EnumSortCategoryItem getItemCategory()
    {
        return EnumSortCategoryItem.SWORD;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack itemStack)
    {
        return ClientProxyCore.galacticraftItem;
    }
}