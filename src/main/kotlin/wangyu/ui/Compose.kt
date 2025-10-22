package wangyu.ui

/**
 * 组合上下文管理对象
 * 使用 ThreadLocal 来确保每个线程有自己的构建栈
 */
object Compose {
    private val stack = ThreadLocal<MutableList<ContainerComponent>>()

    /**
     * 开始一个组合
     * @param init 初始化代码块，返回根组件
     * @return 配置好的根组件，已经完成布局计算
     */
    fun compose(init: () -> Component): Component {
        stack.set(mutableListOf())
        val root = init()
        stack.remove()

        // 执行布局计算
        root.layout()

        return root
    }

    /**
     * 将一个组件添加到当前栈顶的容器中
     * @param component 要添加的组件
     */
    internal fun addComponent(component: Component) {
        stack.get()?.lastOrNull()?.children?.add(component)
            ?: throw IllegalStateException("Cannot add component outside of a container")
    }

    /**
     * 将一个容器压入栈中
     * @param container 要压入的容器组件
     */
    internal fun pushContainer(container: ContainerComponent) {
        // 如果栈中有父容器，将当前容器添加到父容器的子组件中
        stack.get()?.lastOrNull()?.children?.add(container)
        // 将当前容器压入栈顶
        stack.get()?.add(container)
    }

    /**
     * 从栈中弹出一个容器
     */
    internal fun popContainer() {
        stack.get()?.removeLastOrNull()
    }
}