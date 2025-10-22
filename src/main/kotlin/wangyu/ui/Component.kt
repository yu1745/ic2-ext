package wangyu.ui

/**
 * 布局边界信息
 */
data class Bounds(
    val x: Int, val y: Int, val width: Int, val height: Int, val absolute: Boolean = false
)

/**
 * 边距和内边距信息
 */
data class Spacing(
    val left: Int = 0,
    val top: Int = 0,
    val right: Int = 0,
    val bottom: Int = 0
) {
    companion object {
        val ZERO = Spacing(0, 0, 0, 0)
    }

    fun horizontal() = left + right
    fun vertical() = top + bottom
}

/**
 * GUI渲染上下文，用于ThreadLocal存储
 */
object GuiContext {
    private val guiContainer = ThreadLocal<ComposeGuiContainer>()

    /**
     * 设置当前GUI容器
     */
    fun setGui(gui: ComposeGuiContainer) {
        guiContainer.set(gui)
    }

    /**
     * 获取当前GUI容器
     */
    fun getGui(): ComposeGuiContainer? = guiContainer.get()

    /**
     * 清除GUI容器
     */
    fun clearGui() {
        guiContainer.remove()
    }

    /**
     * 获取字体渲染器
     */
    fun getFontRenderer() = getGui()?.getFontRender()
}

/**
 * 定义基础组件接口，所有组件都实现它
 */
interface Component {
    /**
     * 渲染组件
     */
    fun render()

    /**
     * 布局组件，计算bounds
     */
    fun layout()

    /**
     * 布局边界（在布局过程中计算）
     */
    var bounds: Bounds?

    /**
     * 外边距（组件之间的间距）
     */
    var margin: Spacing

    /**
     * 内边距（组件内容与边框的距离）
     */
    var padding: Spacing

    /**
     * 鼠标悬停处理器（可选）
     */
    var onHover: ((mouseX: Int, mouseY: Int) -> Unit)?

    /**
     * 鼠标点击处理器（可选）
     */
    var onClick: ((mouseX: Int, mouseY: Int) -> Unit)?
}

/**
 * 文本组件
 */
data class TextComponent(
    val text: String,
    override var margin: Spacing = Spacing.ZERO,
    override var padding: Spacing = Spacing.ZERO,
    override var onHover: ((mouseX: Int, mouseY: Int) -> Unit)? = null,
    override var onClick: ((mouseX: Int, mouseY: Int) -> Unit)? = null
) : Component {
    override var bounds: Bounds? = null

    override fun layout() {
        val fontRenderer = GuiContext.getFontRenderer() ?: return

        // 使用FontRenderer的API计算字符串宽度
        val stringWidth = fontRenderer.getStringWidth(text)
        // 使用FontRenderer的常量获取字体高度
        val stringHeight = fontRenderer.FONT_HEIGHT

        // 计算内容区域尺寸（不包括margin和padding）
        val contentWidth = stringWidth
        val contentHeight = stringHeight

        // 计算总尺寸（包含padding）
        val totalWidth = contentWidth + padding.horizontal()
        val totalHeight = contentHeight + padding.vertical()

        // 保持原有位置，更新尺寸
        bounds = Bounds(
            x = bounds?.x ?: 0,
            y = bounds?.y ?: 0,
            width = totalWidth,
            height = totalHeight
        )
    }

    override fun render() {
        val bounds = this.bounds ?: return
        val fontRenderer = GuiContext.getFontRenderer() ?: return

        // 计算内容绘制位置（考虑padding）
        val contentX = bounds.x + padding.left
        val contentY = bounds.y + padding.top

        println("text render x:$contentX y:$contentY")
        // 使用字体渲染器绘制文本
        fontRenderer.drawString(
            text, contentX.toFloat(), contentY.toFloat(), 0xFFFFFF, // 白色
            false
        )
    }
}

/**
 * 容器组件的基类，因为它需要包含子组件
 */
