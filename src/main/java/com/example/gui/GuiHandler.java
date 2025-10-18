package com.example.gui;

import com.example.inventory.ContainerChargeableDirt;
import com.example.inventory.ContainerChargeableDirt2;
import com.example.tileentity.TileEntityChargeableDirt;
import com.example.tileentity.TileEntityChargeableDirt2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
    public static final int CHARGEABLE_DIRT_GUI = 0;
    public static final int CHARGEABLE_DIRT2_GUI = 1;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));

        if (ID == CHARGEABLE_DIRT_GUI) {
            if (te instanceof TileEntityChargeableDirt) {
                return new ContainerChargeableDirt(player, (TileEntityChargeableDirt) te);
            }
        } else if (ID == CHARGEABLE_DIRT2_GUI) {
            if (te instanceof TileEntityChargeableDirt2) {
                return new ContainerChargeableDirt2(player, (TileEntityChargeableDirt2) te);
            }
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));

        if (ID == CHARGEABLE_DIRT_GUI) {
            if (te instanceof TileEntityChargeableDirt) {
                return new GuiChargeableDirt(player.inventory, (TileEntityChargeableDirt) te);
            }
        } else if (ID == CHARGEABLE_DIRT2_GUI) {
            if (te instanceof TileEntityChargeableDirt2) {
                return new GuiChargeableDirt2(player.inventory, (TileEntityChargeableDirt2) te);
            }
        }
        return null;
    }
}