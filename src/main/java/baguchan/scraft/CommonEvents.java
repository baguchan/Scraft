package baguchan.scraft;

import baguchan.scraft.client.model.item.ScraItemModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = Scraft.MODID)
public class CommonEvents {
    @SubscribeEvent
    public static void attribute(ItemAttributeModifierEvent event){
        ItemStack stack = event.getItemStack();
            CompoundTag tag = stack.getOrCreateTag();

            if (tag.contains("ScraItem")) {
                ItemStack ingredientStack = ItemStack.of(tag.getCompound("ScraItem"));
                ingredientStack.getAttributeModifiers(event.getSlotType()).forEach(event::addModifier);
                if(event.getSlotType() == EquipmentSlot.MAINHAND){

                    if (ingredientStack.getItem() instanceof BlockItem blockItem) {

                        event.addModifier(Attributes.ATTACK_DAMAGE, new AttributeModifier(UUID.fromString("65ef5197-a9f0-4345-b570-c27b6ccb288a"), "ScraItem Bonus", Mth.clamp(blockItem.getBlock().defaultDestroyTime(), 3, 10), AttributeModifier.Operation.ADDITION));
                        event.addModifier(Attributes.ATTACK_SPEED, new AttributeModifier(UUID.fromString("ea6679b8-74c6-4c0d-b246-99585d019587"), "ScraItem Bonus", Mth.clamp(-(blockItem.getBlock().defaultDestroyTime() / 5F) - 2.0F, -4.0F, -2.0F), AttributeModifier.Operation.ADDITION));

                    }else
                    if (stack.is(Tags.Items.BONES) || stack.is(Tags.Items.RODS_WOODEN)) {
                    event.addModifier(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(UUID.fromString("77f447a4-5940-e156-4d61-4344d920ebb9"), "ScraItem Bonus", 0.5, AttributeModifier.Operation.ADDITION));
                    event.addModifier(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(UUID.fromString("c271a2cb-d4ed-4f42-b4f7-b95fbb885efa"), "ScraItem Bonus", 0.5, AttributeModifier.Operation.ADDITION));
                 }
            }

        }
    }
}
