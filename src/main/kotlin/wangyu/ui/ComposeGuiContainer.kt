package wangyu.ui

import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.inventory.Container
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.awt.Color

/**
 * 基于Compose DSL的Minecraft GUI容器
 * @param container 容器对象
 * @param uiRoot UI根组件
 */
abstract class ComposeGuiContainer(
    container: Container
) : GuiContainer(container) {

    protected lateinit var claimCache: Component

    init {
//        renderCache = render()
    }

    fun getFontRender() = fontRenderer


    /**
     * GUI背景纹理
     */
    private val backgroundTexture: ResourceLocation? = null

    /**
     * 缩放因子，用于适配Minecraft的GUI坐标系统
     */
    protected val scale = 1.0f

    /**
     * UI根组件（懒加载）
     */
//    private var uiRoot: Component?     = null

    init {
        // UI将在第一次渲染时创建
    }

    /**
     * 创建UI根组件（子类必须实现）
     */
    protected abstract fun claim(): Component

    /**
     * 获取UI根组件（懒加载）
     */
//    private fun getUIRoot(): Component {
//        if (uiRoot == null) {
//            uiRoot = render()
//            // 执行布局计算
//            LayoutCalculator.layout(uiRoot!!)
//            // 设置GUI尺寸
//            uiRoot!!.bounds?.let { bounds ->
//                xSize = bounds.width
//                ySize = bounds.height
//            }
//        }
//        return uiRoot!!
//    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {

        // 绘制自定义前景内容（可被子类重写）
        drawCustomForeground(mouseX, mouseY)

        // 设置GUI上下文
        GuiContext.setGui(this)

        // 绘制UI组件
        claimCache = claim()
        // 执行布局计算
        claimCache.layout()
        //声明->渲染
        claimCache.render()

        // 清除GUI上下文
        GuiContext.clearGui()

    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        // 背景绘制在前景层进行，这里可以绘制自定义背景
        drawCustomBackground(partialTicks, mouseX, mouseY)
    }

    /**
     * 自定义背景绘制（可被子类重写）
     */
    protected open fun drawCustomBackground(partialTicks: Float, mouseX: Int, mouseY: Int) {
        // 子类可重写此方法添加自定义背景绘制
    }

    /**
     * 自定义前景绘制（可被子类重写）
     */
    protected open fun drawCustomForeground(mouseX: Int, mouseY: Int) {
        // 子类可重写此方法添加自定义前景绘制
    }

    /**
     * 绘制鼠标悬停提示（可被子类重写）
     */
//    protected open fun drawMouseover(mouseX: Int, mouseY: Int) {
//        // 子类可重写此方法添加自定义悬停提示
//    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)

        // 处理鼠标悬停事件
        handleMouseMove(mouseX, mouseY)

        this.renderHoveredToolTip(mouseX, mouseY)
    }

    /**
     * 检查点是否在组件内部
     */
    private fun isPointInComponent(component: Component, x: Int, y: Int): Boolean {
        val bounds = component.bounds ?: return false
        return x >= bounds.x && x < bounds.x + bounds.width && y >= bounds.y && y < bounds.y + bounds.height
    }

    /**
     * 获取指定坐标下的组件
     */
    protected fun getComponentAt(component: Component, x: Int, y: Int): Component? {
        if (!isPointInComponent(component, x, y)) return null

        // 如果是容器，优先返回子组件
        if (component is ContainerComponent) {
            component.children.forEach { child ->
                val childAt = getComponentAt(child, x - component.bounds!!.x, y - component.bounds!!.y)
                if (childAt != null) return childAt
            }
        }

        return component
    }

    /**
     * 处理鼠标点击事件
     */
    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)

        // 调整坐标到UI坐标系
        val adjustedX = ((mouseX - guiLeft) * scale).toInt()
        val adjustedY = ((mouseY - guiTop) * scale).toInt()

        val clickedComponent = getComponentAt(claimCache, adjustedX, adjustedY)
        if (clickedComponent != null) {
            // 调用组件的onClick处理器
            clickedComponent.onClick?.invoke(adjustedX, adjustedY)
            // 调用父类的组件点击回调
//            onComponentClicked(clickedComponent, adjustedX, adjustedY, mouseButton)
        }
    }

    /**
     * 组件点击回调（可被子类重写）
     */
