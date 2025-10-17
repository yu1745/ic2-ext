package com.example.block;

import com.example.SampleMod112;
import com.example.tileentity.TileEntityChargeableDirt;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class BlockChargeableDirt extends Block {

    public BlockChargeableDirt() {
        super(Material.GROUND);
        setTranslationKey("chargeable_dirt");
        setRegistryName("chargeable_dirt");
        setHardness(0.5F);
        setResistance(0.5F);
        setCreativeTab(SampleMod112.creativeTab);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityChargeableDirt();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityChargeableDirt) {
                TileEntityChargeableDirt chargeableDirt = (TileEntityChargeableDirt) te;
                double energy = chargeableDirt.getEnergy();
                double maxEnergy = chargeableDirt.getMaxEnergy();

                player.sendMessage(new TextComponentString("可充电泥土能量: " +
                    String.format("%.1f", energy) + " / " + String.format("%.1f", maxEnergy) + " EU"));
            } else {
                player.sendMessage(new TextComponentString("这是一个可充电泥土方块"));
            }
        }
        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityChargeableDirt) {
            TileEntityChargeableDirt chargeableDirt = (TileEntityChargeableDirt) te;
            double energy = chargeableDirt.getEnergy();
            if (energy > 0) {
                System.out.println("Breaking Chargeable Dirt with " + energy + " EU stored");
            }
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public Item getItemDropped(IBlockState state, java.util.Random rand, int fortune) {
        return Item.getItemFromBlock(this);
    }
}