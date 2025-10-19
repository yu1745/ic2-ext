package wangyu

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import wangyu.SampleMod112.LOGGER
import java.lang.reflect.Modifier
import java.util.jar.JarFile

@EventBusSubscriber(modid = SampleMod112.MODID)
class ModRegistry {
    companion object {
        private val registeredBlocks = mutableListOf<Block>()
        private val registeredItems = mutableListOf<Item>()
        private val registeredTileEntities = mutableListOf<Class<TileEntity>>()

        init {
            scanAndRegisterClasses()
        }

        private fun scanAndRegisterClasses() {
            LOGGER.info("scanAndRegisterClasses()")
            val packageName = "wangyu"
            val classLoader = Thread.currentThread().contextClassLoader

            try {
                val reflections = ClassLoader.getSystemResources(packageName.replace('.', '/'))
                while (reflections.hasMoreElements()) {
                    val url = reflections.nextElement()
                    LOGGER.info(url.toString())
                    val protocol = url.protocol

                    when (protocol) {
                        "file" -> {
                            val file = java.io.File(url.toURI())
                            scanDirectory(file, packageName, classLoader)
                        }

                        "jar" -> {
                            val jarPath = url.path.substring(5, url.path.indexOf("!"))
                            scanJarFile(java.io.File(jarPath), packageName, classLoader)
                        }
                    }
                }
            } catch (e: Exception) {
                LOGGER.error("Error scanning package $packageName: ${e.message}")
            }
        }

        private fun scanDirectory(directory: java.io.File, packageName: String, classLoader: ClassLoader) {
            val files = directory.listFiles()
            files?.forEach { file ->
                if (file.isDirectory) {
                    scanDirectory(file, "$packageName.${file.name}", classLoader)
                } else if (file.name.endsWith(".class")) {
                    val className = "$packageName.${file.name.substring(0, file.name.length - 6)}"
                    try {
                        val clazz = Class.forName(className, false, classLoader)
                        processClass(clazz)
                    } catch (e: ClassNotFoundException) {
                        LOGGER.error("Class not found: $className")
                    } catch (e: NoClassDefFoundError) {
                        LOGGER.error("No class def found: $className")
                    }
                }
            }
        }

        private fun scanJarFile(jarFile: java.io.File, packageName: String, classLoader: ClassLoader) {
            LOGGER.info("Scanning JAR file: ${jarFile.absolutePath}")
            val packagePath = packageName.replace('.', '/')

            try {
                JarFile(jarFile).use { jar ->
                    val entries = jar.entries()
                    while (entries.hasMoreElements()) {
                        val entry = entries.nextElement()
                        val name = entry.name

                        if (name.startsWith(packagePath) && name.endsWith(".class")) {
                            val className = name.substring(0, name.length - 6)
                                .replace('/', '.')
                            try {
                                val clazz = Class.forName(className, false, classLoader)
                                processClass(clazz)
                            } catch (e: ClassNotFoundException) {
                                LOGGER.error("Class not found: $className")
                            } catch (e: NoClassDefFoundError) {
                                LOGGER.error("No class def found: $className")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                LOGGER.error("Error scanning JAR file ${jarFile.name}: ${e.message}")
            }
        }

        private fun processClass(clazz: Class<*>) {
            if (Modifier.isAbstract(clazz.modifiers) || Modifier.isInterface(clazz.modifiers)) {
                return
            }

            when {
                Block::class.java.isAssignableFrom(clazz) -> {
                    try {
                        val constructor = clazz.getDeclaredConstructor()
                        constructor.isAccessible = true
                        val block = constructor.newInstance() as Block
                        registeredBlocks.add(block)
                        LOGGER.info("Registered block: ${clazz.simpleName}")
                    } catch (e: Exception) {
                        LOGGER.error("Failed to instantiate block ${clazz.simpleName}: ${e.message}")
                    }
                }

                Item::class.java.isAssignableFrom(clazz) && !ItemBlock::class.java.isAssignableFrom(clazz) -> {
                    try {
                        val constructor = clazz.getDeclaredConstructor()
                        constructor.isAccessible = true
                        val item = constructor.newInstance() as Item
                        registeredItems.add(item)
                        LOGGER.info("Registered item: ${clazz.simpleName}")
                    } catch (e: Exception) {
                        LOGGER.error("Failed to instantiate item ${clazz.simpleName}: ${e.message}")
                    }
                }

                TileEntity::class.java.isAssignableFrom(clazz) -> {
                    registeredTileEntities.add(clazz as Class<TileEntity>)
                    LOGGER.info("Registered tile entity: ${clazz.simpleName}")
                }
            }
        }

        @SubscribeEvent
        @JvmStatic
        fun registerBlocks(event: RegistryEvent.Register<Block>) {
            LOGGER.info("registerBlocks()")
            registeredBlocks.forEach { block ->
                block.setCreativeTab(SampleMod112.creativeTab)
                event.registry.register(block)
            }
        }

        @SubscribeEvent
        @JvmStatic
        fun registerItems(event: RegistryEvent.Register<Item>) {
            LOGGER.info("registerItems()")
            registeredItems.forEach { item ->
                item.setCreativeTab(SampleMod112.creativeTab)
                event.registry.register(item)
            }

            registeredBlocks.forEach { block ->
                val itemBlock = ItemBlock(block).setRegistryName(block.registryName)
                event.registry.register(itemBlock)
            }
        }

        @SubscribeEvent
        @SideOnly(Side.CLIENT)
        @JvmStatic
        fun registerModels(event: ModelRegistryEvent) {
            LOGGER.info("registerModels()")
            registeredItems.forEach { item ->
                val assetsName = getAssetsName(item)
                if (assetsName != null) {
                    SampleMod112.proxy.registerItemRenderer(item, 0, assetsName)
                }
            }

            registeredBlocks.forEach { block ->
                val itemBlock = Item.getItemFromBlock(block)
                val assetsName = getAssetsName(block)
                if (assetsName != null) {
                    SampleMod112.proxy.registerItemRenderer(itemBlock, 0, assetsName)
                }
            }
        }

        private fun getAssetsName(obj: Any): String? {
            return try {
                val clazz = obj::class.java
                val field = clazz.getDeclaredField("assets_name")
                field.isAccessible = true
                field.get(obj) as? String
            } catch (e: NoSuchFieldException) {
                LOGGER.warn("No assets_name field found in ${obj::class.simpleName}")
                null
            } catch (e: IllegalAccessException) {
                LOGGER.warn("Cannot access assets_name field in ${obj::class.simpleName}")
                null
            }
        }

        fun registerTileEntities() {
            LOGGER.info("registerTileEntities()")
            registeredTileEntities.forEach { tileEntityClass ->
                val assetsName = try {
                    val field = tileEntityClass.getDeclaredField("assets_name")
                    field.isAccessible = true
                    field.get(null) as? String
                } catch (e: Exception) {
                    null
                }
                if (assetsName == null) {
                    LOGGER.warn("${tileEntityClass.name} do not have assets name")
                    return@forEach
                }
                GameRegistry.registerTileEntity(tileEntityClass, assetsName)
                LOGGER.info("Registered TileEntity: ${tileEntityClass.simpleName} with key: $assetsName")
            }
        }

        @JvmStatic
        fun registerItemRenderers() {
            LOGGER.info("registerItemRenderers()")
            val isClient = FMLCommonHandler.instance().side == Side.CLIENT
            LOGGER.info("isClient:$isClient")
            LOGGER.info("SampleMod112.proxy::class:${SampleMod112.proxy::class}")
            if (isClient)
                registeredItems.forEach { item ->
                    val assetsName = getAssetsName(item)
                    if (assetsName != null) {
                        SampleMod112.proxy.registerItemRenderer(item, 0, assetsName)
                        LOGGER.info("Registered item renderer for ${item::class.simpleName} with assets: $assetsName")
                    }
                }
            if (isClient)
                registeredBlocks.forEach { block ->
                    val itemBlock = Item.getItemFromBlock(block)
                    val assetsName = getAssetsName(block)
                    if (assetsName != null) {
                        SampleMod112.proxy.registerItemRenderer(itemBlock, 0, assetsName)
                        LOGGER.info("Registered item renderer for block ${block::class.simpleName} with assets: $assetsName")
                    }
                }
        }

        @JvmStatic
        fun getItemByClass(clazz: Class<out Item>): Item? {
            return registeredItems.find { item ->
                item::class.java == clazz
            }
        }

        @JvmStatic
        fun getRegisteredItems(): List<Item> {
            return registeredItems.toList()
        }

        @JvmStatic
        fun getRegisteredBlocks(): List<Block> {
            return registeredBlocks.toList()
        }
    }
}