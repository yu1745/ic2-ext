/**
 * 标记需要自动同步的字段
 * @param id 字段ID，用于updateProgressBar
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class SyncField(val id: Int)