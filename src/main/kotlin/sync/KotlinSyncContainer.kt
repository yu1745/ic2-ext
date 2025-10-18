import com.example.sync.SyncRegistry
import net.minecraft.inventory.Container
import net.minecraft.inventory.IContainerListener
import net.minecraft.inventory.IInventory
import net.minecraft.tileentity.TileEntity
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.memberProperties

/**
 * 自动同步容器基类
 * 自动处理 TileEntity 中标记 @SyncField 的字段的同步
 */
abstract class KotlinSyncContainer<T : TileEntity>(
    protected val tile: T
) : Container() {

    override fun addListener(listener: IContainerListener) {
        super.addListener(listener)
        listener.sendAllWindowProperties(this, this.tile as IInventory)
    }

    override fun detectAndSendChanges() {
//        println("detectAndSendChanges() kt")
        super.detectAndSendChanges()
        val tileKClass: KClass<*> = tile::class
        val dirtyFields = SyncRegistry.allDirty(tile)
//        println("dirtyFields=${SyncRegistry.allDirty(tile).size}")

        for (property in tileKClass.memberProperties) {
            val annotation = property.annotations.find { it is SyncField } as? SyncField
//            if(annotation != null){
//                println("${property.name}")
//            }
            if (annotation != null && dirtyFields.contains(property.name)) {
                val value = property.getter.call(tile)
                for (listener in listeners) {
                    when (value) {
                        is Int -> listener.sendWindowProperty(this, annotation.id, value)
                        is Double -> listener.sendWindowProperty(this, annotation.id, value.toInt())
                        is Float -> listener.sendWindowProperty(this, annotation.id, value.toInt())
                        is Boolean -> listener.sendWindowProperty(this, annotation.id, if (value) 1 else 0)
                        // 可以根据需要添加更多类型的处理
                    }
                    println("${property.name} -> $value")
                }
            }
        }

        SyncRegistry.clear(tile)
    }


    override fun updateProgressBar(id: Int, data: Int) {
//        println("updateProgressBar()")
        val tileKClass: KClass<*> = tile::class

        // 找到对应ID的属性
        val targetProperty = tileKClass.memberProperties.find { property ->
            val annotation = property.annotations.find { it is SyncField } as? SyncField
            annotation?.id == id
        } ?: return

        println("${targetProperty.name} <- $data")

        // 检查是否是可变属性
        if (targetProperty !is KMutableProperty1<*, *>) return
        @Suppress("UNCHECKED_CAST")
        (targetProperty as KMutableProperty1<T, Int>).set(tile, data)
    }
}