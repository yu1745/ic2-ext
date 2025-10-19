package wangyu.sync

import net.minecraft.tileentity.TileEntity

/**
 * 同步注册表 - 追踪需要同步的字段
 * 单例模式，内部为每个TileEntity维护独立的脏字段集合
 */
object SyncRegistry {
    private val dirtyFieldsMap = mutableMapOf<TileEntity, MutableSet<String>>()

    fun markDirty(tileEntity: TileEntity, name: String) {
        dirtyFieldsMap.getOrPut(tileEntity) { mutableSetOf() }.add(name)
    }

    fun clear(tileEntity: TileEntity) {
        dirtyFieldsMap.remove(tileEntity)
    }

    fun isDirty(tileEntity: TileEntity, name: String): Boolean {
        return dirtyFieldsMap[tileEntity]?.contains(name) ?: false
    }

    fun allDirty(tileEntity: TileEntity): Set<String> {
        return dirtyFieldsMap[tileEntity]?.toSet() ?: emptySet()
    }
}