//    protected open fun onComponentClicked(component: Component, x: Int, y: Int, mouseButton: Int) {
//        // 子类可重写此方法处理组件点击事件
//    }

    /**
     * 处理鼠标移动事件
     */
    private var lastHoveredComponent: Component? = null

    private fun handleMouseMove(mouseX: Int, mouseY: Int) {
        // 调整坐标到UI坐标系
        val adjustedX = ((mouseX - guiLeft) * scale).toInt()
        val adjustedY = ((mouseY - guiTop) * scale).toInt()

        val hoveredComponent = getComponentAt(claimCache, adjustedX, adjustedY)

        // 如果悬停的组件发生变化
        if (hoveredComponent != lastHoveredComponent) {
            lastHoveredComponent = hoveredComponent
        }

        // 调用当前悬停组件的onHover处理器
        hoveredComponent?.onHover?.invoke(adjustedX, adjustedY)
    }


    /**
     * 绘制线条
     */
    fun drawLine(x1: Float, y1: Float, x2: Float, y2: Float, color: Color, width: Float = 1.0f) {
        GlStateManager.disableTexture2D()
        GlStateManager.enableBlend()
        GlStateManager.disableAlpha()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        GlStateManager.shadeModel(GL11.GL_SMOOTH)

        GL11.glLineWidth(width)
        GL11.glBegin(GL11.GL_LINES)
        GL11.glColor4f(color.red / 255.0f, color.green / 255.0f, color.blue / 255.0f, color.alpha / 255.0f)
        GL11.glVertex2f(x1, y1)
        GL11.glVertex2f(x2, y2)
        GL11.glEnd()

        GlStateManager.shadeModel(GL11.GL_FLAT)
        GlStateManager.disableBlend()
        GlStateManager.enableAlpha()
        GlStateManager.enableTexture2D()
        // 重置颜色为白色，避免影响后续绘制
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
    }

    /**
     * 绘制矩形边框
     */
    fun drawRectBorder(x: Float, y: Float, width: Float, height: Float, color: Color, borderWidth: Float = 1.0f) {
        drawLine(x, y, x + width, y, color, borderWidth)
        drawLine(x + width, y, x + width, y + height, color, borderWidth)
        drawLine(x + width, y + height, x, y + height, color, borderWidth)
        drawLine(x, y + height, x, y, color, borderWidth)
    }

    /**
     * 绘制填充矩形
     */
    fun drawFilledRect(x: Float, y: Float, width: Float, height: Float, color: Color) {
        GlStateManager.disableTexture2D()
        GlStateManager.enableBlend()
        GlStateManager.disableAlpha()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)

        GL11.glBegin(GL11.GL_QUADS)
        GL11.glColor4f(color.red / 255.0f, color.green / 255.0f, color.blue / 255.0f, color.alpha / 255.0f)
        GL11.glVertex2f(x, y)
        GL11.glVertex2f(x + width, y)
        GL11.glVertex2f(x + width, y + height)
        GL11.glVertex2f(x, y + height)
        GL11.glEnd()

        GlStateManager.disableBlend()
        GlStateManager.enableAlpha()
        GlStateManager.enableTexture2D()
        // 重置颜色为白色，避免影响后续绘制
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
    }

    /**
     * 绘制圆角矩形
     */
    fun drawRoundedRect(
        x: Float, y: Float, width: Float, height: Float, radius: Float, color: Color, filled: Boolean = true
    ) {
        GlStateManager.disableTexture2D()
        GlStateManager.enableBlend()
        GlStateManager.disableAlpha()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)

        GL11.glColor4f(color.red / 255.0f, color.green / 255.0f, color.blue / 255.0f, color.alpha / 255.0f)

        if (filled) {
            GL11.glBegin(GL11.GL_POLYGON)
        } else {
            GL11.glBegin(GL11.GL_LINE_LOOP)
        }

        // 绘制四个圆角的点
        val segments = 16
        for (i in 0 until segments) {
            val angle1 = Math.PI * 2 * i / segments
            val angle2 = Math.PI * 2 * (i + 1) / segments

            // 左上角
            if (x + radius <= x && y + radius <= y) {
                GL11.glVertex2f(
                    x + radius + Math.cos(angle1).toFloat() * radius, y + radius + Math.sin(angle1).toFloat() * radius
                )
            }
            // 右上角
            if (x + width - radius >= x + width && y + radius <= y) {
                GL11.glVertex2f(
                    x + width - radius + Math.cos(angle1).toFloat() * radius,
                    y + radius + Math.sin(angle1).toFloat() * radius
                )
            }
            // 右下角
            if (x + width - radius >= x + width && y + height - radius >= y + height) {
                GL11.glVertex2f(
                    x + width - radius + Math.cos(angle1).toFloat() * radius,
                    y + height - radius + Math.sin(angle1).toFloat() * radius
                )
            }
            // 左下角
            if (x + radius <= x && y + height - radius >= y + height) {
                GL11.glVertex2f(
                    x + radius + Math.cos(angle1).toFloat() * radius,
                    y + height - radius + Math.sin(angle1).toFloat() * radius
                )
            }
        }

        GL11.glEnd()

        GlStateManager.disableBlend()
        GlStateManager.enableAlpha()
        GlStateManager.enableTexture2D()
        // 重置颜色为白色，避免影响后续绘制
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
    }

    /**
     * 绘制圆形
     */
    fun drawCircle(centerX: Float, centerY: Float, radius: Float, color: Color, filled: Boolean = true) {
        GlStateManager.disableTexture2D()
        GlStateManager.enableBlend()
        GlStateManager.disableAlpha()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)

        GL11.glColor4f(color.red / 255.0f, color.green / 255.0f, color.blue / 255.0f, color.alpha / 255.0f)

        if (filled) {
            GL11.glBegin(GL11.GL_TRIANGLE_FAN)
            GL11.glVertex2f(centerX, centerY)
        } else {
            GL11.glBegin(GL11.GL_LINE_LOOP)
        }

        val segments = 32
        for (i in 0..segments) {
            val angle = 2.0 * Math.PI * i / segments
            val x = centerX + Math.cos(angle).toFloat() * radius
            val y = centerY + Math.sin(angle).toFloat() * radius
            GL11.glVertex2f(x, y)
        }

        GL11.glEnd()

        GlStateManager.disableBlend()
        GlStateManager.enableAlpha()
        GlStateManager.enableTexture2D()
        // 重置颜色为白色，避免影响后续绘制
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
    }
}