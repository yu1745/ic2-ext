package com.example.inventory

import KotlinSyncContainer
import com.example.tileentity.TileEntityChargeableDirt2
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack

/**
 * 充电泥土Container v2，使用自动同步系统
 */
class ContainerChargeableDirt2(
    player: EntityPlayer,
    tile: TileEntityChargeableDirt2
) : KotlinSyncContainer<TileEntityChargeableDirt2>(tile) {

    init {
        // 添加单个物品槽位
        this.addSlotToContainer(Slot(tile, 0, 80, 35))

        // 添加玩家背包槽位
        for (i in 0 until 3) {
            for (j in 0 until 9) {
                this.addSlotToContainer(Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18))
            }
        }

        // 添加玩家快捷栏槽位
        for (k in 0 until 9) {
            this.addSlotToContainer(Slot(player.inventory, k, 8 + k * 18, 142))
        }
    }

    override fun canInteractWith(playerIn: EntityPlayer): Boolean {
        return tile.isUsableByPlayer(playerIn)
    }

    override fun transferStackInSlot(playerIn: EntityPlayer, index: Int): ItemStack {
        var itemstack = ItemStack.EMPTY
        val slot = this.inventorySlots[index]

        if (slot != null && slot.hasStack) {
            val itemstack1 = slot.stack
            itemstack = itemstack1.copy()

            if (index == 0) {
                // 从方块槽位移动到玩家背包
                if (!this.mergeItemStack(itemstack1, 1, 37, true)) {
                    return ItemStack.EMPTY
                }
                slot.onSlotChange(itemstack1, itemstack)
            } else {
                // 从玩家背包移动到方块槽位
                if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                    return ItemStack.EMPTY
                }
            }

            if (itemstack1.isEmpty) {
                slot.putStack(ItemStack.EMPTY)
            } else {
                slot.onSlotChanged()
            }

            if (itemstack1.count == itemstack.count) {
                return ItemStack.EMPTY
            }

            slot.onTake(playerIn, itemstack1)
        }

        return itemstack
    }

    // 直接访问tile的字段，不需要额外的getter方法
//    fun getEnergy(): Double = tile.energy
//    fun getMaxEnergy(): Double = tile.maxEnergy
}