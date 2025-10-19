package wangyu.gui;

import wangyu.inventory.ContainerChargeableDirt;
import wangyu.tileentity.TileEntityChargeableDirt;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiChargeableDirt extends GuiContainer {
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("textures/gui/container/dispenser.png");
    private final InventoryPlayer playerInventory;
    private final TileEntityChargeableDirt tileEntity;

    public GuiChargeableDirt(InventoryPlayer playerInventory, TileEntityChargeableDirt tileEntity) {
        super(new ContainerChargeableDirt(playerInventory.player, tileEntity));
        this.playerInventory = playerInventory;
        this.tileEntity = tileEntity;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String s = this.tileEntity.getDisplayName().getUnformattedComponentText();
        this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
        this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedComponentText(), 8, this.ySize - 96 + 2, 4210752);

        // 显示能量信息
        double energy = this.tileEntity.getEnergy();
        double maxEnergy = this.tileEntity.getMaxEnergy();
        String energyText = String.format("能量: %.1f / %.1f EU", energy, maxEnergy);
        this.fontRenderer.drawString(energyText, 8, 20, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        // 设置渲染颜色为白色（无色调）
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        // 绑定通用容器背景纹理
        this.mc.getTextureManager().bindTexture(GUI_TEXTURE);

        // 计算GUI的起始位置（居中显示）
        int i = (this.width - this.xSize) / 2;  // X坐标偏移
        int j = (this.height - this.ySize) / 2; // Y坐标偏移

        // 绘制GUI背景（只绘制上半部分，因为我们只有1个槽位）
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, 83);

        // 绘制玩家背包部分
        this.drawTexturedModalRect(i, j + 83, 0, 83, this.xSize, 96);

        // 绘制能量条背景（深色矩形）
        this.drawGradientRect(i + 8, j + 50, i + 168, j + 58, 0xFF000000, 0xFF000000);

        // 绘制能量条填充
        double energy = this.tileEntity.getEnergy();     // 获取当前能量
        double maxEnergy = this.tileEntity.getMaxEnergy(); // 获取最大能量
        if (maxEnergy > 0) {
            int energyWidth = (int) ((energy / maxEnergy) * 160);
            if (energyWidth > 0) {
                // 绘制能量条（绿色渐变）
                this.drawGradientRect(i + 8, j + 50, i + 8 + energyWidth, j + 58, 0xFF00FF00, 0xFF00AA00);
            }
        }
    }
}