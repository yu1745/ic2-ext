package wangyu.gui

import wangyu.inventory.ContainerChargeableDirt
import wangyu.inventory.ContainerChargeableDirt2
import wangyu.tileentity.TileEntityChargeableDirt
import wangyu.tileentity.TileEntityChargeableDirt2
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler
import wangyu.ui.ExampleChargeableDirtGui

class GuiHandler : IGuiHandler {
    companion object {
        const val CHARGEABLE_DIRT_GUI = 0
        const val CHARGEABLE_DIRT2_GUI = 1
    }

    override fun getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        val te = world.getTileEntity(BlockPos(x, y, z))

        return when (ID) {
            CHARGEABLE_DIRT_GUI -> {
                if (te is TileEntityChargeableDirt) {
                    ContainerChargeableDirt(player, te)
                } else null
            }
            CHARGEABLE_DIRT2_GUI -> {
                if (te is TileEntityChargeableDirt2) {
                    ContainerChargeableDirt2(player, te)
                } else null
            }
            else -> null
        }
    }

    override fun getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        val te = world.getTileEntity(BlockPos(x, y, z))

        return when (ID) {
            CHARGEABLE_DIRT_GUI -> {
                if (te is TileEntityChargeableDirt) {
                    GuiChargeableDirt(player.inventory, te)
                } else null
            }
            CHARGEABLE_DIRT2_GUI -> {
                if (te is TileEntityChargeableDirt2) {
                    ExampleChargeableDirtGui(player.inventory, te)
                } else null
            }
            else -> null
        }
    }
}