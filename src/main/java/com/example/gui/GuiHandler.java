package com.example.gui;

import com.example.inventory.ContainerChargeableDirt;
import com.example.tileentity.TileEntityChargeableDirt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
    public static final int CHARGEABLE_DIRT_GUI = 0;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == CHARGEABLE_DIRT_GUI) {
            TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
            if (te instanceof TileEntityChargeableDirt) {
                return new ContainerChargeableDirt(player, (TileEntityChargeableDirt) te);
            }
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == CHARGEABLE_DIRT_GUI) {
            TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
            if (te instanceof TileEntityChargeableDirt) {
                return new GuiChargeableDirt(player.inventory, (TileEntityChargeableDirt) te);
            }
        }
        return null;
    }
}