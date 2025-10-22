package wangyu.proxy

import net.minecraft.item.Item
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Mod.EventBusSubscriber
open class CommonProxy {

    companion object {
        @SubscribeEvent
        @JvmStatic
        fun registerItems(event: RegistryEvent.Register<Item>) {
            // 物品注册已在主模组类中完成
        }
    }

    open fun registerItemRenderer(item: Item, meta: Int, id: String) {
        // 客户端专用，在CommonProxy中为空
    }
}