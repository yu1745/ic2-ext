package com.example.tileentity

import SyncField
import ic2.api.energy.EnergyNet
import ic2.api.energy.event.EnergyTileLoadEvent
import ic2.api.energy.event.EnergyTileUnloadEvent
import ic2.api.energy.tile.IEnergyEmitter
import ic2.api.energy.tile.IEnergySink
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.NonNullList
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.common.MinecraftForge
import mcp.MethodsReturnNonnullByDefault
import syncable
import javax.annotation.ParametersAreNonnullByDefault

/**
 * Kotlin版本的充电泥土TileEntity v2，使用自动同步委托系统
 */
class TileEntityChargeableDirt2 : TileEntity(), IEnergySink, ITickable, IInventory {

    // 使用自动同步的字段 - 不需要手动传递属性名
    @SyncField(0)
    var energy by syncable(0.0)


//    @SyncField(1)
    var maxInput by syncable(32.0)

    // 常量
    val maxEnergy = 10000.0
    private var addedToEnergyNet = false

    // 物品库存
    private val inventory = NonNullList.withSize(1, ItemStack.EMPTY)

    override fun update() {
        if (!world.isRemote) {
            // 确保添加到能源网络
            if (!addedToEnergyNet) {
                addToEnergyNet()
            }

            // 每秒显示一次能量状态（用于调试）
            if (world.totalWorldTime % 20 == 0L && energy > 0) {
                println("Chargeable Dirt2 at $pos has $energy EU")
            }
        }

        // 每 20 tick 同步给客户端
        if (world.totalWorldTime % 20 == 0L) {
            markDirty()
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2)
        }
    }

    override fun validate() {
        super.validate()
        if (!world.isRemote) {
            addToEnergyNet()
        }
    }

    override fun invalidate() {
        super.invalidate()
        if (!world.isRemote) {
            removeFromEnergyNet()
        }
    }

    override fun onChunkUnload() {
        super.onChunkUnload()
        if (!world.isRemote) {
            removeFromEnergyNet()
        }
    }

    private fun addToEnergyNet() {
        if (!addedToEnergyNet && !world.isRemote) {
            MinecraftForge.EVENT_BUS.post(EnergyTileLoadEvent(this))
            EnergyNet.instance.addTile(this)
            addedToEnergyNet = true
        }
    }

    private fun removeFromEnergyNet() {
        if (addedToEnergyNet && !world.isRemote) {
            MinecraftForge.EVENT_BUS.post(EnergyTileUnloadEvent(this))
            EnergyNet.instance.removeTile(this)
            addedToEnergyNet = false
        }
    }

    // IEnergySink 接口实现
    override fun acceptsEnergyFrom(emitter: IEnergyEmitter, side: EnumFacing?): Boolean = true

    override fun getDemandedEnergy(): Double = maxOf(0.0, maxEnergy - energy)

    override fun getSinkTier(): Int = 1

    override fun injectEnergy(directionFrom: EnumFacing?, amount: Double, voltage: Double): Double {
        val energyInjected = minOf(amount, getDemandedEnergy())
        energy += energyInjected

        if (energyInjected > 0) {
            println("Injected $energyInjected EU into Chargeable Dirt2 at $pos, total: $energy EU")
        }

        return amount - energyInjected
    }

    // IInventory 接口实现
    override fun getSizeInventory(): Int = inventory.size

    override fun isEmpty(): Boolean {
        for (stack in inventory) {
            if (!stack.isEmpty) {
                return false
            }
        }
        return true
    }

    @MethodsReturnNonnullByDefault
    override fun getStackInSlot(index: Int): ItemStack = inventory[index]

    @MethodsReturnNonnullByDefault
    override fun decrStackSize(index: Int, count: Int): ItemStack = ItemStackHelper.getAndSplit(inventory, index, count)

    @MethodsReturnNonnullByDefault
    override fun removeStackFromSlot(index: Int): ItemStack = ItemStackHelper.getAndRemove(inventory, index)

    override fun setInventorySlotContents(index: Int, stack: ItemStack) {
        inventory[index] = stack
        if (stack.count > this.getInventoryStackLimit()) {
            stack.count = this.getInventoryStackLimit()
        }
    }

    override fun getInventoryStackLimit(): Int = 64

    override fun markDirty() {
        super.markDirty()
    }

    override fun isUsableByPlayer(@MethodsReturnNonnullByDefault player: EntityPlayer): Boolean {
        return this.world.getTileEntity(this.pos) == this &&
                player.getDistanceSq(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5) <= 64.0
    }

    override fun openInventory(@MethodsReturnNonnullByDefault player: EntityPlayer) {}
    override fun closeInventory(@MethodsReturnNonnullByDefault player: EntityPlayer) {}

    override fun isItemValidForSlot(index: Int, stack: ItemStack): Boolean = true

    override fun getField(id: Int): Int = 0
    override fun setField(id: Int, value: Int) {}
    override fun getFieldCount(): Int = 0

    @MethodsReturnNonnullByDefault
    override fun clear() = inventory.clear()

    @MethodsReturnNonnullByDefault
    override fun getName(): String = "container.chargeable_dirt2"

    override fun hasCustomName(): Boolean = false

    @MethodsReturnNonnullByDefault
    override fun getDisplayName(): ITextComponent = TextComponentString(getName())

    // NBT 持久化
    @MethodsReturnNonnullByDefault
    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)
        compound.setDouble("energy", energy)
        compound.setDouble("maxInput", maxInput)
        ItemStackHelper.saveAllItems(compound, inventory)
        return compound
    }

    @ParametersAreNonnullByDefault
    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        energy = compound.getDouble("energy")
        maxInput = compound.getDouble("maxInput")
        ItemStackHelper.loadAllItems(compound, inventory)
    }

    // 公共方法
    fun getEnergyValue(): Double = energy
    fun getMaxEnergyValue(): Double = maxEnergy
    fun setEnergyValue(energy: Double) {
        this.energy = minOf(energy, maxEnergy)
    }
}