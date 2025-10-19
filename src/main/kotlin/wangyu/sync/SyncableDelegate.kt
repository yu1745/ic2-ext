package wangyu.sync

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import net.minecraft.tileentity.TileEntity

/**
 * 改进版本的同步委托，自动获取属性名
 */
class SyncableDelegate<T>(
    private val initialValue: T
) : ReadWriteProperty<TileEntity, T> {

    private var value: T = initialValue

    override fun getValue(thisRef: TileEntity, property: KProperty<*>): T = value

    override fun setValue(thisRef: TileEntity, property: KProperty<*>, value: T) {
        if (this.value != value) {
            this.value = value
            SyncRegistry.markDirty(thisRef, property.name)
        }
    }
}

/**
 * 创建可同步委托的扩展函数
 */
fun <T> syncable(initial: T): SyncableDelegate<T> {
    return SyncableDelegate(initial)
}