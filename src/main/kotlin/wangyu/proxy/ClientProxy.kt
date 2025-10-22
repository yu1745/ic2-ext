package wangyu.proxy

import wangyu.SampleMod112
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraftforge.client.model.ModelLoader

class ClientProxy : CommonProxy() {

    override fun registerItemRenderer(item: Item, meta: Int, id: String) {
        ModelLoader.setCustomModelResourceLocation(item, meta,
            ModelResourceLocation(SampleMod112.MODID + ":" + id, "inventory"))
    }
}