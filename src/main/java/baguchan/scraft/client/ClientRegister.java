package baguchan.scraft.client;

import baguchan.scraft.Scraft;
import baguchan.scraft.client.model.item.ScraItemModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Scraft.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientRegister {
    @SubscribeEvent
    public static void registerModel(ModelEvent.ModifyBakingResult event) {
        event.getModels().forEach((modelResourceLocation, bakedModel) -> event.getModels().put(modelResourceLocation, new ScraItemModel(event.getModelBakery(), bakedModel)));
    }

}
