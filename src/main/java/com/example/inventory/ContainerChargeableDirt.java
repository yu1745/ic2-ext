package com.example.inventory;

import com.example.tileentity.TileEntityChargeableDirt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerChargeableDirt extends Container {
    private final TileEntityChargeableDirt tileEntity;
    private double energy;
//    private double maxEnergy;

    public ContainerChargeableDirt(EntityPlayer player, TileEntityChargeableDirt tileEntity) {
        this.tileEntity = tileEntity;

        // 添加单个物品槽位
        this.addSlotToContainer(new Slot(tileEntity, 0, 80, 35));

        // 添加玩家背包槽位
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        // 添加玩家快捷栏槽位
        for (int k = 0; k < 9; ++k) {
            this.addSlotToContainer(new Slot(player.inventory, k, 8 + k * 18, 142));
        }
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendAllWindowProperties(this, this.tileEntity);
//        this.energy = this.tileEntity.getEnergy();
//        this.maxEnergy = this.tileEntity.getMaxEnergy();
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        for (int i = 0; i < this.listeners.size(); ++i) {
            IContainerListener icontainerlistener = this.listeners.get(i);
            if (this.energy != this.tileEntity.getEnergy()) {
                icontainerlistener.sendWindowProperty(this, 0, (int) this.tileEntity.getEnergy());
            }
        }

        this.energy = this.tileEntity.getEnergy();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {
        if (id == 0) {
//            this.energy = data;
            this.tileEntity.setEnergy(data);
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.tileEntity.isUsableByPlayer(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index == 0) {
                // 从方块槽位移动到玩家背包
                if (!this.mergeItemStack(itemstack1, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(itemstack1, itemstack);
            } else {
                // 从玩家背包移动到方块槽位
                if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }

    public double getEnergy() {
        return this.tileEntity.getEnergy();
    }

    public double getMaxEnergy() {
        return this.tileEntity.getMaxEnergy();
    }
}