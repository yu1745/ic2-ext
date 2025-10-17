package com.example.tileentity;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.ParametersAreNonnullByDefault;

public class TileEntityChargeableDirt extends TileEntity implements IEnergySink, ITickable {

    // 能量存储 (最大10000EU)
    private double energy = 0.0;
    private final double maxEnergy = 10000.0;
    private final double maxInput = 32.0; // 最大输入功率32EU/t
    private boolean addedToEnergyNet = false;

    public TileEntityChargeableDirt() {
        super();
    }

    @Override
    public void update() {
        if (!world.isRemote) {
            // 确保添加到能源网络
            if (!addedToEnergyNet) {
                addToEnergyNet();
            }

            // 每秒显示一次能量状态（用于调试）
            if (world.getTotalWorldTime() % 20 == 0 && energy > 0) {
                System.out.println("Chargeable Dirt at " + pos + " has " + energy + " EU");
            }
        }
    }

    @Override
    public void validate() {
        super.validate();
        if (!world.isRemote) {
            addToEnergyNet();
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (!world.isRemote) {
            removeFromEnergyNet();
        }
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        if (!world.isRemote) {
            removeFromEnergyNet();
        }
    }

    private void addToEnergyNet() {
        if (!addedToEnergyNet && !world.isRemote) {
            MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
            addedToEnergyNet = true;
        }
    }

    private void removeFromEnergyNet() {
        if (addedToEnergyNet && !world.isRemote) {
            MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
            addedToEnergyNet = false;
        }
    }

    // IEnergySink 接口实现

    @Override
    public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing side) {
        return true; // 接受所有方向的能量
    }

    @Override
    public double getDemandedEnergy() {
        return Math.max(0, maxEnergy - energy);
    }

    @Override
    public int getSinkTier() {
        return 1; // 接受LV等级的能量
    }

    @Override
    public double injectEnergy(EnumFacing directionFrom, double amount, double voltage) {
        double energyInjected = Math.min(amount, getDemandedEnergy());
        energy += energyInjected;

        if (energyInjected > 0) {
            System.out.println("Injected " + energyInjected + " EU into Chargeable Dirt at " + pos + ", total: " + energy + " EU");
        }

        return amount - energyInjected; // 返回未使用的能量
    }

    // NBT 持久化

    @Override
    @MethodsReturnNonnullByDefault
    public NBTTagCompound writeToNBT(@ParametersAreNonnullByDefault NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setDouble("energy", energy);
        return compound;
    }

    @Override
    public void readFromNBT(@ParametersAreNonnullByDefault NBTTagCompound compound) {
        super.readFromNBT(compound);
        energy = compound.getDouble("energy");
    }

    // 公共方法

    public double getEnergy() {
        return energy;
    }

    public double getMaxEnergy() {
        return maxEnergy;
    }

    public void setEnergy(double energy) {
        this.energy = Math.min(energy, maxEnergy);
    }
}