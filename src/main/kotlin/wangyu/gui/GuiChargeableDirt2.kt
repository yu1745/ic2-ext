package wangyu.gui

import wangyu.inventory.ContainerChargeableDirt2
import wangyu.tileentity.TileEntityChargeableDirt2
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation

/**
 * 充电泥土GUI v2
 */
class GuiChargeableDirt2(
    private val playerInventory: InventoryPlayer,
    private val tile: TileEntityChargeableDirt2
) : GuiContainer(ContainerChargeableDirt2(playerInventory.player, tile)) {
    companion object {
        private val TEXTURE = ResourceLocation("minecraft", "textures/gui/container/dispenser.png")
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        this.fontRenderer.drawString(tile.displayName.unformattedText, 8, 6, 4210752)
        this.fontRenderer.drawString(playerInventory.displayName.unformattedText, 8, this.ySize - 96 + 2, 4210752)

        // 显示能量信息
        val energyText = "Energy: ${tile.energy.toInt()} / ${tile.getMaxEnergyValue().toInt()} EU"
        this.fontRenderer.drawString(energyText, 8, 20, 4210752)

        // 显示进度条百分比
        val percentage = (tile.energy / tile.getMaxEnergyValue() * 100).toInt()
        val percentageText = "$percentage%"
        this.fontRenderer.drawString(percentageText, 150, 20, 4210752)
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        this.mc.textureManager.bindTexture(TEXTURE)
        val x = (this.width - this.xSize) / 2
        val y = (this.height - this.ySize) / 2
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize)

        // 绘制能量进度条
        val energyPercentage = (tile.energy / tile.getMaxEnergyValue()).toFloat()
        val progressBarWidth = (energyPercentage * 160).toInt()
        this.drawTexturedModalRect(x + 8, y + 40, 0, 166, progressBarWidth, 10)
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
        this.renderHoveredToolTip(mouseX, mouseY)
    }
}