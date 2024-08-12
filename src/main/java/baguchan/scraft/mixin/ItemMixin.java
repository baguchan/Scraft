package baguchan.scraft.mixin;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "getBarWidth", at = @At("HEAD"), cancellable = true)

    public void getBarWidth(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (stack.getTag() != null && stack.getTag().contains("ScraItem")) {
            cir.setReturnValue(Math.round(13.0F - (float)stack.getDamageValue() * 13.0F / (float)stack.getMaxDamage()));
        }
    }
}
