package stevekung.mods.moreplanets.module.planets.fronos.entity;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevekung.mods.moreplanets.init.MPSounds;
import stevekung.mods.moreplanets.module.planets.fronos.blocks.FronosBlocks;
import stevekung.mods.moreplanets.module.planets.fronos.entity.ai.EntityAIFaceTexture;
import stevekung.mods.moreplanets.module.planets.fronos.entity.ai.EntityAIFronosPanic;
import stevekung.mods.moreplanets.module.planets.fronos.entity.ai.EntityAIFronosTempt;
import stevekung.mods.moreplanets.module.planets.fronos.items.FronosItems;
import stevekung.mods.moreplanets.util.entity.ai.PathNavigateGroundMP;

public abstract class EntityFronosPet extends EntityTameable
{
    protected int timeUntilToDropItem;
    public int closeEyeTimer;
    public int panicTimer;
    public int hungryTimer;
    protected EntityAIFaceTexture aiTexture;
    protected EntityAIFronosPanic aiPanic;
    protected EntityAIFronosTempt aiTempt;

    public EntityFronosPet(World world)
    {
        super(world);
    }

    @Override
    protected PathNavigate getNewNavigator(World world)
    {
        return new PathNavigateGroundMP(this, world);
    }

    @Override
    protected void updateAITasks()
    {
        super.updateAITasks();
        this.closeEyeTimer = this.aiTexture.getTimer();
        this.panicTimer = this.aiPanic.getTimer();
        this.hungryTimer = this.aiTempt.getTimer();
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if (this.worldObj.isRemote)
        {
            this.closeEyeTimer = Math.max(0, this.closeEyeTimer - 1);
            this.panicTimer = Math.max(0, this.panicTimer - 1);

            if (!this.aiTempt.shouldExecute())
            {
                this.hungryTimer = Math.max(0, this.hungryTimer - 1);
            }
        }
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        if (this.getLayItem() != null && !this.isChild() && !this.worldObj.isRemote && --this.timeUntilToDropItem <= 0)
        {
            this.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            this.entityDropItem(this.getLayItem(), 1.0F);
            this.timeUntilToDropItem = this.rand.nextInt(6000) + 2000;
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);

        if (nbt.hasKey("ItemLayTime"))
        {
            this.timeUntilToDropItem = nbt.getInteger("ItemLayTime");
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("ItemLayTime", this.timeUntilToDropItem);
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();

        if (this.isTamed())
        {
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(12.0D);
        }
        else
        {
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
        }
    }

    @Override
    protected void playStepSound(BlockPos pos, Block block)
    {
        this.playSound(MPSounds.FRONOS_MOB_STEP, 0.15F, 1.0F);
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return MPSounds.FRONOS_MOB_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound()
    {
        return MPSounds.FRONOS_MOB_HURT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return MPSounds.FRONOS_MOB_DEATH;
    }

    @Override
    protected int getExperiencePoints(EntityPlayer player)
    {
        return 1 + this.worldObj.rand.nextInt(4);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (this.isEntityInvulnerable(source))
        {
            return false;
        }
        else
        {
            Entity entity = source.getEntity();

            if (this.aiSit != null)
            {
                this.aiSit.setSitting(false);
            }
            if (entity != null && !(entity instanceof EntityPlayer) && !(entity instanceof EntityArrow))
            {
                amount = (amount + 1.0F) / 2.0F;
            }
            return super.attackEntityFrom(source, amount);
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn)
    {
        boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (int)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());

        if (flag)
        {
            this.applyEnchantments(this, entityIn);
        }
        return flag;
    }

    @Override
    public void setTamed(boolean tamed)
    {
        super.setTamed(tamed);

        if (tamed)
        {
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
        }
        else
        {
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
        }
    }

    @Override
    public boolean getCanSpawnHere()
    {
        return this.worldObj.getBlockState(this.getPosition().down()).getBlock() == FronosBlocks.FRONOS_GRASS;
    }

    @Override
    public boolean isBreedingItem(ItemStack itemStack)
    {
        return itemStack != null && itemStack.getItem() == FronosItems.FRONOS_FOOD && itemStack.getItemDamage() == 0;
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand, @Nullable ItemStack itemStack)
    {
        boolean isTamedItem = itemStack != null && itemStack.getItem() == FronosItems.FRONOS_FOOD && itemStack.getItemDamage() == 1;

        if (this.isTamed())
        {
            if (isTamedItem)
            {
                this.heal(5.0F);

                if (!player.capabilities.isCreativeMode)
                {
                    --itemStack.stackSize;
                }
                if (this.aiSit != null)
                {
                    this.aiSit.setSitting(!this.isSitting());
                }
                return true;
            }

            if (this.isOwner(player) && !this.worldObj.isRemote)
            {
                if (this.aiSit != null)
                {
                    this.aiSit.setSitting(!this.isSitting());
                    this.isJumping = false;
                    this.navigator.clearPathEntity();
                }
            }
        }
        else if (isTamedItem)
        {
            if (!player.capabilities.isCreativeMode)
            {
                itemStack.stackSize--;
            }
            if (!this.worldObj.isRemote)
            {
                if (this.rand.nextInt(3) == 0)
                {
                    this.setTamed(true);
                    this.setHealth(20.0F);
                    this.navigator.clearPathEntity();
                    this.setOwnerId(player.getUniqueID());
                    this.playTameEffect(true);
                    this.worldObj.setEntityState(this, (byte)7);
                }
                else
                {
                    this.playTameEffect(false);
                    this.worldObj.setEntityState(this, (byte)6);
                }
            }
            return true;
        }
        return super.processInteract(player, hand, itemStack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id)
    {
        if (id == 10)
        {
            this.closeEyeTimer = 20;
        }
        else if (id == 11)
        {
            this.panicTimer = 30;
        }
        else if (id == 12)
        {
            this.hungryTimer = 10;
        }
        else
        {
            super.handleStatusUpdate(id);
        }
    }

    @Override
    public boolean shouldAttackEntity(EntityLivingBase target, EntityLivingBase owner)
    {
        return false;
    }

    public boolean isCloseEye()
    {
        return this.closeEyeTimer > 0;
    }

    public boolean isHungry()
    {
        return this.hungryTimer > 0;
    }

    public boolean isPanic()
    {
        return this.panicTimer > 0;
    }

    protected abstract ItemStack getLayItem();
}