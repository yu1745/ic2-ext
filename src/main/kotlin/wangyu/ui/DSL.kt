@file:Suppress("FunctionName")

package wangyu.ui

/**
 * Row组件的DSL构建器函数
 * @param x x坐标，默认为0
 * @param y y坐标，默认为0
 * @param width 宽度，默认为0（自动计算）
 * @param height 高度，默认为0（自动计算）
 * @param init 带接收者的Lambda，用于配置Row组件
 * @return 配置好的RowComponent
 */
fun Row(
    x: Int?, y: Int?, width: Int = 0, height: Int = 0,
    margin: Spacing = Spacing.ZERO,
    padding: Spacing = Spacing.ZERO,
    onHover: ((mouseX: Int, mouseY: Int) -> Unit)? = null,
    onClick: ((mouseX: Int, mouseY: Int) -> Unit)? = null,
    init: RowComponent.() -> Unit
): RowComponent {
    val row = RowComponent(
        margin = margin,
        padding = padding,
        onHover = onHover,
        onClick = onClick
    )
    row.bounds = Bounds(x ?: 0, y ?: 0, width, height, absolute = x != null || y != null)
    Compose.pushContainer(row)
    row.init()
    Compose.popContainer()
    return row
}

/**
 * Column组件的DSL构建器函数
 * @param x x坐标，默认为0
 * @param y y坐标，默认为0
 * @param width 宽度，默认为0（自动计算）
 * @param height 高度，默认为0（自动计算）
 * @param margin 外边距
 * @param padding 内边距
 * @param onHover 鼠标悬停事件处理器
 * @param onClick 鼠标点击事件处理器
 * @param init 带接收者的Lambda，用于配置Column组件
 * @return 配置好的ColumnComponent
 */
fun Column(
    x: Int?, y: Int?,
    width: Int = 0,
    height: Int = 0,
    margin: Spacing = Spacing.ZERO,
    padding: Spacing = Spacing.ZERO,
    onHover: ((mouseX: Int, mouseY: Int) -> Unit)? = null,
    onClick: ((mouseX: Int, mouseY: Int) -> Unit)? = null,
    init: ColumnComponent.() -> Unit
): ColumnComponent {
    val column = ColumnComponent(
        margin = margin,
        padding = padding,
        onHover = onHover,
        onClick = onClick
    )
    column.bounds = Bounds(x ?: 0, y ?: 0, width, height, absolute = x != null || y != null)
//    column.width = width
//    column.height = height
    Compose.pushContainer(column)
    column.init()
    Compose.popContainer()
    return column
}

/**
 * Text组件的DSL构建器函数
 * @param text 文本内容
 * @param margin 外边距
 * @param padding 内边距
 * @param onHover 鼠标悬停事件处理器
 * @param onClick 鼠标点击事件处理器
 * @return TextComponent实例
 */
fun Text(
    text: String,
    margin: Spacing = Spacing.ZERO,
    padding: Spacing = Spacing.ZERO,
    onHover: ((mouseX: Int, mouseY: Int) -> Unit)? = null,
    onClick: ((mouseX: Int, mouseY: Int) -> Unit)? = null,
): TextComponent {
    val textComponent = TextComponent(
        text = text,
        margin = margin,
        padding = padding,
        onHover = onHover,
        onClick = onClick
    )
    Compose.addComponent(textComponent)
    return textComponent
}