abstract class ContainerComponent(
//    override var margin: Spacing = Spacing.ZERO,
//    override var padding: Spacing = Spacing.ZERO,
//    override var onHover: ((Int, Int) -> Unit)? = null,
//    override var onClick: ((Int, Int) -> Unit)? = null
) : Component {
    val children = mutableListOf<Component>()

//    /**
//     * 布局参数
//     */
//    var width: Int = 0
//    var height: Int = 0

    override fun layout() {
//        if (bounds == null) {
//            bounds = Bounds(0, 0, 0, 0)
//        }
        // 子类实现具体的布局逻辑
        layoutChildren()
    }

    /**
     * 子类实现具体的子组件布局逻辑
     */
    protected abstract fun layoutChildren()

    /**
     * 获取内容区域边界（排除padding）
     */
    protected fun getContentBounds(): Bounds? {
        val bounds = this.bounds ?: return null
        return Bounds(
            x = bounds.x + padding.left,
            y = bounds.y + padding.top,
            width = bounds.width - padding.horizontal(),
            height = bounds.height - padding.vertical()
        )
    }

    /**
     * 渲染容器背景（子类可重写）
     */
    protected open fun renderBackground() {
//        println("a")
        val bounds = this.bounds ?: return
//        println("b")
        val gui = GuiContext.getGui() ?: return
//        println("c")
        // 绘制调试轮廓线
        gui.drawRectBorder(
            bounds.x.toFloat(),
            bounds.y.toFloat(),
            bounds.width.toFloat(),
            bounds.height.toFloat(),
            java.awt.Color.RED,
            1.0f
        )
    }

    override fun render() {
//        println("${this::class.simpleName} render()")
        // 绘制容器背景
        renderBackground()

        // 渲染子组件
        children.forEach { child ->
            child.render()
        }
    }
}

/**
 * 行组件 - 水平排列子组件
 */
data class RowComponent(
//    val x: Int,
//    val y: Int,
    override var margin: Spacing = Spacing.ZERO,
    override var padding: Spacing = Spacing.ZERO,
    override var onHover: ((mouseX: Int, mouseY: Int) -> Unit)? = null,
    override var onClick: ((mouseX: Int, mouseY: Int) -> Unit)? = null
) : ContainerComponent() {
    override var bounds: Bounds? = null

    override fun layoutChildren() {
        val contentBounds = getContentBounds() ?: return
        var currentX = contentBounds.x
        var maxHeight = 0

        children.forEach { child ->
            child.layout()
            val childBounds = child.bounds ?: return@forEach

            // 如果子组件是绝对定位，跳过布局计算
            if (childBounds.absolute) {
                child.layout()
                return@forEach
            }

            // 考虑子组件的margin，设置子组件位置
            child.bounds = childBounds.copy(
                x = currentX + child.margin.left,
                y = contentBounds.y + child.margin.top
            )

            // 父组件修改了子组件的bounds，触发重新layout
            child.layout()

            // 更新当前X坐标，考虑子组件的宽度及其右边距
            currentX += childBounds.width + child.margin.horizontal()
            maxHeight = maxOf(maxHeight, childBounds.height + child.margin.vertical())
        }

        // 计算容器的内容尺寸
        val contentWidth = currentX - contentBounds.x
        val contentHeight = maxHeight

        // 计算容器的总尺寸（包含padding）
        bounds = Bounds(
            x = bounds?.x ?: 0,
            y = bounds?.y ?: 0,
            width = contentWidth + padding.horizontal(),
            height = contentHeight + padding.vertical()
        )
    }
}

/**
 * 列组件 - 垂直排列子组件
 */
data class ColumnComponent(
//    val x: Int,
//    val y: Int,
    override var margin: Spacing = Spacing.ZERO,
    override var padding: Spacing = Spacing.ZERO,
    override var onHover: ((mouseX: Int, mouseY: Int) -> Unit)? = null,
    override var onClick: ((mouseX: Int, mouseY: Int) -> Unit)? = null
) : ContainerComponent() {
    override var bounds: Bounds? = null

    override fun layoutChildren() {
//        println("Column layoutChildren()")
        val contentBounds = getContentBounds() ?: return
        var currentY = contentBounds.y
        var maxWidth = 0

        children.forEach { child ->
            child.layout()
            val childBounds = child.bounds ?: return@forEach

            // 如果子组件是绝对定位，跳过布局计算
            if (childBounds.absolute) {
                child.layout()
                return@forEach
            }

            // 考虑子组件的margin，设置子组件位置
            child.bounds = childBounds.copy(
                x = contentBounds.x + child.margin.left,
                y = currentY + child.margin.top
            )

            // 父组件修改了子组件的bounds，触发重新layout
            child.layout()

            // 更新当前Y坐标，考虑子组件的高度及其底边距
            currentY += childBounds.height + child.margin.vertical()
            maxWidth = maxOf(maxWidth, childBounds.width + child.margin.horizontal())
        }

        // 计算容器的内容尺寸
        val contentWidth = maxWidth
        val contentHeight = currentY - contentBounds.y

        // 计算容器的总尺寸（包含padding）
        bounds = Bounds(
            x = bounds?.x ?: 0,
            y = bounds?.y ?: 0,
            width = contentWidth + padding.horizontal(),
            height = contentHeight + padding.vertical()
        )
    }
}