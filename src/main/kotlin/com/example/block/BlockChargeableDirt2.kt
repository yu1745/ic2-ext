package com.example.block

import com.example.SampleMod112
import com.example.gui.GuiHandler
import com.example.tileentity.TileEntityChargeableDirt2
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*

/**
 * 充电泥土方块 v2
 */
class BlockChargeableDirt2 : Block(Material.GROUND) {

    init {
        translationKey = "chargeable_dirt2"
//        registryName = "chargeable_dirt2"
        setRegistryName("chargeable_dirt2")
        setHardness(0.5f)
        setResistance(0.5f)
//        creativeTab = SampleMod112.creativeTab
    }

    override fun hasTileEntity(state: IBlockState): Boolean = true

    override fun createTileEntity(world: World, state: IBlockState): TileEntity {
        return TileEntityChargeableDirt2()
    }

    override fun onBlockActivated(
        worldIn: World,
        pos: BlockPos,
        state: IBlockState,
        playerIn: EntityPlayer,
        hand: EnumHand,
        facing: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): Boolean {
        if (!worldIn.isRemote) {
            playerIn.openGui(SampleMod112.instance, GuiHandler.CHARGEABLE_DIRT2_GUI, worldIn, pos.x, pos.y, pos.z)
        }
        return true
    }

    override fun breakBlock(world: World, pos: BlockPos, state: IBlockState) {
        val te = world.getTileEntity(pos)
        if (te is TileEntityChargeableDirt2) {
            val energy = te.energy
            if (energy > 0) {
                println("Breaking Chargeable Dirt2 with " + energy + " EU stored")
            }
        }
        super.breakBlock(world, pos, state)
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        return Item.getItemFromBlock(this)
    }
}