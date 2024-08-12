package baguchan.scraft;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.CreativeModeTabRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Scraft.MODID)
public class Scraft
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "scraft";

    public Scraft()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::creativeSetup);
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void creativeSetup(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.COMBAT) {
            ItemStack stack = new ItemStack(Items.DIAMOND_AXE);
            ItemStack stack2 = new ItemStack(Items.STICK);
            stack2.getOrCreateTag().put("ScraItem", stack.save(new CompoundTag()));
            event.accept(stack2);
            ItemStack stack3 = new ItemStack(Items.DIAMOND_SWORD);
            ItemStack stack4 = new ItemStack(Items.STICK);
            stack4.getOrCreateTag().put("ScraItem", stack3.save(new CompoundTag()));
            event.accept(stack4);

            ItemStack mace = new ItemStack(Items.IRON_BLOCK);
            ItemStack stick = new ItemStack(Items.STICK);
            stick.getOrCreateTag().put("ScraItem", mace.save(new CompoundTag()));
            event.accept(stick);
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
    }
}
