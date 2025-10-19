package wangyu.sync

/**
 * 标记需要自动同步的字段
 * 注意: 所有不是int的基础类型都会被强转int. 而非基础类型会直接失败
 * @param id 字段ID，用于updateProgressBar
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class SyncField(val id: Int)