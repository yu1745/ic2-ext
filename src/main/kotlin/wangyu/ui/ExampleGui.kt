package wangyu.ui

import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.text.TextComponentString
import wangyu.inventory.ContainerChargeableDirt2
import wangyu.tileentity.TileEntityChargeableDirt2
import java.awt.Color

/**
 * 基于Compose DSL的充电泥土GUI示例
 */
class ExampleChargeableDirtGui(
    private val playerInventory: InventoryPlayer,
    private val tile: TileEntityChargeableDirt2
) : ComposeGuiContainer(ContainerChargeableDirt2(playerInventory.player, tile)) {

    override fun claim(): Component {
//        println("render()")
        return Compose.compose {
            Column(x = null, y = null, width = 176, height = 166) {
                // 标题区域
                Row(x = 8, y = 6, height = 16) {
                    Text(tile.displayName.unformattedText)
                }

                // 能量信息区域
                Row(x = null, y = null,width = 160, height = 16, onHover = { mouseX, mouseY ->
                    val energyText = "能量: ${tile.energy.toInt()} / ${tile.getMaxEnergyValue().toInt()} EU"
                    drawHoveringText(listOf(energyText), mouseX, mouseY, fontRenderer)
                }, onClick = { x, y ->
                    playerInventory.player.sendMessage(TextComponentString("nmsl"));
                }) {
                    Text("Energy: ${tile.energy.toInt()}/${tile.getMaxEnergyValue().toInt()} EU")
                    val percentage = (tile.energy / tile.getMaxEnergyValue() * 100).toInt()
                    Text("$percentage%")
                }

                // 玩家物品栏标题
                Row(x = 8, y = 70, width = 160, height = 16) {
                    Text(playerInventory.displayName.unformattedText)
                }
            }
        }
    }

    /**
     * 自定义前景绘制，显示动态信息
     */
    override fun drawCustomForeground(mouseX: Int, mouseY: Int) {
        // 显示实时的能量进度条
        drawEnergyBar()
    }

    /**
     * 绘制能量进度条
     */
    private fun drawEnergyBar() {
        val x = 8
        val y = 40
        val width = 160
        val height = 10

        // 绘制进度条背景
        drawRect(x, y, x + width, y + height, Color(64, 64, 64, 255).rgb)

        // 计算能量进度
        val energyPercentage = (tile.energy / tile.getMaxEnergyValue()).toFloat()
        val progressWidth = (energyPercentage * width).toInt()

        // 绘制进度条前景
        val progressColor = when {
            energyPercentage > 0.75f -> Color(0, 255, 0, 255).rgb // 绿色
            energyPercentage > 0.25f -> Color(255, 255, 0, 255).rgb // 黄色
            else -> Color(255, 0, 0, 255).rgb // 红色
        }

        drawRect(x, y, x + progressWidth, y + height, progressColor)

        // 绘制边框
        drawHorizontalLine(x, x + width, y, Color.GRAY.rgb)
        drawHorizontalLine(x, x + width, y + height, Color.GRAY.rgb)
        drawVerticalLine(x, y, y + height, Color.GRAY.rgb)
        drawVerticalLine(x + width, y, y + height, Color.GRAY.rgb)
    }
}